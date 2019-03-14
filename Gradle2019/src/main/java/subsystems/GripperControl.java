package subsystems;

import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Constants;
import utilPackage.Units;
import utilPackage.Util;

public class GripperControl{
    private static GripperControl instance;
    public static GripperControl getInstance(){
        if(instance == null){
            instance = new GripperControl();
        }
        return instance;
    }

    public enum States{
        disabled,
        reset, 
        running;
    }

    States state;
    Gripper gripper;
    double setpoint = 0;

    boolean disableReset = false;

    private GripperControl(){
        state = States.disabled;
        gripper = Gripper.getInstance();
    }

    public void setSetpoint(double setpoint){
        SmartDashboard.putNumber("Gripper setpoint", setpoint);
        setpoint = Util.forceInRange(setpoint, Constants.Gripper.minAngle, Constants.Gripper.maxAngle);
        this.setpoint = setpoint;
    }

    public void run(){
        switch(state){
            case disabled:
                if(RobotState.isEnabled() && TelescopeControl.getInstance().isRunning()){
                    state = States.reset;
                }
                break;
            case reset:
                gripper.setVoltage(6);
                gripper.enableReset();
                if(gripper.getReset()){
                    state = States.running;
                }
                break;
            case running:
                if(!disableReset && !gripper.getReset()){
                    disableReset = true;
                    gripper.disableReset();
                }
                double feedforward = gripper.getAntigrav();
                double p = 12.6251;
                double d = 0.3200;
                double error;
                if(MainArm.getInstance().getAngle() < Constants.MainArm.insideAngle){
                    error = Math.max(45*Units.Angle.degrees, setpoint) - gripper.getRelAngle();
                }else{
                    error = setpoint - gripper.getRelAngle();
                }
                double dError = -gripper.getRelAngleVel();
                double feedback = p*error + d*dError;
                // gripper.setVoltage(feedforward);
                gripper.setVoltage(feedforward+feedback);
                break;
        }
    }

    public boolean isRunning(){
        return state == States.running;
    }
}
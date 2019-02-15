package subsystems;

import edu.wpi.first.wpilibj.RobotState;
import robot.Constants;
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

    private GripperControl(){
        state = States.disabled;
        gripper = Gripper.getInstance();
    }

    public void setSetpoint(double setpoint){
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
                gripper.setVoltage(3);
                if(gripper.getReset()){
                    state = States.running;
                }
                break;
            case running:
                double feedforward = gripper.getAntigrav();
                double p = 15.2339;
                double d = 0.6684;
                double error = setpoint - gripper.getRelAngle();
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
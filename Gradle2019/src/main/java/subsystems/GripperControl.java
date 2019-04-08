package subsystems;

import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Constants;
import robot.Robot;
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
        fastPreReset,
        fastReset,
        preReset,
        reset, 
        running;
    }

    States state;
    Gripper gripper;
    double setpoint = 0;

    double resetSetpoint = 0;

    boolean disableReset = false;

    private GripperControl(){
        state = States.disabled;
        gripper = Gripper.getInstance();
    }

    public void setSetpoint(double setpoint){
        SmartDashboard.putNumber("Gripper setpoint", setpoint/Units.Angle.degrees);
        setpoint = Util.forceInRange(setpoint, Constants.Gripper.minAngle, Constants.Gripper.maxAngle);
        this.setpoint = setpoint;
    }

    public void reset(){
        state = States.reset;
    }

    public void run(){
        switch(state){
            case disabled:
                if(RobotState.isEnabled() && TelescopeControl.getInstance().isRunning()){
                    gripper.enableReset();
                    state = States.fastPreReset;
                    // state = States.reset;
                }
                break;
            case fastPreReset:
                gripper.setVoltage(-4); //was -3
                gripper.enableReset();
                if(!gripper.getReset()){
                    state = States.fastReset;
                }
                break;
            case fastReset:
                gripper.setVoltage(2); //was 2
                gripper.enableReset();
                if(gripper.getReset()){
                    state = States.preReset;
                }
                break;
            case preReset:
                gripper.setVoltage(-1.25); //was -1
                gripper.enableReset();
                if(!gripper.getReset()){
                    state = States.reset;
                    resetSetpoint = gripper.getRawEnc();
                }
                break;
            case reset:
                resetSetpoint -= 1;
                double tempP = 0.005;//was 0.01
                tempP /= gripper.getEncoderConv();
                gripper.setVoltage(tempP*(resetSetpoint - gripper.getRawEnc())); //was 0.75
                gripper.enableReset();
                if(gripper.getReset()){
                    gripper.setVoltage(0);
                    gripper.reset();
                    state = States.running;
                }
                break;
            case running:
                // if(!disableReset && !gripper.getReset()){
                //     disableReset = true;
                //     gripper.disableReset();
                // }
                if(Robot.getControlBoard().disableTelescopeGripper()){
                    gripper.setVoltage(0);
                    return;
                }
                double feedforward = gripper.getAntigrav();
                double p = 22.4535;
                double d = 0.4829;
                double error;
                if(MainArm.getInstance().getAngle() < Constants.MainArm.insideAngle){
                    // if(Robot.getControlBoard().isCargoMode()){
                    //     error = 35*Units.Angle.degrees - gripper.getRelAngle();
                    // }else{
                        error = Math.max(Constants.Gripper.maxAngle, setpoint) - gripper.getRelAngle();
                    // }
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
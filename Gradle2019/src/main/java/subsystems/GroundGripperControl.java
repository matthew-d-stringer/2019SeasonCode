package subsystems;

import edu.wpi.first.wpilibj.RobotState;
import robot.Constants;
import utilPackage.Units;
import utilPackage.Util;

public class GroundGripperControl{
    private static GroundGripperControl instance;
    public static GroundGripperControl getInstance(){
        if(instance == null){
            instance = new GroundGripperControl();
        }
        return instance;
    }

    public enum States{
        disabled,
        reset,
        running;
    }

    public enum SubStates{
        Static,
        Transitioning;
    }

    GroundGripper gripper;
    MainArm arm;
    MainArmControl armControl;
    States state;
    SubStates substate;
    double setpoint, pSetpoint;
    boolean disabled = false;
    boolean climbing = false;

    final double p = 11.4597;
    final double d = 1.1872;

    private GroundGripperControl(){
        gripper = GroundGripper.getInstance();
        arm = MainArm.getInstance();
        armControl = MainArmControl.getInstance();
        state = States.disabled;
        substate = SubStates.Static;
        setpoint = Constants.GroundGripper.maxAngle;
        pSetpoint = setpoint;
    }

    public void enable(){
        disabled = false;
    }

    public void disable(){
        disabled = true;
    }

    public void retract(){
        setpoint = Constants.GroundGripper.maxAngle;
        climbing = false;
    }

    public void ballGrab(){
        setpoint = Constants.GroundGripper.ballGrabAngle;
        climbing = false;
    }

    public void preClimb(){
        setpoint = 70*Units.Angle.degrees;
        climbing = false;
    }
    public void climbing(){
        setpoint = -5*Units.Angle.degrees;
        climbing = true;
    }
    public void lowclimbing(){
        setpoint = -5*Units.Angle.degrees;
        climbing = true;
    }

    public void afterClimb(){
        setpoint = 20*Units.Angle.degrees;
        climbing = false;
    }

    public void run(){
        if(!disabled){       
            switch(state){
            case disabled:
                if(RobotState.isEnabled()){
                    state = States.reset;
                }
                break;
            case reset:
                gripper.setVoltage(2);
                if(gripper.getReset()){
                    state = States.running;
                }
                break;
            case running:
                double feedforward;
                if(climbing){
                    feedforward = gripper.getClimbAntigrav();
                }else{
                    feedforward = gripper.getAntigrav();
                }

                double clearenceAngle = Constants.GroundGripper.inOutAngle;
                double angle = gripper.getAngle();
                double tSet = pSetpoint;

                switch(substate){
                    case Static:
                        tSet = setpoint;
                        armControl.commandBallClearence(false);
                        // if(setpoint < clearenceAngle && !armControl.finishedMovement()){
                        //     tSet = 10*Units.Angle.degrees;
                        // }
                        if(setpoint > clearenceAngle && pSetpoint <= clearenceAngle){
                            substate = SubStates.Transitioning;
                        }else if(setpoint < clearenceAngle && pSetpoint >= clearenceAngle){
                            substate = SubStates.Transitioning;
                        }else{
                            pSetpoint = setpoint;
                        }
                        break;
                    case Transitioning:
                        armControl.commandBallClearence(true);
                        if(arm.getAngle() > 0){
                            tSet = setpoint;
                        }else{
                            tSet = pSetpoint;
                        }
                        if(Util.inErrorRange(setpoint, angle, 5*Units.Angle.degrees)){
                            pSetpoint = setpoint;
                            substate = SubStates.Static;
                        }
                        break;
                }

                double error = tSet - gripper.getAngle();
                double derror = -gripper.getAngleVel();
                double feedback = p*error+d*derror;

                gripper.setVoltage(feedforward+feedback);
                break;
            }
        }else{
            gripper.setVoltage(0);
            MainArmControl.getInstance().commandBallClearence(false);
        }
     }
}
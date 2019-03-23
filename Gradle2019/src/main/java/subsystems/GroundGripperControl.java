package subsystems;

import edu.wpi.first.wpilibj.RobotState;
import robot.Constants;

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
        in,
        out,
        climbing
    }

    GroundGripper gripper;
    MainArm arm;
    States state;
    SubStates substate;
    double setpoint;
    boolean disabled = false;

    final double p = 5.6031;
    final double d = 1.4005;

    private GroundGripperControl(){
        gripper = GroundGripper.getInstance();
        arm = MainArm.getInstance();
        state = States.disabled;
        substate = SubStates.in;
        setpoint = Constants.GroundGripper.maxAngle;
    }

    public void enable(){
        disabled = false;
    }

    public void disable(){
        disabled = true;
    }

    public void retract(){
        setpoint = Constants.GroundGripper.maxAngle;
        substate = SubStates.in;
    }

    public void ballGrab(){
        setpoint = Constants.GroundGripper.ballGrabAngle;
        substate = SubStates.out;
    }

    public void climbing(){
        setpoint = 0;
        substate = SubStates.climbing;
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
                if(gripper.getReset()){
                    state = States.running;
                }
                break;
            case running:
                double feedforward;
                if(substate == SubStates.climbing){
                    feedforward = gripper.getClimbAntigrav();
                }else{
                    feedforward = gripper.getAntigrav();
                }

                double clearenceAngle = Constants.GroundGripper.inOutAngle;
                double angle = gripper.getAngle();
                if((angle < clearenceAngle && setpoint > clearenceAngle)||
                (angle > clearenceAngle && setpoint < clearenceAngle)){
                    MainArmControl.getInstance().commandBallClearence(true);
                }else{
                    MainArmControl.getInstance().commandBallClearence(false);
                }

                double error;
                // if(arm.getAngle() > 0){
                    error = setpoint - gripper.getAngle();
                // }else{
                //     double tSet = setpoint;
                //     if(angle < clearenceAngle){
                //         tSet = Math.min(Constants.GroundGripper.outClereance, tSet);
                //     }else{
                //         tSet = Math.max(Constants.GroundGripper.inClereance, tSet);
                //     }
                //     error = tSet - gripper.getAngle();
                // }
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
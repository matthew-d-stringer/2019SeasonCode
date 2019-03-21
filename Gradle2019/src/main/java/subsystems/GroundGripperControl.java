package subsystems;

import edu.wpi.first.wpilibj.RobotState;
import robot.Constants;

public class GroundGripperControl{
    private GroundGripperControl instance;
    public GroundGripperControl getInstance(){
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
    States state;
    SubStates substate;
    double setpoint;

    final double p = 9.6819;
    final double d = 1.8380;

    private GroundGripperControl(){
        gripper = GroundGripper.getInstance();
        state = States.disabled;
        substate = SubStates.in;
        setpoint = Constants.GroundGripper.maxAngle;
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

                double error = setpoint - gripper.getAngle();
                double derror = -gripper.getAngleVel();
                double feedback = p*error+d*derror;

                gripper.setVoltage(feedforward+feedback);
                break;
        }
    }
}
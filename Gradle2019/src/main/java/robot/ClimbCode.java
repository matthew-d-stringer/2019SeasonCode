package robot;

import controlBoard.IControlBoard;
import coordinates.Heading;
import drive.DriveOutput;
import edu.wpi.first.wpilibj.Timer;
import subsystems.ArmSystemControl;
import subsystems.Climber;
import subsystems.GroundGripper;
import subsystems.GroundGripperControl;
import subsystems.MainArm;
import subsystems.MainArmControl;
import utilPackage.Units;

public class ClimbCode{
    private static ClimbCode instance;
    public static ClimbCode getInstance(){
        if(instance == null){
            instance = new ClimbCode();
        }
        return instance;
    }

    IControlBoard controls;
    Climber climber;
    GroundGripper gripper;
    GroundGripperControl gripperControl;
    DriveOutput drive;
    IControlBoard controlBoard;
    Timer wait;

    private ClimbCode(){
        controls = Robot.getControlBoard();
        climber = Climber.getInstance();
        gripper = GroundGripper.getInstance();
        gripperControl = GroundGripperControl.getInstance();
        drive = DriveOutput.getInstance();
        gripperControl.retract();
        controlBoard = Robot.getControlBoard();
        wait = new Timer();
    }

    private enum States{
        wait,
        extend,
        roll,
        retract,
        done;
    }

    States state = States.wait;

    public boolean isDone(){
        return state == States.done;
    }

    public void reset(){
        state = States.wait;
    }

    public void run(){
        double vel = 2*Units.Length.feet;
        switch(state){
            case wait:
                climber.setVoltage(0);
                ArmSystemControl.getInstance().setSetpoints(0, 0.1);
                GroundGripper.getInstance().rollersOff();
                if(controls.climbUp() && MainArmControl.getInstance().finishedMovement()){
                    climber.reset();
                    gripperControl.climbing();
                    wait.start();
                    state = States.extend;
                }
                break;
            case extend:
                drive.set(DriveOutput.Modes.Velocity, vel, vel);
                GroundGripper.getInstance().rollersOff();
                if(controls.climbUp() && wait.get() > 1.5){
                    climber.setVoltage(-8);
                }else{
                    climber.setVoltage(climber.getAntigrav());
                }
                if(controlBoard.climbForward()){
                    state = States.roll;
                }
                break;
            case roll:
                climber.setVoltage(climber.getAntigrav());
                drive.set(DriveOutput.Modes.Velocity, vel, vel);
                if(controls.climbForward()){
                    GroundGripper.getInstance().rollersClimb();
                }else if(controls.climbRetract()){
                    state = States.retract;
                }
                break;
            case retract:
                drive.setNoVoltage();
                GroundGripper.getInstance().rollersOff();
                gripperControl.retract();
                if(controls.climbForward()){
                    state = States.roll;
                }
                if(controlBoard.climbRetract()){
                    climber.setVoltage(8); //was 5
                }else{
                    climber.setVoltage(climber.getAntigrav()); 
                }
                if(climber.getClimbLen() < 0.1 && climber.getCurrent() > 30){
                    climber.setVoltage(0);
                }
                break;
            case done:
                climber.setVoltage(0);
                break;
        }
    }
    // boolean disableClimbUp = false;
    // public void run(){
    //     if(controlBoard.climbUp()){

    //     }
    // }
}
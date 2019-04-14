package robot;

import controlBoard.IControlBoard;
import coordinates.Heading;
import drive.DriveOutput;
import drive.DriveOutput.Modes;
import edu.wpi.first.wpilibj.Timer;
import subsystems.ArmSystemControl;
import subsystems.Climber;
import subsystems.GroundGripper;
import subsystems.GroundGripperControl;
import subsystems.MainArm;
import subsystems.MainArmControl;
import utilPackage.FancyDrive;
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
    ArmSystemControl armControl;
    DriveOutput drive;
    IControlBoard controlBoard;
    Timer wait;
    boolean lowclimbing = false;

    private ClimbCode(){
        controls = Robot.getControlBoard();
        climber = Climber.getInstance();
        gripper = GroundGripper.getInstance();
        gripperControl = GroundGripperControl.getInstance();
        armControl = ArmSystemControl.getInstance();
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

    public void run(FancyDrive driveCode){
        double vel = 1*Units.Length.feet;
        switch(state){
            case wait:
                driveCode.run();
                climber.setVoltage(0);
                armControl.setSetpoints(-35*Units.Angle.degrees, 0.1);
                GroundGripper.getInstance().rollersOff();
                gripperControl.preClimb();
                if(controls.climbUp() && MainArmControl.getInstance().finishedMovement()){
                    lowclimbing = controls.lowClimb();
                    climber.reset();
                    // if(lowclimbing){
                    //     gripperControl.lowclimbing();
                    // }else{
                    //     gripperControl.climbing();
                    // }
                    wait.start();
                    state = States.extend;
                }
                break;
            case extend:
                // drive.set(DriveOutput.Modes.Velocity, vel, vel);
                drive.setNoVoltage();
                GroundGripper.getInstance().rollersOff();
                if(lowclimbing){
                    gripperControl.lowclimbing();
                }else if(wait.get() > 0.5){
                    gripperControl.climbing();
                }else{
                    gripperControl.preClimb();
                }
                if(controls.climbUp() /*&& wait.get() > 0.5*/){
                    if(lowclimbing){
                        climber.setVoltage(-5);
                    }else{
                        climber.setVoltage(-11.25);
                    }
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
                }else if(controls.climbUp()){
                    state = States.extend;
                }else{
                    GroundGripper.getInstance().rollersOff();
                }
                break;
            case retract:
                drive.setNoVoltage();
                GroundGripper.getInstance().rollersOff();
                gripperControl.afterClimb();
                if(controls.climbForward()){
                    state = States.roll;
                }
                if(controlBoard.climbRetract()){
                    climber.setVoltage(8); //was 5
                }else{
                    climber.setVoltage(climber.getAntigrav()); 
                }
                if(climber.getCurrent() > 30){
                    climber.setVoltage(0);
                    wait.start();
                    state = States.done;
                }
                break;
            case done:
                armControl.setSetpoints(-80*Units.Angle.degrees, 0.1);
                climber.setVoltage(0);
                if(wait.get() > 1){
                    drive.setNoVelocity();
                }else {
                    drive.set(Modes.Velocity, vel, vel);;
                }
                break;
        }
    }
    // boolean disableClimbUp = false;
    // public void run(){
    //     if(controlBoard.climbUp()){

    //     }
    // }
}
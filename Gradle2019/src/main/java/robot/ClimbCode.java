package robot;

import controlBoard.IControlBoard;
import drive.DriveOutput;
import edu.wpi.first.wpilibj.Timer;
import subsystems.Climber;
import subsystems.GroundGripper;
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
    DriveOutput drive;
    Timer wait;

    private ClimbCode(){
        controls = Robot.getControlBoard();
        climber = Climber.getInstance();
        drive = DriveOutput.getInstance();
        //TODO: guides up
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
        switch(state){
            case wait:
                climber.setVoltage(0);
                GroundGripper.getInstance().setVoltage(0);
                GroundGripper.getInstance().rollersOff();
                if(controls.climbUp()){
                    climber.reset();
                    //TODO: add climbs down and change this
                    GroundGripper.getInstance().setVoltage(-12);
                    wait.start();
                    state = States.extend;
                }
                break;
            case extend:
                drive.setNoVoltage();
                GroundGripper.getInstance().rollersOff();
                if(controls.climbUp() && wait.get() > 1.5){
                    climber.setVoltage(-12);
                }else{
                    climber.setVoltage(climber.getAntigrav());
                }
                if(climber.getClimbLen() > 0.9){
                    state = States.roll;
                }
                break;
            case roll:
                if(climber.getClimbLen() > 0.9)
                    climber.setVoltage(climber.getAntigrav());
                else
                    climber.setVoltage(-6);
                double vel = 2*Units.Length.feet;
                drive.set(DriveOutput.Modes.Velocity, vel, vel);
                if(controls.climbUp()){
                    //TODO: add output to rollers
                    GroundGripper.getInstance().rollersClimb();
                }else{
                    state = States.retract;
                }
                break;
            case retract:
                drive.setNoVoltage();
                GroundGripper.getInstance().rollersOff();
                if(controls.climbUp()){
                    state = States.roll;
                }
                climber.setVoltage(5);
                if(climber.getClimbLen() < 0.1){
                    climber.setVoltage(0);
                    state = States.done;
                }
                break;
            case done:
                climber.setVoltage(0);
                break;
        }
    }
}
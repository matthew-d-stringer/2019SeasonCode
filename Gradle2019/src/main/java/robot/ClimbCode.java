package robot;

import controlBoard.IControlBoard;
import drive.DriveOutput;
import edu.wpi.first.wpilibj.Timer;
import subsystems.Climber;
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
        climber.guidesUp();
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
                if(controls.climbUp()){
                    climber.reset();
                    climber.guidesDown();
                    wait.start();
                    state = States.extend;
                }
                break;
            case extend:
                drive.setNoVoltage();
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
                }else{
                    state = States.retract;
                }
                break;
            case retract:
                drive.setNoVoltage();
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
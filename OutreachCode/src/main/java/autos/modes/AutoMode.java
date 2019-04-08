package autos.modes;

import autos.AutoEndedException;
import autos.actions.Action;
import coordinates.Coordinate;
import drive.PositionTracker;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Robot;
import utilPackage.Units;

public abstract class AutoMode extends Thread{
    private Coordinate initPos = new Coordinate();
    private Timer autoTime = new Timer();
    private boolean isActive = true;
    private Direction initDirection = Direction.Forward;

    public enum Direction{
        Forward,
        Backward,
        Right,
        Left;
    }

    public AutoMode(){
        super("Auto Thread");
        SmartDashboard.putNumber("Auto Time", autoTime.get());
    }

    @Override
    public void run() {
        while(!RobotState.isAutonomous());
        autoTime.start();
        PositionTracker.getInstance().setInitPos(initPos);
        if(initDirection == Direction.Forward){
            PositionTracker.getInstance().robotForward();
        }else if(initDirection == Direction.Backward){
            PositionTracker.getInstance().robotBackward();
        }
        try{
            auto();
        }catch(AutoEndedException e){
            DriverStation.reportError("Auto Mode finished early!", false);
        }
        SmartDashboard.putNumber("Auto Time", autoTime.get());
        autoTime.stop();
    }

    public abstract void auto() throws AutoEndedException;

    protected void setInitPos(double xInFeet, double yInFeet){
        initPos.setXY(xInFeet, yInFeet);
        initPos.mult(Units.Length.feet);
    }

    protected void setInitDirection(Direction direction){
        this.initDirection = direction;
    }

    public boolean isActive(){
        return isActive;
    }

    public boolean isActiveWithThrow() throws AutoEndedException{
        if(!isActive()){
            throw new AutoEndedException();
        }

        return isActive();
    }
    
    public void runAction(Action action) throws AutoEndedException{
        if(Robot.getControlBoard().autoStop()){
            return;
        }
        SmartDashboard.putString("Current Action: ", action.getClass().getSimpleName());
        isActiveWithThrow();
        SmartDashboard.putString("Action Message", "Starting Action");
        action.start();
        while(!action.isFinished() && isActiveWithThrow() && !Robot.getControlBoard().autoStop()){
            SmartDashboard.putString("Action Message", "Updating Action");
            try{
                action.update();
            }catch(RuntimeException e){
                e.printStackTrace();
            }
        }
        if(Robot.getControlBoard().autoStop()){
            return;
        }
        SmartDashboard.putString("Action Message", "Finishing Action");
        action.done();
        SmartDashboard.putString("Action Message", "Finished Action");
    }

    public void end(){
        isActive = false;
    }
}
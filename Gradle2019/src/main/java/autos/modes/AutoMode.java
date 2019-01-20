package autos.modes;

import autos.actions.Action;
import coordinates.Coordinate;
import drive.PositionTracker;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import utilPackage.Units;

public abstract class AutoMode extends Thread{
    private Coordinate initPos = new Coordinate();

    public AutoMode(){
        super("Auto Thread");
    }

    @Override
    public void run() {
        while(!RobotState.isAutonomous());
        PositionTracker.getInstance().setInitPos(initPos);
        auto();
    }

    public abstract void auto();

    protected void setInitPos(double xInFeet, double yInFeet){
        initPos.setXY(xInFeet, yInFeet);
        initPos.mult(Units.Length.feet);
    }
    
    public void runAction(Action action){
        SmartDashboard.putString("Current Action: ", action.getClass().getName());
        if(!RobotState.isAutonomous())
            return;
        SmartDashboard.putString("Action Message", "Starting Action");
        action.start();
        while(!action.isFinished() && RobotState.isAutonomous()){
            SmartDashboard.putString("Action Message", "Updating Action");
            try{
                action.update();
            //TODO: Fix random spline auto bug
            }catch(RuntimeException e){
                e.printStackTrace();
            }
        }
        SmartDashboard.putString("Action Message", "Finishing Action");
        action.done();
        SmartDashboard.putString("Action Message", "Finished Action");
    }
}
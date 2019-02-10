package autos.actions;

import drive.PositionTracker;

public class WaitUntilY extends Action{
    PositionTracker tracker;
    double y;

    public WaitUntilY(double y){
        tracker = PositionTracker.getInstance();
        this.y = y;
    }

    @Override
    public void start() {
    }

    @Override
    public void update() {
    }

    @Override
    public boolean isFinished() {
        return tracker.getPosition().getPos().getY() > y;
    }

    @Override
    public void done() {
    }
}
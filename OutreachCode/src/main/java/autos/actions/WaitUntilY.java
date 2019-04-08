package autos.actions;

import drive.PositionTracker;

public class WaitUntilY extends Action{
    PositionTracker tracker;
    double y;

    boolean reverse = false;

    public WaitUntilY(double y){
        tracker = PositionTracker.getInstance();
        this.y = y;
    }
    public WaitUntilY(double y, boolean reverse){
        tracker = PositionTracker.getInstance();
        this.y = y;
        this.reverse = reverse;
    }

    @Override
    public void start() {
    }

    @Override
    public void update() {
    }

    @Override
    public boolean isFinished() {
        if(reverse)
            return tracker.getPosition().getPos().getY() < y;
        else
            return tracker.getPosition().getPos().getY() > y;
    }

    @Override
    public void done() {
    }
}
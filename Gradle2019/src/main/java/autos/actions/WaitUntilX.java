package autos.actions;

import drive.PositionTracker;

public class WaitUntilX extends Action{
    PositionTracker tracker;
    double x;

    boolean reverse = false;

    public WaitUntilX(double x){
        tracker = PositionTracker.getInstance();
        this.x = x;
    }
    public WaitUntilX(double x, boolean reverse){
        tracker = PositionTracker.getInstance();
        this.x = x;
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
            return tracker.getPosition().getPos().getX() < x;
        else
            return tracker.getPosition().getPos().getX() > x;
    }

    @Override
    public void done() {
    }
}
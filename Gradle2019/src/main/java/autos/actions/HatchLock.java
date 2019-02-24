package autos.actions;

import subsystems.Gripper;

public class HatchLock extends Action{
    public  HatchLock(){
    }

    @Override
    public void start() {
        Gripper.getInstance().hatchLock();
    }

    @Override
    public void update() {
    }

    @Override
    public void done() {
    }

    @Override
    public boolean isFinished(){
        return true;
    }
}
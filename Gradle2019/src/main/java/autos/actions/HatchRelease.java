package autos.actions;

import subsystems.Gripper;

public class HatchRelease extends Action{
    public  HatchRelease(){
    }

    @Override
    public void start() {
        Gripper.getInstance().hatchRelease();
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
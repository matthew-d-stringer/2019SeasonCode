package autos.actions;

import edu.wpi.first.wpilibj.Timer;

public class WaitAction extends Action{
    double time;
    double startTime;
    double endTime;

    public WaitAction(double time){
        this.time = time;
    }

    @Override
    public void start() {
        startTime = Timer.getFPGATimestamp();
        endTime = startTime+endTime;
    }

    @Override
    public void update() {
    }

    @Override
    public boolean isFinished() {
        return Timer.getFPGATimestamp() >= endTime;
    }

    @Override
    public void done() {
    }
}
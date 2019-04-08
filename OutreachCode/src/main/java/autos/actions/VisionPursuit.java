package autos.actions;

import coordinates.Coordinate;
import coordinates.Heading;
import drive.DriveOutput;
import drive.DriveOutput.Modes;
import edu.wpi.first.wpilibj.Timer;
import utilPackage.Derivative;
import utilPackage.Units;
import utilPackage.Util;
import vision.Jevois;

public class VisionPursuit extends Action{

    boolean isDone = false;
    double distance = 1.9*Units.Length.feet; //was 2.6
    double finishThresh = 0.4*Units.Length.feet;
    double deccelDist = 1.1*Units.Length.feet;
    double maxVel = 6*Units.Length.feet;
    boolean useTimer = false;
    Derivative dAngle;
    double time = 0;
    double startTime = 0;

    public VisionPursuit(){
        dAngle = new Derivative();
    }
    public VisionPursuit(double stopDist){
        dAngle = new Derivative();
        distance = stopDist;
    }

    public void disableAfterTime(double time){
        useTimer = true;
        this.time = time;
    }

    /**
     * @param finishThresh the finishThresh to set
     */
    public void setFinishThresh(double finishThresh) {
        this.finishThresh = finishThresh;
    }

    /**
     * @param deccelDist the deccelDist to set
     */
    public void setDeccelDist(double deccelDist) {
        this.deccelDist = deccelDist;
    }

    /**
     * @param maxVel the maxVel to set
     */
    public void setMaxVel(double maxVel) {
        this.maxVel = maxVel;
    }

    @Override
    public void start() {
        isDone = false;
        Heading target = Jevois.getInstance().getPT();
        double angle = Math.PI/2 - target.getAngle();
        dAngle.reset(Timer.getFPGATimestamp(), angle);

        startTime = Timer.getFPGATimestamp();
    }

    @Override
    public void update() {
        double turn, forward;
        Heading target = Jevois.getInstance().getPT();
        double angle = Math.PI/2 - target.getAngle();
        double dist = target.getMagnitude();

        turn = 1.05*angle + 0.1*dAngle.Calculate(angle, Timer.getFPGATimestamp());
        Coordinate pt1;
        pt1 = new Coordinate(distance, 0*Units.Length.feet);
        Coordinate pt2 = new Coordinate(distance+deccelDist, 2*Units.Length.feet);
        forward = Util.mapRange(dist, pt1, pt2);
        forward = Math.min(forward, maxVel);

        double outLeft = -turn + forward;
        double outRight = turn + forward;

        DriveOutput.getInstance().set(Modes.Velocity, outRight, outLeft);

        isDone = forward < finishThresh;
}

    @Override
    public boolean isFinished() {
        return isDone || Timer.getFPGATimestamp() - startTime > time;
    }

    @Override
    public void done() {
        DriveOutput.getInstance().setNoVelocity();
    }
}
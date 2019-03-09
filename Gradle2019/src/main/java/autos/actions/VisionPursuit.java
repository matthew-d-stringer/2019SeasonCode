package autos.actions;

import coordinates.Coordinate;
import coordinates.Heading;
import drive.DriveOutput;
import drive.DriveOutput.Modes;
import utilPackage.Units;
import utilPackage.Util;
import vision.Jevois;

public class VisionPursuit extends Action{

    boolean isDone = false;
    double distance = 2.5*Units.Length.feet;

    public VisionPursuit(){
    }
    public VisionPursuit(double stopDist){
        distance = stopDist;
    }

    @Override
    public void start() {
        isDone = false;
    }

    @Override
    public void update() {
        double turn, forward;
        Heading target = Jevois.getInstance().getPT();
        double angle = Math.PI/2 - target.getAngle();
        double dist = target.getMagnitude();

        turn = 1.1*angle;
        Coordinate pt1;
        pt1 = new Coordinate(3*Units.Length.feet, 0*Units.Length.feet);
        Coordinate pt2 = new Coordinate(4*Units.Length.feet, 3*Units.Length.feet);
        forward = Util.mapRange(dist, pt1, pt2);
        forward = Math.min(forward, 6*Units.Length.feet);

        double outLeft = -turn + forward;
        double outRight = turn + forward;

        DriveOutput.getInstance().set(Modes.Velocity, outRight, outLeft);

        isDone = forward < 1*Units.Length.feet;
}

    @Override
    public boolean isFinished() {
        return isDone;
    }

    @Override
    public void done() {
        DriveOutput.getInstance().setNoVelocity();;
    }
}

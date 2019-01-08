package autos.actions;

import coordinates.Heading;
import drive.DriveOutput;
import drive.PositionTracker;
import path.ProfileHolder;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;
import utilPackage.Util;

public class DriveTurn extends Action{
    private Heading target;
    TrapezoidalMp.constraints constraints;
    ProfileHolder mp;
    double error = Double.POSITIVE_INFINITY;
    private double thresh = 2*Units.Angle.degrees;

    //TODO: Test this program
    public DriveTurn(Heading target, TrapezoidalMp.constraints turnProfile){
        this.target = target;
        constraints = turnProfile;
    }

    /**
     * @param thresh the thresh in order to finish
     */
    public void setThresh(double thresh) {
        this.thresh = thresh;
    }

    @Override
    public void start() {
        Heading cHeading = PositionTracker.getInstance().getPosition().getHeading();
        constraints.setpoint = 0;
        //If robot is left of setpoint, angle is negative
        mp = new ProfileHolder(new TrapezoidalMp(constraints), Heading.headingsToAngle(cHeading, target));
        mp.setMinimumVel(20*Units.Angle.degrees);
        mp.generate();
    }

    @Override
    public void update() {
        Heading cHeading = PositionTracker.getInstance().getPosition().getHeading();
        error = Heading.signedHeadingsToAngle(cHeading, target);
        double anglularVel = mp.calculateVel(Math.abs(error));
        anglularVel *= Util.checkSign(error);
        DriveOutput.getInstance().setTransformation(anglularVel, 0);
    }

    @Override
    public boolean isFinished() {
        return error < thresh;
    }

    @Override
    public void done() {
        DriveOutput.getInstance().setNoVelocity();
    }
}
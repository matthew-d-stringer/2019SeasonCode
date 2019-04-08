package autos.actions;

import coordinates.Coordinate;
import coordinates.Heading;
import coordinates.Pos2D;
import drive.DriveOutput;
import drive.PositionTracker;
import drive.DriveOutput.Modes;
import utilPackage.Units;
import utilPackage.Util;

public class DirectPursuit extends Action{
    Coordinate setpoint;
    Coordinate distToVelpt1 = new Coordinate(0*Units.Length.feet, 0.5*Units.Length.feet);
    Coordinate distToVelpt2 = new Coordinate(3*Units.Length.feet, 3*Units.Length.feet);

    double finishRange = 3*Units.Length.inches;

    double dist = 10000;

    public DirectPursuit(Coordinate setpoint){
        this.setpoint = setpoint;
    }
    public DirectPursuit(Coordinate setpoint, double finishRange){
        this.setpoint = setpoint;
        this.finishRange = finishRange;
    }
    
    @Override
    public void start() {
    }

    @Override
    public void update() {
        Pos2D robotPos = PositionTracker.getInstance().getPosition();
        Heading vecToSetpoint = Heading.headingBetweenPoints(robotPos.getPos(), setpoint);
        double eta = Heading.signedHeadingsToAngle(vecToSetpoint, robotPos.getHeading());
        double dist = vecToSetpoint.getMagnitude();
        this.dist = dist;

        double turn = 1.2*eta;
        double forward = Util.mapRange(dist, distToVelpt1, distToVelpt2);
        forward = Util.forceInRange(forward, 0*Units.Length.feet, 4*Units.Length.feet);

        double outLeft = turn+forward;
        double outRight = -turn+forward;
        
        if(!isFinished())
            DriveOutput.getInstance().set(Modes.Velocity, outRight, outLeft);
        else
            done();
    }

    @Override
    public boolean isFinished() {
        return dist < finishRange;
    }

    @Override
    public void done() {
        DriveOutput.getInstance().set(Modes.Velocity, 0, 0);
    }
}
package autos.modes;

import autos.AutoEndedException;
import autos.actions.DrivePath;
import coordinates.Coordinate;
import drive.PositionTracker;
import path.TrajectoryList;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public abstract class OutreachAuto extends AutoMode{
    final double positionIncrement = 3.21*Units.Length.feet;
    Coordinate cPos;
    Direction cDirection;
    TrapezoidalMp.constraints motionProfile;

    public OutreachAuto(){
        motionProfile = new TrapezoidalMp.constraints(0, 5*Units.Length.feet, 2*Units.Length.feet);
        cPos = new Coordinate();
        cDirection = Direction.Forward;
        setInitPos(0,0);
        setInitDirection(cDirection);
    }

    private void driveForward() throws AutoEndedException {
        TrajectoryList path = new TrajectoryList(cPos);
        Coordinate nextPos;
        switch(cDirection){
            case Forward:
                nextPos = cPos.addC(new Coordinate(0, positionIncrement));
                break;
            case Backward:
                nextPos = cPos.addC(new Coordinate(0, -positionIncrement));
                break;
            case Right:
                nextPos = cPos.addC(new Coordinate(positionIncrement,0));
                break;
            case Left:
                nextPos = cPos.addC(new Coordinate(-positionIncrement,0));
                break;
            default:
                nextPos = cPos;
                break;
        }
        path.add(nextPos);
        DrivePath drive = new DrivePath(path, motionProfile);
        runAction(drive);
        cPos = nextPos;
    }
}
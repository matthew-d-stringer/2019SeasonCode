package autos.modes;

import autos.actions.DrivePath;
import coordinates.Coordinate;
import drive.PositionTracker;
import path.TrajectoryList;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class ReverseAuto extends AutoMode{

    TrajectoryList path;
    DrivePath drive;

    public ReverseAuto(){
        path = new TrajectoryList(new Coordinate());
        path.add(new Coordinate(0, 8*Units.Length.feet));

        drive = new DrivePath(path, new TrapezoidalMp.constraints(0, 2*Units.Length.feet, 5*Units.Length.feet));
        drive.setReverse(false);
    }

    @Override
    public void auto() {
        // PositionTracker.getInstance().robotBackward();
        runAction(drive);
    }
}
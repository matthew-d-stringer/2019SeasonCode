package autos.modes;

import autos.actions.DrivePath;
import coordinates.Coordinate;
import path.TrajectoryList;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class TestAutoMode extends AutoMode{

    TrajectoryList path, path2;
    DrivePath drive; 

    public TestAutoMode(){
        path = new TrajectoryList(new Coordinate());
        path.add(new Coordinate(0, 6*Units.Length.feet));
        path.add(new Coordinate(2*Units.Length.feet, 10*Units.Length.feet));
        path.add(new Coordinate(2*Units.Length.feet, 14*Units.Length.feet));
        path.add(new Coordinate(-5*Units.Length.feet, 22*Units.Length.feet));
        path.add(new Coordinate(-6*Units.Length.feet, 27*Units.Length.feet));
        path.add(new Coordinate(-7*Units.Length.feet, 33*Units.Length.feet));
        path.add(new Coordinate(0*Units.Length.feet, 36*Units.Length.feet));

        // path.add(new Coordinate(1*Units.Length.feet, 38*Units.Length.feet));
        // path.add(new Coordinate(0*Units.Length.feet, 40*Units.Length.feet));
        // path.add(new Coordinate(-5*Units.Length.feet, 40*Units.Length.feet));

        // path.add(new Coordinate(-7*Units.Length.feet, 33*Units.Length.feet));
        // path.add(new Coordinate(-6*Units.Length.feet, 27*Units.Length.feet));
        // path.add(new Coordinate(-5*Units.Length.feet, 22*Units.Length.feet));
        // path.add(new Coordinate(2*Units.Length.feet, 14*Units.Length.feet));
        // path.add(new Coordinate(2*Units.Length.feet, 10*Units.Length.feet));
        // path.add(new Coordinate(0, 6*Units.Length.feet));
        // path.add(new Coordinate());

        path2 = new TrajectoryList(new Coordinate());
        path2.add(new Coordinate(0, 6*Units.Length.feet));
        // path2.add(new Coordinate(3*Units.Length.feet, 6*Units.Length.feet));
        // path2.add(new Coordinate(3*Units.Length.feet, 0*Units.Length.feet));
        // path2.add(new Coordinate());

        TrapezoidalMp.constraints constraints = new TrapezoidalMp.constraints(0, 15*Units.Length.feet, 2*Units.Length.feet);
        drive = new DrivePath(path, constraints);
        // drive = new DrivePath(path2, 0.2*Units.Length.feet);
    }

    @Override
    public void auto() {
        runAction(drive);
    }
}
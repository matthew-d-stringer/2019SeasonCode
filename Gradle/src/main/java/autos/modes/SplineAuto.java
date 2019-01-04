package autos.modes;

import autos.actions.DrivePath;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class SplineAuto extends AutoMode{
    DrivePath drive;

    public SplineAuto(){
        super();
        // Pos2D start = new Pos2D(new Coordinate(0,0), new Heading(0,2*Units.Length.feet));
        // Pos2D mid1 = new Pos2D(
        //     new Coordinate(2.5*Units.Length.feet, 2.5*Units.Length.feet), 
        //     Heading.createPolarHeading(45*Units.Angle.degrees, 5*Units.Length.feet));
        // Pos2D mid2 = new Pos2D(
        //     new Coordinate(5*Units.Length.feet, 5*Units.Length.feet), 
        //     Heading.createPolarHeading(Math.PI/2, 10*Units.Length.feet));
        // Pos2D mid3 = new Pos2D(
        //     new Coordinate(2*Units.Length.feet, 20*Units.Length.feet), 
        //     Heading.createPolarHeading(Math.PI/2 + Math.PI/6, 10*Units.Length.feet));
        // Pos2D end = new Pos2D(
        //     new Coordinate(-2*Units.Length.feet, 30*Units.Length.feet), 
        //     Heading.createPolarHeading(Math.PI/2, 10*Units.Length.feet));
        // SplineSegmentFiller filler = new SplineSegmentFiller(Arrays.asList(start, mid2));
        // System.out.println("Generating");
        // TrajectoryList list = filler.generate();
        // System.out.println("Segments: "+list.getCount());
        // drive = new DrivePath(list, 

        drive = DrivePath.createFromFileOnRoboRio("SplineAuto", "forward2",
            new TrapezoidalMp.constraints(0, 5*Units.Length.feet, 3*Units.Length.feet));
        System.out.println("Done filling");
    }

    @Override
    public void auto() {
        runAction(drive);
    }
}
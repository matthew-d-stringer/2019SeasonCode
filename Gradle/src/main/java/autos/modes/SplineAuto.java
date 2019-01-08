package autos.modes;

import java.util.Arrays;

import autos.actions.DrivePath;
import autos.actions.DriveTurn;
import coordinates.Heading;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class SplineAuto extends AutoMode{
    DrivePath drive;
    DriveTurn turn;

    public SplineAuto(){
        super();
        // drive = DrivePath.createFromFileOnRoboRio("SplineAuto", "forward1",
        //     new TrapezoidalMp.constraints(0, 5*Units.Length.feet, 3*Units.Length.feet));
        // System.out.println("Done filling");

        turn = new DriveTurn(new Heading(1, 0), new TrapezoidalMp.constraints(0, 10*Units.Angle.degrees, 10*Units.Angle.degrees));
    }

    @Override
    public void auto() {
        // runAction(drive);
        runAction(turn);
    }
}
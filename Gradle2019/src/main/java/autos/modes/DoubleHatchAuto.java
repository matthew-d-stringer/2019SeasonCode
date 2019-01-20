package autos.modes;

import autos.actions.DrivePath;
import coordinates.Coordinate;
import drive.PositionTracker;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class DoubleHatchAuto extends AutoMode{
    DrivePath toRocket, toRefill, loadToRocket;
    public DoubleHatchAuto(){
        setInitPos(9.56, 5.64);
        TrapezoidalMp.constraints constraints = 
            new TrapezoidalMp.constraints(0, 5*Units.Length.feet, 5*Units.Length.feet);

        toRocket = DrivePath.createFromFileOnRoboRio("DoubleHatchAuto2", "toRocket", constraints);
        toRocket.setVerticalThresh(1*Units.Length.inches);

        toRefill = DrivePath.createFromFileOnRoboRio("DoubleHatchAuto2", "toRefill", constraints);
        toRefill.setReverse(true);
        toRefill.setVerticalThresh(1*Units.Length.inches);

        loadToRocket = DrivePath.createFromFileOnRoboRio("DoubleHatchAuto2", "loadToRocket", constraints);
        loadToRocket.setVerticalThresh(3*Units.Length.inches);
    }

    @Override
    public void auto() {
        runAction(toRocket);
        // runAction(toRefill);
        // runAction(loadToRocket);
    }
}
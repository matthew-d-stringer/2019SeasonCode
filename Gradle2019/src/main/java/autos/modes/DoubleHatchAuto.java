package autos.modes;

import autos.actions.DrivePath;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class DoubleHatchAuto extends AutoMode{
    DrivePath toRocket, toRefill, loadToRocket;
    public DoubleHatchAuto(){
        TrapezoidalMp.constraints constraints = 
            new TrapezoidalMp.constraints(0, 10*Units.Length.feet, 5*Units.Length.feet);

        toRocket = DrivePath.createFromFileOnRoboRio("DoubleHatchAuto", "toRocket", constraints);
        toRocket.setThresh(3*Units.Length.inches);

        toRefill = DrivePath.createFromFileOnRoboRio("DoubleHatchAuto", "toRefill", constraints);
        toRefill.setReverse(true);
        toRefill.setThresh(3*Units.Length.inches);

        loadToRocket = DrivePath.createFromFileOnRoboRio("DoubleHatchAuto", "loadToRocket", constraints);
        loadToRocket.setThresh(3*Units.Length.inches);
    }

    @Override
    public void auto() {
        runAction(toRocket);
        runAction(toRefill);
        runAction(loadToRocket);
    }
}
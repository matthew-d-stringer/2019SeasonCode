package autos.modes;

import autos.actions.DrivePath;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class TripleHatchAuto extends AutoMode{
    DrivePath toRocket, toRefill, loadToRocket;
    public TripleHatchAuto(){
        TrapezoidalMp.constraints constraints = 
            new TrapezoidalMp.constraints(0, 10*Units.Length.feet, 5*Units.Length.feet);
        TrapezoidalMp.constraints slow = 
            new TrapezoidalMp.constraints(0, 6*Units.Length.feet, 2*Units.Length.feet);

        toRocket = DrivePath.createFromFileOnRoboRio("TripleHatchAuto", "toRocket", constraints);
        toRocket.setVerticalThresh(5*Units.Length.inches);

        toRefill = DrivePath.createFromFileOnRoboRio("TripleHatchAuto", "toRefill", constraints);
        toRefill.setReverse(true);
        toRefill.setVerticalThresh(5*Units.Length.inches);

        loadToRocket = DrivePath.createFromFileOnRoboRio("TripleHatchAuto", "loadToRocket", constraints);
        loadToRocket.setVerticalThresh(5*Units.Length.inches);
    }

    @Override
    public void auto() {
        runAction(toRocket);
        runAction(toRefill);
        runAction(loadToRocket);
        runAction(toRefill);
        runAction(loadToRocket);
    }
}
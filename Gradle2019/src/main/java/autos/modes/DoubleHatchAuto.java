package autos.modes;

import autos.AutoEndedException;
import autos.actions.DrivePath;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class DoubleHatchAuto extends AutoMode{
    DrivePath toRocket, toRefill, loadToRocket;
    DrivePath backToStation;
    public DoubleHatchAuto(){
        setInitPos(9.56, 5.64);
        TrapezoidalMp.constraints constraints = 
            new TrapezoidalMp.constraints(0, 16*Units.Length.feet, 10*Units.Length.feet);
        TrapezoidalMp.constraints slow = 
            new TrapezoidalMp.constraints(0, 5*Units.Length.feet, 3*Units.Length.feet);

        toRocket = DrivePath.createFromFileOnRoboRio("Left/DoubleHatchAuto", "toRocket", slow);
        toRocket.setVerticalThresh(0.5*Units.Length.inches);
        // toRocket.setlookAhead(2*Units.Length.feet);

        toRefill = DrivePath.createFromFileOnRoboRio("Left/DoubleHatchAuto", "toRefill", slow);
        toRefill.setReverse(true);
        toRefill.setVerticalThresh(1*Units.Length.inches);

        loadToRocket = DrivePath.createFromFileOnRoboRio("Left/DoubleHatchAuto", "loadToRocket", slow);
        loadToRocket.setVerticalThresh(0.5*Units.Length.inches);

        backToStation = DrivePath.createFromFileOnRoboRio("Left/DoubleHatchAuto", "backToStation", slow);
        backToStation.setVerticalThresh(1*Units.Length.inches);
        backToStation.setReverse(true);
    }

    @Override
    public void auto() throws AutoEndedException{
        runAction(toRocket);
        runAction(toRefill);
        runAction(loadToRocket);
        // runAction(backToStation);
    }
}
package autos.modes;

import autos.actions.DrivePath;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class FarNearLeftHatchAuto extends AutoMode{
    DrivePath toFarRocket, backToStation, toNear;
    public FarNearLeftHatchAuto(){
        TrapezoidalMp.constraints constraints = 
            new TrapezoidalMp.constraints(0, 16.5*Units.Length.feet, 7*Units.Length.feet);
        TrapezoidalMp.constraints slow = 
            new TrapezoidalMp.constraints(0, 4*Units.Length.feet, 2*Units.Length.feet);
        
        toFarRocket = DrivePath.createFromFileOnRoboRio("Left/FarHatchAuto", "toFarRocket", constraints);
        toFarRocket.setVerticalThresh(0.25*Units.Length.inches);
        toFarRocket.setlookAhead(2.5*Units.Length.feet);
        toFarRocket.setTurnCorrection(0.6);

        backToStation = DrivePath.createFromFileOnRoboRio("Left/FarHatchAuto", "backToStation", constraints);
        backToStation.setVerticalThresh(0.5*Units.Length.inches);
        backToStation.setReverse(true);

        toNear = DrivePath.createFromFileOnRoboRio("Left/FarHatchAuto", "toNear", constraints);
        toNear.setVerticalThresh(0.25*Units.Length.inches);
        
        setInitPos(9.56, 5.64);
    }

    @Override
    public void auto() {
        runAction(toFarRocket);
        runAction(backToStation);
        runAction(toNear);
    }
}
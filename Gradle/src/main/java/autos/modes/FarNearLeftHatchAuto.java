package autos.modes;

import autos.actions.DrivePath;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class FarNearLeftHatchAuto extends AutoMode{
    DrivePath toFarRocket;
    public FarNearLeftHatchAuto(){
        TrapezoidalMp.constraints constraints = 
            new TrapezoidalMp.constraints(0, 4*Units.Length.feet, 2*Units.Length.feet);
        TrapezoidalMp.constraints slow = 
            new TrapezoidalMp.constraints(0, 6*Units.Length.feet, 2*Units.Length.feet);
        
        toFarRocket = DrivePath.createFromFileOnRoboRio("FarNearLeftHatchAuto", "toFarRocket", constraints);
    }

    @Override
    public void auto() {
        runAction(toFarRocket);
    }
}
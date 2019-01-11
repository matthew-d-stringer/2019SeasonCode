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
            new TrapezoidalMp.constraints(0, 4*Units.Length.feet, 2*Units.Length.feet);
        
        toFarRocket = DrivePath.createFromFileOnRoboRio("FarNearLeftHatchAuto", "toFarRocket", slow);
        toFarRocket.setThresh(4*Units.Length.inches);
        toFarRocket.setlookAhead(1.5*Units.Length.feet);
    }

    @Override
    public void auto() {
        runAction(toFarRocket);
    }
}
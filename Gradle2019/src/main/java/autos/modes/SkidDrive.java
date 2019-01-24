package autos.modes;

import autos.AutoEndedException;
import autos.actions.DrivePath;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class SkidDrive extends AutoMode{
    DrivePath toRocket;
    public SkidDrive(){
        TrapezoidalMp.constraints constraints = new TrapezoidalMp.constraints(0, 
            20*Units.Length.feet, 25*Units.Length.feet);
        toRocket = DrivePath.createFromFileOnRoboRio("Left/FarHatchAuto", "toFarRocket", constraints, 20);
        toRocket.setTurnCorrection(1.2);
        toRocket.setVerticalThresh(0.5*Units.Length.inches);
        
        setInitPos(9.56, 5.64);
    }

    @Override
    public void auto() throws AutoEndedException {
        runAction(toRocket);
    }
}
package autos.modes;

import autos.AutoEndedException;
import autos.actions.DrivePath;
import autos.actions.PointTurn;
import autos.actions.VisionPursuit;
import coordinates.Heading;
import drive.PositionTracker;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class DoubleRocketAuto extends AutoMode{

    DrivePath toRocket;
    VisionPursuit finishRocket;
    PointTurn turnAround;

    public static String getSelecterName(){
        return "DoubleRocketAuto";
    }

    public DoubleRocketAuto(){
        String path = "Left/DoubleRocketAuto";

        TrapezoidalMp.constraints constraints = new TrapezoidalMp.constraints(0, 5*Units.Length.feet, 2*Units.Length.feet);

        toRocket = DrivePath.createFromFileOnRoboRio(path, "toRocket", constraints);
        finishRocket = new VisionPursuit();
        turnAround = new PointTurn(new Heading(0, -1));

        setInitPos(9.56, 5.64);
    }

    @Override
    public void auto() throws AutoEndedException {
        PositionTracker.getInstance().robotForward();
        runAction(toRocket);
        runAction(finishRocket);
        runAction(turnAround);
    }
}
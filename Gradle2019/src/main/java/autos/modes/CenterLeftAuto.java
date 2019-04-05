package autos.modes;

import autos.AutoEndedException;
import autos.actions.ArmToLevel;
import autos.actions.DrivePath;
import autos.actions.VisionPursuit;
import autos.actions.ArmToLevel.GripperMode;
import autos.actions.ArmToLevel.Levels;
import drive.PositionTracker;
import subsystems.Gripper;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class CenterLeftAuto extends AutoMode{

    DrivePath toGoal, reverse;
    VisionPursuit place;

    ArmToLevel out;

    public static String getSelectedString(){
        return "CenterLeftAuto";
    }
    
    public CenterLeftAuto(){
        TrapezoidalMp.constraints slow = new TrapezoidalMp.constraints(0, 5*Units.Length.feet, 2*Units.Length.feet);
        toGoal = DrivePath.createFromFileOnRoboRio("CenterLeftAuto", "toGoal", slow, 10);
        reverse = DrivePath.createFromFileOnRoboRio("CenterLeftAuto", "reverse", slow, 10);
        reverse.setReverse(true);

        out = new ArmToLevel(Levels.low, false, GripperMode.hatch);

        place = new VisionPursuit();
        place.setFinishThresh(0.4*Units.Length.feet);
        place.setDeccelDist(1.1*Units.Length.feet);
        setInitPos(13.475, 5.64);
    }

    @Override
    public void auto() throws AutoEndedException {
        PositionTracker.getInstance().robotForward();
        Gripper.getInstance().hatchHold();
        runAction(out);
        runAction(toGoal);
        runAction(place);
        Gripper.getInstance().hatchRelease();
        runAction(reverse);
        Gripper.getInstance().rollerOff();
    }
}
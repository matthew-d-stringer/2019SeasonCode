package autos.modes;

import java.util.Arrays;

import autos.AutoEndedException;
import autos.actions.ArmToLevel;
import autos.actions.DrivePath;
import autos.actions.ParallelAction;
import autos.actions.SeriesAction;
import autos.actions.WaitAction;
import autos.actions.WaitUntilY;
import autos.actions.ArmToLevel.GripperMode;
import autos.actions.ArmToLevel.Levels;
import drive.PositionTracker;
import robot.Constants;
import subsystems.ArmSystemControl;
import subsystems.Gripper;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class DoubleHatchAuto extends AutoMode{
    DrivePath toRocket, toRefill, loadToRocket;
    DrivePath backToStation;

    WaitUntilY waitFirstGoal;
    ArmToLevel high, mid, load;

    ParallelAction placeFirstHatch;
    public DoubleHatchAuto(){
        setInitPos(9.56, 5.64);
        PositionTracker.getInstance().robotBackward();
        TrapezoidalMp.constraints constraints = 
            new TrapezoidalMp.constraints(0, 14*Units.Length.feet, 8*Units.Length.feet);
        TrapezoidalMp.constraints reverseSpeed = 
            new TrapezoidalMp.constraints(0, 6*Units.Length.feet, 4*Units.Length.feet);
        TrapezoidalMp.constraints slow = 
            new TrapezoidalMp.constraints(0, 5*Units.Length.feet, 3*Units.Length.feet);

        toRocket = DrivePath.createFromFileOnRoboRio("Left/DoubleHatchAuto", "toRocket", slow);
        toRocket.setVerticalThresh(0.5*Units.Length.inches);
        toRocket.setTurnCorrection(0.15);
        toRocket.setReverse(true);
        // toRocket.setlookAhead(2*Units.Length.feet);

        toRefill = DrivePath.createFromFileOnRoboRio("Left/DoubleHatchAuto", "toRefill", reverseSpeed);
        // toRefill.setReverse(true);
        toRefill.setVerticalThresh(1*Units.Length.inches);
        toRefill.setTurnCorrection(0.15);

        loadToRocket = DrivePath.createFromFileOnRoboRio("Left/DoubleHatchAuto", "loadToRocket", constraints);
        loadToRocket.setReverse(true);
        loadToRocket.setVerticalThresh(0.5*Units.Length.inches);
        loadToRocket.setTurnCorrection(0.15);

        backToStation = DrivePath.createFromFileOnRoboRio("Left/DoubleHatchAuto", "backToStation", slow);
        backToStation.setVerticalThresh(1*Units.Length.inches);
        backToStation.setReverse(true);
        backToStation.setTurnCorrection(0.15);

        high = new ArmToLevel(Levels.high, true, GripperMode.hatch);
        high.setArmPercent(0.735);
        waitFirstGoal = new WaitUntilY(6*Units.Length.feet);
        mid = new ArmToLevel(Levels.middle, true, GripperMode.hatch);
        load = new ArmToLevel(Levels.loading, false, GripperMode.hatch);
        load.setArmPercent(0.75);

        placeFirstHatch = new ParallelAction(Arrays.asList(toRocket,
            new SeriesAction(Arrays.asList(waitFirstGoal, high))));
    }

    @Override
    public void auto() throws AutoEndedException{
        PositionTracker.getInstance().robotBackward();
        ArmSystemControl.getInstance().setSetpoints(-Math.PI/2, Constants.Telescope.lenRetract);;
        // runAction(placeFirstHatch);
        // Gripper.getInstance().hatchRelease();
        // runAction(new WaitAction(5));
        runAction(toRocket);
        // runAction(new ParallelAction(Arrays.asList(toRefill, load)));
        // Gripper.getInstance().hatchLock();
        // runAction(load);
        // runAction(mid);
        // runAction(loadToRocket);
        // runAction(new ArmToLevel(Levels.loading, false, GripperMode.hatch));

        // runAction(toRefill);
        // runAction(loadToRocket);

        // runAction(backToStation);
    }
}
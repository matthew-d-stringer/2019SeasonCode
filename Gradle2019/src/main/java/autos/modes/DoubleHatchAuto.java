package autos.modes;

import java.util.Arrays;

import autos.AutoEndedException;
import autos.actions.ArmToLevel;
import autos.actions.DrivePath;
import autos.actions.HatchLock;
import autos.actions.HatchRelease;
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
    public static String getLeftName(){
        return "DoubleHatchLeft";
    }
    public static String getRightName(){
        return "DoubleHatchRight";
    }


    DrivePath toRocket, toRefill, loadToRocket;
    DrivePath backToStation;

    WaitUntilY waitFirstGoal;
    ArmToLevel high, mid, load;

    ParallelAction placeFirstHatch, placeSecondHatch;
    SeriesAction spamHatch;
    public DoubleHatchAuto(boolean right){
        if(!right)
            setInitPos(9.56, 5.64);
        else
            setInitPos(17.40, 5.64); 
        PositionTracker.getInstance().robotBackward();
        TrapezoidalMp.constraints constraints = 
            new TrapezoidalMp.constraints(0, 14*Units.Length.feet, 8*Units.Length.feet);
        TrapezoidalMp.constraints reverseSpeed = 
            new TrapezoidalMp.constraints(0, 8*Units.Length.feet, 6*Units.Length.feet);
        TrapezoidalMp.constraints slow = 
            new TrapezoidalMp.constraints(0, 8*Units.Length.feet, 4*Units.Length.feet);
        
        String path;
        if(right){
            path = "Right/DoubleHatchAuto";
        }else{
            path = "Left/DoubleHatchAuto";
        }

        toRocket = DrivePath.createFromFileOnRoboRio(path, "toRocket", slow, 40);
        toRocket.setVerticalThresh(0.5*Units.Length.inches);
        toRocket.setTurnCorrection(0.10);
        toRocket.setReverse(true);
        // toRocket.setlookAhead(2.5*Units.Length.feet);

        toRefill = DrivePath.createFromFileOnRoboRio(path, "toRefill", reverseSpeed);
        // toRefill.setReverse(true);
        toRefill.setVerticalThresh(0.5*Units.Length.inches);
        toRefill.setTurnCorrection(0.10);
        toRefill.setlookAhead(3*Units.Length.feet);

        loadToRocket = DrivePath.createFromFileOnRoboRio(path, "loadToRocket", slow);
        loadToRocket.setReverse(true);
        loadToRocket.setVerticalThresh(0.5*Units.Length.inches);
        loadToRocket.setTurnCorrection(0.15);

        backToStation = DrivePath.createFromFileOnRoboRio(path, "backToStation", slow);
        backToStation.setVerticalThresh(1*Units.Length.inches);
        backToStation.setReverse(true);
        backToStation.setTurnCorrection(0.15);

        high = new ArmToLevel(Levels.high, true, GripperMode.hatch);
        high.setArmPercent(0.95);
        high.useTelescope(false);
        waitFirstGoal = new WaitUntilY(6*Units.Length.feet);
        mid = new ArmToLevel(Levels.middle, true, GripperMode.hatch);
        mid.setArmPercent(0.5);
        mid.useTelescope(false);
        load = new ArmToLevel(Levels.loading, false, GripperMode.hatch);
        load.setArmPercent(0.3);

        placeFirstHatch = new ParallelAction(Arrays.asList(toRocket,
            new SeriesAction(Arrays.asList(waitFirstGoal, high, new WaitAction(0.375)))));
        placeSecondHatch = new ParallelAction(Arrays.asList(loadToRocket,
            new SeriesAction(Arrays.asList(waitFirstGoal, mid, new WaitAction(0.375)))));

        HatchRelease hr = new HatchRelease();
        HatchLock hL = new HatchLock();
        WaitAction simpWait = new WaitAction(0.5);
        spamHatch = new SeriesAction(Arrays.asList(hr, simpWait, hL));
    }

    @Override
    public void auto() throws AutoEndedException{
        PositionTracker.getInstance().robotBackward();
        ArmSystemControl.getInstance().setSetpoints(-Math.PI/2, Constants.Telescope.lenRetract);;
        // runAction(toRocket);
        runAction(placeFirstHatch);
        // Gripper.getInstance().hatchRelease();
        runAction(new HatchRelease());
        runAction(new WaitAction(0.25));
        Gripper.getInstance().hatchLock();
        runAction(new ParallelAction(Arrays.asList(toRefill, load, spamHatch)));
        Gripper.getInstance().hatchLock();
        runAction(placeSecondHatch);
        Gripper.getInstance().hatchRelease();
        runAction(new WaitAction(0.5));
        Gripper.getInstance().hatchLock();

        // runAction(load);
        // runAction(mid);
        // runAction(loadToRocket);
        // runAction(new ArmToLevel(Levels.loading, false, GripperMode.hatch));

        // runAction(toRefill);
        // runAction(loadToRocket);

        // runAction(backToStation);
    }
}
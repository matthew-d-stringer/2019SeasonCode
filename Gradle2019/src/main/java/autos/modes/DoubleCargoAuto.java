package autos.modes;

import autos.AutoEndedException;
import autos.actions.ArmToLevel;
import autos.actions.DrivePath;
import autos.actions.VisionPursuit;
import autos.actions.ArmToLevel.GripperMode;
import autos.actions.ArmToLevel.Levels;
import drive.PositionTracker;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class DoubleCargoAuto extends AutoMode{

    DrivePath startTo1stStop, 
        stopToCloseGoal,
        closeGoalTo2ndStop,
        stopToRefill,
        refillTo3rdStop,
        stopToMidGoal;
    VisionPursuit finishRefill;
    ArmToLevel reset;

    public DoubleCargoAuto(){
        setInitPos(9.56, 5.64);

        TrapezoidalMp.constraints slow = new TrapezoidalMp.constraints(0, 5*Units.Length.feet, 2*Units.Length.feet);
        TrapezoidalMp.constraints place = new TrapezoidalMp.constraints(0, 7*Units.Length.feet, 7*Units.Length.feet);
        TrapezoidalMp.constraints constraints = new TrapezoidalMp.constraints(0, 15*Units.Length.feet, 9*Units.Length.feet);
        TrapezoidalMp.constraints toRefill = new TrapezoidalMp.constraints(0, 15*Units.Length.feet, 5*Units.Length.feet);
        TrapezoidalMp.constraints gloryRun = new TrapezoidalMp.constraints(0, 18*Units.Length.feet, 15*Units.Length.feet);

        String path = "Left/DoubleCargoAuto";
        startTo1stStop = DrivePath.createFromFileOnRoboRio(path, "startTo1stStop", constraints);
        startTo1stStop.setVerticalThresh(0.5*Units.Length.inches);
        startTo1stStop.setTurnCorrection(0.20);
        startTo1stStop.setReverse(true);

        stopToCloseGoal = DrivePath.createFromFileOnRoboRio(path, "1stStopToCloseGoal", place, 1);
        stopToCloseGoal.setVerticalThresh(0.5*Units.Length.inches);
        stopToCloseGoal.setlookAhead(2.25*Units.Length.feet);
        stopToCloseGoal.setTurnCorrection(0.90);// was 0.4

        closeGoalTo2ndStop = DrivePath.createFromFileOnRoboRio(path, "closeGoalTo2ndStop", constraints);
        closeGoalTo2ndStop.setVerticalThresh(0.5*Units.Length.inches);
        closeGoalTo2ndStop.setTurnCorrection(0.9);
        closeGoalTo2ndStop.setlookAhead(2*Units.Length.feet);
        closeGoalTo2ndStop.setReverse(true);

        stopToRefill = DrivePath.createFromFileOnRoboRio(path, "2ndStopToRefill", toRefill);
        stopToRefill.setVerticalThresh(3*Units.Length.inches);
        stopToRefill.setTurnCorrection(0.30);

        finishRefill = new VisionPursuit();

        refillTo3rdStop = DrivePath.createFromFileOnRoboRio(path, "refillTo3rdStop", constraints);
        refillTo3rdStop.setVerticalThresh(15*Units.Length.inches);
        refillTo3rdStop.setTurnCorrection(0.7);
        refillTo3rdStop.setlookAhead(4*Units.Length.feet);
        refillTo3rdStop.setReverse(true);

        stopToMidGoal = DrivePath.createFromFileOnRoboRio(path, "3rdStopToMidGoal", place, 1);
        stopToMidGoal.setVerticalThresh(0.5*Units.Length.inches);
        stopToMidGoal.setTurnCorrection(0.20);
        stopToMidGoal.setlookAhead(2*Units.Length.feet);

        reset = new ArmToLevel(Levels.reset, false, GripperMode.hatch);
    }

    @Override
    public void auto() throws AutoEndedException {
        PositionTracker.getInstance().robotBackward();
        runAction(reset);
        runAction(startTo1stStop);
        runAction(stopToCloseGoal);
        runAction(closeGoalTo2ndStop);
        runAction(stopToRefill);
        runAction(finishRefill);
        runAction(refillTo3rdStop);
    }
}
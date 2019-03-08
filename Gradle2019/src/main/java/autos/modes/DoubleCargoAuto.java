package autos.modes;

import autos.AutoEndedException;
import autos.actions.ArmToLevel;
import autos.actions.DrivePath;
import autos.actions.ArmToLevel.GripperMode;
import autos.actions.ArmToLevel.Levels;
import drive.PositionTracker;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class DoubleCargoAuto extends AutoMode{

    DrivePath startTo1stStop, 
        stopToCloseGoal,
        closeGoalTo2ndStop,
        stopToRefill;
    ArmToLevel reset;

    public DoubleCargoAuto(){
        setInitPos(9.56, 5.64);

        TrapezoidalMp.constraints slow = new TrapezoidalMp.constraints(0, 5*Units.Length.feet, 2*Units.Length.feet);
        TrapezoidalMp.constraints place = new TrapezoidalMp.constraints(0, 5*Units.Length.feet, 3*Units.Length.feet);
        TrapezoidalMp.constraints constraints = new TrapezoidalMp.constraints(0, 10*Units.Length.feet, 8*Units.Length.feet);

        String path = "Left/DoubleCargoAuto";
        startTo1stStop = DrivePath.createFromFileOnRoboRio(path, "startTo1stStop", constraints);
        startTo1stStop.setVerticalThresh(0.5*Units.Length.inches);
        startTo1stStop.setTurnCorrection(0.30);
        startTo1stStop.setReverse(true);

        stopToCloseGoal = DrivePath.createFromFileOnRoboRio(path, "1stStopToCloseGoal", place, 1);
        stopToCloseGoal.setVerticalThresh(0.5*Units.Length.inches);
        stopToCloseGoal.setlookAhead(2*Units.Length.feet);
        stopToCloseGoal.setTurnCorrection(0.30);

        closeGoalTo2ndStop = DrivePath.createFromFileOnRoboRio(path, "closeGoalTo2ndStop", place);
        closeGoalTo2ndStop.setVerticalThresh(0.5*Units.Length.inches);
        closeGoalTo2ndStop.setTurnCorrection(0.30);
        closeGoalTo2ndStop.setlookAhead(2*Units.Length.feet);
        closeGoalTo2ndStop.setReverse(true);

        stopToRefill = DrivePath.createFromFileOnRoboRio(path, "2ndStopToRefill", constraints);
        stopToRefill.setVerticalThresh(0.5*Units.Length.inches);
        stopToRefill.setTurnCorrection(0.30);

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
    }
}
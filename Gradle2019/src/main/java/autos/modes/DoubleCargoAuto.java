package autos.modes;

import autos.AutoEndedException;
import autos.actions.ArmToLevel;
import autos.actions.DrivePath;
import autos.actions.ParallelAction;
import autos.actions.PointTurn;
import autos.actions.SeriesAction;
import autos.actions.VisionPursuit;
import autos.actions.WaitAction;
import autos.actions.WaitUntilX;
import autos.actions.ArmToLevel.GripperMode;
import autos.actions.ArmToLevel.Levels;
import coordinates.Heading;
import drive.PositionTracker;
import robot.Constants;
import subsystems.ArmSystemControl;
import subsystems.Gripper;
import subsystems.GripperControl;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class DoubleCargoAuto extends AutoMode{

    DrivePath startTo1stStop, 
        stopToCloseGoal,
        closeGoalTo2ndStop,
        stopToRefill,
        refillTo3rdStop,
        stopToMidGoal,
        midGoalTo4thStop;
    DrivePath reverseFromRefill;
    VisionPursuit placeCloseGoal, finishRefill, placeMidGoal;
    PointTurn turnFromGoal;
    ArmToLevel reset, low, loading;

    public DoubleCargoAuto(){
        setInitPos(9.56, 5.64);

        TrapezoidalMp.constraints slow = new TrapezoidalMp.constraints(0, 5*Units.Length.feet, 2*Units.Length.feet);
        TrapezoidalMp.constraints place = new TrapezoidalMp.constraints(0, 6*Units.Length.feet, 4*Units.Length.feet);
        TrapezoidalMp.constraints medium = new TrapezoidalMp.constraints(0, 10*Units.Length.feet, 8*Units.Length.feet);
        TrapezoidalMp.constraints constraints = new TrapezoidalMp.constraints(0, 15*Units.Length.feet, 9*Units.Length.feet);
        TrapezoidalMp.constraints toRefill = new TrapezoidalMp.constraints(0, 15*Units.Length.feet, 8*Units.Length.feet);
        TrapezoidalMp.constraints fast = new TrapezoidalMp.constraints(0, 15*Units.Length.feet, 12*Units.Length.feet);
        TrapezoidalMp.constraints omegaFast = new TrapezoidalMp.constraints(0, 20*Units.Length.feet, 16*Units.Length.feet);

        String path = "Left/DoubleCargoAuto";
        startTo1stStop = DrivePath.createFromFileOnRoboRio(path, "startTo1stStop", medium);
        startTo1stStop.setVerticalThresh(4*Units.Length.inches);
        startTo1stStop.setTurnCorrection(0.15);
        startTo1stStop.setlookAhead(3.75*Units.Length.feet);
        startTo1stStop.setReverse(true);

        placeCloseGoal = new VisionPursuit();
        placeCloseGoal.disableAfterTime(2.5);
        placeCloseGoal.setFinishThresh(0.3*Units.Length.feet);
        placeCloseGoal.setDeccelDist(1.1*Units.Length.feet);


        stopToCloseGoal = DrivePath.createFromFileOnRoboRio(path, "1stStopToCloseGoal", medium, 1);
        stopToCloseGoal.setVerticalThresh(0.5*Units.Length.inches);
        stopToCloseGoal.setlookAhead(2.25*Units.Length.feet);
        stopToCloseGoal.setTurnCorrection(0.90);// was 0.4

        closeGoalTo2ndStop = DrivePath.createFromFileOnRoboRio(path, "closeGoalTo2ndStop", medium);
        closeGoalTo2ndStop.setVerticalThresh(3*Units.Length.inches);
        closeGoalTo2ndStop.setTurnCorrection(0.4);
        closeGoalTo2ndStop.setlookAhead(2*Units.Length.feet);
        closeGoalTo2ndStop.setReverse(true);

        turnFromGoal = new PointTurn(new Heading(270*Units.Angle.degrees));

        stopToRefill = DrivePath.createFromFileOnRoboRio(path, "2ndStopToRefill", slow);
        stopToRefill.setVerticalThresh(4*Units.Length.inches);
        stopToRefill.setlookAhead(5*Units.Length.feet);
        stopToRefill.setTurnCorrection(0.15);

        // finishRefill = new VisionPursuit(2.65*Units.Length.feet);
        finishRefill = new VisionPursuit();
        finishRefill.setDeccelDist(0.8*Units.Length.feet);
        finishRefill.setFinishThresh(0.4*Units.Length.feet);

        refillTo3rdStop = DrivePath.createFromFileOnRoboRio(path, "refillTo3rdStop", constraints);
        refillTo3rdStop.setVerticalThresh(2*Units.Length.inches);
        refillTo3rdStop.setTurnCorrection(0.15);
        // refillTo3rdStop.setlookAhead(3.75*Units.Length.feet);
        refillTo3rdStop.setlookAhead(5*Units.Length.feet);
        refillTo3rdStop.setReverse(true);

        stopToMidGoal = DrivePath.createFromFileOnRoboRio(path, "3rdStopToMidGoal", place, 1);
        stopToMidGoal.setVerticalThresh(0.5*Units.Length.inches);
        stopToMidGoal.setTurnCorrection(0.20);
        stopToMidGoal.setlookAhead(2*Units.Length.feet);

        placeMidGoal = new VisionPursuit();

        midGoalTo4thStop = DrivePath.createFromFileOnRoboRio(path, "midGoalTo4thStop", constraints);
        midGoalTo4thStop.setVerticalThresh(0.5*Units.Length.inches);
        midGoalTo4thStop.setTurnCorrection(0.20);
        midGoalTo4thStop.setReverse(true);

        reverseFromRefill = DrivePath.createFromFileOnRoboRio(path, "reverseFromRefill", constraints);
        reverseFromRefill.setVerticalThresh(0.5*Units.Length.inches);
        reverseFromRefill.setTurnCorrection(0.20);
        reverseFromRefill.setReverse(true);

        reset = new ArmToLevel(Levels.reset, false, GripperMode.hatch);
        low = new ArmToLevel(Levels.low, false, GripperMode.hatch);
        loading = new ArmToLevel(Levels.loading, false, GripperMode.hatch);
    }

    @Override
    public void auto() throws AutoEndedException {
        PositionTracker.getInstance().robotBackward();
        Gripper.getInstance().hatchHold();
        ArmSystemControl.getInstance().setGripperSetpoint(0*Units.Angle.degrees);
        runAction(reset);
        runAction(new ParallelAction(startTo1stStop, new SeriesAction(new WaitUntilX(9.1*Units.Length.feet, true), low)));
        // runAction(stopToCloseGoal);

        runAction(placeCloseGoal);
        Gripper.getInstance().hatchRelease();
        // runAction(new WaitAction(0.4));
        runAction(closeGoalTo2ndStop);
        Gripper.getInstance().rollerOff();
        runAction(turnFromGoal);
        runAction(new ParallelAction(stopToRefill, loading));
        // // runAction(low);
        Gripper.getInstance().hatchGrab();
        runAction(finishRefill);
        Gripper.getInstance().hatchHold();
        runAction(reverseFromRefill);

        // runAction(new ParallelAction(refillTo3rdStop, low));
        // runAction(placeMidGoal);
        // Gripper.getInstance().hatchRelease();
        // // runAction(new WaitAction(0.7));
        // // runAction(midGoalTo4thStop);
        // Gripper.getInstance().rollerOff();
    }
}
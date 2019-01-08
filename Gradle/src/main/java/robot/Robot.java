package robot;

import autos.modes.AutoMode;
import autos.modes.ReverseAuto;
import autos.modes.SplineAuto;
import controlBoard.*;
import coordinates.Coordinate;
import edu.wpi.first.wpilibj.IterativeRobot;
import drive.Drive;
import drive.DriveOutput;
import drive.PositionTracker;
import drive.DriveOutput.Modes;

public class Robot extends IterativeRobot {
    private static IControlBoard cb = new ControlBoard();
    public static IControlBoard getControlBoard(){
        return cb;
    }

    Drive drive;
    DriveOutput driveOut;
    IControlBoard controlBoard;
    PositionTracker mRunner;
    AutoMode mode;

    @Override
    public void robotInit() {
        controlBoard = Robot.getControlBoard();
        drive = Drive.getInstance();
        driveOut = DriveOutput.getInstance();
        mRunner = PositionTracker.getInstance();
        driveOut.start();
    }

    @Override
    public void robotPeriodic(){
        // SmartDashboard.putNumber("Right Vel SI", 0);
        // SmartDashboard.putNumber("Left Vel SI", 0);
        driveOut.display();
        drive.display();
        mRunner.display();
    }

    @Override
    public void teleopInit() {
        driveOut.set(Modes.Voltage, 0,0);
    }

    @Override
    public void teleopPeriodic() {
        driveCode();
    }

    private void driveCode(){
        Coordinate control = controlBoard.getJoystickPos();
        control.mult(12);
        double rightOut = control.getY()+control.getX();
        double leftOut = control.getY()-control.getX();
        driveOut.set(Modes.Voltage, rightOut, leftOut);
    }

    @Override
    public void autonomousInit() {
        PositionTracker.getInstance().setInitPos(new Coordinate());
        PositionTracker.getInstance().robotForward();
        // mode = new TestAutoMode();
        mode = new SplineAuto();
        mode.start();
    }

    @Override
    public void autonomousPeriodic() {
        // driveOut.setKin(350*Units.Angle.degrees/(5*Units.Length.feet), 1*Units.Length.feet);
        // driveOut.setKin(1/(4*Units.Length.feet), 2*Units.Length.feet);
        // driveOut.set(Modes.Voltage, 12, 12);
    }

    @Override
    public void disabledInit() {
    }
}
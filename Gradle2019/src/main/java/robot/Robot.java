package robot;

import autos.modes.*;
import controlBoard.*;
import coordinates.Coordinate;
import utilPackage.Units;
import drive.Drive;
import drive.DriveOutput;
import drive.PositionTracker;
import drive.DriveOutput.Modes;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotState;
import subsystems.MainArm;

public class Robot extends IterativeRobot {
    private static IControlBoard cb = new ControlBoard();
    public static IControlBoard getControlBoard(){
        return cb;
    }

    Drive drive;
    DriveOutput driveOut;
    MainArm arm;
    IControlBoard controlBoard;
    PositionTracker mRunner;
    AutoMode mode;

    @Override
    public void robotInit() {
        try{
            Constants.readRobotData();
        }catch(Exception e){
            e.printStackTrace();
        }
        controlBoard = Robot.getControlBoard();
        drive = Drive.getInstance();
        driveOut = DriveOutput.getInstance();
        mRunner = PositionTracker.getInstance();
        arm = MainArm.getInstance();
        driveOut.start();
        // mode = new DoubleHatchAuto();
        // mode = new FarNearLeftHatchAuto();
        mode = new SkidDrive();
    }

    @Override
    public void robotPeriodic(){
        // SmartDashboard.putNumber("Right Vel SI", 0);
        // SmartDashboard.putNumber("Left Vel SI", 0);
        driveOut.display();
        drive.display();
        mRunner.display();
        arm.periodic();
    }

    @Override
    public void teleopInit() {
        driveOut.set(Modes.Voltage, 0,0);
    }

    @Override
    public void teleopPeriodic() {
        double vel = 2.5*Units.Length.feet;
        driveOut.set(Modes.Velocity, vel, vel);
        // driveCode();
        // driveOut.set(Modes.Voltage, 3,3);
        drive.display();
        // driveOut.set(Modes.Voltage, 3, 3);
    }

    private void driveCode(){
        Coordinate control = controlBoard.getJoystickPos();
        // control = new Coordinate(0, 0.5);
        control.mult(5*Units.Length.feet);
        double rightOut = control.getY()+control.getX();
        double leftOut = control.getY()-control.getX();
        driveOut.set(Modes.Velocity, rightOut, leftOut);
    }

    @Override
    public void autonomousInit() {
        PositionTracker.getInstance().robotForward();
        mode.start();
    }

    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void disabledInit() {
        mode.end();
    }
}
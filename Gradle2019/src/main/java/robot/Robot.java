package robot;

import autos.modes.*;
import controlBoard.*;
import coordinates.Coordinate;
import coordinates.Heading;
import utilPackage.FancyDrive;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;
import drive.Drive;
import drive.DriveOutput;
import drive.PositionTracker;
import drive.DriveOutput.Modes;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import subsystems.ArmSystemControl;
import subsystems.MainArm;
import subsystems.MainArmControl;
import subsystems.Telescope;
import subsystems.TelescopeControl;

public class Robot extends IterativeRobot {
    private static IControlBoard cb = new ControlBoard();
    public static IControlBoard getControlBoard(){
        return cb;
    }

    Drive drive;
    DriveOutput driveOut;
    MainArm arm;
    Telescope telescope;

    ArmSystemControl armControl;

    IControlBoard controlBoard;
    PositionTracker mRunner;
    AutoMode mode;

    FancyDrive driveCode;

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
        telescope = Telescope.getInstance();

        driveOut.start();
        mode = new DoubleHatchAuto();
        // mode = new FarNearLeftHatchAuto();
        // mode = new SkidDrive();

        driveCode = new FancyDrive();

        armControl = ArmSystemControl.getInstance();
        armControl.start();
        // arm.disable(true);
    }

    @Override
    public void robotPeriodic(){
        // SmartDashboard.putNumber("Right Vel SI", 0);
        // SmartDashboard.putNumber("Left Vel SI", 0);
        controlBoard.display();
        driveOut.display();
        drive.display();
        mRunner.display();
        arm.periodic();
        telescope.periodic();
    }
    
    TrapezoidalMp mp, mpTelescope;
    Timer time = new Timer();

    Heading armPos;

    @Override
    public void teleopInit() {
        driveOut.set(Modes.Voltage, 0,0);
        armPos = Heading.createPolarHeading(-45*Units.Angle.degrees, Constants.Telescope.lenRetract);

        // armPos = Heading.createPolarHeading(0*Units.Angle.degrees, Constants.Telescope.lenExtend);

        // armPos = telescope.getEndPos().heading();
        // armPos = new Heading(20*Units.Length.inches, -20.5*Units.Length.inches);
        // armPos = new Heading(9*Units.Length.inches, 39*Units.Length.inches);

        armControl.setArmPosition(armPos);
        // armControl.setSetpoints(0*Units.Angle.degrees, 0);
        last = Timer.getFPGATimestamp();
    }

    double last = Timer.getFPGATimestamp();
    @Override
    public void teleopPeriodic() {
        double dt = Timer.getFPGATimestamp() - last;
        last = dt + last;
        Coordinate increase = controlBoard.getCoJoyPos().multC(10*Units.Length.inches*dt);
        if(armPos.getY() < 0 && armPos.getX() >= 0){
            increase.setX(Math.max(0, increase.getX()));
        }
        // armPos.add(increase);
        // arm.adjustToArm(armPos);
        if(controlBoard.armToInside()){
            armPos.setAngle(-90*Units.Angle.degrees);
            armPos.setMagnitude(Constants.Telescope.lenRetract);
        }
        if(controlBoard.armToHatchPickup()){
            armPos.setMagnitude(controlBoard.armLength());
            armPos.setYMaintainMag(-20.5*Units.Length.inches, controlBoard.flipArm());
        }
        if(controlBoard.armToHatchSecondLevel()){
            armPos.setMagnitude(controlBoard.armLength());
            armPos.setYMaintainMag(8*Units.Length.inches, controlBoard.flipArm());
        }
        if(controlBoard.armToHatchThirdLevel()){
            double y = 30*Units.Length.inches;
            armPos.setMagnitude(Math.max(controlBoard.armLength(), y));
            armPos.setYMaintainMag(y,controlBoard.flipArm());
        }
        armControl.setArmPosition(armPos);
        SmartDashboard.putString("Arm pos set", armPos.display());

        // driveCode.run();
    }

    private void driveCode(){
        Coordinate control = controlBoard.getJoystickPos();
        // control = new Coordinate(0, 0.5);
        control.mult(10*Units.Length.feet, 16*Units.Length.feet);
        double rightOut = control.getY()+control.getX();
        double leftOut = control.getY()-control.getX();
        driveOut.set(Modes.Velocity, rightOut, leftOut);
    }

    @Override
    public void autonomousInit() {
        PositionTracker.getInstance().robotForward();
        driveOut.setNoVoltage();
        mode.start();
    }

    @Override
    public void autonomousPeriodic() {
        // double vel = 2*Units.Length.feet;
        // driveOut.set(Modes.Velocity, vel, vel);
        // driveOut.setKin(-1, 0.3);
    }

    @Override
    public void disabledInit() {
        // mode.end();
    }

    @Override
    public void testPeriodic() {
        // arm.setVoltage(-3);
    }
}
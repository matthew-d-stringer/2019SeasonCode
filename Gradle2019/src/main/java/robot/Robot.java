package robot;

import autos.modes.*;
import controlBoard.*;
import coordinates.Coordinate;
import coordinates.Heading;
import coordinates.Pos2D;
import utilPackage.FancyDrive;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;
import utilPackage.Util;
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
import subsystems.Climber;
import subsystems.ClimberControl;
import subsystems.Gripper;
import subsystems.LEDController;
import subsystems.MainArm;
import subsystems.MainArmControl;
import subsystems.Telescope;
import subsystems.TelescopeControl;
import subsystems.ArmSystemControl.GripperMode;
import udp.Client;

public class Robot extends IterativeRobot {
    private static IControlBoard cb = new ControlBoard();
    public static IControlBoard getControlBoard(){
        return cb;
    }

    Drive drive;
    DriveOutput driveOut;
    MainArm arm;
    Telescope telescope;
    Gripper gripper;
    Climber climber;

    ArmSystemControl armControl;
    ClimberControl climberControl;

    ArmSetpoints setpoints;
    TeleopPaths teleopPaths;

    IControlBoard controlBoard;
    PositionTracker mRunner;
    AutoMode mode;

    FancyDrive driveCode;

    LEDController led;

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
        gripper = Gripper.getInstance();
        climber = Climber.getInstance();

        driveOut.start();
        mode = new DoubleHatchAuto();
        // mode = new FarNearLeftHatchAuto();
        // mode = new SkidDrive();

        driveCode = new FancyDrive();

        setpoints = new ArmSetpoints();
        teleopPaths = new TeleopPaths(driveCode);

        climberControl = ClimberControl.getInstance();
        armControl = ArmSystemControl.getInstance();
        // armControl.start();
        // arm.disable(true);

        //TODO: reenable this
        // led = LEDController.getInstance();
        // led.setLED(true);
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
        gripper.periodic();
        climber.periodic();

        // Pos2D visionData = Client.getInstance().updateVision(PositionTracker.getInstance().getPosition());
        // SmartDashboard.putString("Vision Position", visionData.getPos().display());
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
        mRunner.setInitPosFeet(new Coordinate(2.20, 1.74));
    }

    double last = Timer.getFPGATimestamp();
    @Override
    public void teleopPeriodic() {
        double dt = Timer.getFPGATimestamp() - last;
        last = dt + last;

        if(controlBoard.isCargoMode()){
            armControl.setGriperMode(GripperMode.cargo);
        }else{
            armControl.setGriperMode(GripperMode.hatch);
        }

        if(controlBoard.armToInside()){
            armPos.setAngle(-90*Units.Angle.degrees);
            armPos.setMagnitude(Constants.Telescope.lenRetract);
        }
        if(controlBoard.armToHatchPickup()){
            // low = incrementPreset(low);
            // armPos.setMagnitude(controlBoard.armLength());
            armPos.setMagnitude(Constants.Telescope.lenExtend);
            // armPos.setYMaintainMag(setpoints.getLow(), controlBoard.flipArm());
            armPos.setYMaintainMag(-25.5*Units.Length.inches, controlBoard.flipArm());
        }
        // if(controlBoard.armToHatchSecondLevel()){
        //     // mid = incrementPreset(mid);
        //     armPos.setMagnitude(controlBoard.armLength());
        //     armPos.setYMaintainMag(setpoints.getMid(), controlBoard.flipArm());
        // }
        // if(controlBoard.armToHatchThirdLevel()){
        //     // high = incrementPreset(high);
        //     double y = setpoints.getHigh();
        //     armPos.setMagnitude(Math.max(controlBoard.armLength(), y));
        //     armPos.setYMaintainMag(y,controlBoard.flipArm());
        // }
        armControl.setArmPosition(armPos);
        SmartDashboard.putString("Arm pos set", armPos.display());

        if(controlBoard.climbUp()){
            climberControl.setMode(ClimberControl.Modes.climbUp);
        }else if(controlBoard.climbDown()){
            climberControl.setMode(ClimberControl.Modes.climbDown);
        }else{
            climberControl.setMode(ClimberControl.Modes.hold);
        }

        if(controlBoard.hatchShoot()){
            gripper.hatchRelease();
        }else{
            gripper.hatchLock();
        }

        // teleopPaths.run();
        // driveCode.run();
    }

    private double incrementPreset(double cVal){
        double joyY = controlBoard.getCoJoyPos().getY();
        if(Util.inErrorRange(joyY, 0, 0.1)){
            return cVal;
        }
        return cVal + joyY * Units.Length.inches * 0.005;
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
        // arm.disable(true);
        // arm.setVoltage(3);
        if(controlBoard.climbUp()){
            climberControl.setMode(ClimberControl.Modes.climbUp);
        }else if(controlBoard.climbDown()){
            climberControl.setMode(ClimberControl.Modes.climbDown);
        }else{
            climberControl.setMode(ClimberControl.Modes.hold);
        }
        climberControl.run();
    }
}
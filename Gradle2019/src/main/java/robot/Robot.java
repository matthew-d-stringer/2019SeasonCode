package robot;

import autos.actions.ArmToLevel;
import autos.actions.SeriesAction;
import autos.actions.VisionPursuit;
import autos.actions.ArmToLevel.Levels;
import autos.modes.*;
import controlBoard.*;
import coordinates.Coordinate;
import coordinates.Heading;
import coordinates.Pos2D;
import utilPackage.FancyDrive;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;
import utilPackage.Util;
import vision.Jevois;
import vision.LEDController;
import drive.Drive;
import drive.DriveOutput;
import drive.PositionTracker;
import drive.DriveOutput.Modes;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import subsystems.ArmSystemControl;
import subsystems.CargoGripper;
import subsystems.Climber;
import subsystems.ClimberControl;
import subsystems.Gripper;
import subsystems.GroundGripper;
import subsystems.GroundGripperControl;
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
    GroundGripper groundGripper;
    CargoGripper cargoGripper;
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

    SendableChooser autoChooser;

    VisionPursuit testVision;

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
        groundGripper = GroundGripper.getInstance();
        climber = Climber.getInstance();

        testVision = new VisionPursuit();

        driveOut.start();

        driveCode = new FancyDrive();

        setpoints = new ArmSetpoints();
        teleopPaths = new TeleopPaths(driveCode);

        mode = new DoubleCargoAuto();

        // GroundGripperControl.getInstance().disable();
        GroundGripperControl.getInstance().retract();
        climberControl = ClimberControl.getInstance();
        armControl = ArmSystemControl.getInstance();
        armControl.start();
        // arm.disable(true);

        if(Constants.isCompBot){
            led = LEDController.getInstance();
            led.setLED(true);
        }

        cargoGripper = CargoGripper.getInstance();

        autoChooser = new SendableChooser<>();
        autoChooser.setDefaultOption("Center Left Auto", CenterLeftAuto.getSelectedString());
        autoChooser.addOption("Double Hatch Auto", DoubleCargoAuto.getSelectorName());
        SmartDashboard.putData(autoChooser);

        Jevois.getInstance().start();
    }

    @Override
    public void robotPeriodic(){
        arm.display();
        arm.periodic();
        telescope.display();
        telescope.periodic();
        gripper.display();
        gripper.periodic();
        groundGripper.display();
        groundGripper.periodic();
        if(Constants.isCompBot){
            led.setLED(!controlBoard.visionLED());
        }
    }

    TrapezoidalMp mp, mpTelescope;
    Timer time = new Timer();

    Heading armPos;

    @Override
    public void teleopInit() {
        driveOut.set(Modes.Voltage, 0,0);
        armPos = Heading.createPolarHeading(-45*Units.Angle.degrees, Constants.Telescope.lenRetract);

        ClimbCode.getInstance().reset();

        mode.end();

        armControl.disable(false);
        armControl.setArmPosition(armPos);
        last = Timer.getFPGATimestamp();
        climber.reset();
        mRunner.setInitPosFeet(2.20, 1.74);
        mRunner.robotForward();

        groundGripper.rollersOff();

        MainArmControl.getInstance().resetForTeleop();
    }

    double last = Timer.getFPGATimestamp();
    double hatchShootTime;
    @Override
    public void teleopPeriodic() {
        double dt = Timer.getFPGATimestamp() - last;
        last = dt + last;

        if(controlBoard.resetTelescope()){
            TelescopeControl.getInstance().reset();
        }

        if(controlBoard.climbMode()){
            ClimbCode.getInstance().run(driveCode);
            return;
        }else{
            armControl.disable(false);
            climber.setVoltage(1);
        }

        if(controlBoard.armToBallPickup()){
            armPos = Heading.createPolarHeading(-77*Units.Angle.degrees, Constants.Telescope.lenRetract + 2.5*Units.Length.inches);
            teleopPaths.setMiddle(false);
            armControl.setGriperMode(GripperMode.pickup);
        }

        if(controlBoard.armToInside()){
            armPos.setAngle(-90*Units.Angle.degrees);
            armPos.setMagnitude(Constants.Telescope.lenRetract);
            teleopPaths.setMiddle(false);
            armControl.setGriperMode(GripperMode.level);
        }
        if(controlBoard.armToBallGoal()){
            double len = controlBoard.armLength();
            setpoints.incrementBallGoal(controlBoard.getCoJoyPos().getY());
            armPos.setMagnitude(len);
            armPos.setYMaintainMag(setpoints.getBallGoal(),controlBoard.flipArm());
            teleopPaths.setMiddle(false);
            armControl.setGriperMode(GripperMode.level);
        }
        if(controlBoard.armToHatchPickup()){
            armControl.setGriperMode(GripperMode.level);
            double len = controlBoard.armLength();
            if(Double.isNaN(len)){
                len = Constants.Telescope.lenRetract;
            }
            armPos.setMagnitude(Constants.Telescope.lenRetract + 5*Units.Length.inches);

            if(controlBoard.incrementOffset()){
                setpoints.incrementWristLow(1);
            }else if(controlBoard.decrementOffset()){
                setpoints.incrementWristLow(-1);
            }
            armControl.setGripperSetpoint(setpoints.getWristLow());

            setpoints.incrementHatchLow(controlBoard.getCoJoyPos().getY());
            armPos.setYMaintainMag(setpoints.getHatchLow(), controlBoard.flipArm());
            teleopPaths.setMiddle(false);
        }
        if(controlBoard.armToHatchSecondLevel()){
            armControl.setGriperMode(GripperMode.level);
            armPos.setMagnitude(Constants.Telescope.lenRetract);

            if(controlBoard.incrementOffset()){
                setpoints.incrementWristMid(1);
            }else if(controlBoard.decrementOffset()){
                setpoints.incrementWristMid(-1);
            }
            armControl.setGripperSetpoint(setpoints.getWristMid());

            setpoints.incrementHatchMid(controlBoard.getCoJoyPos().getY());
            armPos.setYMaintainMag(setpoints.getHatchMid(), controlBoard.flipArm());

            teleopPaths.setMiddle(true);
        }
        if(controlBoard.armToHatchThirdLevel()){
            armControl.setGriperMode(GripperMode.level);

            if(controlBoard.incrementOffset()){
                setpoints.incrementWristHigh(1);
            }else if(controlBoard.decrementOffset()){
                setpoints.incrementWristHigh(-1);
            }
            armControl.setGripperSetpoint(setpoints.getWristHigh());

            double y;

            setpoints.incrementHatchHigh(controlBoard.getCoJoyPos().getY());
            y = setpoints.getHatchHigh();
            armPos.setMagnitude(Constants.Telescope.lenExtend-2*Units.Length.inches);

            armPos.setYMaintainMag(y,controlBoard.flipArm());
            teleopPaths.setMiddle(false);
        }
        armControl.setArmPosition(armPos);
        SmartDashboard.putString("Arm pos set", armPos.display());

        GroundGripperControl.getInstance().retract();

        if(controlBoard.gripperShootPressed()){
            hatchShootTime = Timer.getFPGATimestamp();
        }

        if(controlBoard.gripperShoot()){
            groundGripper.rollersOff();
            if(Timer.getFPGATimestamp() - hatchShootTime > 0.8){
                gripper.rollerOff();
            }else{
                gripper.hatchRelease();
            }
        }else if(controlBoard.gripperGrab()){
            gripper.hatchGrab();
            groundGripper.rollersOff();
        }else{
            groundGripper.rollersOff();
            gripper.hatchHold();
        }

        if(controlBoard.cargoPivot()){
            cargoGripper.side();
        }else{
            cargoGripper.upright();
        }

        if(controlBoard.cargoGrab()){
            cargoGripper.close();
        }else{
            cargoGripper.open();
        }

        if(controlBoard.cargoShoot()){
            cargoGripper.rollersOut();
        }else{
            cargoGripper.rollersOff();
        }

        teleopPaths.run();
        driveCode.run();
    }

    @Override
    public void autonomousInit() {
        if((String)autoChooser.getSelected() == CenterLeftAuto.getSelectedString()){
            mode = new CenterLeftAuto();
        }else if((String)autoChooser.getSelected() == DoubleCargoAuto.getSelectorName()){
            mode = new DoubleCargoAuto();
        }else{
            mode = new CenterLeftAuto();
        }
        PositionTracker.getInstance().robotForward();
        driveOut.setNoVoltage();
        mode.start();
    }

    @Override
    public void autonomousPeriodic() {

    }

    @Override
    public void disabledInit() {

    }

    ArmToLevel armToLevel;
    SeriesAction vision;
    @Override
    public void testInit() {
        telescope.setVoltage(0);
        gripper.setVoltage(0);
        gripper.rollerOff();
        groundGripper.rollersOff();
        arm.setVoltage(0);

        armToLevel = new ArmToLevel(Levels.loading, false, autos.actions.ArmToLevel.GripperMode.hatch);
        vision = new SeriesAction(armToLevel, testVision);
        vision.resetTeleop();
        testVision.resetTeleop();
    }

    @Override
    public void testPeriodic() {

    }
}
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

        autoChooser = new SendableChooser<>();
        autoChooser.setDefaultOption("Center Left Auto", CenterLeftAuto.getSelectedString());
        autoChooser.addOption("Double Hatch Auto", DoubleCargoAuto.getSelectorName());
        SmartDashboard.putData(autoChooser);

        Jevois.getInstance().start();
    }

    @Override
    public void robotPeriodic(){
        // SmartDashboard.putNumber("Right Vel SI", 0);
        // SmartDashboard.putNumber("Left Vel SI", 0);
        // controlBoard.display();
        // driveOut.display();
        // drive.display();
        // mRunner.display();
        // arm.display();
        arm.periodic();
        // telescope.display();
        telescope.periodic();
        // gripper.display();
        gripper.periodic();
        // groundGripper.display();
        groundGripper.periodic();
        // try{
        //     Jevois.getInstance().run();
        // }catch(Exception e){
        //     System.out.println("Vision error");
        // }

        // SmartDashboard.putString("Delta Position", Jevois.getInstance().getPosition().display());
        // SmartDashboard.putString("Relative Vector", Jevois.getInstance().getPT().display());
    }

    TrapezoidalMp mp, mpTelescope;
    Timer time = new Timer();

    Heading armPos;

    @Override
    public void teleopInit() {
        driveOut.set(Modes.Voltage, 0,0);
        armPos = Heading.createPolarHeading(-45*Units.Angle.degrees, Constants.Telescope.lenRetract);

        ClimbCode.getInstance().reset();

        // armPos = Heading.createPolarHeading(-45*Units.Angle.degrees, Constants.Telescope.lenExtend);

        // armPos = telescope.getEndPos().heading();
        // armPos = new Heading(20*Units.Length.inches, -20.5*Units.Length.inches);
        // armPos = new Heading(9*Units.Length.inches, 39*Units.Length.inches);

        mode.end();

        armControl.disable(false);
        armControl.setArmPosition(armPos);
        // armControl.setSetpoints(0*Units.Angle.degrees, 0);
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
            // armPos.setXY(20*Units.Length.inches, -42*Units.Length.inches);
            // armPos = Heading.createPolarHeading(-65*Units.Angle.degrees, Constants.Telescope.lenExtend);
            armPos = Heading.createPolarHeading(-77*Units.Angle.degrees, Constants.Telescope.lenRetract + 2.5*Units.Length.inches);
            // armPos.setAngle(-100*Units.Angle.degrees);
            // armPos.setMagnitude(Constants.Telescope.lenRetract);
            teleopPaths.setMiddle(false);
            armControl.setGriperMode(GripperMode.pickup);
        }

        if(controlBoard.incrementOffset()){
            armControl.incrementOffset(1);
        }else if(controlBoard.decrementOffset()){
            armControl.incrementOffset(-1);
        }


        if(controlBoard.armToInside()){
            // armPos.setAngle(-100*Units.Angle.degrees);
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
            // armPos.setMagnitude(Math.min(controlBoard.armLength(), Constants.Telescope.lenRetract + 5*Units.Length.inches));
            armPos.setMagnitude(Constants.Telescope.lenRetract + 5*Units.Length.inches);
            if(controlBoard.isCargoMode()){
                setpoints.incrementBallLow(controlBoard.getCoJoyPos().getY());
                armPos.setYMaintainMag(setpoints.getBallLow(), controlBoard.flipArm());
            }else{
                setpoints.incrementHatchLow(controlBoard.getCoJoyPos().getY());
                armPos.setYMaintainMag(setpoints.getHatchLow(), controlBoard.flipArm());
            }
            teleopPaths.setMiddle(false);
            // armPos.setYMaintainMag(-25.5*Units.Length.inches, controlBoard.flipArm());
        }
        if(controlBoard.armToHatchSecondLevel()){
            armControl.setGriperMode(GripperMode.level);
            // armPos.setMagnitude(controlBoard.armLength());
            armPos.setMagnitude(Constants.Telescope.lenRetract);
            if(controlBoard.isCargoMode()){
                setpoints.incrementBallMid(controlBoard.getCoJoyPos().getY());
                armPos.setYMaintainMag(setpoints.getBallMid(), controlBoard.flipArm());
            }else{
                setpoints.incrementHatchMid(controlBoard.getCoJoyPos().getY());
                armPos.setYMaintainMag(setpoints.getHatchMid(), controlBoard.flipArm());
            }
            teleopPaths.setMiddle(true);
        }
        if(controlBoard.armToHatchThirdLevel()){
            armControl.setGriperMode(GripperMode.level);
            double y;
            if(controlBoard.isCargoMode()){
                setpoints.incrementBallHigh(controlBoard.getCoJoyPos().getY());
                y = setpoints.getBallHigh();
                if(controlBoard.flipArm()){
                    y += 6*Units.Angle.degrees;
                }
                // armPos.setMagnitude(Math.max(controlBoard.armLength(), y));
                armPos.setMagnitude(Constants.Telescope.lenExtend);
            }else{
                setpoints.incrementHatchHigh(controlBoard.getCoJoyPos().getY());
                y = setpoints.getHatchHigh();
                armPos.setMagnitude(Constants.Telescope.lenExtend-2*Units.Length.inches);
            }
            // armPos.setMagnitude(Math.max(controlBoard.armLength(), y));
            armPos.setYMaintainMag(y,controlBoard.flipArm());
            teleopPaths.setMiddle(false);
        }
        armControl.setArmPosition(armPos);
        SmartDashboard.putString("Arm pos set", armPos.display());

        if(controlBoard.isCargoMode()){
            if(armPos.getY() > setpoints.getBallMid() && MainArmControl.getInstance().finishedMovement()){
                GroundGripperControl.getInstance().retract();
            }else{
                GroundGripperControl.getInstance().ballGrab();
            }
        }else{
            GroundGripperControl.getInstance().retract();
        }

        if(controlBoard.isCargoMode()){
            if(controlBoard.ballPistonGrab()){
                gripper.ballClamp();
            }else{
                gripper.ballMode();
            }
            // if(armPos.getMagnitude() < Constants.Telescope.lenRetract+1*Units.Length.inches && 
            //     armPos.getAngle() < -80*Units.Angle.degrees){
            //     armControl.setGriperMode(GripperMode.pickup);
            // }else{
            //     armControl.setGriperMode(GripperMode.level);
            // }
        }else{
            gripper.hatchMode();
            armControl.setGriperMode(GripperMode.level);
        }

        if(controlBoard.gripperShootPressed()){
            hatchShootTime = Timer.getFPGATimestamp();
        }

        if(controlBoard.gripperShoot()){
            groundGripper.rollersOff();
            if(controlBoard.isCargoMode()){
                gripper.ballRelease();
            }else{
                if(Timer.getFPGATimestamp() - hatchShootTime > 0.8){
                    gripper.rollerOff();
                }else{
                    gripper.hatchRelease();
                }
            }
        }else if(controlBoard.gripperGrab()){
            if(controlBoard.isCargoMode()){
                gripper.ballGrab();
                groundGripper.rollersGrab();
            }else{
                gripper.hatchGrab();
                groundGripper.rollersOff();
            }
        }else{
            groundGripper.rollersOff();
            if(controlBoard.isCargoMode()){
                gripper.ballHold();
            }else{
                gripper.hatchHold();
            }
        }

        teleopPaths.run();
        driveCode.run();
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
        // teleopInit();
    }

    @Override
    public void autonomousPeriodic() {
        // teleopPeriodic();
        // double vel = 2*Units.Length.feet;
        // driveOut.set(Modes.Velocity, vel, vel);
        // driveOut.setKin(-1, 0.3);
    }

    @Override
    public void disabledInit() {
        // mode.end();
    }

    @Override
    public void testInit() {
        armControl.disable(true);
        telescope.setVoltage(0);
        gripper.setVoltage(0);
        groundGripper.rollersOff();
        arm.setVoltage(0);
    }

    @Override
    public void testPeriodic() {
        arm.setVoltage(arm.getAntigrav());
        // groundGripper.setVoltage(12*controlBoard.getJoystickPos().getY() + groundGripper.getAntigrav());
        // GroundGripperControl.getInstance().run();
        // GroundGripper.getInstance().rollersClimb();
    }
}
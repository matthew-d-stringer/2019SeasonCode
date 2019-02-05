package robot;

import autos.modes.*;
import controlBoard.*;
import coordinates.Coordinate;
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
    MainArmControl armControl;
    Telescope telescope;
    TelescopeControl telescopeControl;
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
        armControl = MainArmControl.getInstance();

        telescope = Telescope.getInstance();
        telescopeControl = TelescopeControl.getInstance();

        driveOut.start();
        // mode = new DoubleHatchAuto();
        // mode = new FarNearLeftHatchAuto();
        mode = new SkidDrive();

        driveCode = new FancyDrive();

        SmartDashboard.putNumber("Arm Setpoint", 90);
    }

    @Override
    public void robotPeriodic(){
        // SmartDashboard.putNumber("Right Vel SI", 0);
        // SmartDashboard.putNumber("Left Vel SI", 0);
        driveOut.display();
        drive.display();
        mRunner.display();
        arm.periodic();
        telescope.periodic();
    }
    
    TrapezoidalMp mp, mpTelescope;
    Timer time = new Timer();

    @Override
    public void teleopInit() {
        driveOut.set(Modes.Voltage, 0,0);
        double setpoint = SmartDashboard.getNumber("Arm Setpoint", 90)*Units.Angle.degrees;
        TrapezoidalMp.constraints constraints = new TrapezoidalMp.constraints(setpoint, 
            2*Units.Angle.revolutions, 
            0.333*Units.Angle.revolutions);
        mp = new TrapezoidalMp(arm.getAngle(), constraints);

        TrapezoidalMp.constraints constraints2 = new TrapezoidalMp.constraints(Constants.Telescope.lenExtend, 
            60*Units.Length.inches, 
            40*Units.Length.inches);
        mpTelescope = new TrapezoidalMp(telescope.getDistance(), constraints2);


        time.start();
    }

    @Override
    public void teleopPeriodic() {
        // double setpoint = SmartDashboard.getNumber("Arm Setpoint", 90)*Units.Angle.degrees;
        // armControl.setSetpoint(setpoint);
        // arm.setVoltage(arm.getAntigrav());

        // armControl.setSetpoint(mp.Calculate(time.get())[0]);

        // armControl.setSetpoint(20*Units.Angle.degrees);
        // armControl.run();

        // telescope.setVoltage(1);
        // telescope.setVoltage(telescope.getAntigrav());
        // telescopeControl.setSetpoint(Constants.Telescope.lenExtend);

        // telescopeControl.setSetpoint(mpTelescope.Calculate(time.get())[0]);
        // telescopeControl.run();


        // double vel = 2.5*Units.Length.feet;
        // driveOut.set(Modes.Velocity, vel, vel);
        // driveCode();
        // driveOut.set(Modes.Voltage, 3,3);
        // drive.display();
        // driveOut.set(Modes.Voltage, 3, 3);

        driveCode.run();
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
        mode.start();
    }

    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void disabledInit() {
        mode.end();
    }

    @Override
    public void testPeriodic() {
        arm.setVoltage(-3);
    }
}
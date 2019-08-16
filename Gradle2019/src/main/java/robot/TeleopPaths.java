package robot;

import autos.actions.DrivePath;
import coordinates.Coordinate;
import coordinates.Heading;
import drive.DriveOutput;
import drive.PositionTracker;
import drive.DriveOutput.Modes;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import path.TrajectoryList;
import utilPackage.Derivative;
import utilPackage.FancyDrive;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;
import utilPackage.Util;
import vision.Jevois;

public class TeleopPaths{
    Joystick joyPath;
    FancyDrive drive;

    TrapezoidalMp.constraints constraints;

    DrivePath home;

    DrivePath feedToClosestCargo;
    DrivePath feedToMidCargo;
    DrivePath feedToFarCargo;
    Derivative dAngle;

    boolean middle = false;
    public TeleopPaths(FancyDrive drive){
        joyPath = Robot.getControlBoard().getPathsJoystick();
        this.drive = drive;

        dAngle = new Derivative();

        constraints = new TrapezoidalMp.constraints(0, 13*Units.Length.feet, 7*Units.Length.feet);

        feedToClosestCargo = DrivePath.createFromFileOnRoboRio("Teleop/Left", "feedToClosestCargo", constraints);
        feedToMidCargo = DrivePath.createFromFileOnRoboRio("Teleop/Left", "feedToMidCargo", constraints);
        feedToFarCargo = DrivePath.createFromFileOnRoboRio("Teleop/Left", "feedToFarCargo", constraints);
    }

    public void setMiddle(boolean middle){
        this.middle = middle;
    }

    public void run(){
        boolean disableDrive = false;

        if(Robot.getControlBoard().visionDrive()){
            disableDrive = true;
        }
        if(!disableDrive){
            drive.enabled(true);
            return;
        }
        drive.enabled(false);

        if(Robot.getControlBoard().visionDrivePressed()){
            Heading target = Jevois.getInstance().getPT();
            double angle = Math.PI/2 - target.getAngle();
            dAngle.reset(Timer.getFPGATimestamp(), angle);
        }

        if(Robot.getControlBoard().visionDrive()){
            double turn, forward;
            Heading target = Jevois.getInstance().getPT();
            double angle = Math.PI/2 - target.getAngle();
            double dist = target.getMagnitude();

            turn = 1.05*angle + 0.1*dAngle.Calculate(angle, Timer.getFPGATimestamp());

            forward = 8*Units.Length.feet*Robot.getControlBoard().getJoystickPos().getY();

            double outLeft = -turn + forward;
            double outRight = turn + forward;

            DriveOutput.getInstance().set(Modes.Velocity, outRight, outLeft);

            return;
        }
    }
}
package robot;

import autos.actions.DrivePath;
import coordinates.Coordinate;
import coordinates.Heading;
import drive.DriveOutput;
import drive.PositionTracker;
import drive.DriveOutput.Modes;
import edu.wpi.first.wpilibj.Joystick;
import path.TrajectoryList;
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
    public TeleopPaths(FancyDrive drive){
        joyPath = Robot.getControlBoard().getPathsJoystick();
        this.drive = drive;

        constraints = new TrapezoidalMp.constraints(0, 13*Units.Length.feet, 7*Units.Length.feet);

        feedToClosestCargo = DrivePath.createFromFileOnRoboRio("Teleop/Left", "feedToClosestCargo", constraints);
        feedToMidCargo = DrivePath.createFromFileOnRoboRio("Teleop/Left", "feedToMidCargo", constraints);
        feedToFarCargo = DrivePath.createFromFileOnRoboRio("Teleop/Left", "feedToFarCargo", constraints);
        // feedToFarCargo.setlookAhead(3*Units.Length.feet);
    }

    public void run(){
        boolean disableDrive = false;
        // for(int i = 0; i < joyPath.getButtonCount(); i++){
        //     if(joyPath.getRawButton(i)){
        //         disableDrive = true;
        //         break;
        //     }
        // }
        if(Robot.getControlBoard().visionDrive()){
            disableDrive = true;
        }
        if(!disableDrive){
            drive.enabled(true);
            return;
        }
        drive.enabled(false);

        if(Robot.getControlBoard().visionDrive()){
            double turn, forward;
            Heading target = Jevois.getInstance().getPT();
            double angle = Math.PI/2 - target.getAngle();
            double dist = target.getMagnitude();

            turn = 1.1*angle;
            // turn = 2*angle;
            Coordinate pt1;
            // if(Robot.getControlBoard().isCargoMode())
            //     pt1 = new Coordinate(4.5*Units.Length.feet, 0*Units.Length.feet);
            // else
            pt1 = new Coordinate(2.916*Units.Length.feet, 0*Units.Length.feet);
            Coordinate pt2 = new Coordinate(4*Units.Length.feet, 2*Units.Length.feet);
            forward = Util.mapRange(dist, pt1, pt2);
            forward = Math.min(forward, 6*Units.Length.feet);
            // forward = 2*Units.Length.feet;

            double outLeft = -turn + forward;
            double outRight = turn + forward;

            // if(Jevois.getInstance().useVision())
                DriveOutput.getInstance().set(Modes.Velocity, outRight, outLeft);
            // else
            //     DriveOutput.getInstance().setNoVelocity();
        }

        //Left, closest Cargo
        // if(joyPath.getRawButton(7)){
        //     feedToClosestCargo.runTeleop();
        // }else{
        //     feedToClosestCargo.resetTeleop();
        // }
        // if(joyPath.getRawButton(6)){
        //     feedToMidCargo.runTeleop();
        // }else{
        //     feedToMidCargo.resetTeleop();
        // }
        // if(joyPath.getRawButton(5)){
        //     feedToFarCargo.runTeleop();
        // }else{
        //     feedToFarCargo.resetTeleop();
        // }

        // if(joyPath.getRawButtonPressed(3)){
        //     TrajectoryList toHome = new TrajectoryList(PositionTracker.getInstance().getPosition().getPos());
        //     toHome.add(new Coordinate(4.20, 4.74).mult(Units.Length.feet));
        //     toHome.add(new Coordinate(3.20, 3.74).mult(Units.Length.feet));
        //     toHome.add(new Coordinate(3.20, 2.74).mult(Units.Length.feet));
        //     home = new DrivePath(toHome, constraints);
        //     home.setReverse(true);
        // }

        // if(joyPath.getRawButton(3)){
        //     home.runTeleop();
        // }
    }
}
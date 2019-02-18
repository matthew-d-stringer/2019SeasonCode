package robot;

import autos.actions.DrivePath;
import coordinates.Coordinate;
import drive.PositionTracker;
import edu.wpi.first.wpilibj.Joystick;
import path.TrajectoryList;
import utilPackage.FancyDrive;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

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
        for(int i = 0; i < joyPath.getButtonCount(); i++){
            if(joyPath.getRawButton(i)){
                disableDrive = true;
                break;
            }
        }
        if(!disableDrive){
            drive.enabled(true);
            return;
        }        
        drive.enabled(false);
        //Left, closest Cargo
        if(joyPath.getRawButton(7)){
            feedToClosestCargo.runTeleop();
        }else{
            feedToClosestCargo.resetTeleop();
        }
        if(joyPath.getRawButton(6)){
            feedToMidCargo.runTeleop();
        }else{
            feedToMidCargo.resetTeleop();
        }
        if(joyPath.getRawButton(5)){
            feedToFarCargo.runTeleop();
        }else{
            feedToFarCargo.resetTeleop();
        }

        if(joyPath.getRawButtonPressed(3)){
            TrajectoryList toHome = new TrajectoryList(PositionTracker.getInstance().getPosition().getPos());
            toHome.add(new Coordinate(4.20, 4.74).mult(Units.Length.feet));
            toHome.add(new Coordinate(3.20, 3.74).mult(Units.Length.feet));
            toHome.add(new Coordinate(3.20, 2.74).mult(Units.Length.feet));
            home = new DrivePath(toHome, constraints);
            home.setReverse(true);
        }

        if(joyPath.getRawButton(3)){
            home.runTeleop();
        }
    }
}
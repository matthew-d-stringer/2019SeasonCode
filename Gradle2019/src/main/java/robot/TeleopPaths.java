package robot;

import autos.actions.DrivePath;
import edu.wpi.first.wpilibj.Joystick;
import utilPackage.FancyDrive;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class TeleopPaths{
    Joystick joyPath;
    FancyDrive drive;

    DrivePath feedToClosestCargo;
    public TeleopPaths(FancyDrive drive){
        joyPath = Robot.getControlBoard().getPathsJoystick();
        this.drive = drive;

        TrapezoidalMp.constraints constraints = new TrapezoidalMp.constraints(0, 5*Units.Length.feet, 5*Units.Length.feet);

        feedToClosestCargo = DrivePath.createFromFileOnRoboRio("Teleop/Left", "feedToClosestCargo", constraints);
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
            drive.enabled(false);
            feedToClosestCargo.runTeleop();
        }else{
            feedToClosestCargo.resetTeleop();
        }
    }
}
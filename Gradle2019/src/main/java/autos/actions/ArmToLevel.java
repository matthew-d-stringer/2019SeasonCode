package autos.actions;

import coordinates.Heading;
import edu.wpi.first.wpilibj.Timer;
import robot.Constants;
import subsystems.ArmSystemControl;
import subsystems.MainArmControl;
import utilPackage.Units;

public class ArmToLevel extends Action{
    public static enum Levels{
        reset,
        loading,
        low,
        middle,
        high;
    }
    public static enum GripperMode{
        hatch,
        ball;
    }

    boolean reverse, checkWithTelescope = true;
    Levels level;
    GripperMode gripMode;
    Heading setpoint;
    double armLength;

    ArmSystemControl armControl;
    double startVal;

    public ArmToLevel(Levels level, boolean reverse, GripperMode mode){
        this.reverse = reverse;
        this.level = level;
        gripMode = mode;
        setpoint = new Heading();
        setArmPercent(0);
        armControl = ArmSystemControl.getInstance();
    }
    public ArmToLevel(Levels level, boolean reverse, GripperMode mode, double armPercent){
        this.reverse = reverse;
        this.level = level;
        gripMode = mode;
        setpoint = new Heading();
        setArmPercent(armPercent);
        armControl = ArmSystemControl.getInstance();
    }

    public void useTelescope(boolean useTelescope){
        this.checkWithTelescope = useTelescope;
    }

    public void setArmLen(double len){
        armLength = len;
    }

    public void setArmPercent(double percent){
        armLength = (Constants.Telescope.lenExtend - Constants.Telescope.lenRetract)*percent + Constants.Telescope.lenRetract;
    }

    @Override
    public void start() {
        setpoint.setMagnitude(armLength);
        switch(level){
            case reset:
                setpoint.setAngle(-85*Units.Angle.degrees);
                setpoint.setMagnitude(Constants.Telescope.lenRetract);
                break;
            case loading:
                setpoint.setMagnitude(Constants.Telescope.lenRetract + 5*Units.Length.inches);
                setpoint.setYMaintainMag(-24.5*Units.Length.inches, reverse);
                break;
            case low:
                setpoint.setMagnitude(Constants.Telescope.lenRetract + 5*Units.Length.inches);
                setpoint.setYMaintainMag(-24.25*Units.Length.inches, reverse);
                break;
            case middle:
                setpoint.setYMaintainMag(5*Units.Length.inches, reverse);
                break;
            case high:
                double y = 33*Units.Length.inches;
                setpoint.setMagnitude(Math.max(armLength, y));
                setpoint.setYMaintainMag(y, reverse);
                break;
        }
        armControl.setArmPosition(setpoint);
        startVal = Timer.getFPGATimestamp();
    }
        
    @Override
    public void update() {
        armControl.setArmPosition(setpoint);
    }

    @Override
    public boolean isFinished() {
        if(checkWithTelescope)
            return armControl.isDone() && Timer.getFPGATimestamp() - startVal > 0.5;
        else
            return MainArmControl.getInstance().finishedMovement() && Timer.getFPGATimestamp() - startVal > 0.1;
    }

    @Override
    public void done() {
    }
}
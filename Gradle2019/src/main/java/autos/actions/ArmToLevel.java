package autos.actions;

import coordinates.Heading;
import robot.Constants;
import subsystems.ArmSystemControl;
import subsystems.MainArmControl;
import utilPackage.Units;

public class ArmToLevel extends Action{
    public static enum Levels{
        loading,
        low,
        middle,
        high;
    }
    public static enum GripperMode{
        hatch,
        ball;
    }

    boolean reverse;
    Levels level;
    GripperMode gripMode;
    Heading setpoint;
    double armLength;

    ArmSystemControl armControl;

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
            case loading:
                setpoint.setYMaintainMag(-20.5*Units.Length.inches, reverse);
                break;
            case low:
                setpoint.setYMaintainMag(-20.5*Units.Length.inches, reverse);
                break;
            case middle:
                setpoint.setYMaintainMag(5*Units.Length.inches, reverse);
                break;
            case high:
                double y = 30*Units.Length.inches;
                setpoint.setMagnitude(Math.max(armLength, y));
                setpoint.setYMaintainMag(y, reverse);
                break;
        }
        armControl.setArmPosition(setpoint);
    }
        
    @Override
    public void update() {
        armControl.setArmPosition(setpoint);
    }

    @Override
    public boolean isFinished() {
        return armControl.isDone();
    }

    @Override
    public void done() {
    }
}
package controlBoard;

import coordinates.Heading;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class IControlBoard{
    public abstract Heading getJoystickPos();
    public abstract double getWheelPos();
    public abstract Heading getCoJoyPos();
    public abstract double getCoJoySlider();
    public abstract Joystick getPathsJoystick();
    public abstract double armLength();
    public abstract boolean fastDrive();
    public abstract boolean visionDrive();

    public abstract boolean retardClimb();

    public abstract boolean quickTurn();

    public abstract boolean ballRollerGrab();

    public abstract boolean isCargoMode();
    public abstract boolean flipArm();
    public abstract boolean armToInside();
    public abstract boolean armToBallPickup();
    public abstract boolean armToBallGoal();
    public abstract boolean armToHatchPickup();
    public abstract boolean armToHatchSecondLevel();
    public abstract boolean armToHatchThirdLevel();
    public abstract boolean incrementOffset();
    public abstract boolean decrementOffset();

    public abstract boolean gripperShoot();
    public abstract boolean ballPistonGrab();

    public abstract boolean climbUp();
    public abstract boolean climbRetract();

    public void display(){
        SmartDashboard.putString("Co-joystick pos", getCoJoyPos().display());
        SmartDashboard.putBoolean("Arm to inside", armToInside());
        SmartDashboard.putBoolean("Arm to hatch pickup", armToHatchPickup());
        SmartDashboard.putBoolean("Arm to hatch second", armToHatchSecondLevel());
        SmartDashboard.putBoolean("Arm to hatch third", armToHatchThirdLevel());
    }
}
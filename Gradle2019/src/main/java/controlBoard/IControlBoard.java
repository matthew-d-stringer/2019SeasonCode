package controlBoard;

import coordinates.Heading;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class IControlBoard{
    public abstract Heading getJoystickPos();
    public abstract double getWheelPos();
    public abstract Heading getCoJoyPos();
    public abstract double getCoJoySlider();
    public abstract double armLength();

    public abstract boolean quickTurn();

    public abstract boolean armToInside();
    public abstract boolean armToHatchPickup();
    public abstract boolean armToHatchSecondLevel();

    public void display(){
        SmartDashboard.putString("Co-joystick pos", getCoJoyPos().display());
    }
}
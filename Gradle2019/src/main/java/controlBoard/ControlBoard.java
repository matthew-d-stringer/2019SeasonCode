package controlBoard;

import coordinates.Heading;
import edu.wpi.first.wpilibj.Joystick;

public class ControlBoard extends IControlBoard{
    Joystick joy, wheel, buttonPad, coJoy;

    public ControlBoard(){
        joy = new Joystick(0);
        wheel = new Joystick(1);
        buttonPad = new Joystick(2);
        coJoy = new Joystick(3);
    }

    @Override
    public Heading getJoystickPos() {
        return new Heading(wheel.getX(), -joy.getY());
        // return new Heading(joy.getX(), -joy.getY());
    }

    @Override
    public double getWheelPos() {
        return wheel.getX();
    }

    public Heading getCoJoyPos(){
        return new Heading(coJoy.getX(), -coJoy.getY());
    }

    @Override
    public boolean quickTurn() {
        return wheel.getRawButton(7);
    }
}
package controlBoard;

import coordinates.Heading;
import edu.wpi.first.wpilibj.Joystick;

public class ControlBoard extends IControlBoard{
    Joystick joy, wheel, buttonPad;

    public ControlBoard(){
        joy = new Joystick(0);
        wheel = new Joystick(1);
        buttonPad = new Joystick(2);
    }

    @Override
    public Heading getJoystickPos() {
        // return new Heading(-wheel.getX(), -joy.getY());
        return new Heading(-joy.getX(), -joy.getY());
    }

    @Override
    public double getWheelPos() {
        return wheel.getX();
    }

    @Override
    public boolean gripperUp() {
        return buttonPad.getRawButton(8);
    }

    @Override
    public boolean gripperDown() {
        return buttonPad.getRawButton(13);
    }

    @Override
    public boolean elevatorUp() {
        return buttonPad.getRawButton(23);
    }

    @Override
    public boolean elevatorDown() {
        return buttonPad.getRawButton(24);
    }

    @Override
    public boolean armUp() {
        return buttonPad.getRawButton(21);
    }

    @Override
    public boolean armDown() {
        return buttonPad.getRawButton(22);
    }

    @Override
    public boolean vault() {
        return buttonPad.getRawButton(12);
    }

    @Override
    public boolean Switch() {
        return buttonPad.getRawButton(14);
    }
}
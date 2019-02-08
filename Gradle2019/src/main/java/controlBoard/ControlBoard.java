package controlBoard;

import coordinates.Heading;
import edu.wpi.first.wpilibj.Joystick;
import robot.Constants;

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

    @Override
    public Heading getCoJoyPos(){
        Heading out = new Heading();
        if(Math.abs(coJoy.getX()) > 0.1){
            out.setX(coJoy.getX());
        }
        if(Math.abs(coJoy.getY()) > 0.1){
            out.setY(-coJoy.getY());
        }
        return out;
    }

    @Override
    public double getCoJoySlider() {
        return (coJoy.getRawAxis(2)+1)/2;
    }

    @Override
    public double armLength() {
        return (Constants.Telescope.lenExtend - Constants.Telescope.lenRetract)*getCoJoySlider()+Constants.Telescope.lenRetract;
    }

    @Override
    public boolean quickTurn() {
        return wheel.getRawButton(7);
    }

    @Override
    public boolean armToInside() {
        return coJoy.getRawButton(2);
    }

    @Override
    public boolean armToHatchPickup() {
        return coJoy.getRawButton(6);
    }

    @Override
    public boolean armToHatchSecondLevel() {
        return coJoy.getRawButton(4);
    }
}
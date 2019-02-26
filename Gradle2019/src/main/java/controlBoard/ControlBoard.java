package controlBoard;

import coordinates.Heading;
import edu.wpi.first.wpilibj.Joystick;
import robot.Constants;
import utilPackage.Toggle;
import utilPackage.Util;

public class ControlBoard extends IControlBoard{
    Joystick joy, wheel, buttonPad, coJoy, paths;
    Toggle ballPistonGrab;

    public ControlBoard(){
        joy = new Joystick(0);
        wheel = new Joystick(1);
        buttonPad = new Joystick(2);
        coJoy = new Joystick(3);
        paths = new Joystick(4);

        ballPistonGrab = new Toggle(false);
    }

    @Override
    public Heading getJoystickPos() {
        double y = -joy.getY();
        // if(Util.inErrorRange(y, 0, 0.05)){
        //     y = 0;
        // }
        return new Heading(wheel.getX(), y);
        // return new Heading(joy.getX(), -joy.getY());
    }

    @Override
    public double getWheelPos() {
        return wheel.getX();
    }

    @Override
    public Heading getCoJoyPos(){
        Heading out = new Heading();
        out.setX(coJoy.getX());
        if(-coJoy.getY() == 1.0){
            out.setY(0);
        }else{
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
    public Joystick getPathsJoystick() {
        return paths;
    }

    @Override
    public boolean fastDrive() {
        return joy.getRawButton(1);
    }

    @Override
    public boolean quickTurn() {
        return wheel.getRawButton(7);
    }

    @Override
    public boolean ballRollerGrab() {
        return joy.getRawButton(7);
    }
    
    @Override
    public boolean isCargoMode() {
        return buttonPad.getRawButton(10);
    }

    @Override
    public boolean flipArm() {
        return buttonPad.getRawButton(9);
    }

    @Override
    public boolean armToInside() {
        return buttonPad.getRawButton(23);
    }

    @Override
    public boolean armToBallPickup() {
        return buttonPad.getRawButton(14);
    }

    @Override
    public boolean armToHatchPickup() {
        return buttonPad.getRawButton(20);//23
    }

    @Override
    public boolean armToHatchSecondLevel() {
        return buttonPad.getRawButton(17);
    }
    
    @Override
    public boolean armToHatchThirdLevel() {
        return buttonPad.getRawButton(13);
    }

    @Override
    public boolean climbUp() {
        return buttonPad.getRawButton(11);
    }

    @Override
    public boolean climbRetract() {
        return buttonPad.getRawButton(18);
    }

    @Override
    public boolean gripperShoot() {
        return coJoy.getRawButton(1);
    }

    @Override
    public boolean ballPistonGrab() {
        return ballPistonGrab.toggleVar(joy.getRawButton(2));
    }
}
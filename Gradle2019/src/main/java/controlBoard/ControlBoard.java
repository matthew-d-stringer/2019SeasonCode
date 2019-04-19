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
        // if(-coJoy.getY() == 1.0){
        //     out.setY(0);
        // }else{
            out.setY(-coJoy.getY());
        // }
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
    public boolean slowDrive() {
        // return joy.getRawButton(1);
        return false;
    }

    @Override
    public boolean visionDrive() {
        // return joy.getRawButton(7);
        return joy.getRawButton(1);
    }

    @Override
    public boolean visionDrivePressed() {
        return joy.getRawButtonPressed(7);
    }

    @Override
    public boolean visionLED() {
        return buttonPad.getRawButton(4);
    }

    @Override
    public boolean autoStop() {
        return buttonPad.getRawButton(14);
    }

    @Override
    public boolean climbMode() {
        return buttonPad.getRawButton(6);
    }

    @Override
    public boolean resetTelescope() {
        return buttonPad.getRawButton(22);
    }
    
    @Override
    public boolean disableTelescopeGripper() {
        return buttonPad.getRawButton(21);
    }

    @Override
    public boolean quickTurn() {
        return wheel.getRawButton(7);
    }

    @Override
    public boolean gripperGrab() {
        return joy.getRawButton(5);
    }

    @Override
    public boolean flipArm() {
        // return buttonPad.getRawButton(9);
        return false; //TODO: fix flipping arm
    }

    @Override
    public boolean armToInside() {
        return buttonPad.getRawButton(23);
    }

    @Override
    public boolean armToBallPickup() {
        return buttonPad.getRawButton(16);
    }

    @Override
    public boolean armToBallGoal() {
        return buttonPad.getRawButton(12);
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
    public boolean incrementOffset() {
        return coJoy.getPOV(0) == 0;
    }

    @Override
    public boolean decrementOffset() {
        return coJoy.getPOV(0) == 180;
    }

    @Override
    public boolean lowClimb() {
        return buttonPad.getRawButton(2);
    }

    @Override
    public boolean climbUp() {
        return buttonPad.getRawButton(11);
    }

    @Override
    public boolean climbForward() {
        return buttonPad.getRawButton(15);
    }

    @Override
    public boolean climbRetract() {
        return buttonPad.getRawButton(18);
    }

    @Override
    public boolean gripperShoot() {
        return /*coJoy.getRawButton(1) ||*/ joy.getRawButton(6);
    }

    @Override
    public boolean gripperShootPressed() {
        return /*coJoy.getRawButtonPressed(1) ||*/ joy.getRawButtonPressed(6);
    }

    @Override
    public boolean ballPistonGrab() {
        return ballPistonGrab.toggleVar(joy.getRawButton(2));
    }

    @Override
    public boolean cargoPivot() {
        return buttonPad.getRawButton(3);
    }

    @Override
    public boolean cargoGrab() {
        return !buttonPad.getRawButton(8);
    }

    @Override
    public boolean cargoShoot() {
        return coJoy.getRawButton(1);
    }
}
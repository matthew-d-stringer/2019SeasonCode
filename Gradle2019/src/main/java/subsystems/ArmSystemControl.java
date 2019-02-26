package subsystems;

import coordinates.Coordinate;
import coordinates.Heading;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import robot.Constants;
import utilPackage.Units;
import utilPackage.Util;

public class ArmSystemControl extends Thread{
    private static ArmSystemControl instance = null;
    public static ArmSystemControl getInstance(){
        if(instance == null){
            instance = new ArmSystemControl();
        }
        return instance;
    }

    public enum States{
        disabled,
        reset,
        running;
    }

    public static enum GripperMode{
        hatch,
        cargo,
        cargoPickup;
    }
    GripperMode gMode;

    MainArmControl arm;
    TelescopeControl telescope;
    GripperControl gripper;
    ClimberControl climber;
    States state;

    private ArmSystemControl(){
        gMode = GripperMode.hatch;
        arm = MainArmControl.getInstance();
        telescope = TelescopeControl.getInstance();
        gripper = GripperControl.getInstance();
        climber = ClimberControl.getInstance();
        state = States.disabled;
    }

    public void setArmPosition(Heading position){
        position.setX(Util.forceInRange(position.getX(), Constants.MainArm.minXVal, Constants.MainArm.maxXVal));
        arm.setHeading(position);
        telescope.setSetpoint(position.getMagnitude());
        // position.setAngle(arm.getSetpoint());
        // position.setMagnitude(telescope.getSetpoint());
    }

    public void setSetpoints(double armSetpoint, double telescopeSetpoint){
        arm.setSetpoint(armSetpoint);
        telescope.setSetpoint(telescopeSetpoint);
    }

    public void setGriperMode(GripperMode mode){
        gMode = mode;
    }

    @Override
    public void run() {
        while(true){
            arm.run();
            telescope.run();
            gripper.run();
            // climber.run();
            switch(state){
                case disabled:
                    if(RobotState.isEnabled()){
                        state = States.reset;
                    }
                    break;
                case reset:
                    if(arm.isRunning() && telescope.isRunning() && gripper.isRunning()){
                        state = States.running;
                    }
                    break;
                case running:
                    double armAngle = MainArm.getInstance().getAngle();
                    if(armAngle < Math.PI/2){
                        if(gMode == GripperMode.cargoPickup){
                            gripper.setSetpoint(-3*Math.PI/4 - armAngle);
                        }else if(gMode == GripperMode.hatch){
                            gripper.setSetpoint(-armAngle);
                        }else{
                            gripper.setSetpoint(-Math.PI/2 - armAngle);
                        }
                    }else{
                        if(gMode == GripperMode.cargoPickup){
                            gripper.setSetpoint(Math.PI - 3*Math.PI/4 - armAngle);
                        }else if(gMode == GripperMode.hatch){
                            gripper.setSetpoint(Math.PI - armAngle);
                        }else{
                            gripper.setSetpoint(Math.PI - Math.PI/2 - armAngle);
                        }
                    }
                    break;
            }
            Timer.delay(0.007);
        }
    }

    public boolean isDone(){
        boolean out = arm.inErrorRange(10*Units.Angle.degrees); 
        out &= arm.mpFinished();
        out &= telescope.inErrorRange(1*Units.Length.inches);
        return out;
    }
}
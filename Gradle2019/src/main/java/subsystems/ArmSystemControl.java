package subsystems;

import coordinates.Coordinate;
import coordinates.Heading;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;

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
        cargo;
    }
    GripperMode gMode;

    MainArmControl arm;
    TelescopeControl telescope;
    GripperControl gripper;
    States state;

    private ArmSystemControl(){
        gMode = GripperMode.hatch;
        arm = MainArmControl.getInstance();
        telescope = TelescopeControl.getInstance();
        gripper = GripperControl.getInstance();
        state = States.disabled;
    }

    public void setArmPosition(Heading position){
        arm.setHeading(position);
        telescope.setSetpoint(position.getMagnitude());
        position.setAngle(arm.getSetpoint());
        position.setMagnitude(telescope.getSetpoint());
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
                        if(gMode == GripperMode.hatch){
                            gripper.setSetpoint(-armAngle);
                        }else{
                            gripper.setSetpoint(-Math.PI/2 - armAngle);
                        }
                    }else{
                        if(gMode == GripperMode.hatch){
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
}
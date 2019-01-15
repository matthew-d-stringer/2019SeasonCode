package drive;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import coordinates.Coordinate;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Constants;
import utilPackage.Units;

public class DriveOutput extends Thread{
    private static DriveOutput instance = null;
    public static DriveOutput getInstance(){
        if(instance == null){
            instance = new DriveOutput();
        }
        return instance;
    }

    public static enum Modes{
        Voltage,
        Velocity;
    }

    private double rightSet, leftSet;

    private Modes mode = Modes.Voltage;

    private DriveController vController;

    private DriveOutput(){
        vController = DriveController.getInstance();
    }

    public void setMode(Modes mode) {
        this.mode = mode;
    }

    public void set(double right, double left){
        rightSet = right;
        leftSet = left;
    }

    public void set(Modes mode, double right, double left){
        this.mode = mode;
        rightSet = right;
        leftSet = left;
    }

    /**
     * Allows you to define a curvature and a velocity
     * @param kappa Curvature(1/radius)
     * @param vel forward velocity
     */
    public void setKin(double kappa, double vel){
        this.mode = Modes.Velocity;
        rightSet = vel*(1+Constants.robotWidth*kappa);
        leftSet = vel*(1-Constants.robotWidth*kappa);
    }

    /**
     * Allows you to define angular vel and translational vel
     * @param omega angular velocity
     * @param vel translational velocity
     */
    public void setTransformation(double omega, double vel){
        this.mode = Modes.Velocity;
        rightSet = vel + Constants.robotWidth*omega;
        leftSet = vel - Constants.robotWidth*omega;
    }

    /**
     * Outputs no voltage to the motors
     */
    public void setNoVoltage(){
        this.mode = Modes.Voltage;
        rightSet = 0;
        leftSet = 0;
    }

    /**
     * Set no velocity
     */
    public void setNoVelocity(){
        this.mode = Modes.Voltage;
        rightSet = 0;
        leftSet = 0;
    }

    private Drive mDrive = Drive.getInstance();

    @Override
    public void run() {
        while(true){
            if(RobotState.isOperatorControl())
                mDrive.brake(NeutralMode.Brake);
            else
                mDrive.brake(NeutralMode.Coast);
            switch(mode){
                case Voltage:
                    mDrive.outputToDrive(rightSet, leftSet);
                    break;
                case Velocity:
                    vController.setSetpoint(new Coordinate(rightSet, leftSet));
                    Coordinate cVels = new Coordinate(mDrive.getRightVel(), mDrive.getLeftVel());
                    // cVels = new Coordinate();
                    Coordinate out = vController.run(cVels);
                    mDrive.outputToDrive(out.getX(), out.getY());
                    break;
            }
            Timer.delay(0.005);
        }
    }

    public void display(){
        SmartDashboard.putNumber("Right Vel SI", mDrive.getRightVel());
        SmartDashboard.putNumber("Left Vel SI", mDrive.getLeftVel());
        Coordinate error = new Coordinate(vController.getError());
        error.mult(1/Units.Length.feet);
        SmartDashboard.putNumber("Right Error", error.getX());
        SmartDashboard.putNumber("Left Error", error.getY());
    }
}
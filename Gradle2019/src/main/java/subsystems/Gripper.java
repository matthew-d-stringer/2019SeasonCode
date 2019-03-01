package subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import coordinates.Coordinate;
import coordinates.Heading;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Constants;
import utilPackage.Units;
import utilPackage.Util;

public class Gripper{
    private static Gripper instance = null;
    public static Gripper getInstance(){
        if(instance == null)
            instance = new Gripper();
        return instance;
    }

    TalonSRX pivot, rollers;
    Coordinate senZero, senNinety;
    DoubleSolenoid hatch;
    Solenoid grip;
    DigitalInput reset;

    private Gripper(){
        pivot = new TalonSRX(Constants.Gripper.pivotNum);
        pivot.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        senZero = new Coordinate(Constants.Gripper.zeroDegVal, 0);
        senNinety = new Coordinate(Constants.Gripper.ninetyDegVal, Math.PI/2);

        rollers = Constants.Drive.rightEncoder;

        reset = new DigitalInput(Constants.Gripper.resetNum);

        hatch = new DoubleSolenoid(Constants.Gripper.hatchNums[0], Constants.Gripper.hatchNums[1]);
        grip = new Solenoid(Constants.Gripper.gripNum);
    }

    public void periodic(){
        SmartDashboard.putNumber("Raw Gripper Enc", pivot.getSelectedSensorPosition());
        SmartDashboard.putNumber("Rel Gripper Enc", getRelAngle()/Units.Angle.degrees);
        SmartDashboard.putNumber("Abs Gripper Enc", getAbsAngle()/Units.Angle.degrees);

        SmartDashboard.putBoolean("Gripper Reset", getReset());

        if(getReset())
            pivot.setSelectedSensorPosition(0);
    }

    public boolean getReset(){
        return !reset.get();
    }

    /**
     * Sets voltage to motor
     * @param voltage positive is up, negative is down
     */
    public void setVoltage(double voltage){
        // if(Constants.isCompBot)
            pivot.set(ControlMode.PercentOutput, -voltage/12);
        // else
        //     pivot.set(ControlMode.PercentOutput, -voltage/12);
    }

    public void hatchLock(){
        hatch.set(Value.kReverse);
    }

    public void hatchRelease(){
        hatch.set(Value.kForward);
    }

    public void rollerGrab(){
        rollers.set(ControlMode.PercentOutput, 1);
    }
    public void rollerShoot(){
        rollers.set(ControlMode.PercentOutput, -1);
    }
    public void rollerOff(){
        rollers.set(ControlMode.PercentOutput, 0);
    }

    public void grab(){
        grip.set(true);
    }
    public void unGrab(){
        grip.set(false);
    }

    /**
     * Calculates relative angle
     */
    public double getRelAngle(){
        return Util.mapRange(pivot.getSelectedSensorPosition(), senNinety, senZero);
    }
    /**
     * Calculates angle velocity of gripper 
     */
    public double getRelAngleVel(){
        return pivot.getSelectedSensorVelocity()*Util.slope(senNinety, senZero);
    }
    /**
     * Returns angle of gripper that is based off of the angle of the arm
     */
    public double getAbsAngle(){
        return MainArm.getInstance().getAngle() + getRelAngle();
    }
    /**
     * Returns the angle of the Center of mass of the gripper
     */
    public double getRelComAngle(){
        return getRelAngle() + Constants.Gripper.angleOffsetFromHatch;
    }
    /**
     * Returns the angle of the Center of mass of the gripper including the arm
     */
    public double getAbsComAngle(){
        return getRelComAngle() + MainArm.getInstance().getAngle();
    }
    //TODO: complete this function
    /**
     * Calculates the distance the center of mass is from the gripper
     */
    public double getComDist(){
        return Constants.Gripper.com;
    }
    /**
     * Calculates center of mass where the gripper pivot is the origin
     */
    public Coordinate getCom(){
        Heading out = new Heading(getAbsComAngle());
        out.setMagnitude(getComDist());
        return out;
    }
    /**
     * Calculates center of mass where the arm pivot is the origin
     */
    public Coordinate getAbsCom(){
        return getCom().add(Telescope.getInstance().getEndPos());
    }

    public double getAntigrav(){
        return 2.2247*getCom().getX();
    }
}
package subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import coordinates.Coordinate;
import coordinates.Heading;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Constants;
import utilPackage.Derivative;
import utilPackage.Units;
import utilPackage.Util;

public class Gripper{
    private static Gripper instance = null;
    public static Gripper getInstance(){
        if(instance == null)
            instance = new Gripper();
        return instance;
    }

    private TalonSRX pivot, rollers;
    private Coordinate senZero, senNinety;

    private Gripper(){
        pivot = new TalonSRX(Constants.Gripper.pivotNum);
        // pivot = Constants.Gripper.pivotTalon;
        // pivot.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
        pivot.configSelectedFeedbackSensor(FeedbackDevice.Analog);
        pivot.configFeedbackNotContinuous(false, 0);
        
        senZero = new Coordinate(Constants.Gripper.zeroDegVal, 0);
        senNinety = new Coordinate(Constants.Gripper.ninetyDegVal, Math.PI/2);


        rollers = Constants.Drive.rightEncoder;
        rollers.configPeakCurrentDuration(1000);
        rollers.configPeakCurrentLimit(40);
        rollers.enableCurrentLimit(true);
    }

    public void display(){
        SmartDashboard.putNumber("Raw Gripper Enc", pivot.getSelectedSensorPosition());
        SmartDashboard.putNumber("Raw Gripper Enc Vel", pivot.getSelectedSensorVelocity());
        SmartDashboard.putNumber("Rel Gripper Enc", getRelAngle()/Units.Angle.degrees);
        SmartDashboard.putNumber("Rel Gripper Enc Vel", getRelAngleVel()/Units.Angle.degrees);
        SmartDashboard.putNumber("Abs Gripper Enc", getAbsAngle()/Units.Angle.degrees);

        SmartDashboard.putNumber("Gripper Currrent", rollers.getOutputCurrent());
    }

    public void periodic(){
        // boolean reset = getReset();
        // if(reset != pReset && resetEnabled){
        //     pivot.setSelectedSensorPosition(0);
        //     hasReset = true;
        // }
        // // Timer.delay(0.02);
        // pReset = reset;
    }

    public void reset(){
        pivot.setSelectedSensorPosition(0);
    }

    /**
     * Sets voltage to motor
     * @param voltage positive is up, negative is down
     */
    public void setVoltage(double voltage){
        pivot.set(ControlMode.PercentOutput, voltage/12);
    }

    public double getCurrent(){
        return rollers.getOutputCurrent();
    }

    public void hatchGrab(){
        rollers.set(ControlMode.PercentOutput, 1);
    }

    public void hatchRelease(){
        rollers.set(ControlMode.PercentOutput, -1);
    }

    public void hatchHold(){
        rollers.set(ControlMode.PercentOutput, 0.125);
    }

    public void rollerOff(){
        rollers.set(ControlMode.PercentOutput, 0);
    }

    /**
     * Calculates relative angle
     */
    public double getRelAngle(){
        return Util.mapRange(pivot.getSelectedSensorPosition(), senNinety, senZero);
    }

    public double getRawEnc(){
        return pivot.getSelectedSensorPosition();
    }

    public double getEncoderConv(){
        return Util.slope(senNinety, senZero);
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
        return Constants.Gripper.comLength;
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
        // return 2.3134*getCom().getX();
        return 2.0202*getCom().getX();
    }
}
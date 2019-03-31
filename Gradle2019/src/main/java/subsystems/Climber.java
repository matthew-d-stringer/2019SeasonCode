package subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import coordinates.Coordinate;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Constants;
import utilPackage.Util;

public class Climber{
    private static Climber instance;
    public static Climber getInstance(){
        if(instance == null){
            instance = new Climber();
        }
        return instance;
    }

    TalonSRX climbMotor;
    Coordinate conv1, conv2;

    private Climber(){
        climbMotor = new TalonSRX(Constants.Climber.climbNum);
        climbMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
        // climbMotor.configPeakCurrentLimit(38);
        // climbMotor.enableCurrentLimit(true);
        conv1 = new Coordinate(Constants.Climber.topVal, Constants.Climber.topLen);
        conv2 = new Coordinate(Constants.Climber.bottomVal, Constants.Climber.bottomLen);
        SmartDashboard.putBoolean("Climber Reset", false);
    }

    public void periodic(){
        SmartDashboard.putNumber("Climber Raw sensor", climbMotor.getSelectedSensorPosition(0));
        SmartDashboard.putNumber("Climber Adjusted sensor", getClimbLen());
        SmartDashboard.putNumber("Climber Antigrav", getAntigrav());
        SmartDashboard.putNumber("Climber Current", climbMotor.getOutputCurrent());
        if(SmartDashboard.getBoolean("Climber Reset", false)){
            reset();
            SmartDashboard.putBoolean("Climber Reset", false);
        }
    }

    public double getAntigrav(){
        return -1.5;
    }

    public double getCurrent(){
        return climbMotor.getOutputCurrent();
    }

    public double getClimbLen(){
        return Util.mapRange(climbMotor.getSelectedSensorPosition(), conv1, conv2);
    }

    /**
     * Sets voltage to climb motor
     * @param voltage positive is retracts, (brings robot down), and vice versa
     */
    public void setVoltage(double voltage){
        climbMotor.set(ControlMode.PercentOutput, voltage/12);
    }

    public void reset(){
        climbMotor.setSelectedSensorPosition(0);
    }
}
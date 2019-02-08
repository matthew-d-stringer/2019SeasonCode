package drive;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.*;
import utilPackage.Units;
import utilPackage.Util;

import java.util.Arrays;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Drive {
    private static Drive instance = null;
    public static Drive getInstance(){
        if(instance == null)
            instance = new Drive();
        return instance;
    }

    CANSparkMax mLeft, mRight;
	CANSparkMax[] slaves = new CANSparkMax[4];
	TalonSRX leftEncoder, rightEncoder;
    private Drive(){
        //set up drive
        mLeft = new CANSparkMax(Constants.Drive.MLeftNum, MotorType.kBrushless);
		mRight = new CANSparkMax(Constants.Drive.MRightNum, MotorType.kBrushless);
        for(int i = 0; i < 4; i++){
			slaves[i] = new CANSparkMax(Constants.Drive.slaveNums[i], MotorType.kBrushless);
            if(i < 2){
				slaves[i].follow(mLeft);
			}else{
				slaves[i].follow(mRight);
			}
		}
		leftEncoder = Constants.Drive.leftEncoder;
		leftEncoder.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
		rightEncoder = Constants.Drive.rightEncoder;
		rightEncoder.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
	}

	public void display(){
		String data = "Voltage";
		SmartDashboard.putNumber("Master Left "+data, mLeft.getAppliedOutput());
		SmartDashboard.putNumber("Master Right "+data, mRight.getAppliedOutput());
		for(int i =  0; i < 4; i++){
			SmartDashboard.putNumber("Slave #"+i+" "+data, slaves[i].getAppliedOutput());
		}

		SmartDashboard.putNumber("Encoder Pos", getEnc()[0]);
		SmartDashboard.putNumber("Encoder Vel", getEnc()[1]);

		SmartDashboard.putNumber("Left Encoder Pos", getLeftPosition());
		SmartDashboard.putNumber("Right Encoder Pos", getRightPosition());
	}
	
    /**
     * Output puts a voltage to the drive
     * @param output X: right voltage, Y: left voltage
     */
    public void outputToDrive(double rightVoltage, double leftVoltage){
		mRight.set(rightVoltage/12);
		mLeft.set(-leftVoltage/12);
    }

	/**
	 * Gets encoder velocity and position
	 * @return [0] = position, [1] = velocity
	 */
	public double[]	getEnc(){
		double position = Util.average(Arrays.asList(getLeftPosition(), getRightPosition()));
		double velocity = Util.average(Arrays.asList(getLeftVel(), getRightVel()));
		double[] out = {position, velocity};
		return out;
	}

	public double getLeftPosition(){
		if(Constants.isCompBot)
			return -leftEncoder.getSelectedSensorPosition()*Units.Angle.encoderTicks*Units.Length.radians;
		else 
			return leftEncoder.getSelectedSensorPosition()*Units.Angle.encoderTicks*Units.Length.radians;
	}

	public double getRightPosition(){
		if(Constants.isCompBot)
			return rightEncoder.getSelectedSensorPosition()*Units.Angle.encoderTicks*Units.Length.radians;
		else 
			return -rightEncoder.getSelectedSensorPosition()*Units.Angle.encoderTicks*Units.Length.radians;
	}

	/**
	 * Returns Left Velocity
	 */
	public double getLeftVel(){
		double out = leftEncoder.getSelectedSensorVelocity()*
				Units.Angle.encoderTicks*Units.Length.radians/(0.1*Units.Time.seconds);
		if(Constants.isCompBot)
			out *= -1;
		return out;
	}

	/**
	 * Returns Right Velocity
	 */
	public double getRightVel(){
		double out = -rightEncoder.getSelectedSensorVelocity()*
				Units.Angle.encoderTicks*Units.Length.radians/(0.1*Units.Time.seconds);
		if(Constants.isCompBot)
			out *= -1;
		return out;
	}

	public void brake(IdleMode mode){
		mLeft.setIdleMode(mode);
		mRight.setIdleMode(mode);
		for(int i = 0; i < 4; i++){
			slaves[i].setIdleMode(mode);
		}
	}

}

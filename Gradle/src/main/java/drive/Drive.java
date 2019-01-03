package drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.*;
import utilPackage.Units;
import utilPackage.Util;

import java.util.Arrays;

public class Drive {
    private static Drive instance = null;
    public static Drive getInstance(){
        if(instance == null)
            instance = new Drive();
        return instance;
    }

    TalonSRX mLeft, mRight;
    TalonSRX[] slaves = new TalonSRX[4];
    private Drive(){
        //set up drive
        mLeft = new TalonSRX(Constants.Drive.MLeftNum);
        mRight = new TalonSRX(Constants.Drive.MRightNum);
        for(int i = 0; i < 4; i++){
            slaves[i] = new TalonSRX(Constants.Drive.slaveNums[i]);
            if(i < 2){
				slaves[i].set(ControlMode.Follower, mLeft.getDeviceID());
				slaves[i].setInverted(false);
			}else{
				slaves[i].set(ControlMode.Follower, mRight.getDeviceID());
				slaves[i].setInverted(false);
			}
		}
		mLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
		mRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
	}

	public void display(){
		String data = "Voltage";
		SmartDashboard.putNumber("Master Left "+data, mLeft.getMotorOutputVoltage());
		SmartDashboard.putNumber("Master Right "+data, mRight.getMotorOutputVoltage());
		for(int i =  0; i < 4; i++){
			SmartDashboard.putNumber("Slave #"+i+" "+data, slaves[i].getMotorOutputVoltage());
		}
	}
	
    /**
     * Output puts a voltage to the drive
     * @param output X: right voltage, Y: left voltage
     */
    public void outputToDrive(double rightVoltage, double leftVoltage){
    	mRight.set(ControlMode.PercentOutput, -rightVoltage/12);
        mLeft.set(ControlMode.PercentOutput, leftVoltage/12);
    }

	/**
	 * Gets encoder velocity and position
	 * @return [0] = position, [1] = velocity
	 */
	public double[]	getEnc(){
		double position = (-(mLeft.getSelectedSensorPosition(0)+mRight.getSelectedSensorPosition(0))/2)*
				Units.Angle.encoderTicks*Units.Length.radians;
		double velocity = Util.average(Arrays.asList(getLeftVel(), getRightVel()));
		double[] out = {position, velocity};
		return out;
	}

	/**
	 * Returns Left Velocity
	 */
	public double getLeftVel(){
		return -mLeft.getSelectedSensorVelocity(0)*
				Units.Angle.encoderTicks*Units.Length.radians/(0.1*Units.Time.seconds);
	}

	/**
	 * Returns Right Velocity
	 */
	public double getRightVel(){
		return mRight.getSelectedSensorVelocity(0)*
				Units.Angle.encoderTicks*Units.Length.radians/(0.1*Units.Time.seconds);
	}

	public void brake(NeutralMode mode){
		mLeft.setNeutralMode(mode);
		mRight.setNeutralMode(mode);
	}

}

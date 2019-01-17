package drive;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.*;
import utilPackage.Units;
import utilPackage.Util;

import java.util.Arrays;

import com.revrobotics.CANEncoder;
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
	CANEncoder encoderLeft, encoderRight;
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
		encoderLeft = new CANEncoder(mLeft);
		encoderRight = new CANEncoder(mRight);
	}

	public void display(){
		String data = "Voltage";
		SmartDashboard.putNumber("Master Left "+data, mLeft.getAppliedOutput());
		SmartDashboard.putNumber("Master Right "+data, mRight.getAppliedOutput());
		for(int i =  0; i < 4; i++){
			SmartDashboard.putNumber("Slave #"+i+" "+data, slaves[i].getAppliedOutput());
		}
	}
	
    /**
     * Output puts a voltage to the drive
     * @param output X: right voltage, Y: left voltage
     */
    public void outputToDrive(double rightVoltage, double leftVoltage){
    	mRight.set(-rightVoltage/12);
        mLeft.set(leftVoltage/12);
    }

	/**
	 * Gets encoder velocity and position
	 * @return [0] = position, [1] = velocity
	 */
	public double[]	getEnc(){
		// double position = (-(mLeft.getSelectedSensorPosition(0)+mRight.getSelectedSensorPosition(0))/2)*
		// 		Units.Angle.encoderTicks*Units.Length.radians;
		double position = (-(encoderLeft.getPosition()+encoderRight.getPosition())/2)*
				Units.Angle.encoderTicks*Units.Length.radians;
		double velocity = Util.average(Arrays.asList(getLeftVel(), getRightVel()));
		double[] out = {position, velocity};
		return out;
	}

	/**
	 * Returns Left Velocity
	 */
	public double getLeftVel(){
		return -encoderLeft.getVelocity()*
				Units.Angle.encoderTicks*Units.Length.radians/(1*Units.Time.minutes);
	}

	/**
	 * Returns Right Velocity
	 */
	public double getRightVel(){
		return encoderRight.getVelocity()*
				Units.Angle.encoderTicks*Units.Length.radians/(1*Units.Time.minutes);
	}

	public void brake(IdleMode mode){
		mLeft.setIdleMode(mode);
		mRight.setIdleMode(mode);
		for(int i = 0; i < 4; i++){
			slaves[i].setIdleMode(mode);
		}
	}

}

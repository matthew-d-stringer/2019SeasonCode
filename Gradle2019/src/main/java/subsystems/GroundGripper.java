package subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import coordinates.Coordinate;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Constants;
import utilPackage.Util;

public class GroundGripper{
    private static GroundGripper instance;
    public static GroundGripper getInstance(){
        if(instance == null){
            instance = new GroundGripper();
        }
        return instance;
    }

    TalonSRX pivot, rollers, encoder;
    DigitalInput reset;

    Coordinate encConv0, encConv90;

    private GroundGripper(){
        pivot = new TalonSRX(Constants.GroundGripper.pivotNum);
        pivot.configFactoryDefault();
        pivot.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
        rollers = new TalonSRX(Constants.GroundGripper.rollersNum);
        rollers.configFactoryDefault();
        encoder = Constants.GroundGripper.pivotEncoder;
        reset = new DigitalInput(Constants.GroundGripper.resetNum);

        encConv0 = new Coordinate(Constants.GroundGripper.encAt0, 0);
        encConv90 = new Coordinate(Constants.GroundGripper.encAt90, Math.PI/2);

        SmartDashboard.putBoolean("Manual Reset", false);
    }
    
    public void periodic(){
        if(getReset() && SmartDashboard.getBoolean("Manual Reset", false)){
            // pivot.setSelectedSensorPosition(0);
            encoder.setSelectedSensorPosition(0);
        }
    }

    public void display(){
        SmartDashboard.putBoolean("Ground Gripper Reset", getReset());
        SmartDashboard.putNumber("Ground Gripper Raw Enc", encoder.getSelectedSensorPosition());
        SmartDashboard.putNumber("Ground Gripper Angle", getAngle());
    }

    public boolean getReset(){
        return !reset.get();
    }

    public double getAngle(){
        return Util.mapRange(encoder.getSelectedSensorPosition(), encConv0, encConv90);
    }

    public double getAngleVel(){
        return Util.slope(encConv0, encConv90)*encoder.getSelectedSensorVelocity()/0.1;
    }

    public double getAntigrav(){
        return 0.50155*Math.cos(getAngle()-Constants.GroundGripper.comOffsetAngle)+0.021256*getAngleVel();
    }

    public double getClimbAntigrav(){
        return -6;
    }

    public void setVoltage(double volts){
        //TODO: correct the direction
        encoder.set(ControlMode.PercentOutput, volts/12);
    }

    public void rollersGrab(){
        //TODO: correct the direction
        rollers.set(ControlMode.PercentOutput, 1);
    }

    public void rollersClimb(){
        //TODO: correct the direction
        rollers.set(ControlMode.PercentOutput, 1);
    }

    public void rollersOff(){
        //TODO: correct the direction
        rollers.set(ControlMode.PercentOutput, 0);
    }
}
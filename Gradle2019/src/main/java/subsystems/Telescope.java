package subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import coordinates.Coordinate;
import coordinates.Heading;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Constants;
import utilPackage.Units;
import utilPackage.Util;

public class Telescope{
    private static Telescope instance = null;
    public static Telescope getInstance(){
        if(instance == null)
            instance = new Telescope();
        return instance;
    }

    TalonSRX telescope;
    Coordinate senRetract, senExtend;
    DigitalInput reset;
    private Telescope(){
        telescope = new TalonSRX(Constants.Telescope.telescopeNum);
        telescope.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
        telescope.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_5Ms);

        senRetract = new Coordinate(Constants.Telescope.retractVal, Constants.Telescope.lenRetract);
        senExtend = new Coordinate(Constants.Telescope.extendVal, Constants.Telescope.lenExtend);

        reset = new DigitalInput(Constants.Telescope.resetNum);
    }

    public void periodic(){
        // SmartDashboard.putNumber("Raw Telescope Enc", telescope.getSelectedSensorPosition(0));
        // SmartDashboard.putNumber("Telescope Distance", getDistance());
        // SmartDashboard.putNumber("Telescope Antigrav", getAntigrav());
        // SmartDashboard.putString("Main Arm Endpoint", getEndPos().display());
        
        // SmartDashboard.putBoolean("Telescope reset", getReset());

        if(getReset())
            telescope.setSelectedSensorPosition(0);
    }

    public boolean getReset(){
        return !reset.get();
    }

    public void setVoltage(double voltage){
        SmartDashboard.putNumber("Telescope voltage out", voltage);
        telescope.set(ControlMode.PercentOutput, voltage/12);
    }

    public double getAntigrav(){
        return 0.51901*Math.sin(MainArm.getInstance().getAngle());
    }

    public double getDistance(){
        //TODO: undo this
        return Util.mapRange(telescope.getSelectedSensorPosition(0), senRetract, senExtend);
        // return senRetract.getY();
    }
    public double getVel(){
        return Util.slope(senRetract, senExtend)*telescope.getSelectedSensorVelocity()/(0.1);
    }

    public Coordinate getEndPos(){
        Heading out = new Heading(MainArm.getInstance().getAngle());
        out.setMagnitude(getDistance());
        return out;
    }
}
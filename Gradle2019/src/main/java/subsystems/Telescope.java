package subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import coordinates.Coordinate;
import coordinates.Heading;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Constants;
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

        senRetract = new Coordinate(Constants.Telescope.retractVal, Constants.Telescope.lenRetract);
        senExtend = new Coordinate(Constants.Telescope.extendVal, Constants.Telescope.lenExtend);

        reset = new DigitalInput(Constants.Telescope.resetNum);
    }

    public void periodic(){
        SmartDashboard.putNumber("Raw Telescope Enc", telescope.getSelectedSensorPosition());
        SmartDashboard.putNumber("Telescope Enc", getDistance());
        SmartDashboard.putString("Main Arm Endpoint", getEndPos().display());
        
        SmartDashboard.putBoolean("Telescope reset", getReset());

        if(getReset())
            telescope.setSelectedSensorPosition(0);
    }

    public boolean getReset(){
        return reset.get();
    }

    public double getDistance(){
        //TODO: undo this
        // return Util.mapRange(telescope.getSelectedSensorPosition(), senRetract, senExtend);
        return senRetract.getY();
    }

    public Coordinate getEndPos(){
        Heading out = new Heading(MainArm.getInstance().getAngle());
        out.setMagnitude(getDistance());
        return out;
    }
}
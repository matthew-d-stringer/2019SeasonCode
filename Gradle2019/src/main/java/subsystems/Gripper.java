package subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import coordinates.Coordinate;
import coordinates.Heading;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Constants;
import utilPackage.Util;

public class Gripper{
    private static Gripper instance = null;
    public static Gripper getInstance(){
        if(instance == null)
            instance = new Gripper();
        return instance;
    }

    TalonSRX pivot;
    Coordinate senZero, senNinety;
    DigitalInput reset;

    private Gripper(){
        pivot = new TalonSRX(Constants.Gripper.pivotNum);
        pivot.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
        senZero = new Coordinate(Constants.Gripper.zeroDegVal, 0);
        senNinety = new Coordinate(Constants.Gripper.zeroDegVal, Math.PI/2);
        reset = new DigitalInput(Constants.Gripper.resetNum);
    }

    public void periodic(){
        SmartDashboard.putNumber("Raw Gripper Enc", pivot.getSelectedSensorPosition());
        SmartDashboard.putNumber("Rel Gripper Enc", getRelAngle());
        SmartDashboard.putNumber("Abs Gripper Enc", getAbsAngle());

        SmartDashboard.putBoolean("Gripper Reset", getReset());

        if(getReset())
            pivot.setSelectedSensorPosition(0);
    }

    public boolean getReset(){
        return reset.get();
    }

    public double getRelAngle(){
        return Util.mapRange(pivot.getSelectedSensorPosition(), senZero, senNinety);
    }
    public double getAbsAngle(){
        return MainArm.getInstance().getAngle() + getRelAngle();
    }
    //TODO: complete this function
    public double getComDist(){
        return Constants.Gripper.comClosed;
    }
    public Coordinate getCom(){
        Heading out = new Heading(getAbsAngle());
        out.setMagnitude(getComDist());
        return out;
    }
    public Coordinate getAbsCom(){
        return getCom().add(Telescope.getInstance().getEndPos());
    }
}
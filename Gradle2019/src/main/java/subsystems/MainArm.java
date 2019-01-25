package subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import coordinates.Coordinate;
import coordinates.Heading;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Constants;
import utilPackage.Util;

public class MainArm{
    private static MainArm instance = null;
    public static MainArm getInstance(){
        if(instance == null)
            instance = new MainArm();
        return instance;
    }
    TalonSRX pivot;
    DigitalInput reset;
    Coordinate senZero, senNinety;
    Coordinate comRetract, comExtend;

    private MainArm(){
        pivot = new TalonSRX(Constants.MainArm.pivotNum);
        pivot.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);

        senZero = new Coordinate(Constants.MainArm.zeroDegVal, 0);
        senNinety = new Coordinate(Constants.MainArm.ninetyDegVal, Math.PI/2);

        comRetract = new Coordinate(Constants.Telescope.lenRetract, Constants.Telescope.comRetract);
        comExtend = new Coordinate(Constants.Telescope.lenExtend, Constants.Telescope.comExtend);

        reset = new DigitalInput(Constants.MainArm.resetNum);
    }

    public void periodic(){
        SmartDashboard.putNumber("Raw Arm Enc", pivot.getSelectedSensorPosition());
        SmartDashboard.putNumber("Arm Enc", getAngle());

        SmartDashboard.putBoolean("Arm Reset", getReset());
        if(getReset())
            pivot.setSelectedSensorPosition(0);
    }

    public boolean getReset(){
        return reset.get();
    }

    public double getAngle(){
        return Util.mapRange(pivot.getSelectedSensorPosition(), senZero, senNinety);
    }
    public double getComDist(){
        return Util.mapRange(Telescope.getInstance().getDistance(), comRetract, comExtend);
    }
    public Coordinate getComWithoutGripper(){
        Heading cHeading = new Heading(getAngle());
        cHeading.setMagnitude(getComDist());
        return cHeading;
    }
    public Coordinate getCom(){
        Coordinate comofArm = getComWithoutGripper();
        Coordinate comofGrip = Gripper.getInstance().getAbsCom();
        Coordinate com = comofArm.multC(Constants.MainArm.mass).addC(comofGrip.multC(Constants.Gripper.mass));
        com.mult(1/(Constants.MainArm.mass + Constants.Gripper.mass));
        return com;
    }
}
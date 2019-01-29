package subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import coordinates.Coordinate;
import coordinates.Heading;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Constants;
import utilPackage.Units;
import utilPackage.Util;

public class MainArm{
    private static MainArm instance = null;
    public static MainArm getInstance(){
        if(instance == null)
            instance = new MainArm();
        return instance;
    }
    TalonSRX pivot;
    Coordinate senZero, senNinety;
    Coordinate comRetract, comExtend;

    private MainArm(){
        pivot = new TalonSRX(Constants.MainArm.pivotNum);
        pivot.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);

        senZero = new Coordinate(Constants.MainArm.zeroDegVal, 0);
        senNinety = new Coordinate(Constants.MainArm.ninetyDegVal, Math.PI/2);

        comRetract = new Coordinate(Constants.Telescope.lenRetract, Constants.Telescope.comRetract);
        comExtend = new Coordinate(Constants.Telescope.lenExtend, Constants.Telescope.comExtend);

    }

    public void periodic(){
        SmartDashboard.putNumber("Raw Arm Enc", pivot.getSelectedSensorPosition());
        SmartDashboard.putNumber("Arm Enc", Units.convertUnits(getAngle(), Units.Angle.degrees));
        SmartDashboard.putNumber("Arm Antigrav", getAntigrav());

        if(SmartDashboard.getBoolean("Arm Reset", false))
            pivot.setSelectedSensorPosition(0);
        SmartDashboard.putBoolean("Arm Reset", false);
    }

    public void setVoltage(double voltage){
        pivot.set(ControlMode.PercentOutput, -voltage/12);
    }

    public double getAngle(){
        return Util.mapRange(pivot.getSelectedSensorPosition(), senZero, senNinety);
    }

    public double getAntigrav(){
        return 0.32076*getComWithoutGripper().normalizeC().getX();
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
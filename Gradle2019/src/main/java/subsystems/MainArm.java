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

public class MainArm{
    private static MainArm instance = null;
    public static MainArm getInstance(){
        if(instance == null)
            instance = new MainArm();
        return instance;
    }
    TalonSRX pivot, pivotSlave;
    Coordinate senZero, senNinety;
    Coordinate comRetract, comExtend;
    DigitalInput reset;

    private boolean disable = false;

    private MainArm(){
        pivot = new TalonSRX(Constants.MainArm.pivotNum);
        pivot.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
        pivot.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_1Ms);
        pivotSlave = new TalonSRX(Constants.MainArm.slaveNum);
        pivotSlave.set(ControlMode.Follower, pivot.getDeviceID());
        reset = new DigitalInput(Constants.MainArm.resetNum);

        senZero = new Coordinate(Constants.MainArm.zeroDegVal, 0);
        senNinety = new Coordinate(Constants.MainArm.ninetyDegVal, Math.PI/2);

        comRetract = new Coordinate(Constants.Telescope.lenRetract, Constants.Telescope.comRetract);
        comExtend = new Coordinate(Constants.Telescope.lenExtend, Constants.Telescope.comExtend);

        SmartDashboard.putBoolean("Arm Reset", false);
    }

    public void periodic(){
        SmartDashboard.putNumber("Raw Arm Enc", pivot.getSelectedSensorPosition());
        SmartDashboard.putNumber("Arm Enc", Units.convertUnits(getAngle(), Units.Angle.degrees));
        SmartDashboard.putNumber("Arm Enc Vel", Units.convertUnits(getAngleVel(), Units.Angle.degrees));
        SmartDashboard.putNumber("Arm Antigrav", getAntigrav());
        SmartDashboard.putString("Arm end pos(inches)", Telescope.getInstance().getEndPos().multC(1/Units.Length.inches).display());

        SmartDashboard.putNumber("Arm Master current", pivot.getOutputCurrent());
        SmartDashboard.putNumber("Arm Slave current", pivotSlave.getOutputCurrent());

        SmartDashboard.putBoolean("Arm Reset", getReset());
        if(getReset()){
            pivot.setSelectedSensorPosition(0);
        }
    }

    public void disable(boolean disable){
        this.disable = disable;
    }

    public boolean getReset(){
        return !reset.get();
    }

    public void setVoltage(double voltage){
        if(disable){
            voltage = 0;
        }
        // voltage = Util.forceInRange(voltage, -4, 4);
        pivot.set(ControlMode.PercentOutput, -voltage/12);
    }

    public double getFeedForward(double vel, double acc){
        return 0.056613*acc + 0.026452*vel;
    }

    public double getAngle(){
        return Util.mapRange(pivot.getSelectedSensorPosition(), senZero, senNinety);
    }
    public double getAngleVel(){
        return pivot.getSelectedSensorVelocity()*Util.slope(senZero, senNinety)/0.1;
    }

    public double getAntigrav(){
        // return 1.2*getComWithoutGripper().normalizeC().getX();
        return 1.8607*getCom().getX()+0.026452*getAngleVel();
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

    public Coordinate adjustToArm(Coordinate pos){
        double mag = pos.getMagnitude();
        mag = Util.forceInRange(mag, Constants.Telescope.lenRetract, Constants.Telescope.lenExtend);
        pos.setMagnitude(mag);
        return pos;
    }
}
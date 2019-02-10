package subsystems;

import coordinates.Heading;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import utilPackage.Derivative;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;
import utilPackage.Util;

public class MainArmControl{
    private static MainArmControl instance = null;
    public static MainArmControl getInstance(){
        if(instance == null)
            instance = new MainArmControl();
        return instance;
    }

    private enum States{
        disabled,
        reset,
        running;
    }

    States state = States.disabled;
    double setpoint = 0;
    MainArm arm;
    Derivative dError;
    double mpMaxVel = 1*Units.Angle.revolutions;
    double mpMaxAcc = 1*Units.Angle.revolutions;
    volatile TrapezoidalMp mp;
    Timer time = new Timer();
    double mpStartTime, mpStartAngle;
    boolean wasEnabled = false;

    private MainArmControl(){
        arm = MainArm.getInstance();
        mpStartAngle = arm.getAngle();
        mp = new TrapezoidalMp(mpStartAngle, new TrapezoidalMp.constraints(setpoint, mpMaxVel, mpMaxAcc));
    }

    public void setSetpoint(double set){
        set = Math.max(set, -90*Units.Angle.degrees);
        set = Math.min(set, 235*Units.Angle.degrees);
        if(!Util.inErrorRange(set, setpoint, 5*Units.Angle.degrees)){
            mpStartTime = time.get();
            mpStartAngle = arm.getAngle();
        }
        setpoint = set;
        // mp = new TrapezoidalMp(mpStartAngle, new TrapezoidalMp.constraints(setpoint, mpMaxVel, mpMaxAcc));
        mp.updateConstraints(mpStartAngle, new TrapezoidalMp.constraints(set-mpStartAngle, mpMaxVel, mpMaxAcc));
    }

    public double getSetpoint(){
        return setpoint;
    }

    public void setHeading(Heading set){
        double tSet = set.getAngle();
        if(tSet < -90*Units.Angle.degrees){
            tSet = 2*Math.PI - tSet;
        }
        tSet = Math.max(tSet, -90*Units.Angle.degrees);
        tSet = Math.min(tSet, 235*Units.Angle.degrees);
        if(!Util.inErrorRange(tSet, setpoint, 5*Units.Angle.degrees)){
            mpStartTime = time.get();
            mpStartAngle = arm.getAngle();
        }
        setpoint = tSet;
        mp.updateConstraints(mpStartAngle, new TrapezoidalMp.constraints(tSet-mpStartAngle, mpMaxVel, mpMaxAcc));
    }

    public boolean isRunning(){
        return state == States.running;
    }

    public void run() {
        // mp = new TrapezoidalMp(mpStartAngle, new TrapezoidalMp.constraints(setpoint, mpMaxVel, mpMaxAcc));
        // mp.updateConstraints(mpStartAngle, new TrapezoidalMp.constraints(setpoint, mpMaxVel, mpMaxAcc));
        switch(state){
            case disabled:
                if(RobotState.isEnabled()){
                    setpoint = arm.getAngle();
                    dError = new Derivative();
                    time.start();
                    state = States.running;
                    mpStartTime = time.get();
                    mpStartAngle = arm.getAngle();
                }
                break;
            case reset:
                break;
            case running:
                if(RobotState.isEnabled() && !wasEnabled){
                    time.start();
                }
                wasEnabled = RobotState.isEnabled();

                double tempSetpoint = mp.Calculate(time.get() - mpStartTime)[0];
                double feedForward = arm.getAntigrav();
                // double error = setpoint - arm.getAngle();
                double error = tempSetpoint - arm.getAngle();
                // dError.Calculate(error, time.get());
                double dError = -arm.getAngleVel();
                double p = 13.3109;
                double d = 1.4268;
                // double feedBack = p*error + d*dError.getOut();
                double feedBack = p*error + d*dError;
                if(setpoint < arm.getAngle() && arm.getAngle() < Math.PI/2){
                    feedBack = Math.min(feedBack, 1+arm.getAntigrav()); //max up
                    feedBack = Math.max(feedBack, -3); //max down
                }else if(setpoint > arm.getAngle() && arm.getAngle() > Math.PI/2){
                    feedBack = Math.max(feedBack, 1+arm.getAntigrav()); //max up
                    feedBack = Math.min(feedBack, 7+arm.getAngle()); //max down
                }
                // SmartDashboard.putNumber("Arm Setpoint", mp.getConstraints().setpoint/Units.Angle.degrees);
                // SmartDashboard.putNumber("Arm Temp Setpoint", tempSetpoint/Units.Angle.degrees);
                // SmartDashboard.putNumber("Arm Error", error/Units.Angle.degrees);
                // arm.setVoltage(feedForward);
                arm.setVoltage(feedForward+feedBack);
                break;
        }
    }

    public boolean mpFinished(){
        return time.get() - mpStartTime >= mp.getEndTime();
    }
}
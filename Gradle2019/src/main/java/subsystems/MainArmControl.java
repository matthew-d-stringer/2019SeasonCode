package subsystems;

import coordinates.Coordinate;
import coordinates.Heading;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Constants;
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
    double mpMaxVel = 1*Units.Angle.revolutions;
    double mpMaxAcc = 1*Units.Angle.revolutions;
    double mpAcc = mpMaxAcc;
    Coordinate mpAccCalc1 = new Coordinate(10*Units.Angle.degrees, mpMaxAcc);
    Coordinate mpAccCalc2 = new Coordinate(180*Units.Angle.degrees, 0.25*Units.Angle.revolutions);
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
            mpAcc = calculateAcc(set, mpStartAngle, mpMaxAcc);
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
            mpAcc = calculateAcc(tSet, mpStartAngle, mpMaxAcc);
        }
        setpoint = tSet;
        mp.updateConstraints(mpStartAngle, new TrapezoidalMp.constraints(tSet-mpStartAngle, mpMaxVel, mpMaxAcc));
    }

    private double calculateAcc(double setpoint, double angle, double maxAcc){
        double dist = Math.abs(setpoint - angle);
        if(!Util.inErrorRange(dist, 0, 10*Units.Angle.degrees)){
            return Util.mapRange(dist, mpAccCalc1, mpAccCalc2);
        }else{
            return maxAcc;
        }
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
                    state = States.reset;
                }
                break;
            case reset:
                TelescopeControl telescope = TelescopeControl.getInstance();
                if(telescope.isRunning() && Telescope.getInstance().getDistance() < Constants.Telescope.lenRetract + 0.02){
                    setpoint = arm.getAngle();
                    time.start();
                    mpStartTime = time.get();
                    mpStartAngle = arm.getAngle();
                    state = States.running;
                }
                break;
            case running:
                if(RobotState.isEnabled() && !wasEnabled){
                    time.start();
                }
                wasEnabled = RobotState.isEnabled();

                double mpSetpoint = mp.Calculate(time.get() - mpStartTime)[0];
                double feedForward = arm.getAntigrav();
                // double error = setpoint - arm.getAngle();
                double error = mpSetpoint - arm.getAngle();
                double dError = -arm.getAngleVel();
                double p = 13.4358;
                double d = 1.5713;
                // double feedBack = p*error + d*dError.getOut();
                double feedBack = p*error + d*dError;
                //If going down in front of bot
                if(setpoint < arm.getAngle() && arm.getAngle() < Math.PI/2){
                    feedBack = Math.min(feedBack, 1+arm.getAntigrav()); //max up
                    feedBack = Math.max(feedBack, -2); //max down
                //If going down in back of bot
                }else if(setpoint > arm.getAngle() && arm.getAngle() > Math.PI/2){
                    feedBack = Math.max(feedBack, 1+arm.getAntigrav()); //max up
                    feedBack = Math.min(feedBack, 4+arm.getAngle()); //max down
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

    public boolean inErrorRange(double range){
        if(isRunning())
            return Util.inErrorRange(setpoint, arm.getAngle(), range);
        else
            return false;
    }
}
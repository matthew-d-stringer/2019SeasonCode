package subsystems;

import org.apache.commons.math3.filter.DefaultProcessModel;
import org.apache.commons.math3.filter.KalmanFilter;
import org.apache.commons.math3.filter.ProcessModel;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import coordinates.Coordinate;
import coordinates.Heading;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import robot.Constants;
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
    double mpDefaultMaxVel = 1*Units.Angle.revolutions;
    double mpMaxVel = 1*Units.Angle.revolutions; // was 6
    double mpMaxAcc = 2.25*Units.Angle.revolutions;
    double mpAcc = mpMaxAcc;
    Coordinate mpAccCalc1 = new Coordinate(10*Units.Angle.degrees, mpMaxAcc);
    Coordinate mpAccCalc2 = new Coordinate(210*Units.Angle.degrees, 0.25*Units.Angle.revolutions);
    volatile TrapezoidalMp mp;
    Timer time = new Timer();
    double mpStartTime, mpStartAngle;
    boolean wasEnabled = false;

    // KalmanFilter filter;
    // RealMatrix A = new Array2DRowRealMatrix(new double[][] {
    //     {0d, 1d},
    //     {0d, -0.4672}
    // });
    // RealMatrix B = new Array2DRowRealMatrix(new double[][] {
    //     {0d},
    //     {17.66}
    // });
    // RealMatrix H = new Array2DRowRealMatrix(new double[][] {{1, 1}});
    // RealMatrix Q = new Array2DRowRealMatrix(new double[] {0});

    private MainArmControl(){
        arm = MainArm.getInstance();
        mpStartAngle = arm.getAngle();
        mp = new TrapezoidalMp(mpStartAngle, new TrapezoidalMp.constraints(setpoint, mpMaxVel, mpMaxAcc));
    }

    public void resetForTeleop(){
        mpStartAngle = arm.getAngle();
    }

    public void setSetpoint(double set){
        set = Math.max(set, -95*Units.Angle.degrees);
        set = Math.min(set, 235*Units.Angle.degrees);
        if(!Util.inErrorRange(set, setpoint, 5*Units.Angle.degrees)){
            mpStartTime = time.get();
            mpStartAngle = arm.getAngle();
            if(set < Math.PI/2 && set < mpStartAngle){
                mpMaxVel = 0.25*Units.Angle.revolutions;
            }else if(set > Math.PI/2 && set > mpStartAngle){
                mpMaxVel = 0.25*Units.Angle.revolutions;
            }else{
                mpMaxVel = mpDefaultMaxVel;
            }
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
        if(tSet < -130*Units.Angle.degrees){
            tSet = 2*Math.PI - Math.abs(tSet);
        }
        tSet = Math.max(tSet, -110*Units.Angle.degrees);
        tSet = Math.min(tSet, 215*Units.Angle.degrees);
        // System.out.println("tSet before: "+tSet/Units.Angle.degrees);
        if(!Util.inErrorRange(tSet, setpoint, 5*Units.Angle.degrees)){
            mpStartTime = time.get();
            mpStartAngle = arm.getAngle();
            if(tSet < Math.PI/2 && tSet < mpStartAngle){
                mpMaxVel = 0.25*Units.Angle.revolutions;
            }else if(tSet > Math.PI/2 && tSet > mpStartAngle){
                mpMaxVel = 0.25*Units.Angle.revolutions;
            }else{
                mpMaxVel = mpDefaultMaxVel;
            }
            mpAcc = calculateAcc(tSet, mpStartAngle, mpMaxAcc);
        }
        // System.out.println("mpStartAngle: "+mpStartAngle);
        // System.out.println("tSet after: "+tSet/Units.Angle.degrees);
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
                    mpStartTime = time.get();
                }
                wasEnabled = RobotState.isEnabled();

                double[] mpSetpoints = mp.Calculate(time.get() - mpStartTime);
                double mpSetpoint = mpSetpoints[0];
                // System.out.println("pos: "+mpSetpoints[0]+", vel: "+mpSetpoints[1]+", acc: "+mpSetpoints[2]);
                // double feedForward = arm.getAntigrav(); 
                double feedForward = arm.getAntigrav() + 1*arm.getFeedForward(mpSetpoints[1], mpSetpoints[2]);
                // double error = setpoint - arm.getAngle();
                double error = mpSetpoint - arm.getAngle();
                double dError = -arm.getAngleVel();
                double p = 8.474;
                double d = 1.6341;
                // double feedBack = p*error + d*dError.getOut();
                double feedBack = p*error + d*dError;
                //If going down in front of bot
                if(setpoint < arm.getAngle() && arm.getAngle() < Math.PI/2){
                    feedBack = Math.min(feedBack, 1+arm.getAntigrav()); //max up
                    feedBack = Math.max(feedBack, -12); //max down
                //If going down in back of bot
                }else if(setpoint > arm.getAngle() && arm.getAngle() > Math.PI/2){
                    // feedBack = Math.max(feedBack, 1+arm.getAntigrav()); //max up
                    // feedBack = Math.min(feedBack, -4+arm.getAntigrav()); //max down
                    feedBack = Util.forceInRange(feedBack, 12, -2+arm.getAntigrav());
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

    public boolean finishedMovement(){
        return mpFinished() && inErrorRange(5*Units.Angle.degrees);
    }
}
package subsystems;

import coordinates.Coordinate;
import coordinates.Heading;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import robot.Constants;
import utilPackage.LowPassFilter;
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
    double mpMaxVel = 0.45*Units.Angle.revolutions; //was 6
    double mpMaxAcc = 0.6*Units.Angle.revolutions; //was 2.25
    double mpAcc = mpMaxAcc;
    Coordinate mpAccCalc1 = new Coordinate(10*Units.Angle.degrees, mpMaxAcc);
    Coordinate mpAccCalc2 = new Coordinate(180*Units.Angle.degrees, 1.4*Units.Angle.revolutions);
    TrapezoidalMp mp;
    Timer time = new Timer();
    double mpStartTime, mpStartAngle;
    boolean wasEnabled = false;

    boolean ballClearence = false;

    LowPassFilter armFilter;

    private MainArmControl(){
        arm = MainArm.getInstance();
        mpStartAngle = arm.getAngle();
        mp = new TrapezoidalMp(mpStartAngle, new TrapezoidalMp.constraints(setpoint, mpMaxVel, mpMaxAcc));
        armFilter = new LowPassFilter(0.7);
    }

    public void commandBallClearence(boolean ballClearence){
        this.ballClearence = ballClearence;
    }

    public void resetForTeleop(){
        mpStartAngle = arm.getAngle();
    }

    boolean prev = false;
    public void setSetpoint(double set){
        if(!ballClearence){
            set = Math.max(set, -100*Units.Angle.degrees);
        }else{
            set = Math.max(set, 20*Units.Angle.degrees);
        }
        set = Math.min(set, 215*Units.Angle.degrees);
        // System.out.println("tSet before: "+tSet/Units.Angle.degrees);
        if(!Util.inErrorRange(set, setpoint, 5*Units.Angle.degrees)){
            if(set > Math.PI/2){
                mpMaxAcc = 0.25*Units.Angle.revolutions;
            }else{
                mpMaxAcc = 2.25*Units.Angle.revolutions;
            }
            restartMP(set);
        }
        // System.out.println("mpStartAngle: "+mpStartAngle);
        // System.out.println("tSet after: "+tSet/Units.Angle.degrees);
        setpoint = set;
        //if the set is more than 90 and the angle is less than 85 or the set is more than 90 and the angle is less than 95
        //aka if the arm is flipping
        boolean val = (set > Math.PI/2 && arm.getAngle() < 85*Units.Angle.degrees) || (set < Math.PI/2 && arm.getAngle() > 95*Units.Angle.degrees); 
        if(val){
            set = Math.PI/2;
        }else if(!val && prev){
            restartMP(set);
        }
        prev = val;
        mp.updateConstraints(mpStartAngle, new TrapezoidalMp.constraints(set-mpStartAngle, mpMaxVel, mpMaxAcc));
    }

    private void restartMP(double set){
        mpStartTime = time.get();
        mpStartAngle = arm.getAngle();
        armFilter.setup(mpStartAngle);
        mpAcc = calculateAcc(set, mpStartAngle, mpMaxAcc);
    }

    public double getSetpoint(){
        return setpoint;
    }

    public void setHeading(Heading set){
        double tSet = set.getAngle();
        if(tSet < -130*Units.Angle.degrees){
            tSet = 2*Math.PI - Math.abs(tSet);
        }
        setSetpoint(tSet);
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
                    armFilter.setup(arm.getAngle());
                    state = States.running;
                }
                break;
            case running:
                if(RobotState.isEnabled() && !wasEnabled){
                    mpStartTime = time.get();
                }
                wasEnabled = RobotState.isEnabled();

                if(arm.getAngle() > 255*Units.Angle.degrees || arm.getAngle() < -130*Units.Angle.degrees){
                    arm.setVoltage(0);
                    throw new RuntimeException("Arm Angle is broken");
                }

                double[] mpSetpoints = mp.Calculate(time.get() - mpStartTime);
                double mpSetpoint = mpSetpoints[0];
                // System.out.println("pos: "+mpSetpoints[0]+", vel: "+mpSetpoints[1]+", acc: "+mpSetpoints[2]);
                // double feedForward = arm.getAntigrav(); 
                double feedForward = arm.getAntigrav() + arm.getFeedForward(mpSetpoints[1], mpSetpoints[2]);
                // double error = setpoint - arm.getAngle();
                // double error = mpSetpoint - arm.getAngle();
                double error = mpSetpoint - armFilter.run(arm.getAngle());
                double dError = -arm.getAngleVel();
                double p = 9.7581;
                double d = 1.8418;
                p = 6.3307;
                d = 2.0345;
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
                if(Util.inErrorRange(setpoint, arm.getAngle(), 6*Units.Angle.degrees)){
                    arm.setVoltage(feedForward);
                }else{
                    arm.setVoltage(feedForward+feedBack);
                }
                break;
        }
    }

    public boolean mpFinished(){
        return time.get() - mpStartTime >= mp.getEndTime();
    }

    public boolean mpFinished(double timeAdd){
        return time.get() - mpStartTime >= mp.getEndTime()+timeAdd;
    }
    public boolean inErrorRange(double range){
        if(isRunning())
            return Util.inErrorRange(setpoint, arm.getAngle(), range);
        else
            return false;
    }

    public boolean finishedMovement(){
        // return mpFinished(0.25) && inErrorRange(10*Units.Angle.degrees);
        return mpFinished() && inErrorRange(15*Units.Angle.degrees);
    }
}
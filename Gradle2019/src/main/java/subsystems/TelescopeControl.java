package subsystems;

import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Constants;
import robot.Robot;
import utilPackage.Units;
import utilPackage.Util;
import utilPackage.TrapezoidalMp;

public class TelescopeControl{
    private static TelescopeControl instance = null;
    public static TelescopeControl getInstance(){
        if(instance == null){
            instance = new TelescopeControl();
        }
        return instance;
    }

    Telescope telescope;
    public enum States{
        disabled,
        reset,
        running;
    }
    States state = States.disabled;
    TrapezoidalMp.constraints constraints = new TrapezoidalMp.constraints(0, 5*Units.Length.feet, 1*Units.Length.feet);
    TrapezoidalMp mp;
    double mpStartTime, mpStartDist;
    double setpoint = 0;
    Timer time;
    double resetSafetyStartTime = 0;

    private TelescopeControl(){
        telescope = Telescope.getInstance();
        mpStartDist = telescope.getDistance();
        constraints.setpoint = mpStartDist;
        mp = new TrapezoidalMp(mpStartDist, constraints);
        time = new Timer();
        SmartDashboard.putNumber("Telescope setpoint", Constants.Telescope.lenRetract/Units.Length.inches);
    }

    public double setSetpoint(double setpoint){
        setpoint = Math.min(setpoint, Constants.Telescope.lenExtend);
        setpoint = Math.max(setpoint, Constants.Telescope.lenRetract);
        if(setpoint != this.setpoint){
            mpStartTime = time.get();
            mpStartDist = telescope.getDistance();
        }
        constraints.setpoint = setpoint;
        mp.updateConstraints(mpStartDist, constraints);
        this.setpoint = setpoint;
        return setpoint;
    }

    public double getSetpoint(){
        return setpoint;
    }

    public boolean isRunning(){
        return state == States.running;
    }

    public void run(){
        SmartDashboard.putString("Telescope state", state.toString());
        // setSetpoint(SmartDashboard.getNumber("Telescope setpoint", 0)*Units.Length.inches);
        switch(state){ 
            case disabled:
                if(RobotState.isEnabled()){
                    resetSafetyStartTime = Timer.getFPGATimestamp();
                    state = States.reset;
                    // telescope.setVoltage(telescope.getAntigrav());
                    // time.start();
                    // derror.Calculate(setpoint - telescope.getDistance(), time.get());
                }
                break;
            case reset:
                if(Timer.getFPGATimestamp() > resetSafetyStartTime + 5){
                    telescope.setVoltage(0);
                }else{
                    telescope.setVoltage(-6);
                }
                if(telescope.getReset()){
                    telescope.setVoltage(telescope.getAntigrav());
                    state = States.running;
                    time.start();
                }
                break;
            case running:
                if(Robot.getControlBoard().disableTelescopeGripper()){
                    telescope.setVoltage(0);
                    return;
                }
                double feedforward = telescope.getAntigrav();
                double p = 69.9800;
                double d = 5.6434;
                double error;
                // double tmpSetpoint = mp.Calculate(time.get())[0];
                // ble tmpSetpoint = setpoint;
                double tmpSetpoint = setpoint;
                
                if(!MainArmControl.getInstance().finishedMovement()){
                    error = Constants.Telescope.lenRetract - telescope.getDistance();
                }else if(MainArm.getInstance().getAngle() < Constants.MainArm.insideAngle){
                    error = Math.min(Constants.Telescope.lenInside, tmpSetpoint) - telescope.getDistance();
                }else{
                    error = tmpSetpoint - telescope.getDistance();
                }

                double derror = -telescope.getVel();
                SmartDashboard.putNumber("Telescope error", error);
                double feedback = p*error + d*derror;
                // telescope.setVoltage(feedforward);
                telescope.setVoltage(feedforward+feedback);
                break;
        }
    }

    public boolean inErrorRange(double range){
        return Util.inErrorRange(setpoint, telescope.getDistance(), range);
    }

    public boolean retracted(){
        return Util.inErrorRange(Constants.Telescope.lenRetract, telescope.getDistance(), 1*Units.Length.inches);
    }

    public void reset(){
        resetSafetyStartTime = Timer.getFPGATimestamp();
        state = States.reset;
    }
}
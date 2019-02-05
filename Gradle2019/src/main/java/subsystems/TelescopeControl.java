package subsystems;

import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Constants;
import utilPackage.Derivative;
import utilPackage.Units;

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
    double setpoint = 0;
    Derivative derror;
    Timer time;

    private TelescopeControl(){
        telescope = Telescope.getInstance();
        time = new Timer();
        SmartDashboard.putNumber("Telescope setpoint", Constants.Telescope.lenRetract/Units.Length.inches);
        derror = new Derivative();
    }

    public void setSetpoint(double setpoint){
        setpoint = Math.min(setpoint, Constants.Telescope.lenExtend);
        setpoint = Math.max(setpoint, Constants.Telescope.lenRetract);
        this.setpoint = setpoint;
    }

    public void run(){
        SmartDashboard.putString("Telescope state", state.toString());
        // setSetpoint(SmartDashboard.getNumber("Telescope setpoint", 0)*Units.Length.inches);
        switch(state){
            case disabled:
                if(RobotState.isEnabled()){
                    state = States.reset;
                }
                break;
            case reset:
                telescope.setVoltage(-4+telescope.getAntigrav());
                if(telescope.getReset()){
                    telescope.setVoltage(telescope.getAntigrav());
                    state = States.running;
                    time.start();
                    derror.Calculate(setpoint - telescope.getDistance(), time.get());
                }
                break;
            case running:
                double feedforward = telescope.getAntigrav();
                double p = 75.3160;
                double d = 11.7167;
                double error = setpoint - telescope.getDistance();
                SmartDashboard.putNumber("Telescope error", error);
                derror.Calculate(error, time.get());
                double feedback = p*error + d*derror.getOut();
                // telescope.setVoltage(feedforward);
                telescope.setVoltage(feedforward+feedback);
                break;
        }
    }
}
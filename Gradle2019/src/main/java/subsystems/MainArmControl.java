package subsystems;

import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import utilPackage.Derivative;

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
    Timer time = new Timer();

    private MainArmControl(){
        arm = MainArm.getInstance();
    }

    public void setSetpoint(double set){
        setpoint = set;
    }

    public void run() {
        switch(state){
            case disabled:
                if(RobotState.isEnabled()){
                    setpoint = arm.getAngle();
                    dError = new Derivative();
                    time.start();
                    state = States.running;
                }
                break;
            case reset:
                break;
            case running:
                double feedForward = arm.getAntigrav();
                double error = setpoint - arm.getAngle();
                dError.Calculate(error, time.get());
                double p = 9.1056;
                double d = 2.3169;
                double feedBack = p*error + d*dError.getOut();
                // arm.setVoltage(feedForward);
                arm.setVoltage(feedForward+feedBack);
                break;
        }
    }
}
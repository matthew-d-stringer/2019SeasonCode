package subsystems;

import coordinates.Heading;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import utilPackage.Derivative;
import utilPackage.Units;

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
        Math.max(set, -90*Units.Angle.degrees);
        Math.min(set, 220*Units.Angle.degrees);
        setpoint = set;
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
        tSet = Math.min(tSet, 220*Units.Angle.degrees);
        setpoint = tSet;
    }

    public boolean isRunning(){
        return state == States.running;
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
                double p = 10.3780;
                double d = 2.9035;
                double feedBack = p*error + d*dError.getOut();
                SmartDashboard.putNumber("Arm Error", error);
                // arm.setVoltage(feedForward);
                arm.setVoltage(feedForward+feedBack);
                break;
        }
    }
}
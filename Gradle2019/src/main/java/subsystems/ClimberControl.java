package subsystems;

import edu.wpi.first.wpilibj.RobotState;
import utilPackage.Util;

public class ClimberControl{
    private static ClimberControl instance;
    public static ClimberControl getInstance(){
        if(instance == null){
            instance = new ClimberControl();
        }
        return instance;
    }

    public static enum Modes{
        disabled,
        hold,
        climbUp,
        climbDown;
    }

    Climber climb;
    Modes mode;

    private ClimberControl(){
        climb = Climber.getInstance();
        mode = Modes.disabled;
    }

    public void setMode(Modes mode){
        this.mode = mode;
    }

    public void run(){
        switch(mode){
            case disabled:
                if(RobotState.isEnabled()){
                    mode = Modes.hold;
                    climb.reset();
                }
                break;
            case hold:
                if(Util.inErrorRange(climb.getClimbLen(), 0, 0.02)){
                    climb.setVoltage(0.5);
                }else if(Util.inErrorRange(climb.getClimbLen(), 0, 0.05)){
                    climb.setVoltage(2);
                }else{
                    climb.setVoltage(climb.getAntigrav());
                }
                break;
            case climbUp:
                if(!Util.inErrorRange(climb.getClimbLen(), 1, 0.03))
                    climb.setVoltage(-8+climb.getAntigrav());
                else
                    climb.setVoltage(climb.getAntigrav());
                break;
            case climbDown:
                if(climb.getClimbLen() >= 0.01)
                    climb.setVoltage(6);
                else
                    climb.setVoltage(climb.getAntigrav());
                break;
        }
    }
}
package controlBoard;

import coordinates.Heading;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class IControlBoard{
    public abstract Heading getJoystickPos();
    public abstract double getWheelPos();

    public abstract boolean quickTurn();

    public abstract boolean gripperUp();
    public abstract boolean gripperDown();

    public abstract boolean elevatorUp();
    public abstract boolean elevatorDown();
    
    public abstract boolean armUp();
    public abstract boolean armDown();

    public abstract boolean vault();
    public abstract boolean Switch();

    public void display(){
        SmartDashboard.putBoolean("Gripper Up", gripperUp());
        SmartDashboard.putBoolean("Gripper Down", gripperDown());
        
        SmartDashboard.putBoolean("Elevator Up", elevatorUp());
        SmartDashboard.putBoolean("Elevator Down", elevatorDown());

        SmartDashboard.putBoolean("Arm Up", armUp());
        SmartDashboard.putBoolean("Arm Down", armDown());

        SmartDashboard.putBoolean("vault", vault());
        SmartDashboard.putBoolean("switch", Switch());
    }
}
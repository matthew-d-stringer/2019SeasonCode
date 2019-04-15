package subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import robot.Constants;

public class CargoGripper{
    private static CargoGripper instance;
    public static CargoGripper getInstance(){
        if(instance == null){
            instance = new CargoGripper();
        }
        return instance;
    }

    TalonSRX roller;
    DoubleSolenoid pivot;
    DoubleSolenoid grabber;
    private CargoGripper(){
        pivot = new DoubleSolenoid(Constants.CargoGripper.pivotNums[0], Constants.CargoGripper.pivotNums[1]);
        grabber = new DoubleSolenoid(Constants.CargoGripper.grabNums[0], Constants.CargoGripper.grabNums[1]);
        //TODO: add roller
    }

    public void upright(){
        pivot.set(Value.kForward);
    }

    public void side(){
        pivot.set(Value.kReverse);
    }

    public void open(){
        grabber.set(Value.kForward);
    }
    public void close(){
        grabber.set(Value.kReverse);
    }
}
package robot;

import utilPackage.Units;

public class ArmSetpoints{
    double low = -25.5*Units.Length.inches;
    double mid = 5*Units.Length.inches;
    double high = 30*Units.Length.inches;

    public ArmSetpoints(){
    }

    public double getLow(){
        return low;
    }

    public void incrementLow(double joystick){
        low += joystick*Units.Length.inches * 0.005;
    }

    public double getMid(){
        return mid;
    }

    public void incrementMid(double joystick){
        mid += joystick*Units.Length.inches * 0.005;
    }

    public double getHigh(){
        return high;
    }

    public void incrementHigh(double joystick){
        high += joystick*Units.Length.inches * 0.005;
    }
}
package robot;

import utilPackage.Units;

public class ArmSetpoints{
    double hatchLow;
    double hatchMid;
    double hatchHigh;

    double ballLow, ballMid, ballHigh;

    public ArmSetpoints(){
        // hatchLow = -34*Units.Length.inches;
        hatchLow = -30*Units.Length.inches;
        hatchMid = 7*Units.Length.inches;
        hatchHigh = 33*Units.Length.inches;

        ballLow = -10.5*Units.Length.inches;
        ballMid = 19*Units.Length.inches;
        ballHigh = 34*Units.Length.inches;
    }

    public double getHatchLow(){
        return hatchLow;
    }

    public double getBallLow(){
        return ballLow;
    }

    public void incrementHatchLow(double joystick){
        hatchLow += joystick*3*Units.Length.inches * 0.1;
    }

    public void incrementBallLow(double joystick){
        ballLow += joystick*3*Units.Length.inches * 0.1;
    }

    public double getHatchMid(){
        return hatchMid;
    }

    public double getBallMid(){
        return ballMid;
    }

    public void incrementHatchMid(double joystick){
        hatchMid += joystick*3*Units.Length.inches * 0.1;
    }

    public void incrementBallMid(double joystick){
        ballMid += joystick*3*Units.Length.inches * 0.1;
    }

    public double getHatchHigh(){
        return hatchHigh;
    }
    public double getBallHigh(){
        return ballHigh;
    }

    public void incrementHatchHigh(double joystick){
        hatchHigh += joystick*3*Units.Length.inches * 0.1;
    }
    public void incrementBallHigh(double joystick){
        ballHigh += joystick*3*Units.Length.inches * 0.1;
    }
}
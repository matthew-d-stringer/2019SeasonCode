package robot;

import utilPackage.Units;

public class ArmSetpoints{
    double hatchLow;
    double hatchMid;
    double hatchHigh;

    double ballLow, ballMid, ballHigh, ballGoal;

    public ArmSetpoints(){
        hatchLow = -30*Units.Length.inches;
        hatchMid = 7*Units.Length.inches;
        hatchHigh = 33*Units.Length.inches;

        ballLow = -15.5*Units.Length.inches;
        ballGoal = -5*Units.Length.inches;
        ballMid = 19*Units.Length.inches;
        ballHigh = 34*Units.Length.inches;
    }

    public double getHatchLow(){
        return hatchLow;
    }

    public double getBallLow(){
        return ballLow;
    }
    public double getBallGoal(){
        return ballGoal;
    }

    public void incrementHatchLow(double joystick){
        hatchLow += joystick*2*Units.Length.inches * 0.1;
    }

    public void incrementBallLow(double joystick){
        ballLow += joystick*2*Units.Length.inches * 0.1;
    }

    public void incrementBallGoal(double joystick){
        ballGoal += joystick*2*Units.Length.inches * 0.1;
    }

    public double getHatchMid(){
        return hatchMid;
    }

    public double getBallMid(){
        return ballMid;
    }

    public void incrementHatchMid(double joystick){
        hatchMid += joystick*2*Units.Length.inches * 0.1;
    }

    public void incrementBallMid(double joystick){
        ballMid += joystick*2*Units.Length.inches * 0.1;
    }

    public double getHatchHigh(){
        return hatchHigh;
    }
    public double getBallHigh(){
        return ballHigh;
    }

    public void incrementHatchHigh(double joystick){
        hatchHigh += joystick*2*Units.Length.inches * 0.1;
    }
    public void incrementBallHigh(double joystick){
        ballHigh += joystick*2*Units.Length.inches * 0.1;
    }
}
package robot;

import utilPackage.Units;

public class ArmSetpoints{
    double hatchLow;
    double hatchMid;
    double hatchHigh;

    double ballLow, ballMid, ballHigh, ballGoal;

    double sensitivity = 4;

    public ArmSetpoints(){
        hatchLow = -22*Units.Length.inches;
        hatchMid = 6*Units.Length.inches;
        hatchHigh = 30*Units.Length.inches;

        ballLow = -7.5*Units.Length.inches;
        ballGoal = -3*Units.Length.inches;//was -3, 6 for loading zone
        ballMid = 15*Units.Length.inches;
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
        hatchLow += joystick*sensitivity*Units.Length.inches * 0.1;
    }

    public void incrementBallLow(double joystick){
        ballLow += joystick*sensitivity*Units.Length.inches * 0.1;
    }

    public void incrementBallGoal(double joystick){
        ballGoal += joystick*sensitivity*Units.Length.inches * 0.1;
    }

    public double getHatchMid(){
        return hatchMid;
    }

    public double getBallMid(){
        return ballMid;
    }

    public void incrementHatchMid(double joystick){
        hatchMid += joystick*sensitivity*Units.Length.inches * 0.1;
    }

    public void incrementBallMid(double joystick){
        ballMid += joystick*sensitivity*Units.Length.inches * 0.1;
    }

    public double getHatchHigh(){
        return hatchHigh;
    }
    public double getBallHigh(){
        return ballHigh;
    }

    public void incrementHatchHigh(double joystick){
        hatchHigh += joystick*sensitivity*Units.Length.inches * 0.1;
    }
    public void incrementBallHigh(double joystick){
        ballHigh += joystick*sensitivity*Units.Length.inches * 0.1;
    }
}
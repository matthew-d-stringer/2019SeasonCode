package robot;

import utilPackage.Units;

public class ArmSetpoints{
    double hatchLow;
    double hatchMid;
    double hatchHigh;

    double ballLow, ballMid, ballHigh, ballGoal;

    double wristLow;
    double wristMid;
    double wristHigh;

    double sensitivity = 4;

    public ArmSetpoints(){
        hatchLow = -21.5*Units.Length.inches;
        hatchMid = 12*Units.Length.inches;
        hatchHigh = 34*Units.Length.inches;

        wristLow = 0;
        wristMid = 0;
        wristHigh = 5*Units.Angle.degrees;

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

    public double getWristLow(){
        return wristLow;
    }
    public double getWristMid(){
        return wristMid;
    }
    public double getWristHigh(){
        return wristHigh;
    }

    public void incrementWristLow(double joystick){
        wristLow += joystick*4*6*Units.Length.inches * 0.1;
    }
    public void incrementWristMid(double joystick){
        wristMid += joystick*4*6*Units.Length.inches * 0.1;
    }
    public void incrementWristHigh(double joystick){
        wristHigh += joystick*4*6*Units.Length.inches * 0.1;
    }
}
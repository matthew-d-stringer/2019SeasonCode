package teleop;

import edu.wpi.first.wpilibj.RobotState;

public class TeleopStateMachine extends Thread{
    @Override
    public void run(){
        setup();
        while(RobotState.isOperatorControl()){
            update();
        }
    }

    private void setup(){
    }

    private void update(){
        driveCode();
    }

    private void driveCode(){
    }
}
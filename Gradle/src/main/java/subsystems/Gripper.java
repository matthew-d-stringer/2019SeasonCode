package subsystems;

public class Gripper extends Thread{
    public enum States{
        Disabled,
        Calibrate,
        Running;
    }

    @Override
    public void run(){
        States state = States.Disabled;
        while(true){
            switch(state){
                case Disabled:
                    break;
                case Calibrate:
                    break;
                case Running:
                    break;
            }
        }
    }
    //TODO: Matthew Pls Do This!
    private double controlLoop(double setpoint, double sensor){
        return 0;
    }
}
package subsystems;

public class ArmControl extends Thread{
    private static ArmControl instance = null;
    public static ArmControl getInstance(){
        if(instance == null)
            instance = new ArmControl();
        return instance;
    }

    private ArmControl(){
    }
    
    @Override
    public void run() {
    }
}
package autos.actions;

public abstract class Action{
    
    public abstract void start();

    public abstract void update();

    public abstract void done();

    public abstract boolean isFinished();

    private enum TeleopRunState{
        start,
        update,
        done,
        completeDone;
    }

    TeleopRunState state = TeleopRunState.start;
    public void resetTeleop(){
        state = TeleopRunState.start;
    }
    public void runTeleop(){
        switch(state){
            case start:
                start();
                state = TeleopRunState.update;
                break;
            case update:
                update();
                if(isFinished()){
                    state = TeleopRunState.done;
                }
                break;
            case done:
                done();
                state = TeleopRunState.completeDone;
                break;
            case completeDone:
                break;
        }
    }
}
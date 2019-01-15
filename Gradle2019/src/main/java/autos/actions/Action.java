package autos.actions;

public abstract class Action{
    
    public abstract void start();

    public abstract void update();

    public abstract void done();

    public abstract boolean isFinished();
}
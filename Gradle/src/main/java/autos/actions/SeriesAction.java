package autos.actions;

import java.util.ArrayList;
import java.util.List;

public class SeriesAction extends Action{
    Action cAction;
    ArrayList<Action> remainingActions;

    public SeriesAction(List<Action> actions){
        remainingActions = new ArrayList<>(actions);
        cAction = null;
    }

    @Override
    public void start() {
    }

    @Override
    public void update() {
        if(cAction == null){
            cAction = remainingActions.get(0);
            remainingActions.remove(0);
            cAction.start();
        }
        cAction.update();
        if(cAction.isFinished()){
            cAction.done();
            cAction = null;
        }
    }

    @Override
    public void done() {
    }

    @Override
    public boolean isFinished() {
        return remainingActions.isEmpty() && cAction == null;
    }
}
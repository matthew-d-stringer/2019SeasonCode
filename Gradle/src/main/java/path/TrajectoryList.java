package path;

import coordinates.Coordinate;

public class TrajectoryList{
    private Trajectory root = null;
    private Trajectory last = null;
    protected Trajectory current = null;
    private boolean filledRoot = false;

    public TrajectoryList(Coordinate startPos){
        root = new Trajectory(0, startPos);
        last = root;
        current = root;
        filledRoot = false;
    }
    
    public void add(Coordinate nextPos){
        if(!filledRoot){
            root.setEnd(nextPos);
            filledRoot = true;
        }else{
            Trajectory newTrajectory = new Trajectory(last.getID()+1,last, nextPos);
            last = newTrajectory;
        }
    }

    public Trajectory findRelevant(Coordinate robotPos){
        if(current == null){
            current = root;
        }
        //out should be the same or infront of current
        Trajectory out = findRelevantHelperNext(current, robotPos);
        //if not however
        if(out == null)
            out = findRelevantHelperPrev(current.prev, robotPos);
        if(out == null)
            throw new RuntimeException("Could not find relevant position on path!\n"+
                robotPos.display("Robot Pos")+"\n"+
                "Current Trajectory: "+current.display()+"\n"+
                "Next Trajectory: "+current.next.display()+"\n"+
                "Front Beta Front: "+current.data.getBetaBack()+" A1: "+current.data.getA1()+"\n"+
                "Next Beta Front: "+current.next.data.getBetaBack()+" A1: "+current.next.data.getA1());
        current = out;
        return out;
    }

    private Trajectory findRelevantHelperNext(Trajectory cTrajectory, Coordinate robotPos){
        if(cTrajectory == null)
            return null;
        else if(cTrajectory.isRelevant(robotPos))
            return cTrajectory;
        else
            return findRelevantHelperNext(cTrajectory.next, robotPos);
    }
    private Trajectory findRelevantHelperPrev(Trajectory cTrajectory, Coordinate robotPos){
        if(cTrajectory == null)
            return null;
        else if(cTrajectory.isRelevant(robotPos))
            return cTrajectory;
        else
            return findRelevantHelperNext(cTrajectory.prev, robotPos);
    }

    public Coordinate findGoalPos(Coordinate robotPos, double lookAhead){
        Coordinate out = null;
        Trajectory cTrajectory = current;
        while(out == null){
            out = cTrajectory.calculateGoalPoint(robotPos, lookAhead);
            if(cTrajectory == last && out == null){
                out = cTrajectory.getEnd();
                break;
            }
            cTrajectory = cTrajectory.next;
        }
        return out;
    }

    public Trajectory getTrajectory(int id){
        int distanceToRoot = Math.abs(id - root.getID());
        int distanceToCurrent = Math.abs(id - current.getID());
        int distanceToLast = Math.abs(id - last.getID());
        if(id < 0 || id > last.getID())
            throw new RuntimeException("ID out of bounds");
        if(distanceToRoot >= Math.max(distanceToCurrent, distanceToLast)){
            Trajectory out = root;
            while(out.getID() != id){
                out = out.next;
            }
            return out;
        }else if(distanceToLast >= Math.max(distanceToCurrent, distanceToRoot)){
            Trajectory out = last;
            while(out.getID() != id){
                out = out.prev;
            }
            return out;
        }else{
            Trajectory out = current;
            //if in front of current
            if(id >= current.getID()){
                while(out.getID() != id){
                    out = out.next;
                }
            //else if behind current
            }else{
                while(out.getID() != id){
                    out = out.prev;
                }
            }
            return out;
        }
    }

    /**
     * Determines whether the robot is in a certain distance of the goal
     * @param robotPos position of the robot
     * @param distanceThresh minimum distance from the end
     */
    public boolean isDone(Coordinate robotPos, double distanceThresh){
        Trajectory out = findRelevant(robotPos);
        //if not last trajectory, then ur not done
        if(out.getID() != last.getID())
            return false;
        double cDist = Coordinate.DistanceBetween(robotPos, out.getEnd());
        return cDist <= distanceThresh;
    }

    public int getCount(){
        return last.getID();
    }

    public int getCurrentID(){
        return current.getID();
    }

    public double getDistOnPath(){
        Trajectory relevantTrajectory = current;
        return relevantTrajectory.beginDist + relevantTrajectory.getDistOnPath();
    }

    public double getTotalDistance(){
        return last.beginDist + last.getDistance();
    }

    public String display(){
        StringBuilder out = new StringBuilder();

        return out.toString();
    }
}
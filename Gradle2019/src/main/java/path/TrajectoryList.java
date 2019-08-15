package path;

/*
    This class is used to store multiple trajectories that make up one path. 
    The class includes functions to create and modify a path, as well as functions to
    help in tracking the robot as it travels along a path.

    The trajectories are stored as a linked list.

*/

import coordinates.Coordinate;

/*
    When we create a linked list, we use the variables root, last, and current.

    The "root" variable stores the very first trajectory. As we iterate through the list, we need
    to know where in the list we are. So, the root variable serves as a reference point.

    The "current" variable stores the trajectory that the robot is currently following.
    It updates everytime we iterate through the list.

    The "last" variable store the last trajectory in the path. By knowing the last trajectory,
    it makes it easy to add more trajectories to the path, if needed.
*/
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

    /*
        This function finds the relevent trajectory
        a.k.a the trajectory the robot needs to be following.

        It works by taking the current trajectory (the last known trajectory the robot was following) and
        checks if it is relevent. If the current trajectory isn't relevent, then it checks if the next
        trajectory in the list is relevent. If it reaches the end of the list without finding a relevent
        trajectory, then it it goes back the the current trajectory and searches backward. The function
        then returns the relevent trajectory. (If there is no relevent trajectory it returns null)
    */
    public Trajectory findRelevant(Coordinate robotPos){
        if(current == null){
            current = root;
        }
        //out should be the same or infront of current
        Trajectory out = findRelevantHelperNext(current, robotPos); // iterating forward
        //if not however
        if(out == null)
            out = findRelevantHelperPrev(current.prev, robotPos); // iterating backward
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

    // This function iterates throught the list forward, searching for a relevent trajectory
    private Trajectory findRelevantHelperNext(Trajectory cTrajectory, Coordinate robotPos){
        if(cTrajectory == null)
            return null;
        else if(cTrajectory.isRelevant(robotPos))
            return cTrajectory;
        else
            return findRelevantHelperNext(cTrajectory.next, robotPos);
    }

    // This function iterates through the list backward, searching for a relevent trajectory
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
     * @param epsilonThresh maximum cross track error
     */
    public boolean isDone(Coordinate robotPos, double distanceThresh, double epsilonThresh){
        Trajectory out = findRelevant(robotPos);
        double remainingDist = getTotalDistance() - getDistOnPath();
        return remainingDist <= distanceThresh && getCrossWidthError() < epsilonThresh;
    }

    public int getCount(){
        return last.getID();
    }

    public int getCurrentID(){
        return current.getID();
    }

    public boolean onLastSegment(){
        return getCount() == getCurrentID();
    }

    public double getDistOnPath(){
        Trajectory relevantTrajectory = current;
        return relevantTrajectory.beginDist + relevantTrajectory.getDistOnPath();
    }

    public double getTotalDistance(){
        return last.beginDist + last.getDistance();
    }

    public double getCrossWidthError(){
        return current.getCrossTrackError();
    }

    public String display(){
        StringBuilder out = new StringBuilder();

        return out.toString();
    }
}
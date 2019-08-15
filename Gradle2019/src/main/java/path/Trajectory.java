package path;

import coordinates.Coordinate;
import coordinates.Heading;
import coordinates.Pos2D;
import utilPackage.Units;
import utilPackage.Util;

public class Trajectory{
    protected Trajectory next = null;
    protected Trajectory prev = null;
    private int id = 0;

    private Coordinate begin, end;
    private Pos2D pathVect;

    private enum RelevantSettings{
        CustomSimple,
        Complicated;
    }

    protected double beginDist = 0;

    private final RelevantSettings relevantSettings = RelevantSettings.CustomSimple;

    protected static class CalculationData{
        //Q1 is the distance between start and the robot
        //Q2 is the distance between stop and the robot
        //a1 is the angle between start and the robot
        //a2 is the angle between stop and the robot
        //epsilon is the cross track error
        double Q1, Q2, a1, a2, epsilon;
        //ThetaFront is the angle between the current trajectory and the next
        //ThetaBack is the angle between the previous trajectory and the current
        double thetaFront, thetaBack;
        Coordinate robotPos, correspondingCoordinate;
        public CalculationData(){
        }
        public void calculate(Trajectory cTrajectory, Coordinate robotPos){
            this.robotPos = robotPos;
            Pos2D startToPos = Pos2D.createVector(cTrajectory.getBegin(), robotPos);
            Pos2D posToStop = Pos2D.createVector(robotPos, cTrajectory.getEnd());

            Q1 = startToPos.getHeading().getMagnitude();
            Q2 = posToStop.getHeading().getMagnitude();

            if(Q1 != 0){
                a1 = Heading.getAngleBetween(cTrajectory.getPathVect().getHeading(), startToPos.getHeading());
                a1 *= Coordinate.crossProduct(startToPos.getHeading(), cTrajectory.getPathVect().getHeading()) >= 0 ? 1.0:-1.0;
            }else
                a1 = 0;

            if(Q2 != 0){
                a2 = Heading.getAngleBetween(cTrajectory.getHeading(), posToStop.getHeading());
                a2 *= Coordinate.crossProduct(posToStop.getHeading(), cTrajectory.getHeading()) >= 0 ? 1.0:-1.0;
            }else
                a2 = 0;

            Heading reversedCHeading = cTrajectory.getHeading().multC(-1).heading();
            if(cTrajectory.next != null){
                thetaFront = Heading.getAngleBetween(reversedCHeading, 
                    cTrajectory.next.getHeading());
                if(Coordinate.crossProduct(cTrajectory.next.getHeading(), reversedCHeading) > 0){
                    thetaFront = 360*Units.Angle.degrees - thetaFront;
                }
            }else
                thetaFront = 180*Units.Angle.degrees;
            if(cTrajectory.prev != null){
                Heading reversedPrevHeading = cTrajectory.prev.getHeading().multC(-1).heading();
                thetaBack = Heading.getAngleBetween(reversedPrevHeading, 
                    cTrajectory.getHeading());
                if(Coordinate.crossProduct(cTrajectory.getHeading(), reversedPrevHeading) > 0){
                    thetaBack = 360*Units.Angle.degrees - thetaBack;
                }
            }else
                thetaBack = 180*Units.Angle.degrees;

            epsilon = Q1*Math.sin(a1);

            double dist = Q1*Math.cos(a1);
            Pos2D tempVect = new Pos2D(cTrajectory.getPathVect());
            tempVect.getHeading().setMagnitude(dist);
            correspondingCoordinate = tempVect.getEndPos();
        }

        public double getQ1(){
            return Q1;
        }
        public double getQ2(){
            return Q2;
        }
        public double getA1(){
            return a1;
        }
        public double getA2(){
            return a2;
        }
        public double getThetaFront(){
            return thetaFront;
        }
        public double getThetaBack(){
            return thetaBack;
        }
        public double getBetaFront(){
            return thetaFront/2;
        }
        public double getBetaBack(){
            return thetaBack/2;
        }

        public double getEpsilon(){
            return epsilon;
        }

        public Coordinate getCorrespondingPos(){
            return correspondingCoordinate;
        }
    }

    protected CalculationData data = new CalculationData();

    protected Trajectory(int id){
        this.id = id;
        begin = new Coordinate();
        end = new Coordinate();
        getPathVect();
    }
    protected Trajectory(int id, Coordinate begin){
        this.id = id;
        this.begin = new Coordinate(begin);
        end = new Coordinate();
        getPathVect();
    }
    protected Trajectory(int id, Trajectory prev, Coordinate end){
        this.id = id;
        this.begin = new Coordinate(prev.getEnd());
        this.prev = prev;
        prev.next = this;
        this.end = new Coordinate(end);
        getPathVect();
        beginDist = prev.beginDist + prev.getDistance();
    }
    protected void fillRobotData(Coordinate robotPos){
        data.calculate(this, robotPos); 
    }
    

    /*
        Checks if the trajectory is relevent.
        The relevent trajectory is the trajectory that the robot is currently following

        Each path consists of multiple trajectories. When the robot is traveling along the path,
        it needs to know which trajectory to use. This is especially important for when the robot
        is reaching the end of its current trajectory and needs to start traveling on the next one.

    */
    public boolean isRelevant(Coordinate robotPos){
        fillRobotData(robotPos);
        switch(relevantSettings){
            case CustomSimple:
                double BetaBack = Util.round(data.getBetaBack(), 8);
                double BetaFront = Util.round(data.getBetaFront(), 8);

                boolean inFrontRange = data.getA2() >= -BetaFront && 
                    data.getA2() <= - BetaFront + 180*Units.Angle.degrees;

                boolean inBackRange = data.getA1() <= BetaBack && 
                    data.getA1() >= BetaBack - 180*Units.Angle.degrees;

                if(data.getQ1() == 0 || data.getQ2() == 0)
                    return true;

                //if there is only one path
                if(id == 0 && next == null)
                    return true;
                //if we are the first path
                else if(id == 0)
                    //don't check to see if were perefectly at the start
                    return inFrontRange;
                //if we are the last path
                else if(next == null){
                    //don't care we are on the end of the path
                    return inBackRange;
                //else
                }else{
                    //Check all conditions
                    return inFrontRange && inBackRange;
                }
            case Complicated:
                throw new RuntimeException("Complicated not implemented yet!");
        }
        return false;
    }

    /**
     * Calculates goal point on trajectory
     * @param robotPos position of the robot
     * @param lookAhead look ahead distance
     * @return goal point, is null if not on the trajectory
     */
    public Coordinate calculateGoalPoint(Coordinate robotPos, double lookAhead){
        fillRobotData(robotPos);
        
        if(data.getQ1() <= lookAhead && data.getQ2() >= lookAhead){
            double cosLambda = Math.cos(data.getA1());
            double distOnPath = data.getQ1()*cosLambda +
                Math.sqrt(data.getQ1()*data.getQ1()*(cosLambda*cosLambda-1)+lookAhead*lookAhead);
            if(distOnPath == Double.NaN)
                throw new Error("distance on path is null");
            Pos2D tempPathVect = new Pos2D(getPathVect());
            tempPathVect.getHeading().setMagnitude(distOnPath);
            return tempPathVect.getEndPos();
            //yeet
        //2 intersection points
        }else if(data.getQ1() > lookAhead && data.getQ2() > lookAhead && data.getEpsilon() <= lookAhead){
            double distOnPath = Math.sqrt(lookAhead*lookAhead - data.getEpsilon()*data.getEpsilon());
            Pos2D tempPathVect = new Pos2D(getPathVect());
            tempPathVect.setPos(data.getCorrespondingPos());
            tempPathVect.getHeading().setMagnitude(distOnPath);
            return tempPathVect.getEndPos();
        //no intersection points
        }else if(data.getQ1() > lookAhead && data.getQ2() > lookAhead && data.getEpsilon() > lookAhead){
            return data.getCorrespondingPos();
        }
        return null;
    }

    public double getRemainingDistOnPath(){
        double cosLambda = Math.cos(data.getA1());
        double distOnPath = data.getQ1()*cosLambda;
        return getDistance() - distOnPath;
    }

    public double getDistOnPath(){
        double cosLambda = Math.cos(data.getA1());
        double distOnPath = data.getQ1()*cosLambda;
        return distOnPath;
    }

    public double getCrossTrackError(){
        return data.getEpsilon();
    }

    public int getID(){
        return id;
    }

    public void setEnd(Coordinate end){
        this.end = end;
    }

    public Coordinate getBegin(){
        return begin;
    }

    public Coordinate getEnd(){
        return end;
    }

    public Pos2D getPathVect(){
        pathVect = Pos2D.createVector(begin, end);
        return pathVect;
    }

    public Heading getHeading(){
        return getPathVect().getHeading();
    }

    public double getDistance(){
        return Coordinate.DistanceBetween(begin, end);
    }

    public String display(){
        return begin.display("Start") + " " + end.display("End");
    }

    public String displayCalculationData(){
        StringBuilder out = new StringBuilder();
        out.append("Theta Front: "+data.getThetaFront());
        out.append("\n");
        out.append("Theta Back: "+data.getThetaBack());
        out.append("\n");
        out.append("Beta Front: "+data.getBetaFront());
        out.append("\n");
        out.append("Beta Back: "+data.getBetaBack());
        out.append("\n");
        out.append("Front: Angle Max: "+(180*Units.Angle.degrees - data.getBetaFront()));
        out.append("\tAngle Min: "+(-data.getBetaFront()));
        out.append("\n");
        out.append("Back: Angle Max: "+(180*Units.Angle.degrees - data.getBetaBack()));
        out.append("\tAngle Min: "+(-data.getBetaBack()));
        out.append("\n");
        out.append("A1: "+data.getA1());
        out.append("\n");
        out.append("A2: "+data.getA2());
        out.append("\n");

        return out.toString();
    }
}
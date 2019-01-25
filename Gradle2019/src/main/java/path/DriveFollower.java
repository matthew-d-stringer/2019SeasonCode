package path;

import coordinates.*;
import drive.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import utilPackage.Units;
import utilPackage.Util;

public class DriveFollower{
    private final Modes mode = Modes.PurePuresuit;
    private enum Modes{
        PurePuresuit
    }
    private double lookAhead = 3*Units.Length.feet;
    //For 1fps, 2ft look ahead
    //For 15fps, 3ft look ahead

    private double turnCorrection = 0.8;

    public void setLookAhead(double lookAhead){
        this.lookAhead = lookAhead;
    }

    public void setTurnCorrection(double turnCorrection){
        this.turnCorrection = turnCorrection;
    }

    /**
     * Uses pure pursuit for robot control
     * @param eta Angle between robot and goal (not estimated time of arrival)
     * @param lookAhead Distance from robot to goal
     * @param velocity desired speed of robot
     */
    private void updatePurePursuit(double eta, double lookAhead, double velocity, boolean reverse){
        lookAhead = Math.max(lookAhead, 1*Units.Length.feet);
        double curvature = (2*Math.sin(eta/2))/lookAhead;
        Coordinate velPoint1 = new Coordinate(0, 1);
        Coordinate velPoint2 = new Coordinate(1, turnCorrection);
        double outVel = Util.mapRange(Math.abs(curvature), velPoint1, velPoint2);
        outVel = Math.max(outVel, 0.2);
        outVel *= velocity;
        if(reverse){
            outVel *= -1;
        }
        SmartDashboard.putNumber("Eta", eta);
        SmartDashboard.putNumber("Pure Pursuit Curvature", curvature);
        SmartDashboard.putNumber("Input Vel", velocity);
        SmartDashboard.putNumber("Output Vel", outVel);
        DriveOutput.getInstance().setKin(curvature, outVel);
    }

    public void update(TrajectoryList path, double velocity){
        Pos2D robotPos = null; 
        boolean reverse = false;
        if(velocity < 0){
            // robotPos = new Pos2D(PositionTracker.getInstance().getReversePosition());
            robotPos = new Pos2D(PositionTracker.getInstance().getPosition());
            reverse = true;
            velocity = Math.abs(velocity);
        }else
            robotPos = new Pos2D(PositionTracker.getInstance().getPosition());
        
        SmartDashboard.putNumber("Follower Heading", robotPos.getHeading().getAngle());
        SmartDashboard.putString("Follower Heading Full", robotPos.getHeading().display());
        Coordinate goalPosition = path.findGoalPos(robotPos.getPos(), lookAhead);
        // System.out.println(goalPosition.display("Goal Pos"));
        // System.out.println(goalPosition == null);
        if(goalPosition == null || goalPosition.getX() == Double.NaN || goalPosition.getY() == Double.NaN){
            System.out.println(robotPos.getPos().multC(1/Units.Length.feet).display("Last Known Robot Pos"));
            SmartDashboard.putString("Follower Message", "Goal Pos is null (HELP)");
        }
        SmartDashboard.putString("Goal Pos", goalPosition.multC(1/Units.Length.feet).display());
        double distToGoalPos = Coordinate.DistanceBetween(robotPos.getPos(), goalPosition);
        SmartDashboard.putNumber("Dist To Goal Pos", Units.convertUnits(distToGoalPos, Units.Length.feet));
        Coordinate vecRobotToGoal = Heading.headingBetween(robotPos.getPos(), goalPosition);

        double eta = Heading.getAngleBetween(robotPos.getHeading(), vecRobotToGoal);
        if(reverse){
            eta = Math.PI - eta;
        }
        double etaSign = Util.checkSign(Coordinate.crossProduct(robotPos.getHeading(), vecRobotToGoal));
        eta *= etaSign;

        switch(mode){
            case PurePuresuit:
                // updatePurePursuit(eta, lookAhead, velocity, reverse);
                updatePurePursuit(eta, distToGoalPos, velocity, reverse);
            break;
        }
    }
}
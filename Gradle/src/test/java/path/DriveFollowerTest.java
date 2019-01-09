package path;

import org.junit.jupiter.api.Test;

import coordinates.Coordinate;
import coordinates.Heading;
import coordinates.Pos2D;
import utilPackage.Util;

public class DriveFollowerTest{
    @Test
    public void testReverse(){
        DriveFollower follower = new DriveFollower();
        Coordinate goalpos = new Coordinate(0, 1);
        Pos2D robotPos = new Pos2D(new Coordinate(1, 0), new Heading(0, -1));
        robotPos.getHeading().mult(-1);
        Coordinate vecRobotToGoal = Heading.headingBetween(robotPos.getPos(), goalpos);
        double eta = Heading.getAngleBetween(robotPos.getHeading(), vecRobotToGoal);
        double etaSign = Util.checkSign(Coordinate.crossProduct(robotPos.getHeading(), vecRobotToGoal));
        eta *= etaSign;
        System.out.println(eta);
    }
}
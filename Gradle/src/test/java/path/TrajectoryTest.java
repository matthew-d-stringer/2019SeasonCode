package path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import coordinates.Coordinate;
import utilPackage.Units;

public class TrajectoryTest{

    Trajectory trajectory;

    @BeforeEach
    public void setup(){
        trajectory = new Trajectory(0, new Coordinate());
        trajectory.setEnd(new Coordinate(0, 1));
    }

    @Test
    public void checkCalculationData(){
        trajectory.fillRobotData(new Coordinate(1, 1)); 
        //Check Q1
        assertEquals(Math.sqrt(2), trajectory.data.getQ1(), 0.01);
        //Check Q2
        assertEquals(1, trajectory.data.getQ2(), 0.01);
        //Check a1
        assertEquals(45*Units.Angle.degrees, trajectory.data.getA1(), 0.01);
        //Check a2
        assertEquals(-90*Units.Angle.degrees, trajectory.data.getA2(), 0.01);

        Trajectory trajectory2 = new Trajectory(1, trajectory, new Coordinate(0, 2));
        trajectory.fillRobotData(new Coordinate(1,1));
        //Check theta front
        assertEquals(180*Units.Angle.degrees, trajectory.data.getThetaFront(), 0.001);
        //Check theta back
        trajectory2.fillRobotData(new Coordinate(1,1));
        assertEquals(180*Units.Angle.degrees, trajectory2.data.getThetaBack(), 0.001);
        
        trajectory2 = new Trajectory(1, trajectory, new Coordinate(1, 2));
        trajectory.fillRobotData(new Coordinate(1, 1));
        trajectory2.fillRobotData(new Coordinate(1, 1));
        assertEquals(135*Units.Angle.degrees, trajectory.data.getThetaFront(), 0.001);
        assertEquals(trajectory.data.getThetaFront(), trajectory2.data.getThetaBack(), 0.001);

    }

    @Test
    public void checkRelevancy(){
        //check if functional when not on path
        assertTrue(trajectory.isRelevant(new Coordinate(1, 0.5)));
        assertTrue(trajectory.isRelevant(new Coordinate(1, 0)));
        assertTrue(trajectory.isRelevant(new Coordinate(1, 1)), trajectory.displayCalculationData());
        assertTrue(trajectory.isRelevant(new Coordinate(0, -1)), "Trajectory should default to path if on first path");

        //check if function when on path
        String msg = "Not functional when directly on path";
        assertTrue(trajectory.isRelevant(new Coordinate()), msg);
        assertTrue(trajectory.isRelevant(new Coordinate(0,0.5)), msg);
        assertTrue(trajectory.isRelevant(new Coordinate(0,1)), msg);

        Trajectory trajectory2 = new Trajectory(1, trajectory, new Coordinate(1, 2));
        assertFalse(trajectory.isRelevant(new Coordinate(0, 1.000001)));
        assertTrue(trajectory2.isRelevant(new Coordinate(0, 1)));
        assertTrue(trajectory2.isRelevant(new Coordinate(0.5, 1)), trajectory2.displayCalculationData());

        assertFalse(trajectory.isRelevant(new Coordinate(0.5, 0.8)), trajectory.displayCalculationData());
        assertTrue(trajectory2.isRelevant(new Coordinate(0.5, 0.8)));

    }

    @Test
    public void checkGoalPoint(){
        //test if trajectory is off the first path, set goal is the first position
        Coordinate robotPos = new Coordinate(2, 0);
        assertEquals(0, trajectory.calculateGoalPoint(robotPos, 1).getX(), 0.01);
        assertEquals(0, trajectory.calculateGoalPoint(robotPos, 1).getY(), 0.01);

        robotPos = new Coordinate(0.5, 0);
        Coordinate goalPos = trajectory.calculateGoalPoint(robotPos, 1);
        assertEquals(1, Coordinate.DistanceBetween(robotPos, goalPos), 0.01);
        assertEquals(0, goalPos.getX(), 0.01);
        assertEquals(0.866, goalPos.getY(), 0.01);

        Trajectory trajectory2 = new Trajectory(1, trajectory, new Coordinate(0, 8));
        robotPos = new Coordinate(0.5, 4);
        goalPos = trajectory2.calculateGoalPoint(robotPos, 1);
        assertEquals(1, Coordinate.DistanceBetween(robotPos, goalPos), 0.01);
        assertEquals(0, goalPos.getX(), 0.01);
        assertEquals(4.866, goalPos.getY(), 0.01);

        robotPos = new Coordinate(-0.5, 4);
        goalPos = trajectory2.calculateGoalPoint(robotPos, 1);
        assertEquals(1, Coordinate.DistanceBetween(robotPos, goalPos), 0.01);
        assertEquals(0, goalPos.getX(), 0.01);
        assertEquals(4.866, goalPos.getY(), 0.01);
        
        robotPos = new Coordinate(0.5, 0.5);
        goalPos = trajectory2.calculateGoalPoint(robotPos, 1);
        assertEquals(1, Coordinate.DistanceBetween(robotPos, goalPos), 0.01);
        assertEquals(0, goalPos.getX(), 0.01);
        assertEquals(1.366, goalPos.getY(), 0.01);

        robotPos = new Coordinate(-0.5, 0.5);
        goalPos = trajectory2.calculateGoalPoint(robotPos, 1);
        assertEquals(1, Coordinate.DistanceBetween(robotPos, goalPos), 0.01);
        assertEquals(0, goalPos.getX(), 0.01);
        assertEquals(1.366, goalPos.getY(), 0.01);

        robotPos = new Coordinate(0.5, 4);
        goalPos = trajectory2.calculateGoalPoint(robotPos, 60);
        assertNull(goalPos);

        robotPos = new Coordinate(0.5, 7.5);
        goalPos = trajectory2.calculateGoalPoint(robotPos, 1);
        assertNull(goalPos);
    }
}
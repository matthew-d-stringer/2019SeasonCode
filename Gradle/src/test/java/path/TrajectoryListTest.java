package path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import coordinates.Coordinate;
import utilPackage.Units;

public class TrajectoryListTest {
    @Test
    public void testCount(){
        TrajectoryList list = new TrajectoryList(new Coordinate());
        assertSame(0, list.getCount());
        list.add(new Coordinate());
        assertSame(0, list.getCount());
        list.add(new Coordinate());
        assertSame(1, list.getCount());
        list.add(new Coordinate());
        assertSame(2, list.getCount());
        list.add(new Coordinate());
        assertSame(3, list.getCount());

        assertSame(list.getTrajectory(0).getID(), 0);
        assertSame(list.getTrajectory(1).getID(), 1);
        assertSame(list.getTrajectory(2).getID(), 2);
        assertSame(list.getTrajectory(3).getID(), 3);

        assertThrows(RuntimeException.class, () -> {
            list.getTrajectory(-1);
        });
        assertThrows(RuntimeException.class, () -> {
            list.getTrajectory(4);
        });
    }

    @Test
    public void testRelevancy(){
        TrajectoryList list = new TrajectoryList(new Coordinate());
        list.add(new Coordinate(0, 1));
        list.add(new Coordinate(1, 2));
        list.add(new Coordinate(2.5, 2));
        list.add(new Coordinate(3, 1.5));
        list.add(new Coordinate(3, 0.5));
        list.add(new Coordinate(3, 0));
        list.add(new Coordinate());

        System.out.println("Theta ID: 0 \t"+list.findRelevant(new Coordinate(0.5,0.5)).data.getThetaFront());
        System.out.println("Beta ID: 0 \t"+list.findRelevant(new Coordinate(0.5,0.5)).data.getBetaFront());
        assertSame(0, list.findRelevant(new Coordinate(0.5,0.5)).getID());
        System.out.println("Theta ID: 1 \t"+list.findRelevant(new Coordinate(0.5,1)).data.getThetaBack());
        System.out.println("Beta ID: 1 \t"+list.findRelevant(new Coordinate(0.5,1)).data.getBetaBack());
        assertSame(1, list.findRelevant(new Coordinate(0.5,1)).getID());
        assertSame(2, list.findRelevant(new Coordinate(1.5,1.5)).getID());
        assertSame(3, list.findRelevant(new Coordinate(2.5,1.5)).getID());
        assertSame(4, list.findRelevant(new Coordinate(2.5,1)).getID());
        assertSame(5, list.findRelevant(new Coordinate(2.9,0.3)).getID());
        assertSame(6, list.findRelevant(new Coordinate(1.8,0.2)).getID());
    }

    @Test
    public void testGoalPosition(){
        TrajectoryList list = new TrajectoryList(new Coordinate());
        list.add(new Coordinate(0, 1));
        list.add(new Coordinate(1, 2));
        list.add(new Coordinate(2, 2));
        list.add(new Coordinate(3, 1));
        list.add(new Coordinate(3, 0));
        list.add(new Coordinate());

        List<Coordinate> robotPoses = Arrays.asList(
            // new Coordinate(1.5,0.5),
            new Coordinate(0.5,-0.5),
            new Coordinate(0.5,0.5)
        );
        List<Coordinate> goalPoses = Arrays.asList(
            // new Coordinate(),
            new Coordinate(0,0.366),
            new Coordinate(0.5,1.5)
        );

        assertSame(robotPoses.size(), goalPoses.size(), "robot poses and goal poses should have equal number of elements");

        for(int i = 0; i < robotPoses.size(); i++){
            goalPositionParam(i, list, robotPoses.get(i), 1, goalPoses.get(i));
        }
    }

    private void goalPositionParam(int i, TrajectoryList list, Coordinate robotPos, double lookAhead, Coordinate goalPos){
        Coordinate out = list.findGoalPos(robotPos, lookAhead);
        assertEquals(goalPos.getX(), out.getX(), 0.01, "index: "+i);
        assertEquals(goalPos.getY(), out.getY(), 0.01, "index: "+i);
    }

    @Test
    /**
     * Created to remove a random bug 
     */
    public void randomFetchErrorTest(){
        TrajectoryList path = new TrajectoryList(new Coordinate());
        path.add(new Coordinate(0, 6*Units.Length.feet));
        path.add(new Coordinate(2*Units.Length.feet, 10*Units.Length.feet));
        path.add(new Coordinate(2*Units.Length.feet, 14*Units.Length.feet));
        path.add(new Coordinate(-5*Units.Length.feet, 22*Units.Length.feet));
        path.add(new Coordinate(-7*Units.Length.feet, 33*Units.Length.feet));
        path.add(new Coordinate(0*Units.Length.feet, 36*Units.Length.feet));

        path.add(new Coordinate(1*Units.Length.feet, 38*Units.Length.feet));
        Coordinate E = new Coordinate(0*Units.Length.feet, 40*Units.Length.feet);
        path.add(E);

        Coordinate B = new Coordinate(-7*Units.Length.feet, 33*Units.Length.feet);
        path.add(B);
        
        path.current = path.getTrajectory(8);

        double lookAhead = 2*Units.Length.feet;
        Coordinate robotPos = new Coordinate(-2*Units.Length.feet, 38*Units.Length.feet);

        Coordinate goalPos = path.findGoalPos(robotPos, lookAhead); 
        System.out.println(goalPos.multC(1/Units.Length.feet).display("Goal Pos"));
        assertNotEquals(goalPos.getX(), Double.NaN);
        assertNotEquals(goalPos.getY(), Double.NaN);
    }
}
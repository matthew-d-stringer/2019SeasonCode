package path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import coordinates.*;
import utilPackage.Units;

public class SplineSegmentFillerTest{
    @Test
    public void fillingTest(){
        Pos2D start = new Pos2D(new Coordinate(), new Heading());
        Pos2D mid = new Pos2D(
            new Coordinate(5*Units.Length.feet, 5*Units.Length.feet), 
            new Heading(1, 0));
        Pos2D end = new Pos2D(
            new Coordinate(10*Units.Length.feet, 10*Units.Length.feet), 
            new Heading(0, 1));
        SplineSegmentFiller filler = new SplineSegmentFiller(Arrays.asList(start, mid));

        TrajectoryList segments = filler.generate();
        
        assertEquals(filler.pointsPerSpline - 1, segments.getCount());

        Trajectory segment = segments.findRelevant(new Coordinate(0.021399875356306563, 0.12870362810841207));
        assertNotNull(segment);
    }

    @Test
    public void JSONTest(){
        Pos2D start = new Pos2D(new Coordinate(), new Heading());
        SplineSegmentFiller filler = new SplineSegmentFiller(Arrays.asList(start));
        List<Pos2D> out = filler.pointsFromJSON("./src/deploy/Autos/SplineAuto.json", "forward1");
        System.out.println("Ouput of function");
        Iterator<Pos2D> iterator = out.iterator();
        while(iterator.hasNext()){
            Pos2D cPos2d = iterator.next();
            System.out.println(cPos2d.outputData());
        }
    }
}
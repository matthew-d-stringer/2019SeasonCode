package splines;

import org.junit.jupiter.api.Test;

import coordinates.*;
import utilPackage.Units;

public class CubicSplineTest{
    @Test
    public void basicSplineTest(){
        CubicSpline spline = new CubicSpline();
        Pos2D start = new Pos2D(new Coordinate(0,0), new Heading(0,1));
        Pos2D mid = new Pos2D(
            new Coordinate(5*Units.Length.feet, 5*Units.Length.feet), 
            new Heading(1, 0));
        Pos2D end = new Pos2D(
            new Coordinate(10*Units.Length.feet, 10*Units.Length.feet), 
            new Heading(0, 1));
        System.out.println(mid.outputData());
        spline.setPoints(start, mid);
        System.out.println(spline.display());
        for(int i = 0; i <= 10; i++){
            double t = ((double)i)/10;
            Coordinate newPos = spline.calculatePosition(t);
            System.out.println("t = "+t+"\t pos = "+newPos.display());
            // if(lastPos != null)
            //     assertTrue(newPos.getX() != lastPos.getX() && newPos.getY() != lastPos.getY());
        }
    }
}
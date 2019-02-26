package vision;

import java.util.ArrayList;

import coordinates.Coordinate;
import utilPackage.Units;
import utilPackage.Util;

public class Target{

    private double contourNumber;
    private Contour ct1, ct2;

    public Target(){
        double[] blank = {0,0,0,0,0,0,0};
        ct1 = new Contour(blank);
        ct2 = new Contour(blank);
    }

    public double getContourNumber(){
        return contourNumber;
    }

    // Sets The Input Stream
    public void input(double[] input) {
        int contourIndex = (int)input[1];
        contourNumber = input[0];
        double[] cRow = {input[2],input[3],input[4],input[5],input[6]};
        if(contourIndex == 0){
            ct1.input(cRow);;
        }else if(contourIndex == 1){
            ct2.input(cRow);;
        }
    }

    //What is this?
    public double[] target = {3.31, 5.82557203}; //TODO

    public double calcDistance(){
        double averageHeight = getAvgDims().getY();
        double distance = 5605.44 * (Math.pow(averageHeight, -0.9758289));
        return distance*Units.Length.inches;
    }

    //TODO: probably remove this
    public double angle(double distance, double angle, double theta){ //Wtf is angle compared to theta
        double a1 = (Math.sin(theta)/target[1])*distance;
        double output = a1 - angle;
        return output;
    }

    public Coordinate getCenter(){
        Coordinate center1 = ct1.getCenter();
        Coordinate center2 = ct2.getCenter();
        Coordinate center = new Coordinate((center1.getX()+center2.getX())/2, (center1.getY()+center2.getY())/2);
        return center;
    }

    public Coordinate getAvgDims(){
        Coordinate dim1 = ct1.getContourDimensions();
        Coordinate dim2 = ct2.getContourDimensions();
        Coordinate dim = new Coordinate((dim1.getX()+dim2.getX())/2, (dim1.getY()+dim2.getY())/2);
        return dim;
    }

    //TODO: Test
    public double angleX(){
        final double maxX = 500, maxAngle = 65*Units.Angle.degrees;
        Coordinate pt1 = new Coordinate(0, 0),
            pt2 = new Coordinate(maxX, maxAngle/2);
        double x1 = ct1.getCenter().getX();
        double x2 = ct2.getCenter().getX();
        double midX = (x1+x2)/2;
        return Util.mapRange(midX, pt1, pt2);
    }
}
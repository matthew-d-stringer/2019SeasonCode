package vision;

import java.util.ArrayList;

import coordinates.Coordinate;
import utilPackage.Units;
import utilPackage.Util;

public class Target{

    private Contour ct1, ct2;

    public Target(){
        double[] blank = {0,0,0,0,0,0,0};
        ct1 = new Contour(blank);
        ct2 = new Contour(blank);
    }

    // Sets The Input Stream
    public boolean input(double[] input) {
        try{
            int contourId = (int)input[0];
            // contourNumber = input[0];
            double[] cRow = {input[1],input[2],input[3],input[4]};
            if(contourId == 0){
                ct1.input(cRow);
            }else if(contourId == 1){
                ct2.input(cRow);
            }
            return true;
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Array out of bounds");
            return false;
        }
    }

    //What is this?
    public double[] target = {3.31, 5.82557203}; //TODO

    public double calcDistance(){
        double heightLeft = ct1.getContourDimensions().getY();
        double heightRight = ct2.getContourDimensions().getY();
        double dist = (calcDistanceHelper(heightLeft)+calcDistanceHelper(heightRight))/2;
        return dist*Units.Length.inches;
    }

    private double calcDistanceHelper(double height){
        return 15726.97* Math.pow(height, -1.241622);
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
package vision;

import coordinates.Coordinate;

import robot.Constants;

public class Contour{

    private double[] contourRaw = new double[7];

    private double x;
    private double y;
    private double w;
    private double h;

    public Contour(double[] data){
        input(data);
    }

    public void input(double[] data) {
        contourRaw = data;
        x = contourRaw[0];
        y = contourRaw[1];
        h = contourRaw[2];
        w = contourRaw[3];
    }

    private double[] pixelToAngle(double[] input) {
        double x = input[0];
        double y = input[1];
        double horizontalFocalLength = (Constants.Image.imageWidth/2)/Math.tan(Constants.Camera.horizontalFov/2);
        double verticalFocalLength = (Constants.Image.imageHeight/2)/Math.tan(Constants.Camera.verticalFov/2);
        double horizontalAngle = Math.atan((x-(Constants.Image.imageWidth))/horizontalFocalLength);
        double verticalAngle = Math.atan((y-(Constants.Image.imageHeight))/verticalFocalLength);
        double[] output = {horizontalAngle, verticalAngle};
        return output;
    }

    public double[][] contourAngles = {pixelToAngle(getTopLeft().getXY()),
        pixelToAngle(getTopRight().getXY()),
        pixelToAngle(getBottomLeft().getXY()),
        pixelToAngle(getBottomRight().getXY())
    };

    public double[][] contourCoords = {
        getTopLeft().getXY(), 
        getTopRight().getXY(), 
        getBottomLeft().getXY(), 
        getBottomRight().getXY()
    };

    public Coordinate getTopLeft(){
        return new Coordinate(x,y);
    }
    public Coordinate getTopRight(){
        return new Coordinate(x+w,y);
    }
    public Coordinate getBottomLeft(){
        return new Coordinate(x,y+h);
    }
    public Coordinate getBottomRight(){
        return new Coordinate(x+w,y+h);
    }
    public Coordinate getCenter(){
        return new Coordinate(x + w/2, y + h/2);
    }

    public Coordinate getContourDimensions(){
        return new Coordinate(w, h);
    }

}
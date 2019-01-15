package simulation;

import coordinates.Coordinate;
import processing.core.PApplet;
import utilPackage.Units;

public class MainSim extends PApplet{
    public static final float distancePerPixel = (float)(50*Units.Length.feet)/(500);
    public static final float defaultStroke = (float)(6);
    public static void main(String[] args) {
        PApplet.main("simulation.MainSim");
    }

    @Override
    public void settings(){
        size(500, 500);
    }

    @Override
    public void setup() {
        translate(width/2, height/2);
        scale(1, -1);
        scale(distancePerPixel*width, distancePerPixel*height);
        pushMatrix();
    }

    @Override
    public void draw() {
        background(150);
        popMatrix();
        drawPoint(this, new Coordinate(5*Units.Length.feet, 5*Units.Length.feet));

        scale(1, -1);
        text("MX: "+MainSim.getMouseX(this)+" MY: "+MainSim.getMouseY(this), width/2 - 200, 
        (height/2 - 100));
        scale(1, -1);
        pushMatrix();
    }
    
    public static double getMouseX(PApplet frame){
        return Math.round(Units.convertUnits((frame.mouseX - frame.width/2)*distancePerPixel, 
            Units.Length.inches));
    }
    
    public static double getMouseY(PApplet frame){
        return -Math.round(Units.convertUnits((frame.mouseY - frame.height/2)*distancePerPixel, 
            Units.Length.inches));
    }

    public static void drawPoint(PApplet frame, Coordinate pos){
        frame.strokeWeight(defaultStroke);
        frame.point((float)(pos.getX()), (float)(pos.getY()));
    }
    public static void drawPoint(PApplet frame, Coordinate pos, float weight){
        frame.strokeWeight(weight);
        frame.point((float)(pos.getX()), (float)(pos.getY()));
        frame.strokeWeight(defaultStroke);
    }
}
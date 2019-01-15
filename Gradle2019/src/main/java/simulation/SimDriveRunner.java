package simulation;

import java.util.Arrays;

import coordinates.*;
import drive.IPositionTracker;
import edu.wpi.first.wpilibj.Timer;
import processing.core.PApplet;
import utilPackage.Util;

public class SimDriveRunner extends Thread implements IPositionTracker{
    private static SimDriveRunner instance = null;
    public static SimDriveRunner getInstance(){
        if(instance == null)
            instance = new SimDriveRunner();
        return instance;
    }

    private Coordinate position;
    private Heading heading;
    private Coordinate vels = new Coordinate();
    private double rotation;

    private SimDriveRunner(){
    }

    public void setInitPos(Coordinate pos){
        position = new Coordinate(pos);
    }

    public void robotForward(){
    }

    public void robotBackward(){
    }

    @Override
    public void run() {
        double last = Timer.getFPGATimestamp();
        while(true){
            double dt = Timer.getFPGATimestamp() - last;
            last = Timer.getFPGATimestamp();

            heading = new Heading();
            heading.setRobotAngle(heading.getAngle() + rotation*dt);
    
            double leftVel = vels.getY();
            double rightVel = vels.getX();
            double forwardVelocity = Util.average(Arrays.asList(leftVel, rightVel));
            heading.setMagnitude(forwardVelocity);

            Pos2D nextPos = new Pos2D(position, new Heading(heading));
            nextPos.getHeading().mult(dt);
            position = nextPos.getEndPos();

            Timer.delay(0.005);
        }
    }

    @Override
    public Pos2D getPosition() {
        return new Pos2D(new Coordinate(position), new Heading(heading));
    }

    @Override
    public Heading getVelocity() {
        return new Heading(heading);
    }

    //TODO: Fix this sim code
    // @Override
    // public void setRelativeDriveVelocities(double forwardVel, double rotation) {
    //     vels.setX(forwardVel - Constants.robotWidth*rotation/2);
    //     vels.setY(forwardVel + Constants.robotWidth*rotation/2);
    // }

    public void display(PApplet frame){
        frame.rectMode(PApplet.CENTER);
    }
}
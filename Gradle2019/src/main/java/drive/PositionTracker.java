package drive;

import java.util.Arrays;

import com.kauailabs.navx.frc.AHRS;

import coordinates.*;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import utilPackage.Units;
import utilPackage.Util;

public class PositionTracker extends Thread implements IPositionTracker{
    private static PositionTracker instance = null;
    public static PositionTracker getInstance(){
        if(instance == null)
            instance = new PositionTracker();
        return instance;
    }

    private AHRS navx;
    private Coordinate position = new Coordinate();
    private Heading heading = new Heading();
    private Pos2D fullPos = new Pos2D();
    private double offset;

    private PositionTracker(){
        navx = new AHRS(Port.kMXP);
        this.start();
    }

    public void setInitPos(Coordinate pos){
        position = new Coordinate(pos);
    }

    public void robotForward(){
        // angle - cangle = 0
        offset = navx.getAngle();
    }

    public void robotBackward(){
        // angle - cangle = 180
        offset = navx.getAngle() + 180;
    }

    @Override
    public void run() {
        double last = Timer.getFPGATimestamp();
        SmartDashboard.putBoolean("Reset Location", false);
        SmartDashboard.putBoolean("Reset Heading", false);
        offset = 0;
        heading = new Heading();
        navx.reset();
        while(true){
            double dt = Timer.getFPGATimestamp() - last;
            last = Timer.getFPGATimestamp();

            heading.setRobotAngle(getAngle());
    
            double leftVel = Drive.getInstance().getLeftVel();
            double rightVel = Drive.getInstance().getRightVel();
            double forwardVelocity = Util.average(Arrays.asList(leftVel, rightVel));
            Heading special = new Heading(heading);
            special.setMagnitude(forwardVelocity);

            Pos2D nextPos = new Pos2D(position, special);
            nextPos.getHeading().mult(dt);
            position = nextPos.getEndPos();

            fullPos.setPos(position);
            fullPos.setHeading(heading);

            Timer.delay(0.005);
        }
    }

    private double getAngle(){
        return (navx.getAngle()-offset)*Units.Angle.degrees;
    }

    @Override
    public synchronized Pos2D getPosition() {
        // return new Pos2D(new Coordinate(position), new Heading(heading));
        return fullPos;
    }

    public synchronized Pos2D getReversePosition(){
        Pos2D output = new Pos2D(fullPos);
        output.getHeading().multC(-1);
        return output;
    }

    @Override
    public Heading getVelocity() {
        return new Heading(heading);
    }

    public void display(){
        if(SmartDashboard.getBoolean("Reset Location", false)){
            setInitPos(new Coordinate());
            SmartDashboard.putBoolean("Reset Location", false);
        }

        if(SmartDashboard.getBoolean("Reset Heading", false)){
            robotForward();
            SmartDashboard.putBoolean("Reset Heading", false);
        }

        try{
            Coordinate position = new Coordinate(this.position);
            position.mult(1/Units.Length.feet);
            SmartDashboard.putNumber("X direction feet", position.getX());
            SmartDashboard.putNumber("Y direction feet", position.getY());
            SmartDashboard.putNumber("Angle", navx.getAngle()-offset);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
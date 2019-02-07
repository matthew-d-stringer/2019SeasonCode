package drive;

import java.util.Arrays;

import com.kauailabs.navx.frc.AHRS;

import coordinates.*;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.SerialPort.Port;
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

    private AHRS vmxPi;
    private Coordinate position = new Coordinate();
    private Heading heading = new Heading();
    private Pos2D fullPos = new Pos2D();
    private double offset;

    private PositionTracker(){
        vmxPi = new AHRS(Port.kUSB);
        this.start();
    }

    public void setInitPos(Coordinate pos){
        position = new Coordinate(pos);
    }

    public void resetHeading(){
        // vmxPi.reset();
        // offset = 0;
        offset = getRawAngle();
    }

    public void robotForward(){
        // vmxPi.reset();
        // offset = 0;

        // angle - cangle = 0
        offset = getRawAngle();
    }

    public void robotBackward(){
        // vmxPi.reset();
        // offset = 180;
        
        // angle - cangle = 180
        offset = getRawAngle() + 180;
    }

    @Override
    public void run() {
        double last = Timer.getFPGATimestamp();
        SmartDashboard.putBoolean("Reset Location", false);
        SmartDashboard.putBoolean("Reset Heading", false);
        heading = new Heading();
        heading.setRobotAngle(getAngle());
        Heading pHeading = new Heading(heading);
        resetHeading();
        Timer.delay(0.02);
        Drive mDrive = Drive.getInstance();
        double pCircum = Util.average(Arrays.asList(mDrive.getLeftPosition(), mDrive.getRightPosition()));
        double cCircum = pCircum;
        while(true){
            double dt = Timer.getFPGATimestamp() - last;
            last = Timer.getFPGATimestamp();

            pHeading.setAngle(heading.getAngle());
            heading.setRobotAngle(getAngle());
            Heading tempHeading = Heading.headingBetween(heading, pHeading).normalize().heading();
            
            pCircum = cCircum;
            cCircum = Util.average(Arrays.asList(mDrive.getLeftPosition(), mDrive.getRightPosition()));
            double dCircum = cCircum - pCircum;
            double dAngle = Heading.headingsToAngle(pHeading, heading);
            double radius = dCircum/dAngle;
            double distance = radius*Math.sqrt(2-2*Math.cos(dAngle));
            tempHeading.setMagnitude(distance);
    
            Pos2D nextPos = new Pos2D(position, tempHeading);
            position = nextPos.getEndPos();

            fullPos.setPos(position);
            fullPos.setHeading(heading);

            Timer.delay(0.005);
        }
    }

    private double getRawAngle(){
        return vmxPi.getAngle();
    }

    private double getAngle(){
        // return (vmxPi.getAngle()-offset)*Units.Angle.degrees;
        return (getRawAngle()-offset)*Units.Angle.degrees;
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
            SmartDashboard.putNumber("Angle", getAngle()/Units.Angle.degrees);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
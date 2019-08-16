package vision;

import java.util.Arrays;
import java.util.List;

import coordinates.Coordinate;
import coordinates.Heading;
import coordinates.Pos2D;
import drive.PositionTracker;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import utilPackage.Units;

public class Jevois extends Thread{
    private static Jevois instance;
    public static Jevois getInstance(){
        if(instance == null){
            instance = new Jevois();
        }
        return instance;
    }

    final List<String> ignoreStrings = Arrays.asList(
        "HT",
        "NT",
        "ALIVE",
        "OK"
    );

    Coordinate placementOffset;
    Coordinate position;
    Heading PT;
    SerialPort serial;
    Target target;
    double horizontalAngleOffset = 0;

    boolean useVision = false;

    private Jevois(){
        this.setPriority(4);
        placementOffset = new Coordinate(-6.5, 2.25);
        placementOffset.mult(Units.Length.inches);
        position = new Coordinate();
        target = new Target();
        PT = new Heading();
    }

    @Override
    public void run() {
        serial = new SerialPort(115200, SerialPort.Port.kUSB);
        serial.enableTermination();
        startJevois();
        while(!this.interrupted()){
            String rawInput = serial.readString();

            double[] input = getNumberData(rawInput);
            if(input == null){
                useVision = false;
                continue;
            }
            if(!target.input(input)){
                useVision = false;
                continue;
            }
            useVision = true;
            position = getDeltaDistance(PositionTracker.getInstance().getPosition(), target.angleX(), target.calcDistance());
            System.out.println(getPT().multC(1/Units.Length.inches).display("PT vector")+"\n");
            Timer.delay(0.01);
        }
    }

    public Heading getPT(){
        return PT;
    }

    public Coordinate getPosition(){
        return position;
    }

    public boolean useVision(){
        return useVision;
    }

    private void startJevois(){
        serial.writeString("streamoff\n");
        serial.writeString("setmapping2 YUYV 320 240 60.0 JeVois PythonTest\n");
        serial.writeString("setpar serlog None\n");
        serial.writeString("setpar serout All\n");
        serial.writeString("setcam absexp 100\n");
        serial.writeString("streamon\n");
    }
    private boolean shouldIgnore(String input){
        if(input.isBlank())
            return true;
        for(String s : ignoreStrings){
            if(input.contains(s)){
                return true;
            }
        }
        if(!input.contains(","))
            return true;
        return false;
    }
    public double[] getNumberData(String input){
        if(shouldIgnore(input)){
            //removes whitespace 
            input = input.replaceAll("\\s", "");
            input = input.trim().replaceAll("\n ", "");
            if(!input.isEmpty())
                System.out.println(input);
            return null;
        }
        String[] items = input.split(",");
        double[] numberData = new double[items.length];
        for(int i = 0; i < items.length; i++){
            try{
                numberData[i] = Double.parseDouble(items[i]);
            }catch(NumberFormatException e){
                System.out.println("Error in data input: "+input);
                continue;
            }
        }
        return numberData;
    }
    private Coordinate getDeltaDistance(Pos2D robotPos, double angle, double dist){
        //CT is the vector from the camara to the target
        //TP is the vector from the target to the center of the robot
        Heading CT = new Heading();
        CT.setRobotAngle(angle+horizontalAngleOffset);
        CT.setMagnitude(dist);
        Heading TP = CT.addC(placementOffset).heading();
        this.PT = new Heading(TP);
        TP.mult(-1);

        Heading forHeading = robotPos.getHeading();
        Heading perpHeading = forHeading.perpendicularCwC();
        double x1 = perpHeading.getX();
        double x2 = forHeading.getX();
        double x3 = perpHeading.getY();
        double x4 = forHeading.getY();

        Heading TPabs = new Heading();
        TPabs.setX(x4*TP.getX() - x2*TP.getY());
        TPabs.setY(-x3*TP.getX() + x1*TP.getY());
        TPabs.mult(1/(x1*x4-x2*x3));
        return TPabs;
    }
}
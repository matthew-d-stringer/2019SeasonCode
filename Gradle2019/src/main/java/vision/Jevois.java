package vision;

import java.util.Arrays;
import java.util.List;

import coordinates.Coordinate;
import coordinates.Heading;
import coordinates.Pos2D;
import drive.PositionTracker;
import edu.wpi.first.wpilibj.SerialPort;
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
    SerialReader reader;
    SerialPort serial;
    Target target;
    double horizontalAngleOffset = 0;

    private Jevois(){
        reader = new SerialReader(0);
        serial = new SerialPort(1152000, SerialPort.Port.kUSB);
        serial.enableTermination();
        placementOffset = new Coordinate(-10, 1.5);
        placementOffset.mult(Units.Length.inches);
        position = new Coordinate();
        target = new Target();
    }

    @Override
    public void run() {
        startJevois();
        while(true){
            // String rawInput = reader.readLine();
            String rawInput = serial.readString();
            double[] input = getNumberData(rawInput);
            if(input == null){
                continue;
            }
            target.input(input);
            if(target.getContourNumber() < 2){
                continue;
            }
            position = getDeltaDistance(PositionTracker.getInstance().getPosition(), target.angleX(), target.calcDistance());
        }
    }

    public Heading getPT(){
        return PT;
    }

    public Coordinate getPosition(){
        return position;
    }

    private void startJevois(){
        // reader.sendMessage("setmapping2 YUYV 320 240 60.0 JeVois PythonTest");
        // reader.sendMessage("setpar serlog None");
        // reader.sendMessage("setpar serout All");
        // reader.sendMessage("setcam absexp 100");
        // reader.sendMessage("streamon");
        serial.writeString("setmapping2 YUYV 320 240 60.0 JeVois PythonTest");
        serial.writeString("setpar serlog None");
        serial.writeString("setpar serout All");
        serial.writeString("setcam absexp 100");
        serial.writeString("streamon");
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
        // System.out.println("Received: "+rawInput);
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

        // System.out.println(TP.multC(1/Units.Length.inches).display("Relative dist from pos"));

        Heading forHeading = robotPos.getHeading();
        Heading perpHeading = forHeading.perpendicularCwC();
        // System.out.println(forHeading.display("Forward Heading"));
        // System.out.println(perpHeading.display("Perp Heading"));
        double x1 = perpHeading.getX();
        double x2 = forHeading.getX();
        double x3 = perpHeading.getY();
        double x4 = forHeading.getY();

        Heading TPabs = new Heading();
        TPabs.setX(x4*TP.getX() - x2*TP.getY());
        TPabs.setY(-x3*TP.getX() + x1*TP.getY());
        TPabs.mult(1/(x1*x4-x2*x3));
        return TPabs;
        // return TP;
    }
}
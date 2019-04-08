package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import coordinates.Heading;
import coordinates.Pos2D;
import drive.PositionTracker;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import utilPackage.Util;

public class Client extends Thread{
    private static Client instance;
    public static Client getInstance(){
        if(instance == null){
            instance = new Client("10.32.50.22", 5800);
        }
        return instance;
    }
    
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    private int port;

    private String recvData;
    private Pos2D readPosition;
    private Heading relTargetFront, relTargetReverse;

    private Client(String ipAddress, int port) {
        try{
            socket = new DatagramSocket();
            socket.setSoTimeout(1000);
            address = InetAddress.getByName(ipAddress);
        }catch(Exception e){
            e.printStackTrace();
        }
        readPosition = new Pos2D();
        relTargetFront = new Heading();
        relTargetReverse = new Heading();
        this.port = port;
    }

    @Override
    public void run() {
        while(true){
            Pos2D readPosition = updateVision(PositionTracker.getInstance().getPosition());
            if(readPosition != null){
                this.readPosition = readPosition;
                System.out.println(readPosition.outputData("Read From Vision"));
            }
            System.out.println();
            SmartDashboard.putNumber("Target Angle Front", relTargetFront.getAngle());
            SmartDashboard.putNumber("Target Angle Reverse", relTargetReverse.getAngle());
            try{
                Thread.sleep(100);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public synchronized Pos2D getPosition(){
        return readPosition;
    }

    private Pos2D updateVision(Pos2D robotPos){
        JSONObject send = new JSONObject();
        float Px = (float)Util.round(robotPos.getPos().getX(), 3);
        float Py = (float)Util.round(robotPos.getPos().getY(), 3);
        float Hx = (float)Util.round(robotPos.getHeading().getX(), 3);
        float Hy = (float)Util.round(robotPos.getHeading().getY(), 3);
        send.put("Px", Px);
        send.put("Py", Py);
        send.put("Hx", Hx);
        send.put("Hy", Hy);
        String recvString = send(send.toJSONString());
        // return recvString;
        if(recvString == null){
            return null;
        }
        JSONObject recv;
        try{
            recv = (JSONObject)Util.getParser().parse(recvString);
        }catch(ParseException e){
            // DriverStation.reportError("Random parse error for vision", true);
            System.out.println("Parse error");
            return null;
        }
        // if((boolean)recv.get("send")){
            // return null;
        // }
        Pos2D recvPos = new Pos2D();
        recvPos.setPos(Double.parseDouble(recv.get("Px").toString()), Double.parseDouble(recv.get("Py").toString()));
        recvPos.setHeading(Double.parseDouble(recv.get("Hx").toString()), Double.parseDouble(recv.get("Hy").toString()));

        relTargetFront.setAngle(Double.parseDouble(recv.get("FAng").toString()));
        relTargetFront.setMagnitude(Double.parseDouble(recv.get("FDist").toString()));
        relTargetReverse.setAngle(Double.parseDouble(recv.get("RAng").toString()));
        relTargetReverse.setMagnitude(Double.parseDouble(recv.get("RDist").toString()));

        return recvPos;
    }

    private String send(String msg){
        System.out.println("Sending: \""+msg+"\"");
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        try{
            socket.send(packet);
        }catch(Exception e){
            e.printStackTrace();
        }
        buf = new byte[1000];
        packet = new DatagramPacket(buf, buf.length);
        try{
            socket.receive(packet);
        }catch(SocketTimeoutException e){
            System.out.println("Client timed out while waiting for a message");
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Recv: \""+received+"\"");
        return received;
    }

    private void close(){
        send("end");
        socket.close();
    }

    public static void main(String[] args) {
        Client c = new Client("localhost", 4445);
        System.out.println(c.send("hello"));
        System.out.println(c.send("hi"));
        System.out.println(c.send("good day"));
        c.close();
    }
}
package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import coordinates.Pos2D;
import drive.PositionTracker;
import edu.wpi.first.wpilibj.DriverStation;
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

    private Pos2D readPosition;

    private Client(String ipAddress, int port) {
        try{
            socket = new DatagramSocket();
            socket.setSoTimeout(250);
            address = InetAddress.getByName(ipAddress);
        }catch(Exception e){
            e.printStackTrace();
        }
        readPosition = new Pos2D();
        this.port = port;
    }

    @Override
    public void run() {
        while(true){
            readPosition = updateVision(PositionTracker.getInstance().getPosition());
        }
    }

    public synchronized Pos2D getPosition(){
        return readPosition;
    }

    private Pos2D updateVision(Pos2D robotPos){
        JSONObject send = new JSONObject();
        send.put("Px", robotPos.getPos().getX());
        send.put("Py", robotPos.getPos().getY());
        send.put("Hx", robotPos.getHeading().getX());
        send.put("Hy", robotPos.getHeading().getY());
        String recvString = send(send.toJSONString());
        if(recvString == null){
            return null;
        }
        JSONObject recv;
        try{
            recv = (JSONObject)Util.getParser().parse(recvString);
        }catch(ParseException e){
            DriverStation.reportError("Random parse error for vision", true);
            return null;
        }
        if((boolean)recv.get("send")){
            return null;
        }
        Pos2D recvPos = new Pos2D();
        recvPos.setPos((double)recv.get("Px"), (double)recv.get("Py"));
        recvPos.setHeading((double)recv.get("Px"), (double)recv.get("Py"));
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
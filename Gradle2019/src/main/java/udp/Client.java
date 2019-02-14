package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import coordinates.Pos2D;
import edu.wpi.first.wpilibj.DriverStation;
import utilPackage.Util;

public class Client {
    private static Client instance;
    public static Client getInstance(){
        if(instance == null){
            instance = new Client("10.32.50.22", 3250);
        }
        return instance;
    }
    
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;

    private int port;

    private Client(String ipAddress, int port) {
        try{
            socket = new DatagramSocket();
            address = InetAddress.getByName(ipAddress);
        }catch(Exception e){
            e.printStackTrace();
        }

        this.port = port;
    }

    public Pos2D updateVision(Pos2D robotPos){
        JSONObject send = new JSONObject();
        send.put("Px", robotPos.getPos().getX());
        send.put("Py", robotPos.getPos().getY());
        send.put("Hx", robotPos.getHeading().getX());
        send.put("Hy", robotPos.getHeading().getY());
        String recvString = send(send.toJSONString());
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

    public String send(String msg){
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
        }catch(Exception e){
            e.printStackTrace();
        }
        String received = new String(packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close(){
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
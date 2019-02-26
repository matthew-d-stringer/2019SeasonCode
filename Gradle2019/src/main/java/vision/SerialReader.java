package vision;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.fazecast.jSerialComm.SerialPort;

public class SerialReader{
    private SerialPort comPort;
    private BufferedReader buffer;

    public SerialReader(){
        comPort = SerialPort.getCommPorts()[0];
        System.out.println(comPort.getSystemPortName());
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        buffer = new BufferedReader(new InputStreamReader(comPort.getInputStream()));
    }
    public SerialReader(int num){
        comPort = SerialPort.getCommPorts()[num];
        System.out.println(comPort.getSystemPortName());
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        buffer = new BufferedReader(new InputStreamReader(comPort.getInputStream()));
    }
    public SerialReader(String port){
        comPort = SerialPort.getCommPort(port);
        System.out.println(comPort.getSystemPortName());
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        buffer = new BufferedReader(new InputStreamReader(comPort.getInputStream()));
    }

    public void close(){
        comPort.closePort();
    }

    public void sendMessage(String msg){
        msg = msg+"\n";
        byte[] byteMsg = (msg).getBytes();
        comPort.writeBytes(byteMsg, byteMsg.length);
    }

    public String readUntilChar(char target){
        StringBuilder sb = new StringBuilder();
        try{
            int r;
            while((r = buffer.read()) != -1){
                char c = (char) r;
                if(c == target)
                    break;
                sb.append(c);
            }
        }catch(IOException e){
        }

        return sb.toString();
    }

    public String readLine(){
        return readUntilChar('\n');
    }
}
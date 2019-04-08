package vision;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.LEDChannel;

public class LEDController{
    private static LEDController instance;
    public static LEDController getInstance(){
        if(instance == null){
            instance = new LEDController();
        }
        return instance;
    }

    CANifier led;

    private LEDController(){
        led = new CANifier(1);
        led.configFactoryDefault();
    }

    public void setLED(boolean on){
        if(on){
            led.setLEDOutput(0, LEDChannel.LEDChannelA);
            led.setLEDOutput(0, LEDChannel.LEDChannelB);
            // C is both
            led.setLEDOutput(1, LEDChannel.LEDChannelC);
        }else{
            led.setLEDOutput(0, LEDChannel.LEDChannelA);
            led.setLEDOutput(0, LEDChannel.LEDChannelB);
            led.setLEDOutput(0, LEDChannel.LEDChannelC);
        }
    }
}
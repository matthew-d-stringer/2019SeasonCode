package robot;

import java.io.FileReader;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import utilPackage.Units;
import utilPackage.Util;

public class Constants {
	/*
	 * Add sub class for every subsystem
	 * should look something like this:
	 */
	
	public static int pressureSensorNum = 3;
	
	public static double robotWidth = 27*Units.Length.inches;

	public static boolean isCompBot = true;

	public static class Drive{
		public static int MLeftNum = 11, MRightNum = 14;
		public static int[] slaveNums = {10,12, 13,15}; //left to right
		public static TalonSRX leftEncoder = new TalonSRX(1);
		public static TalonSRX rightEncoder = new TalonSRX(5);
		public static double wheelDiameter = 5.5, // inches
				wheelCircumference = wheelDiameter * Math.PI, // inches
				robotDiameter = 29; // inches (for estimating angle without a gyro)
	}

	public static void readRobotData() throws Exception{
		JSONParser parser = Util.getParser();
		Object tempObj = parser.parse(new FileReader("/home/lvuser/robot.json"));
		JSONObject fileObj = (JSONObject)tempObj;
		isCompBot = (boolean)fileObj.get("isCompBot");
	}
}

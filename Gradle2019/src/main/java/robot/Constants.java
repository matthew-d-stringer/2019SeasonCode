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

	public static class MainArm{
		public static int pivotNum = 1;

		public static double mass = 0;

		public static double zeroDegVal = 0;
		public static double ninetyDegVal = 0;
	}

	public static class Telescope{
		public static int telescopeNum = 1;

		public static double mass = 0;

		public static double retractVal = 0;
		public static double extendVal = 0;

		public static double lenRetract = 0, lenExtend = 0;
		public static double comRetract = 0, comExtend = 0;
	}

	public static class Gripper{
		public static int pivotNum = 1;

		public static double mass = 0;
		
		public static double zeroDegVal = 0;
		public static double ninetyDegVal = 0;

		public static double comOpen = 0, comClosed = 0, comWithBall = 0;
	}

	public static void readRobotData() throws Exception{
		JSONParser parser = Util.getParser();
		Object tempObj = parser.parse(new FileReader("/home/lvuser/robot.json"));
		JSONObject fileObj = (JSONObject)tempObj;
		isCompBot = (boolean)fileObj.get("isCompBot");
	}
}

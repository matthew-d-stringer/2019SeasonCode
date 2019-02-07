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
	
	public static double robotWidth = 27*Units.Length.inches;

	public static boolean isCompBot = true;

	public static class Drive{
		public static int MLeftNum = 11, MRightNum = 14;
		public static int[] slaveNums = {10,12, 13,15}; //left to right
		public static TalonSRX rightEncoder = new TalonSRX(50);
		public static TalonSRX leftEncoder = new TalonSRX(51);
		public static double wheelDiameter = 5.5, // inches
				wheelCircumference = wheelDiameter * Math.PI, // inches
				robotDiameter = 29; // inches (for estimating angle without a gyro)
	}

	public static class MainArm{
		public static int pivotNum = 20;

		public static double mass = 6.362332*Units.Mass.kilograms;

		public static double gearRatio = 1/373.33;

		public static double zeroDegVal = 990;
		public static double ninetyDegVal = 2030;
	}

	public static class Telescope{
		public static int telescopeNum = 21;
		public static int resetNum = 7;

		public static double mass = 2.53139782*Units.Mass.kilograms;

		public static double gearRatio = 1/100;
		public static double radius = 37.5*Units.Length.milimeters;

		public static double retractVal = 0;
		public static double extendVal = 4712;

		public static double lenRetract = 24.2675*Units.Length.inches, lenExtend = 36.7009*Units.Length.inches;
		public static double comRetract = 0.43391551*Units.Length.meters, comExtend = 0.57042751*Units.Length.meters;

		public static double momentOfInertiaRetracted = 1.48278951;//Kg m^2
		public static double momentOfInertiaExtended = 2.69233476;//Kg m^2
	}

	public static class Gripper{
		public static int pivotNum = 1;
		public static int resetNum = 1;

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

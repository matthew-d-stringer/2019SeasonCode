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
	
	public static double robotWidth = 26*Units.Length.inches;

	public static boolean isCompBot = true;

	public static class Drive{
		public static int MLeftNum = 11, MRightNum = 14;
		public static int[] slaveNums = {10,12, 13,15}; //left to right
		public static TalonSRX rightEncoder = new TalonSRX(41);
		public static TalonSRX leftEncoder = new TalonSRX(22);
		public static double wheelDiameter = 5.5, // inches
				wheelCircumference = wheelDiameter * Math.PI, // inches
				robotDiameter = 29; // inches (for estimating angle without a gyro)
	}

	public static class MainArm{
		public static int pivotNum = 20;
		public static int slaveNum = 22;

		public static double mass = 8.69052712-5.104*Units.Mass.kilograms;

		public static double zeroDegVal = 79320;
		public static double ninetyDegVal = 150174;

		public static double insideAngle = -70*Units.Angle.degrees;
		
		public static double maxXVal = (44-15)*Units.Length.inches;
		public static double minXVal = (-45.5+15)*Units.Length.inches;
	}

	public static class Telescope{
		public static int telescopeNum = 21;
		public static int resetNum = 7;

		public static double mass = 2.53139782*Units.Mass.kilograms;

		public static double gearRatio = 1/100;
		public static double radius = 37.5*Units.Length.milimeters;

		public static double retractVal = 0;
		public static double extendVal = 4890;

		public static double lenRetract = 24.2675*Units.Length.inches, lenExtend = 36.7009*Units.Length.inches;
		public static double comRetract = 0.3291279*Units.Length.meters, comExtend = 0.3996022*Units.Length.meters;

		public static double momentOfInertiaRetracted = 0.5029945;//Kg m^2
		public static double momentOfInertiaExtended = 0.73141154;//Kg m^2
	}

	public static class Gripper{
		public static int pivotNum = 40;
		public static int rollerNum = 41;
		public static int resetNum = 8;

		public static int[] hatchNums = {6,2};
		public static int gripNum = 5;

		public static double mass = 5.104*Units.Mass.kilograms;
		public static double momentOfInertia = 0.352305668;
		public static double comLength = 0.10397782*Units.Length.meters;
		public static double angleOffsetFromHatch = 0.51953129*Units.Angle.radians;
		
		public static double zeroDegVal = 3344;
		public static double ninetyDegVal = 0;
		public static double maxAngle = 90*Units.Angle.degrees; 
		public static double minAngle = -112*Units.Angle.degrees; 

		public static double com = 0, comWithBall = 0;
	}

	public static class Climber{
		public static int climbNum = 30;

		public static double topVal = 0;
		public static double bottomVal = -1987149;

		public static double topLen = 0;
		public static double bottomLen = 1;
	}

	public static void readRobotData() throws Exception{
		JSONParser parser = Util.getParser();
		Object tempObj = parser.parse(new FileReader("/home/lvuser/robot.json"));
		JSONObject fileObj = (JSONObject)tempObj;
		isCompBot = (boolean)fileObj.get("isCompBot");
	}
}

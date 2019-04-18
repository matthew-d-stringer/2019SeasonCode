package robot;

import java.io.FileReader;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
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
		public static TalonSRX leftEncoder = new TalonSRX(50); //was 22
		public static double wheelDiameter = 5.5, // inches
				wheelCircumference = wheelDiameter * Math.PI, // inches
				robotDiameter = 29; // inches (for estimating angle without a gyro)
	}

	public static class MainArm{
		public static int pivotNum = 20;
		public static int slaveNum = 22;
		public static int resetNum = 5;

		public static double mass = 7.14505445-3.39681515*Units.Mass.kilograms; //everything but the gripper

		public static double zeroDegVal = 297;
		public static double ninetyDegVal = 556;

		public static double insideAngle = -82*Units.Angle.degrees;
		
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
		// public static double extendVal = 4880;
		public static double extendVal = -6357;

		public static double lenRetract = 24.2675*Units.Length.inches, lenExtend = 36.7009*Units.Length.inches;
		public static double comRetract = 0.4908*Units.Length.meters, comExtend = 0.663*Units.Length.meters;

		public static double lenInside = 29.2675*Units.Length.inches;

		public static double momentOfInertiaRetracted = 2.226;//Kg m^2
		public static double momentOfInertiaExtended = 4.1849;//Kg m^2
	}

	public static class Gripper{
		public static int pivotNum = 40;
		public static int rollerNum = 41;
		public static int resetNum = 9;

		public static int[] gripNum = {6,2};

		public static double mass = 2.9661274*Units.Mass.kilograms;
		public static double momentOfInertia = 0.10709323;
		public static double comLength = 0.16288015*Units.Length.meters;
		public static double angleOffsetFromHatch = 0*Units.Angle.radians;
		
		public static double zeroDegVal = 309;
		public static double ninetyDegVal = 570;
		public static double maxAngle = 89*Units.Angle.degrees; 
		public static double minAngle = -112*Units.Angle.degrees; 
	}

	public static class GroundGripper{
		public static int pivotNum = 50; 
		public static int rollersNum = 51; 
		public static int resetNum = 8; 
		public static TalonSRX pivotEncoder = new TalonSRX(22); 

		//NOT ARM GRIPPER
		public static double encAt10 = 10781; 
		//NOT ARM GRIPPER
		public static double encAt90 = 5280; 
		//NOT ARM GRIPPER

		public static double maxAngle = 166*Units.Angle.degrees; 
		public static double ballGrabAngle = 39*Units.Angle.degrees; 
		public static double inClereance = 155*Units.Angle.degrees;
		public static double outClereance = 45*Units.Angle.degrees;

		public static double armClearenceAngle = 0*Units.Angle.degrees; 
		public static double inOutAngle = 130*Units.Angle.degrees; 

		public static double comOffsetAngle = -0.50483; 
	}

	public static class Climber{
		public static int climbNum = 30;
		public static int footNum = 31;

		public static double topVal = 0;
		public static double bottomVal = 308677;

		public static double topLen = 0;
		public static double bottomLen = 1;

		public static int[] guideNums = {7, 3}; //7 is down
		public static Value down = Value.kForward;
		public static Value up = Value.kReverse;
	}

	public static class CargoGripper{
		public static int rollerNum = 8; 
		public static int[] pivotNums = {0,5};
		public static int[] grabNums = {2,6};
	}

    public static class Image{
        public static int imageWidth = 320;
        public static int imageHeight = 240;
    }

    public static class Camera{
        public static double horizontalFov = 60*Units.Angle.degrees;
        public static double verticalFov = 45*Units.Angle.degrees;
	}

	public static void readRobotData() throws Exception{
		JSONParser parser = Util.getParser();
		Object tempObj = parser.parse(new FileReader("/home/lvuser/robot.json"));
		JSONObject fileObj = (JSONObject)tempObj;
		isCompBot = (boolean)fileObj.get("isCompBot");
	}
}

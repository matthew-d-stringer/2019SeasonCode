package robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import utilPackage.Units;

public class Constants {
	/*
	 * Add sub class for every subsystem
	 * should look something like this:
	 */
	
	public static int pressureSensorNum = 3;
	
	public static double robotWidth = 26*Units.Length.inches;
	
	public static class Drive{
		public static int MLeftNum = 10, MRightNum = 13;
		public static int[] slaveNums = {11,12, 14,15}; //left to right
		public static double wheelDiameter = 4.5, // inches
				wheelCircumference = wheelDiameter * Math.PI, // inches
				robotDiameter = 29; // inches (for estimating angle without a gyro)
		public static int[] shifterNums = {0,1,5};
		public static DoubleSolenoid.Value firstGear = DoubleSolenoid.Value.kReverse,
				secondGear = DoubleSolenoid.Value.kForward;
		
		public static double LeftMaxVel = 104,
				RightMaxVel = 100;
	}
	
	public static class Gripper{
		public static int pivotNum = 40,
				rollerLeftNum = 42,
				rollerRightNum = 41;
		public static int resetDIO = 0,
				cubeDIO = 2; //yeet
//		public static int[] grabNums = {0, 0, 4};
		public static int[] grabNums = {0, 1, 5};
		
		public static DoubleSolenoid.Value grabClosed = DoubleSolenoid.Value.kForward,
				grabOpen = DoubleSolenoid.Value.kReverse;
		
		public static double Resistence = 0,
				GearRatio = 1,
				Kt = 0.5,
				Kv = 0.5,
				radiusOfArm = 5* Units.Length.feet;
	}
	
	public static class Arm{
		public static int armNum = 32,
				roller = 21;
		public static int potPin = 0;
		public static  int[] grabNums = {1,1,5};
		public static DoubleSolenoid.Value grabOn = DoubleSolenoid.Value.kReverse,
				grabOff = DoubleSolenoid.Value.kForward;
	}
	
	public static class Elevator{
		public static int masterNum = 30,
				slaveNum = 31;
		public static int resetDIO = 1;
		public static int[] ptoNums = {1,0,4}; 
		public static DoubleSolenoid.Value climb = DoubleSolenoid.Value.kForward,
				normal = DoubleSolenoid.Value.kReverse;
		public static int[] safety = {1,2,6};
		public static DoubleSolenoid.Value safetyOn = DoubleSolenoid.Value.kReverse,
				safetyOff = DoubleSolenoid.Value.kForward;
		public static int[] shooterNums = {0,3,7};
		public static DoubleSolenoid.Value shoot = DoubleSolenoid.Value.kReverse,
				dontShoot = DoubleSolenoid.Value.kForward;
	}
}

package robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class Controls {
	// Joysticks
	public static Joystick stick = new Joystick(0);
	public static Joystick wheel = new Joystick(1);
	public static Joystick buttonPad = new Joystick(2);

	public static class Drive{
		public static JoystickButton slowSpeed = new JoystickButton(stick, 5);
		public static JoystickButton fastSpeed = new JoystickButton(stick, 3);
	}

	public static class Gripper{
		public static JoystickButton HoldSuck = new JoystickButton(stick, 1);
		public static JoystickButton ReverseSuck = new JoystickButton(wheel, 5);
		public static JoystickButton Down = new JoystickButton(stick, 6);
		public static JoystickButton Up = new JoystickButton(stick, 4);
		public static JoystickButton Reset = new JoystickButton(buttonPad, 7);
		public static JoystickButton Switch = new JoystickButton(buttonPad, 12);
		public static JoystickButton Rollers = new JoystickButton(stick, 1);
		public static JoystickButton Vault = new JoystickButton(buttonPad, 16);
		public static JoystickButton HighGrab = new JoystickButton(buttonPad, 6);
	}
	
	public static class Elevator{
		public static JoystickButton DownNow = new JoystickButton(buttonPad, 8);
		public static JoystickButton Zero = new JoystickButton(buttonPad, 9);
		public static JoystickButton Low = new JoystickButton(buttonPad, 15);
		public static JoystickButton High = new JoystickButton(buttonPad, 11);
		public static JoystickButton Up1 = new JoystickButton(stick, 2); 
		public static JoystickButton Down1 = new JoystickButton(stick, 7); 
		public static JoystickButton CashClear = new JoystickButton(buttonPad, 13); 
	}
	
	public static class Arm{
		public static JoystickButton Eject = new JoystickButton(buttonPad, 14);
		public static JoystickButton ExtraSuck = new JoystickButton(wheel, 5);
		public static JoystickButton Transfer = new JoystickButton(stick, 4);
		public static JoystickButton Flip = new JoystickButton(buttonPad, 1);
		public static JoystickButton DropCube = new JoystickButton(buttonPad, 2);
		public static JoystickButton UseReverseGoal = new JoystickButton(buttonPad, 3);
		public static JoystickButton Shoot = new JoystickButton(stick, 1);
		public static JoystickButton ContinueDown = new JoystickButton(stick, 1);
	}
	
	public static class Climb{
		public static JoystickButton ClimbMode = new JoystickButton(buttonPad, 5);
		public static JoystickButton SafetyOff = new JoystickButton(buttonPad, 20);
		public static JoystickButton Shoot = new JoystickButton(buttonPad, 20);
		public static JoystickButton climb = new JoystickButton(buttonPad, 19);
	}

	public static void setup(){
	}
	
	public static void periodic(){
	}
}
package utilPackage;

import controllers.Controller;
import drive.DriveOutput;
import drive.DriveOutput.Modes;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Robot;
import subsystems.MainArm;

public class FancyDrive {
	DriveOutput drive;

	double kWheelNonLinearity = 0.25;

	boolean enabled = true;
	
	public FancyDrive() {
		drive = DriveOutput.getInstance();
		
		SmartDashboard.putNumber("Wheel Linearity", kWheelNonLinearity);
	}
	
	public void enabled(boolean enable){
		enabled = enable;
	}
	
	@SuppressWarnings("unused")
	public void run(){
		if(Robot.getControlBoard().slowDrive()){
			kWheelNonLinearity = 0.1;
		}else{
			kWheelNonLinearity = 0.25;
		}
		double wheel = Robot.getControlBoard().getJoystickPos().getX()*1;
		boolean isQuickTurn = Robot.getControlBoard().quickTurn();
		// kWheelNonLinearity = SmartDashboard.getNumber("Wheel Linearity", kWheelNonLinearity);
		double outwheel = outputWheel(kWheelNonLinearity, wheel);

		double y = Robot.getControlBoard().getJoystickPos().getY();
		double rightVal = 12*(y+outwheel);
		double leftVal = 12*(y-outwheel);
		if(Robot.getControlBoard().slowDrive()){
			rightVal = Util.forceInRange(rightVal, -3, 3);
			leftVal = Util.forceInRange(leftVal, -3, 3);
		}
		if(enabled)
			drive.set(Modes.Voltage, rightVal, leftVal);
	}

	private double outputWheel(double wheelNonLinearity, double wheel){
		final double denominator = Math.sin(Math.PI / 2.0 * wheelNonLinearity);
		// Apply a sin function that's scaled to make it feel better.
		return Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / denominator;
	}
}

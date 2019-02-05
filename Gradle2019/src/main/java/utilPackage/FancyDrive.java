package utilPackage;

import controllers.Controller;
import drive.DriveOutput;
import drive.DriveOutput.Modes;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Robot;

public class FancyDrive {
	DriveOutput drive;

	double kWheelNonLinearity = 0.25;
	
	
	public FancyDrive() {
		drive = DriveOutput.getInstance();
		
		SmartDashboard.putNumber("Wheel Linearity", kWheelNonLinearity);
	}
	
	
	
	@SuppressWarnings("unused")
	public void run(){
		double wheel = Robot.getControlBoard().getJoystickPos().getX()*1;
		boolean isQuickTurn = Robot.getControlBoard().quickTurn();
		kWheelNonLinearity = SmartDashboard.getNumber("Wheel Linearity", kWheelNonLinearity);
		double outwheel = outputWheel(kWheelNonLinearity, wheel);

		double y = Robot.getControlBoard().getJoystickPos().getY();
		drive.set(Modes.Voltage, 12*(y+outwheel), 12*(y-outwheel));
	}

	private double outputWheel(double wheelNonLinearity, double wheel){
		final double denominator = Math.sin(Math.PI / 2.0 * wheelNonLinearity);
		// Apply a sin function that's scaled to make it feel better.
		return Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / denominator;
	}
}

package drive;

import coordinates.*;
import utilPackage.Units;

public class DriveController {
	private static DriveController instance = null;
	public static DriveController getInstance(){
		if(instance == null)
			instance = new DriveController();
		return instance;
	}

	public static double maximumSpeed = 16*Units.Length.feet;

	private double goalLeft, goalRight;
	private double dGoalLeft = 0, dGoalRight = 0;
	private double errorLeft, errorRight;
	
	private Coordinate feedForward, feedBack, output;

	private DriveController() {
		feedForward = new Coordinate(); 
		feedBack = new Coordinate(); 
		output = new Coordinate(); 
	}
	
	public void setSetpoint(Coordinate setpoint){
		goalRight = setpoint.getX();
		goalLeft = setpoint.getY();
	}
	
	public void setDSetpoint(Coordinate dSetpoint){
		dGoalRight = dSetpoint.getX();
		dGoalLeft = dSetpoint.getY();
	}
	
	public Coordinate run(Coordinate cVels){
		errorRight = goalRight - cVels.getX();
		errorLeft = goalLeft - cVels.getY();
		// System.out.println("Error Right: "+errorRight);
		// System.out.println("Error Left: "+errorLeft);

		feedBack.setX(0.0001974*errorLeft + 0.3814*errorRight);
		feedBack.setY(0.3814*errorLeft + 0.0001974*errorRight);
		
		feedForward.setX(0.01277*dGoalLeft + 0.4167*dGoalRight + 2.381e-17*goalLeft + 2.385*goalRight);
		feedForward.setY(0.4167*dGoalLeft + 0.01277*dGoalRight + 2.385*goalLeft + 1.406e-17*goalRight);
		
		output = feedForward.addC(feedBack);
		// output = feedForward;
		return output;
	}
	
	public Coordinate getOut(){
		return output;
	}
	
	public synchronized Coordinate getError(){
		return new  Coordinate(errorRight, errorLeft);
	}
}

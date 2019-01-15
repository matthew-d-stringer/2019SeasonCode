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

		feedBack.setX(6.237*errorRight - 0.03589*errorLeft);
		feedBack.setY(6.237*errorLeft - 0.03589*errorRight);
		
		feedForward.setX(1.756*dGoalRight - 0.5702*dGoalLeft - 7.427e-17*goalLeft + 4.702*goalRight);
		feedForward.setY(1.756*dGoalLeft - 0.5702*dGoalRight + 4.702*goalLeft - 7.427e-17*goalRight);
		
		output = feedForward.addC(feedBack);
		// output = feedForward;
		// output.mult(-1);
		return output;
	}
	
	public Coordinate getOut(){
		return output;
	}
	
	public synchronized Coordinate getError(){
		return new  Coordinate(errorRight, errorLeft);
	}
}

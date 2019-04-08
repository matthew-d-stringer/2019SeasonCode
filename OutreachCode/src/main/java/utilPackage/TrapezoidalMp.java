package utilPackage;

public class TrapezoidalMp {
	private double maxAccel, maxVel, setpoint;
	private double startSetpoint;
	
	private double endAccel;
	private double endConstSpeed;
	private double endDeccel;
	
	private int setpointSign = 1;
	
	private constraints mConstraints;
	
	public enum phase{
		accel,
		constSpeed,
		deccel,
		done;
	}
	
	public abstract class vector{
		double position;
		double velocity;
	}
	
	public static class constraints{
		public double maxAccel,
			maxVel,
			setpoint;
		
		public constraints(double _setpoint, double _maxVel, double _maxAccel){
			setpoint = _setpoint;
			maxVel = _maxVel;
			maxAccel = _maxAccel;
		}
	}
	
	@SuppressWarnings("unused")
	private vector initial;
	
	//TODO: Fix negative number setpoints
	
	public constraints getConstraints(){
		return mConstraints;
	}
	phase currentPhase = phase.accel;
	public TrapezoidalMp(constraints input) {
		 mConstraints = input;
		 maxAccel = input.maxAccel;
		 maxVel = input.maxVel;
		 setpoint = Math.abs(input.setpoint);
		 setpointSign = (int) (Math.abs(input.setpoint)/input.setpoint);
		 
		 double fullTrapDist = setpoint;
		 double accelTime = maxVel/maxAccel;
		 double fullSpeedDist = fullTrapDist - accelTime*accelTime*maxAccel;
		
		 if(fullSpeedDist < 0){
			 accelTime = Math.sqrt(fullTrapDist/maxAccel);
			 fullSpeedDist = 0;
		 }
		
		 currentPhase = phase.accel;
		 endAccel = accelTime;
		 endConstSpeed = endAccel + fullSpeedDist/maxVel;
		 endDeccel = endConstSpeed + accelTime;
	}
	public TrapezoidalMp(double startVal, constraints input) {
		 mConstraints = input;
		 startSetpoint = startVal;
		 maxAccel = input.maxAccel;
		 maxVel = input.maxVel;
		 setpoint = Math.abs(input.setpoint-startSetpoint);
		 setpointSign = (int) (Math.abs(input.setpoint-startSetpoint)/(input.setpoint-startSetpoint));
		 
		 double fullTrapDist = setpoint;
		 double accelTime = maxVel/maxAccel;
		 double fullSpeedDist = fullTrapDist - accelTime*accelTime*maxAccel;
		
		 if(fullSpeedDist < 0){
			 accelTime = Math.sqrt(fullTrapDist/maxAccel);
			 fullSpeedDist = 0;
		 }
		
		 currentPhase = phase.accel;
		 endAccel = accelTime;
		 endConstSpeed = endAccel + fullSpeedDist/maxVel;
		 endDeccel = endConstSpeed + accelTime;
	}	

	public TrapezoidalMp(constraints input, vector _initial) {
		 mConstraints = input;
		maxAccel = input.maxAccel;
		maxVel = input.maxVel;
		setpoint = input.setpoint;
		setpointSign = (int) (Math.abs(input.setpoint)/input.setpoint);
		
		double fullTrapDist = setpoint;
		double accelTime = maxVel/maxAccel;
		double fullSpeedDist = fullTrapDist - accelTime*accelTime*maxAccel;
		
		if(fullSpeedDist < 0){
			accelTime = Math.sqrt(fullTrapDist/maxAccel);
			fullSpeedDist = 0;
		}
		
		currentPhase = phase.accel;
		endAccel = accelTime;
		endConstSpeed = endAccel + fullSpeedDist/maxVel;
		endDeccel = endConstSpeed + accelTime;
	}
	
	public void updateConstraints(double startVal, constraints input){
		 mConstraints = input;
		 startSetpoint = startVal;
		 maxAccel = input.maxAccel;
		 maxVel = input.maxVel;
		 setpoint = Math.abs(input.setpoint);
		 setpointSign = (int) (Math.abs(input.setpoint)/input.setpoint);
		 
		 double fullTrapDist = setpoint;
		 double accelTime = maxVel/maxAccel;
		 double fullSpeedDist = fullTrapDist - accelTime*accelTime*maxAccel;
		
		 if(fullSpeedDist < 0){
			 accelTime = Math.sqrt(fullTrapDist/maxAccel);
			 fullSpeedDist = 0;
		 }
		
		 currentPhase = phase.accel;
		 endAccel = accelTime;
		 endConstSpeed = endAccel + fullSpeedDist/maxVel;
		 endDeccel = endConstSpeed + accelTime;
	}

	/**
	 * Calculates position and velocity setpoints
	 * @param time Current time since start, can be recorded from a timer
	 * @return Returns array {position, velocity}
	 */
	public double[] Calculate(double time){
		double relatedTime;
		double position = 0, velocity = 0, acceleration = 0;
		if(time < endAccel){
			relatedTime = time;
			velocity = maxAccel * relatedTime; //the integral of acceleration
			position += (maxAccel * relatedTime* relatedTime/2); //the integral of maxAccel * relatedTime (aka velocity)
			acceleration = maxAccel;
			currentPhase = phase.accel;
		}else if(time < endConstSpeed){
			relatedTime = time - endAccel;
			velocity = maxVel;
			position += (maxAccel * endAccel / 2)*endAccel + maxVel * relatedTime;
			acceleration = 0;
			currentPhase = phase.constSpeed;
		}else if(time <= endDeccel){
//			relatedTime = time - endConstSpeed;
			double TimeLeft = (endDeccel - time);
			velocity = maxAccel * TimeLeft;
			position = setpoint - (TimeLeft*TimeLeft*maxAccel/2);
			acceleration = -maxAccel;
			currentPhase = phase.deccel;
		}else{
			velocity = 0;
			position = setpoint;
			acceleration = 0;
			currentPhase = phase.done;
		}
		
//		SmartDashboard.putNumber("TrapezoidalMP::EndConstSpeed", endConstSpeed);
//		SmartDashboard.putNumber("TrapezoidalMP::EndDeccel", endDeccel);
//		SmartDashboard.putString("TrapezoidalMP::CurrentPhase", currentPhase.toString());
//		SmartDashboard.putNumber("TrapezoidalMP::time", time);
		
		double[] out  = {position*setpointSign+startSetpoint, velocity*setpointSign, 
				acceleration*setpointSign};
		return out;
	}
	
	public double getEndTime(){
		return endDeccel;
	}
	public phase getCurrentPhase(){
		return currentPhase;
	}
}

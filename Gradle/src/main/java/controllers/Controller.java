package controllers;

public abstract class Controller {
	public abstract double Calculate(double sensor);
	public abstract double getOut();	
	public abstract double getError();
	public abstract void setSetpoint(double setpoint);
	public abstract double getSetpoint();
	public abstract void reverseOutput(boolean reverse);
	public abstract void reset();
}
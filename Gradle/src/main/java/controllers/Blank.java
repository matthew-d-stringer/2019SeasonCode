package controllers;

public class Blank extends Controller {
	double setpoint, error;

	@Override
	public double Calculate(double sensor) {
		error = setpoint - sensor;
		return 0;
	}

	@Override
	public double getOut() {
		return 0;
	}

	@Override
	public double getError() {
		return error;
	}

	@Override
	public void setSetpoint(double _setpoint) {
		setpoint = _setpoint;
	}

	@Override
	public double getSetpoint() {
		return setpoint;
	}

	@Override
	public void reverseOutput(boolean reverse) {
	}

	@Override
	public void reset() {
	}
}

package controllers;

public class BangBang extends Controller{
	
	double setpoint, error;
	double minOut, maxOut, zeroRange;
	double output;
	boolean reverseOutput;
	
	public BangBang(double _setpoint, double _minOut, double _maxOut, double _zeroRange) {
		setpoint = _setpoint;
		zeroRange = _zeroRange;
		minOut = _minOut;
		maxOut = _maxOut;
	}
	
	@Override
	public double Calculate(double sensor) {
		error = setpoint - sensor;
		if(Math.abs(error) > zeroRange && error > 0)
			output = maxOut;
		else if (Math.abs(error) > zeroRange && error < 0)
			output = minOut;
		else
			output = 0;

		if(reverseOutput)
			output *= -1;

		return output;
	}

	@Override
	public double getOut() {
		return output;
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
		reverseOutput = reverse;
	}

	@Override
	public void reset() {
	}

}

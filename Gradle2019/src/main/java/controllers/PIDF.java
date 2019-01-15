package controllers;

import utilPackage.Derivative;
import utilPackage.Integrator;

import edu.wpi.first.wpilibj.Timer;

public class PIDF extends Controller {
	private double p,i,d,f;
	private double setpoint, error, sensor, out;
	
	private boolean useInputRange = false;
	private double inMin, inMax;

	private boolean useOutputRange = false;
	private double outMin, outMax;
	
	private Integrator iTerm;
	private Derivative dTerm;
	
	private Timer time;
	
	public PIDF() {
		time = new Timer();
		time.start();
		iTerm = new Integrator(0);
		dTerm = new Derivative();
	}
	
	public PIDF(double p, double i, double d, double f){
		time = new Timer();
		time.start();
		iTerm = new Integrator(0);
		dTerm = new Derivative();
		setPidf(p,i,d,f);
	}
	
	private double getTime(){
		return time.get();
	}
	
	public void setPidf(double p, double i, double d, double f){
		setP(p);
		setI(i);
		setD(d);
		setF(f);
	}
	
	public void useInputRange(boolean useInputRange){
		this.useInputRange = useInputRange;
	}
	public void setInputRange(double min, double max){
		inMin = min;
		inMax = max;
	}
	public void useOutputRange(boolean useInputRange){
		this.useOutputRange = useInputRange;
	}
	public void setOutputRange(double min, double max){
		outMin = min;
		outMax = max;
	}

	@Override
	public double Calculate(double sensor) {
		this.sensor = sensor;
		if(useInputRange){
			this.sensor = Math.max(this.sensor, inMin);
			this.sensor = Math.min(this.sensor, inMax);
		}
		error = setpoint - this.sensor;
		iTerm.calc(error, getTime());
		dTerm.Calculate(error, getTime());
		
		double pOut = p*error;
		double iOut = i*iTerm.getIntegral();
		double dOut = d*dTerm.getOut();
		double fOut = f*setpoint;
		out = pOut+iOut+dOut+fOut;
		if(useOutputRange){
			out = Math.max(out, outMin);
			out = Math.min(out, outMax);
		}
		return out;
	}

	@Override
	public double getOut() {
		return out;
	}

	@Override
	public double getError() {
		return error;
	}

	@Override
	public void setSetpoint(double setpoint) {
		this.setpoint = setpoint;
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
		iTerm.overrideVal(0);
	}

	
	
	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}

	public double getI() {
		return i;
	}

	public void setI(double i) {
		this.i = i;
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}
}

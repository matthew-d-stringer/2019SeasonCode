package utilPackage;

public class Integrator {
	private double integral = 0;
	double pTime = 0, pVal = 0;
	
	/**
	 * Sets up Integrator
	 * @param initial Initial value of the integral
	 */
	public Integrator(double initial) {
		integral = initial;
		pVal= initial;
		pTime = 0;
	}
	
	/**
	 * Calculates the integral live
	 * @param val new value to the integral
	 * @param dt change in time or independant variable
	 * @return new integral value
	 */
	public double calc(double val, double time){
		double dt = time - pTime;
		integral += (val+pVal)*dt/2;
		pTime = time;
		pVal = val;
		return integral;
	}
	
	public double getIntegral(){
		return integral;
	}
	
	public void overrideVal(double val){
		integral = val;
	}
}

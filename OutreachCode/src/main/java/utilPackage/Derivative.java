package utilPackage;
public class Derivative {
	double pVal = 0, val = 0, output = 0, pTime = 0;
	
	public Derivative() {
	}

	public void reset(double time, double val){
		pTime = time;
		pVal = val;
	}
	
	public double Calculate(double _val, double time){
		val = _val;
		if(time == pTime)
			output = 0;
		else
			output = (val-pVal)/(time - pTime);
		pTime = time;
		pVal = val;
		return output;
	}
	
	public double getOut(){
		return output;
	}
}

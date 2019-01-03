package utilPackage;

public class LowPassFilter {
	double alpha = 0.4;
	double emaS = 0;
	
	public LowPassFilter() {
	}
	
	public LowPassFilter(double _alpha){
		alpha = _alpha;
	}

	public void setup(double init){
		emaS = init;
	}
	
	public double run(double input){
		emaS = (alpha*input)+((1-alpha)*emaS);
		return emaS;
	}
	
	public double getOut(){
		return emaS;
	}
}

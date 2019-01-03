package utilPackage;

public class HighestReadVal {
	private double max = 0;
	
	public HighestReadVal() {
		max = 0;
	}
	
	public void reset(){
		max = 0;
	}
	
	public double run(double currentVal){
		if(Math.abs(currentVal) > max)
			max = Math.abs(currentVal);
		return max;
	}
	
	public double getMax(){
		return max;
	}
}

package utilPackage;

import coordinates.Coordinate;

import java.util.List;

import org.json.simple.parser.JSONParser;

public class Util {
	private static JSONParser parser = null;
	public static JSONParser getParser(){
		if(parser == null)
			parser = new JSONParser();
		return parser;
	}

	public static double round(double val, double places){
		return (double)(Math.round(val * Math.pow(10, places)))/Math.pow(10, places);
	}

	public static double forceInRange(double val, double min, double max){
		double out = val;
		out = Math.min(out, max);
		out = Math.max(out, min);
		return out;
	}

	public static double average(List<Double> in){
		double total = 0;
		for(int i = 0; i < in.size(); i++){
			total += in.get(i);
		}
		return total/in.size();
	}

	public static double checkSign(double val){
		return val/Math.abs(val);
	}
	
	public static double onOffOut(double cTime, double onTimePeriod, double onTimeOut, double offTimePeriod, double offTimeOut){
		double periodTime = onTimePeriod + offTimePeriod;
		double cycleTime = cTime % periodTime;
		if(cycleTime > onTimePeriod)
			return onTimeOut;
		else
			return offTimeOut;
	}

	public static double outputDeadband(double input, double inputMax, double outMin, double outMax){
		double out;
		if(input > outMin)
			out = (outMax-outMin)*input/inputMax+outMin;
		else if(input < -outMin)
			out = (outMax-outMin)*input/inputMax-outMin;
		else
			out = 0;
		return out;
	}
	
	public static double sinWaveOccilation(double time, double center, double occilationFactor, double period){
		double timeMultiplier = (2*Math.PI)/period;
		double out = occilationFactor*Math.sin(timeMultiplier*time)+center;
		return out;
	}
	
	public static double signlessError(double in1, double in2){
		return Math.abs(in2-in1);
	}
	public static boolean inErrorRange(double in1, double in2, double range){
		return Math.abs(in2-in1) < range;
	}
	
	/**
	 * Maps the sensor value from one range of values to another
	 * @param sensor input sensor value
	 * @param pt1 what one val should turn into (x->y)
	 * @param pt2 what another val should turn into (x->y)
	 * @return new mapped sensor value
	 */
	public static double mapRange(double sensor, Coordinate pt1, Coordinate pt2){
		double x = sensor;
		double y = ((pt2.getY() - pt1.getY())/(pt2.getX()-pt1.getX()))*(x-pt1.getX()) + pt1.getY();
		return y;
	}

	public static double slope(Coordinate pt1, Coordinate pt2){
		return (pt2.getY() - pt1.getY())/(pt2.getX() - pt1.getX());
	}
	

}

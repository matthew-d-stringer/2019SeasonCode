package utilPackage;

public class Units {
	public static class Length{
		//Everything is converted to Meters
		public final static double meters = 1;
		public final static double milimeters = 0.001;
		public final static double inches = 0.0254;
		public final static double feet = 0.3048;
		public final static double radians = 0.06985; //Diameter = 5.5
	}
	
	public static class Angle{
		//Everything is converted to Radians 
		public final static double radians = 1;
		public final static double degrees = 0.01745329252;
		public final static double revolutions = 6.2832;
		public final static double encoderTicks = 0.0015340; //4096 encoder ticks per revolution
		// public final static double encoderTicks = 6.2832; //4096 encoder ticks per revolution
	}
	
	public static class Mass{
		//Everything is converted to kilograms
		public final static double kilograms = 1; 
		public final static double grams = 0.001;
		public final static double pounds = 2.20462;
		public final static double ounces = 35.274;
	}
	
	public static class Time{
		//Everything converts to seconds
		public final static double milliseconds = 0.001; 
		public final static double seconds = 1; 
		public final static double minutes = 60; 
		public final static double hours = 3600;
		public final static double days = 86400;
	}
	
	public static class Velocity{
		public final static double talonVel = 0.042185; //A.encoderTicks*L.radians/0.1*T.seconds
	}
	
	public static double convertUnits(double value, double to){
		return value/to;
	}
	public static double convertUnits(double value, double from, double to){
		return value*from/to;
	}
}

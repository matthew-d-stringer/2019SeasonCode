package coordinates;

import utilPackage.Util;

public class Heading extends Coordinate {

	public static Heading createPolarHeading(double angle, double mag){
		return new Heading(mag*Math.cos(angle), mag*Math.sin(angle));
	}

	public Heading(){
		x = 0;
		y = 1;
	}
	public Heading(double _x, double _y) {
		super(_x, _y);
	}
	public Heading(double angle){
		super(Math.cos(angle), Math.sin(angle));
	}
	public Heading(Heading c){
		super(c);
	}
	
	public void setAngle(double angle){
		setXY(Math.cos(angle), Math.sin(angle));
	}
	
	public void setRobotAngle(double angle){
		setXY(Math.sin(angle), Math.cos(angle));
	}
	
	public Heading perpendicularCCw(){
		Heading New = perpendicularCCwC();
		x = New.x;
		y = New.y;
		return this;
	}
	
	public Heading perpendicularCCwC(){
		return new Heading(-this.getY(), this.getX());
	}
	
	/**
	 * Finds the perpendicular based on a 90 deg clockwise rotation and updates current object
	 * @return 90 deg rotation
	 */
	public Heading perpendicularCw(){
		Heading New = perpendicularCwC();
		x = New.x;
		y = New.y;
		return this;
	}
	/**
	 * Finds the perpendicular based on a 90 deg clockwise rotation
	 * @return 90 deg rotation
	 */
	public Heading perpendicularCwC(){
		return new Heading(this.getY(), -this.getX());
	}
	
	public double getAngle(){
		return Math.atan2(y, x);
	}
	
	public Heading inverseC() {
		return new Heading(x, -y);
	}
	
	public static Heading headingBetween(Coordinate pt1, Coordinate pt2){
//		double angle = Math.atan2(pt2.getY() - pt1.getY(), pt2.getX() - pt1.getX());
//		Heading out = new Heading(angle);
		Heading out = new Heading(pt2.getX()-pt1.getX(), pt2.getY()-pt1.getY());
		return out;
	}
	
	public static double headingsToAngle(Heading startHeading, Heading robotPos) {
		double cAngle = Math.acos(Coordinate.dotProduct(startHeading.normalizeC(), robotPos.normalizeC()));
		return cAngle;
	}

	public static double signedHeadingsToAngle(Heading right, Heading left) {
		double cAngle = Math.acos(Coordinate.dotProduct(right.normalizeC(), left.normalizeC()));
		cAngle *= Util.checkSign(crossProduct(right, left));
		return cAngle;
	}
	
	public static Heading addHeadings(Heading hed1, Heading hed2){
		Heading out = new Heading();
		out.setXY(hed1.getX()+hed2.getX(), hed1.getY()+hed2.getY());
		return out;
	}
	
	public static Heading toHeading(Coordinate pt){
		Heading out = new Heading(pt.x, pt.getY());
		return out;
	}
}
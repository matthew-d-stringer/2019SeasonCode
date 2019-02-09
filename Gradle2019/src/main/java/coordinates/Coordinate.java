package coordinates;

import utilPackage.Util;

public class Coordinate {
	protected double x;
	protected double y;
	
	public Coordinate(){
		setX(0);
		setY(0);
	}
	public Coordinate(double _x, double _y){
		setX(_x);
		setY(_y);
	}
	
	public Coordinate(Coordinate c){
		setX(c.x);
		setY(c.y);
	}
	
	public Coordinate copy(){
		return this;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}
	public void setXMaintainMag(double x, boolean invert){
		double length = getMagnitude();
		double y = Math.sqrt(length*length - x*x);
		this.x = x;
		if(invert)
			this.y = -y;
		else
			this.y = y;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	public void setYMaintainMag(double y, boolean invert){
		double length = getMagnitude();
		double x = Math.sqrt(length*length - y*y);
		if(invert)
			this.x = -x;
		else
			this.x = x;
		this.y = y;
	}


	public void setXY(double x, double y){
		this.x = x;
		this.y = y;
	}

	public void setXY(double[] coord){
		x = coord[0];
		y = coord[1];
	}
	
	public double[] getXY(){
		double[] out = {x, y};
		return out;
	}
	
	public Coordinate add(Coordinate c){
		x += c.getX();
		y += c.getY();
		return this;
	}
	public Coordinate addC(Coordinate c){
		Coordinate New = new Coordinate();
		New.x = this.x+c.x;
		New.y = this.y+c.y;
		return New;
	}
	public Coordinate sub(Coordinate c){
		x -= c.getX();
		y -= c.getY();
		return this;
	}
	public Coordinate subC(Coordinate c){
		Coordinate out = new Coordinate();
		out.x = this.x - c.x;
		out.y = this.y - c.y;
		return out;
	}
	public Coordinate mult(double c){
		x *= c;
		y *= c;
		return this;
	}
	public Coordinate mult(double cx, double cy){
		x *= cx;
		y *= cy;
		return this;
	}
	public Coordinate multC(double c){
		Coordinate out = new Coordinate();
		out.x = c*this.x;
		out.y = c*this.y;
		return out;
	}
	public Coordinate multC(double cx, double cy){
		Coordinate out = new Coordinate();
		out.x = cx*this.x;
		out.y = cy*this.y;
		return out;
	}
	
	public Coordinate changeBasis(Coordinate iHat, Coordinate jHat){
		this.setX(x*iHat.x+y*jHat.x);
		this.setX(x*iHat.y+y*jHat.y);
		return this;
	}
	
	public Coordinate changeBasisC(Coordinate iHat, Coordinate jHat){
		Coordinate out = new Coordinate();
		out.setX(x*iHat.x+y*jHat.x);
		out.setX(x*iHat.y+y*jHat.y);
		return out;
	}
	
	public Coordinate rotateByC(Heading heading) {
		return new Coordinate(x * heading.getX() - y * heading.getY(), x * heading.getY() + y * heading.getY());
	}
	
	public Coordinate inverseC() {
		return new Coordinate(-x, -y);
	}
	
	public Heading heading(){
		return Heading.toHeading(this);
	}
	
	public String display(){
		String out = "[ "+x+", "+y+" ]";
		return out;
	}
	
	public String display(String name){
		StringBuilder out = new StringBuilder();
		out.append(name+": ");
		out.append(display());
		return out.toString();
	}

	public static double DistanceBetween(Coordinate from, Coordinate to){
		double dX = to.getX() - from.getX();
		double dY = to.getY() - from.getY();
		double distance = Math.hypot(dX, dY);
		return distance;
	}
	
	public static double SlopeBetween(Coordinate from, Coordinate to){
		double dX = to.getX() - from.getX();
		double dY = to.getY() - from.getY();
		return dY/dX;
	}
	
	public static Coordinate Midpoint(Coordinate pt1, Coordinate pt2){
		Coordinate out = new Coordinate();
		out.setX((pt1.getX()+pt2.getX())/2);
		out.setY((pt1.getY()+pt2.getY())/2);
		return out;
	}
	
	public static double dotProduct(Coordinate pt1, Coordinate pt2){
		return pt1.getX()*pt2.getX()+pt1.getY()*pt2.getY();
	}
	
	public static double crossProduct(Coordinate pt1, Coordinate pt2) {
		return pt1.getX() * pt2.getY() - pt1.getY() * pt2.getX();
	}
	
	public Coordinate normalize(){
		return this.mult(1/getMagnitude());
	}
	public Coordinate normalizeC(){
		return this.multC(1/getMagnitude());
	}
	public Coordinate setMagnitude(double mag){
		this.normalize();
		this.mult(mag);
		return this;
	}
	public double getMagnitude(){
		return Math.hypot(this.getX(), this.getY());
	}
	
	public static double getAngleBetween(Coordinate pt1, Coordinate pt2){
		double dotProduct = Coordinate.dotProduct(pt1, pt2);
		double cosOut = dotProduct/(pt1.getMagnitude()*pt2.getMagnitude());
		Util.forceInRange(cosOut, -0.99999, 0.9999);
		double out = Math.acos(cosOut);
		return out;
	}
	
	public String outputData(){
		return ("X: "+this.getX()+"\t Y: "+this.getY());
	}
}

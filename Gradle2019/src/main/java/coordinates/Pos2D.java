package coordinates;

public class Pos2D {
	private static double t;
	private Coordinate position;
	private Heading heading;

	public Pos2D(){
		position = new Coordinate();
		heading = new Heading();
	}
	
	public Pos2D(Pos2D copy){
		this.position = new Coordinate(copy.position);
		this.heading = new Heading(copy.heading);
	}
	
	public Pos2D(Coordinate position, Heading heading){
		this.position = position;
		this.heading = heading;
	}	
	
	public Coordinate getPos() {
		return position;
	}
	
	public Pos2D copy(){
		try {
			return (Pos2D) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setPos(Coordinate position) {
		this.position = new Coordinate(position);
	}
	
	public void setPos(double x, double y){
		position = new Coordinate(x, y);
	}
	
	public Pos2D transformByC(Pos2D other) {
		return new Pos2D(position.addC(other.getPos().rotateByC(heading)), 
				heading.rotateByC(other.getHeading()).heading());
	}
	
	public Pos2D inverseC() {
		Heading rotInvert = heading.inverseC();
		return new Pos2D(position.inverseC().rotateByC(rotInvert), rotInvert);
	}

	public static Pos2D createVector(Coordinate pt1, Coordinate pt2){
		Coordinate start = new Coordinate(pt1);
		Heading change = new Heading(pt2.subC(pt1).heading());
		Pos2D out = new Pos2D(start, change);
		return out;
	}
	
	public Coordinate getEndPos(){
		return position.addC(heading);
	}

	public Heading getHeading() {
		return heading;
	}

	public Pos2D setHeading(Heading heading) {
		this.heading = heading;
		return this;
	}
	public Pos2D setHeading(double x, double y) {
		this.heading = new Heading(x, y);
		return this;
	}
	
	public Coordinate multC(double c){
		Pos2D out = new Pos2D(this);
		return out.getHeading().multC(c).addC(out.getPos());
	}
	
	public static Coordinate intersectionBetween(Pos2D pt1, Pos2D pt2){
		Coordinate pos1 = pt1.getPos(), pos2 = pt2.getPos();
		Heading heading1 = pt1.getHeading(), heading2 = pt2.getHeading();
		float tan_b = (float) (heading2.getY()/heading2.getX());
		t = ((pos1.getX()-pos2.getX())*tan_b+pos2.getY()-pos1.getY())/(heading1.getY() - heading1.getX()*tan_b);
		Coordinate out = pos1.addC(heading1.multC(t));
		return out;
	}
	
	public static double getTFromIntersectionBetween(){
		return t;
	}
	
	public String outputData(){
		return (this.getPos().outputData()+"\t Heading: "+this.getHeading().outputData());
	}
}

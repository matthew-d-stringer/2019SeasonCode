package splines;

import coordinates.Coordinate;
import coordinates.Pos2D;

public class CubicSpline implements Spline{
    private Pos2D start, end;
    private Coordinate a, b, c, d;

    public CubicSpline(){
    }

    public void setPoints(Pos2D start, Pos2D end){
        this.start = start;
        this.end = end;
        
        createConstants();
    }

    private void createConstants(){
        final Coordinate P0 = new Coordinate(start.getPos());
        final Coordinate V0 = new Coordinate(start.getHeading());
        final Coordinate P1 = new Coordinate(end.getPos());
        final Coordinate V1 = new Coordinate(end.getHeading());

        a = P0;
        b = V0;

        c = new Coordinate(P1.multC(3));
        c.sub(P0.multC(3));
        c.sub(V0.multC(2));
        c.sub(V1);

        d = new Coordinate(V1);
        d.add(V0);
        d.add(P0.multC(2));
        d.sub(P1.multC(2));
    }

    public Coordinate calculatePosition(double percent){
        double t = percent;
        Coordinate out = new Coordinate(a);
        out.add(b.multC(t));
        out.add(c.multC(t*t));
        out.add(d.multC(t*t*t));
        return out;
    }
    public Coordinate calculateVelocity(double percent){
        double t = percent;
        Coordinate out = new Coordinate(b);
        out.add(c.multC(2*t));
        out.add(d.multC(3*t*t));
        return out;
    }

    public String display(){
        return a.display("A") + "\n" + b.display("B") + "\n" + c.display("C") + "\n" + d.display("D");
    }
}
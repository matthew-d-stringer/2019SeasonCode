package splines;

import coordinates.Coordinate;
import coordinates.Pos2D;

public class QuinticSpline implements Spline{
    private Pos2D start, end;

    public QuinticSpline(){
    }

    @Override
    public void setPoints(Pos2D start, Pos2D end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public Coordinate calculatePosition(double percent) {
        double t = percent;
        Coordinate out = new Coordinate();

        out.add(start.getPos().multC(h0(t)));
        out.add(start.getHeading().multC(h1(t)));

        out.add(end.getPos().multC(h5(t)));
        out.add(end.getHeading().multC(h4(t)));
        return out;
    }

    private double h0(double t){
        return 1-10*t*t*t+15*t*t*t*t-6*t*t*t*t*t*t;
    }
    private double h1(double t){
        return t-6*t*t*t+8*t*t*t*t-3*t*t*t*t*t;
    }
    private double h2(double t){
        return 0.5*t*t-1.5*t*t*t+1.5*t*t*t*t-0.5*t*t*t*t*t;
    }
    private double h3(double t){
        return 0.5*t*t*t-t*t*t*t+0.5*t*t*t*t*t;
    }
    private double h4(double t){
        return -4*t*t*t+7*t*t*t*t-3*t*t*t*t*t;
    }
    private double h5(double t){
        return 10*t*t*t-15*t*t*t*t+6*t*t*t*t*t*t;
    }
}
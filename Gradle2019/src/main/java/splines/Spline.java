package splines;

import coordinates.Coordinate;
import coordinates.Pos2D;

public abstract interface Spline{
    public void setPoints(Pos2D start, Pos2D end);
    public Coordinate calculatePosition(double percent);
}
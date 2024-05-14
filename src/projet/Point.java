	package src.projet;

import java.util.Objects;

public class Point {

	private double x;
	private double y;
    
    public Point(double x, double y) {
        this.x = x;
        this.y=y;
    }
    public Point() {
        this(0,0); 
    }
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	
	}
	@Override
	public String toString() {
		return "Point : ("+x+","+y+")";
	}

	@Override
		public boolean equals(Object obj) {
    	if (this == obj) {
        	return true;
    	}
    	if (!(obj instanceof Point)) {
        	return false;
    	}
    	Point other = (Point) obj;
    	final double EPSILON = 1e-10;  
    	return Math.abs(this.x - other.x) < EPSILON && Math.abs(this.y - other.y) < EPSILON;
	}
	@Override
	public int hashCode() {
    	return Objects.hash(x, y);
	}

}


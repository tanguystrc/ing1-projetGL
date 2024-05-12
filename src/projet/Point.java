package src.projet;

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
		return "Point : ("+x+")";
	}

	public boolean equals(Object obj) {
	    if (!(obj instanceof Point)) {
	        return false; 
	    }
	    
	    Point otherPoint = (Point) obj; 
	    return this.getX() == otherPoint.getX();
	}
	public Point calculerVecteur(Point p1, Point p2) {
		Point p=new Point();
		p.setX(p1.getX()-p2.getX());
		p.setY(p1.getY()-p2.getY());
		return p;
	}


}


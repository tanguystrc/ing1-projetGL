package src.projet;
/**
 * Class représentant les segments pour le morphisme de visage
 */
public class Segment {
	private Point debut;
	private Point fin;
	
	public Segment(Point debut, Point fin) {
		this.debut = debut;
		this.fin = fin;
	}
	
	public Segment() {
		this.debut = new Point();
		this.fin = new Point();
	}
	
	public Segment(double x1,double y1,double x2,double y2) {
		Point d = new Point (x1,y1);
		Point f = new Point(x2,y2);
		this.debut=d;
		this.fin=f;
	}
	
	public Point getDebut() {
		return debut;
	}
	public void setDebut(Point debut) {
		this.debut = debut;
	}
	public Point getFin() {
		return fin;
	}
	public void setFin(Point fin) {
		this.fin = fin;
	}
	
	@Override
	public String toString() {
		return "("+debut.toString()+fin.toString()+")";
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Segment) {
			Segment s = (Segment) o;
			if(debut.equals(s.getDebut())&& fin.equals(s.getFin())){
				return true;
			}
		}
		return false;
	}
	

}


package src.projet.traitement;
/**
 * Class représentant les segments pour le morphing de visage
 */
public class Segment {
	private Point debut;
	private Point fin;
	
	/**
	 * Constructeur de la classe Segment
	 * @param debut Premier point
	 * @param fin Second point
	 */
	public Segment(Point debut, Point fin) {
		this.debut = debut;
		this.fin = fin;
	}
	
	/**
	 * Constructeur de la classe Segment par défault
	 */
	public Segment() {
		this.debut = new Point();
		this.fin = new Point();
	}
	
	/**
	 * Constructeur de la classe Segment en rentrant les coordonnées des extrémités
	 * @param x1 Abscisse du premier point
	 * @param y1 Ordonnée du premier point
	 * @param x2 Abscisse du second point
	 * @param y2 Abscisse du second point
	 */
	public Segment(double x1,double y1,double x2,double y2) {
		Point d = new Point (x1,y1);
		Point f = new Point(x2,y2);
		this.debut=d;
		this.fin=f;
	}
	
	/**
	 * Getteur du premier point
	 * @return Le premier point
	 */
	public Point getDebut() {
		return debut;
	}

	/**
	 * Setteur du premier point
	 * @param debut Point
	 */
	public void setDebut(Point debut) {
		this.debut = debut;
	}

	/**
	 * Getteur du second Point
	 * @return Le second point
	 */
	public Point getFin() {
		return fin;
	}

	/**
	 * Setteur du second point
	 * @param fin Point
	 */
	public void setFin(Point fin) {
		this.fin = fin;
	}
	
	/**
	 * Transformer un segment en String
	 * @return Le Segment sous forme ((x1,y1),(x2,y2))
	 */
	@Override
	public String toString() {
		return "("+debut.toString()+fin.toString()+")";
	}
	
	/**
	 * Fonction equals pour les Segments
	 * @param o Object
	 * @return True si le segment est identique à celui en paramètre
	 */
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


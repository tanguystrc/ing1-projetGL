package src.projet.traitement;
/**
 * Classe Point avec une abscisse et une ordonnée
 */
public class Point {
    private double x;
    private double y;

    /**
     * Constructeur de la classe Point
     * @param x Abscisse
     * @param y Ordonnée
     */
    public Point(double x, double y) {
        setX(x);
        setY(y);
    }

    /**
     * Constructeur par default
     */
    public Point() {
        this(0, 0);
    }

    /**
     * Getteur de l'abscisse
     * @return double l'abscisse
     */
    public double getX() {
        return x;
    }

    /**
     * Setteur de l'abscisse
     * @param x double
     */
    public void setX(double x) {
     
            this.x = x;

    }

    /**
     * Getteur de l'ordonnée
     * @return double l'ordonnée
     */
    public double getY() {
        return y;
    }

    /**
     * Setteur de l'ordonnée
     * @param y double
     */
    public void setY(double y) {
     
            this.y = y;
        
    }

    /**
     * Retourne la distance entre deux point
     * @param p Point
     * @return La distance sous forme d'un double
     */
    public double distance(Point p) {
        double dx = this.x - p.x;
        double dy = this.y - p.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calcule le vecteur entre deux points.
     * @param p1 le premier point
     * @param p2 le deuxième point
     * @return le vecteur résultant du calcul
     */
    public static Point calculerVecteur(Point p1, Point p2) {
        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        return new Point(deltaX, deltaY);
    }

    /**
     * Fonction ToString pour les points
     * @return (x,y)
     */
    @Override
    public String toString() {
        return "Point : (" + x + "," + y + ")";
    }

    /**
     * Fonction equals pour les points
     * @param o Object
     * @return True si l'argument et identique au point
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point p = (Point) obj;
        final double EPSILON = 1e-10;
        return Math.abs(this.x - p.x) < EPSILON && Math.abs(this.y - p.y) < EPSILON;
    }

    /**
     * Renvoie la somme des deux points
     * @param p Point
     * @return La somme des point
     */
    public Point somme(Point p) {
		return new Point(x+p.getX(),y+p.getY());
	}
	
    /**
     * Renvoie la diffèrence entre le point et le point argument
     * @param p Point
     * @return La soustraction entre les deux points
     */
	public Point soustraction(Point p) {
		return new Point(x-p.getX(),y-p.getY());
	}
	
    /**
     * Renvoie le vecteur perpendiculaire 
     * @return Point symbolisant le vecteur perpendiculaire
     */
	public Point perp() {
		return new Point(y,-x);
	}
	
    /**
     * Renvoie la norme du vecteur
     * @return La norme
     */
	public double norme() {
		return Math.sqrt(x*x+y*y);
	}

	/**
     * Produit scalaire en prenant les points pour des vecteurs
     * @param p
     * @return
     */
	public double produit(Point p) {
		return x*p.getX()+y*p.getY();
	}
	
    /**
     * Renvoie le produit du point avec un scalaire
     * @param v Double
     * @return Le produit
     */
	public Point produit(double v) {
		return new Point(x*v,y*v);
	}

    /**
     * Renvoie le pixel le plus proche du point
     * @return Un point en valeur entière
     */
    public Point pixel(){
        return new Point(Math.round(x),Math.round(y));
    }
}

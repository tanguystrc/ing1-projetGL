package src.projet.traitement;

import java.util.Objects;

public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        setX(x);
        setY(y);
    }

    public Point() {
        this(0, 0);
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

    public double distance(Point other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
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

    @Override
    public String toString() {
        return "Point : (" + x + "," + y + ")";
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

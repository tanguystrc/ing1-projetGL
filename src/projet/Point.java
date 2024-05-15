package src.projet;

import java.util.Objects;

public class Point {

    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        this(0, 0);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        if (x >= 0 && x <= 600) {
            this.x = x;
        } else {
            throw new IllegalArgumentException("La valeur de x doit être comprise entre 0 et 600.");
        }
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        if (y >= 0 && y <= 600) {
            this.y = y;
        } else {
            throw new IllegalArgumentException("La valeur de y doit être comprise entre 0 et 600.");
        }
    }

    public double distance(Point other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
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
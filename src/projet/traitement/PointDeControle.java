package src.projet.traitement;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PointDeControle {
    private List<Couple<Point, Point>> pointsList;

    public PointDeControle() {
        this.pointsList = new LinkedList<>();
    }

    /**
     * Constructeur qui fait une copie profonde des valeurs.
     * @param p
     */
    public PointDeControle(PointDeControle p) {
        this.pointsList = new LinkedList<>();
        for (Couple<Point, Point> couple : p.getPointsList()) {
            ajouter(new Point(couple.getA().getX(), couple.getA().getY()),
                    new Point(couple.getB().getX(), couple.getB().getY()));
        }
    }

    public List<Couple<Point, Point>> getPointsList() {
        return pointsList;
    }

    public void setPointsList(List<Couple<Point, Point>> pointsList) {
        this.pointsList = new LinkedList<>(pointsList);
    }

    public void ajouter(Point key, Point value) {
        if (isValidPoint(key) && isValidPoint(value)) {
            this.pointsList.add(new Couple<>(key, value));
        } else {
            throw new IllegalArgumentException("Les coordonnées des points doivent être comprises entre 0 et 600.");
        }
    }

    public void supprimer(Point key) {
        pointsList.removeIf(couple -> couple.getA().equals(key));
    }

    private boolean isValidPoint(Point p) {
        return p.getX() >= 0 && p.getX() <= 600 && p.getY() >= 0 && p.getY() <= 600;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Couple<Point, Point> couple : pointsList) {
            builder.append("Key: ").append(couple.getA().toString()).append(" -> Value: ").append(couple.getB().toString()).append("\n");
        }
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointsList);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PointDeControle other = (PointDeControle) obj;
        return Objects.equals(pointsList, other.pointsList);
    }
}

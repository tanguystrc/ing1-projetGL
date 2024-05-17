package src.projet;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

public class PointDeControle {
    private Map<Point, Point> pointsMap;

    public PointDeControle() {
        this.pointsMap = new LinkedHashMap<>();
    }

    public Map<Point, Point> getPointsMap() {
        return pointsMap;
    }

    public void setPointsMap(Map<Point, Point> pointsMap) {
        this.pointsMap = new LinkedHashMap<>(pointsMap);
    }

    public void ajouter(Point key, Point value) {
        if (isValidPoint(key) && isValidPoint(value)) {
            this.pointsMap.put(key, value);
        } else {
            throw new IllegalArgumentException("Les coordonnées des points doivent être comprises entre 0 et 600.");
        }
    }

    public void supprimer(Point key) {
        this.pointsMap.remove(key);
    }

    private boolean isValidPoint(Point p) {
        return p.getX() >= 0 && p.getX() <= 600 && p.getY() >= 0 && p.getY() <= 600;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Point, Point> entry : pointsMap.entrySet()) {
            builder.append("Key: ").append(entry.getKey().toString()).append(" -> Value: ").append(entry.getValue().toString()).append("\n");
        }
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointsMap);
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
        return Objects.equals(pointsMap, other.pointsMap);
    }
}

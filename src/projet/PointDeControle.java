package src.projet;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class PointDeControle {
    private Map<Point, Point> pointsMap;

    public PointDeControle() {
        this.pointsMap = new LinkedHashMap<>();
    }

    /**
     * Constructeur qui a ses valeurs en copie profonde
     * @param p
     */
    public PointDeControle(PointDeControle p) {
        this.pointsMap = new LinkedHashMap<>();
        for (Entry<Point, Point> entry : p.getPointsMap().entrySet()) {        
        	ajouter( new Point(entry.getKey().getX(),entry.getKey().getY()), new Point(entry.getValue().getX(),entry.getValue().getY()));
        }
    }

    public void supprimer(Point pointA) {
        pointsMap.remove(pointA);
    }

    public Map<Point, Point> getPointsMap() {
        return pointsMap;
    }

    public void setPointsMap(Map<Point, Point> pointsMap) {
        this.pointsMap = new LinkedHashMap<>(pointsMap);
    }

    public void ajouter(Point key, Point value) {
        this.pointsMap.put(key, value);
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
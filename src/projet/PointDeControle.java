package src.projet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PointDeControle {
    private Map<Point, Point> pointsMap;
    private List<Point> listePoint;

    public PointDeControle() {
        this.pointsMap = new HashMap<>();
        this.listePoint = new ArrayList<>();
    }

    public Map<Point, Point> getPointsMap() {
        return pointsMap;
    }

    public void setPointsMap(Map<Point, Point> pointsMap) {
        this.pointsMap = pointsMap;
    }

    public List<Point> getListePoint() {
        return listePoint;
    }

    public void setListePoint(List<Point> listePoint) {
        this.listePoint = listePoint;
    }

    public void ajouter(Point key, Point value) {
        this.pointsMap.put(key, value);
        this.listePoint.add(key); 
    }
    public Point calculerVecteur(Point p1, Point p2) {
		Point p=new Point();
		p.setX(p1.getX()-p2.getX());
		p.setY(p1.getY()-p2.getY());
		return p;
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
        return Objects.hash(pointsMap, listePoint);
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
        return Objects.equals(pointsMap, other.pointsMap) && Objects.equals(listePoint, other.listePoint);
    }
}
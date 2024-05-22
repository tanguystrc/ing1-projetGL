package src.projet;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

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
        System.out.println("A Map : "+pointsMap);
        pointsMap.remove(pointA);    
        System.out.println("B Map : "+pointsMap);   
    }

    public void modifierPoint(Point oldPointA, Point newPointA){

        if (pointsMap.containsKey(oldPointA)) {
            // On doit recreer la map si on veut garder le même ordre (aaaAAAAAAA)
            // Optimisation bonjouuuur
            Map<Point, Point> newMap = new LinkedHashMap<>();
            Point pointB = pointsMap.get(oldPointA);//donne la valeur, donc le pointB
            for (Entry<Point, Point> entry : pointsMap.entrySet()) {
                if (entry.getKey().equals(oldPointA)) {
                    newMap.put(newPointA, pointB);
                } else {
                    newMap.put(entry.getKey(), entry.getValue());
                }
            }
            pointsMap.clear();
            pointsMap = newMap;
        }
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

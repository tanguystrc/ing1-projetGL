package src.projet.traitement;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Liste de point pris en compte pour les diffèrents morphings
 */
public class PointDeControle {
    private List<Couple<Point, Point>> pointsList;

    /**
     * Constructeur par défault
     */
    public PointDeControle() {
        this.pointsList = new LinkedList<>();
    }

    /**
     * Constructeur qui fait une copie profonde des valeurs.
     * @param p PointDeControle
     */
    public PointDeControle(PointDeControle p) {
        this.pointsList = new LinkedList<>();
        for (Couple<Point, Point> couple : p.getPointsList()) {
            ajouter(new Point(couple.getA().getX(), couple.getA().getY()),
                    new Point(couple.getB().getX(), couple.getB().getY()));
        }
    }

    /**
     * Getteur de liste de couple point
     * @return Liste de de couple Point
     */
    public List<Couple<Point, Point>> getPointsList() {
        return pointsList;
    }

    /**
     * Setteur de la liste de couple point
     * @param pointsList Liste de Couple de Point
     */
    public void setPointsList(List<Couple<Point, Point>> pointsList) {
        this.pointsList = new LinkedList<>(pointsList);
    }

    /**
     * Ajouter un couple de Point à la liste
     * @param p1 Premier point
     * @param p2 Second point
     */
    public void ajouter(Point p1, Point p2) {
        if (estValidePoint(p1) && estValidePoint(p2)) {
            this.pointsList.add(new Couple<>(p1, p2));
        } else {
            throw new IllegalArgumentException("Les coordonnées des points doivent être comprises entre 0 et 600.");
        }
    }

    /**
     * Supprimer un couple de la liste
     * @param p1 Premier Point
     */
    public void supprimer(Point p1) {
        pointsList.removeIf(couple -> couple.getA().equals(p1));
    }

    /**
     * Vérifie si le point est dans l'image
     * @param p Point
     * @return True si le point de dans l'image
     */
    private boolean estValidePoint(Point p) {
        return p.getX() >= 0 && p.getX() <= 600 && p.getY() >= 0 && p.getY() <= 600;
    }

    /**
     * Fonction toString implémentée aux points de contrôle
     * @return Une colonne des couple dans PointDeControle
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Couple<Point, Point> couple : pointsList) {
            builder.append(couple.toString()).append("\n");
        }
        return builder.toString();
    }

    /**
     * Fonction equals implémentée aux point de
     * @param o Object
     * @return True si les objets sont identique
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PointDeControle p = (PointDeControle) obj;
        return Objects.equals(pointsList, p.pointsList);
    }
}

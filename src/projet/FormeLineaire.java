package src.projet;


import java.util.List;
import java.awt.Color;
import java.util.Arrays;

public class FormeLineaire extends Forme {
    private Color[][] matrix1;
    private Color[][] matrix2;

    public FormeLineaire(PointDeControle pointsDeControle, int nbFrame, Color[][] matrix1, Color[][] matrix2) {
        super(pointsDeControle, null, null, nbFrame);
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FormeLineaire other = (FormeLineaire) obj;
        return Arrays.deepEquals(matrix1, other.matrix1) && Arrays.deepEquals(matrix2, other.matrix2);
    }

        /**
     * Vérifie si un point donné est à l'intérieur d'un polygone défini par une liste de points.
     * 
     * @param listePoint la liste des points définissant le polygone.
     * @param p le point à vérifier.
     * @return true si le point est à l'intérieur du polygone, false sinon.
     */
    public boolean estDomaine(List<Point> listePoint, Point p) {
        int compteur = 0;
        int nbPts = listePoint.size();
        Point dernierPoint = listePoint.get(nbPts - 1);
    
        for (Point pointActuel : listePoint) {
            // Vérifie si p est exactement sur un segment horizontal ou vertical
            if (pointActuel.getY() == p.getY() && dernierPoint.getY() == p.getY()) {
                // Segment horizontal
                if ((pointActuel.getX() <= p.getX() && p.getX() <= dernierPoint.getX()) || 
                    (dernierPoint.getX() <= p.getX() && p.getX() <= pointActuel.getX())) {
                    return true; // Le point est sur un segment horizontal
                }
            } else if (pointActuel.getX() == p.getX() && dernierPoint.getX() == p.getX()) {
                // Segment vertical
                if ((pointActuel.getY() <= p.getY() && p.getY() <= dernierPoint.getY()) || 
                    (dernierPoint.getY() <= p.getY() && p.getY() <= pointActuel.getY())) {
                    return true; // Le point est sur un segment vertical
                }
            } else {
                // Vérifie les intersections avec le segment actuel
                if ((pointActuel.getY() < p.getY() && dernierPoint.getY() >= p.getY()) || 
                    (pointActuel.getY() >= p.getY() && dernierPoint.getY() < p.getY())) {
                    double intersectX = pointActuel.getX() + (p.getY() - pointActuel.getY()) * (dernierPoint.getX() - pointActuel.getX()) / (dernierPoint.getY() - pointActuel.getY());
                    if (p.getX() < intersectX) {
                        compteur++;
                    }
                }
            }
            dernierPoint = pointActuel;
        }
        return compteur % 2 != 0;
    }
   
    

    
}

package src.projet;

import java.awt.Color;
import java.util.List;

public class FormeArrondie extends Forme{
    private Color[][] matrice1;
    private Color[][] matrice2;

    public FormeArrondie(PointDeControle pointsDeControle, int nbFrame, Color[][] matrice1, Color[][] matrice2) {
        super(pointsDeControle, null, null, nbFrame);
        this.matrice1 = matrice1;
        this.matrice2 = matrice2;
    }

    public Color[][] getMatrice1() {
        return matrice1;
    }

    public void setMatrice1(Color[][] matrice1) {
        this.matrice1 = matrice1;
    }

    public Color[][] getMatrice2() {
        return matrice2;
    }

    public void setMatrice2(Color[][] matrice2) {
        this.matrice2 = matrice2;
    }

    public boolean estDomaine(List<Point> listePoints, Point point) {
        int traversées = 0;
        int nbPts = listePoints.size();
    
        // Boucle pour traiter chaque courbe de Bézier
        for (int i = 0; i < nbPts - 1; i += 3) {
            Point p0 = listePoints.get(i);
            Point p1 = listePoints.get((i + 1) % nbPts);
            Point p2 = listePoints.get((i + 2) % nbPts);
            Point p3 = listePoints.get((i + 3) % nbPts);
    
            traversées += traverseCourbe(p0, p1, p2, p3, point);
        }
    
        // Ajouter une courbe pour relier le dernier point au premier point
        if (nbPts % 3 == 0) {
            Point p0 = listePoints.get(nbPts - 1);
            Point p1 = listePoints.get((nbPts - 2 + nbPts) % nbPts);
            Point p2 = listePoints.get((nbPts - 3 + nbPts) % nbPts);
            Point p3 = listePoints.get(0);
    
            traversées += traverseCourbe(p0, p1, p2, p3, point);
        }
    
        return (traversées % 2 != 0);
    }
    
    private int traverseCourbe(Point p0, Point p1, Point p2, Point p3, Point point) {
        final int etapes = 20; // Nombre de points d'évaluation sur la courbe
        double[] xPoints = new double[etapes];
        double[] yPoints = new double[etapes];
        int traversées = 0;
    
        for (int i = 0; i < etapes; i++) {
            double t = (double) i / (etapes - 1);
            double x = Math.pow(1 - t, 3) * p0.getX() +
                       3 * t * Math.pow(1 - t, 2) * p1.getX() +
                       3 * Math.pow(t, 2) * (1 - t) * p2.getX() +
                       Math.pow(t, 3) * p3.getX();
            double y = Math.pow(1 - t, 3) * p0.getY() +
                       3 * t * Math.pow(1 - t, 2) * p1.getY() +
                       3 * Math.pow(t, 2) * (1 - t) * p2.getY() +
                       Math.pow(t, 3) * p3.getY();
    
            xPoints[i] = x;
            yPoints[i] = y;
        }
    
        for (int i = 0; i < etapes - 1; i++) {
            if (segmentTraverseLigne(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1], point.getX(), point.getY())) {
                traversées++;
            }
        }
    
        return traversées;
    }
    
    private boolean segmentTraverseLigne(double x1, double y1, double x2, double y2, double px, double py) {
        // Évite les segments horizontaux pour le comptage des traversées
        if (y1 == y2) {
            return false;
        }
    
        // Évite les segments verticaux pour le comptage des traversées
        if (x1 == x2) {
            return px == x1 && ((y1 <= py && py <= y2) || (y2 <= py && py <= y1));
        }
    
        // Vérifie les traversées pour les segments non horizontaux et non verticaux
        boolean intersect = ((y1 > py) != (y2 > py)) && (px < (x2 - x1) * (py - y1) / (y2 - y1) + x1);
        return intersect;
    }
    
}

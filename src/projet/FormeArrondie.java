package src.projet;

import java.awt.Color;
import java.util.List;

public class FormeArrondie {
    private Color[][] matrice1;
    private Color[][] matrice2;

    public FormeArrondie(Color[][] matrice1, Color[][] matrice2) {
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

        for (int i = 0; i < listePoints.size() - 3; i += 3) {
            Point p0 = listePoints.get(i);
            Point p1 = listePoints.get(i + 1);
            Point p2 = listePoints.get(i + 2);
            Point p3 = listePoints.get(i + 3);

            if (traverseCourbe(p0, p1, p2, p3, point)) {
                traversées++;
            }
        }

        return (traversées % 2 != 0);
    }

    private boolean traverseCourbe(Point p0, Point p1, Point p2, Point p3, Point point) {
        final int étapes = 500; // Nombre de points d'évaluation sur la courbe
        double[] xPoints = new double[étapes];
        double[] yPoints = new double[étapes];

        for (int i = 0; i < étapes; i++) {
            double t = (double) i / (étapes - 1);
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

        for (int i = 0; i < étapes - 1; i++) {
            if (segmentTraverseLigne(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1], point.getX(), point.getY())) {
                return true;
            }
        }

        return false;
    }

    private boolean segmentTraverseLigne(double x1, double y1, double x2, double y2, double px, double py) {
        if (((y1 <= py && py < y2) || (y2 <= py && py < y1)) &&
            px < ((x2 - x1) * (py - y1) / (y2 - y1) + x1)) {
            return true;
        }

        return false;
    }
}

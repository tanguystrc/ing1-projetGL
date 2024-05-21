package src.projet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;

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



    @Override
    public BufferedImage morphismeRemplissage(Color[][] matrix, Color couleur, Color autreCouleur, List<Point> points) {
        int hauteur = matrix.length;
        int largeur = matrix[0].length;
        Color[][] newMatrix = new Color[hauteur][largeur];
        List<Point> pointsIntermediaires =  calculerPointsBezier(points);
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (estDomaine(pointsIntermediaires, new Point(x, y))) {
                    newMatrix[y][x] = couleur;
                } else {
                    newMatrix[y][x] = autreCouleur;
                }
            }
        }
    
        return genereImage(newMatrix);
    }


    public List<Point> calculerPointsBezier(List<Point> listePoints) {
        List<Point> pointsIntermediaires = new ArrayList<>();
        final int etapes = 50;
        for (int i = 0; i < listePoints.size() - 3; i += 3) {
            Point p0 = listePoints.get(i);
            Point p1 = listePoints.get(i + 1);
            Point p2 = listePoints.get(i + 2);
            Point p3 = listePoints.get(i + 3);
    
            for (int j = 0; j < etapes; j++) {
                double t = (double) j / (etapes - 1);
                double x = Math.pow(1 - t, 3) * p0.getX() +
                           3 * t * Math.pow(1 - t, 2) * p1.getX() +
                           3 * Math.pow(t, 2) * (1 - t) * p2.getX() +
                           Math.pow(t, 3) * p3.getX();
                double y = Math.pow(1 - t, 3) * p0.getY() +
                           3 * t * Math.pow(1 - t, 2) * p1.getY() +
                           3 * Math.pow(t, 2) * (1 - t) * p2.getY() +
                           Math.pow(t, 3) * p3.getY();
    
                pointsIntermediaires.add(new Point(x, y));
            }
        }
        return pointsIntermediaires;
    }
}

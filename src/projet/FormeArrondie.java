package src.projet;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FormeArrondie extends Forme {

    public FormeArrondie(PointDeControle pointsDeControle, int nbFrame) {
        super(pointsDeControle, null, null, nbFrame);
    }

    @Override
    public BufferedImage morphismeRemplissage(BufferedImage image, int couleur, int autreCouleur, List<Point> points) {
        int hauteur = image.getHeight();
        int largeur = image.getWidth();
        BufferedImage newImage = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_ARGB);
        List<Point> pointsIntermediaires = calculerPointsBezier(points);

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (estDomaine(pointsIntermediaires, new Point(x, y))) {
                    newImage.setRGB(x, y, couleur);
                } else {
                    newImage.setRGB(x, y, autreCouleur);
                }
            }
        }

        return newImage;
    }

    public List<Point> calculerPointsBezier(List<Point> listePoints) {
        List<Point> pointsIntermediaires = new ArrayList<>();
        final int etapes = 40;
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

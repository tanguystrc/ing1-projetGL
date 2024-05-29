package src.projet.traitement;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe FormeArrondie représente une forme avec des contours arrondis
 * basée sur des courbes de Bézier. Elle hérite de la classe Forme.
 */
public class FormeArrondie extends Forme {

    /**
     * Constructeur de la classe FormeArrondie.
     *
     * @param pointsDeControle Les points de contrôle pour générer la forme arrondie.
     * @param nbFrame Le nombre de frames pour l'animation.
     * @param ajouteGIF Le fichier GIF à ajouter.
     * @param avant Indique si l'animation est avant (true) ou après (false).
     */
    public FormeArrondie(PointDeControle pointsDeControle, int nbFrame, File ajouteGIF, boolean avant) {
        super(pointsDeControle, null, null, nbFrame, ajouteGIF, avant);
    }

    /**
     * Applique un morphisme de remplissage sur l'image en utilisant les couleurs spécifiées
     * et une liste de points pour définir la forme.
     *
     * @param image L'image de base à remplir.
     * @param couleur La couleur à utiliser pour remplir la forme.
     * @param autreCouleur La couleur à utiliser pour l'extérieur de la forme.
     * @param points La liste de points définissant la forme.
     * @return Une nouvelle image avec la forme remplie.
     */
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

    /**
     * Calcule une liste de points intermédiaires en utilisant des courbes de Bézier.
     *
     * @param listePoints La liste de points de contrôle pour les courbes de Bézier.
     * @return Une liste de points intermédiaires représentant les courbes de Bézier.
     */
    public List<Point> calculerPointsBezier(List<Point> listePoints) {
        List<Point> pointsIntermediaires = new ArrayList<>();
        final int etapes = 40; //Nombre de points intermédiaires
        for (int i = 0; i < listePoints.size() - 3; i += 3) {
            Point p0 = listePoints.get(i);
            Point p1 = listePoints.get(i + 1);
            Point p2 = listePoints.get(i + 2);
            Point p3 = listePoints.get(i + 3);

            for (int j = 0; j < etapes; j++) {
                double t = (double) j / (etapes - 1);
                //On calcule les coordonnées x des points
                double x = Math.pow(1 - t, 3) * p0.getX() +
                           3 * t * Math.pow(1 - t, 2) * p1.getX() +
                           3 * Math.pow(t, 2) * (1 - t) * p2.getX() +
                           Math.pow(t, 3) * p3.getX();
                //On calcule les coordonnées y des points
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

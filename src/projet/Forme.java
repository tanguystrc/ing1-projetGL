package src.projet;

import java.awt.image.BufferedImage;
import java.util.List;
import java.awt.Color;

/**
 * Classe abstraite représentant une forme avec des points de contrôle et des images associées.
 */
public abstract class Forme {
    
    protected PointDeControle pointsDeControle;
    protected BufferedImage image1;
    protected BufferedImage image2;
    protected int nbFrame;

    public Forme(PointDeControle pointsDeControle, BufferedImage image1, BufferedImage image2, int nbFrame) {
        this.pointsDeControle = pointsDeControle;
        this.image1 = image1;
        this.image2 = image2;
        this.nbFrame = nbFrame;
    }

    /**
     * Génère une matrice de couleurs à partir d'une image.
     * 
     * @param image l'image source.
     * @return une matrice 2D de couleurs représentant les pixels de l'image.
     */
    public Color[][] genererMatrice(BufferedImage image) {
        int largeur = image.getWidth();
        int hauteur = image.getHeight();
        Color[][] colorMatrix = new Color[hauteur][largeur];

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                int rgb = image.getRGB(x, y);
                colorMatrix[y][x] = new Color(rgb, true);
            }
        }
        return colorMatrix;
    }

    /**
     * Génère une image à partir d'une matrice de couleurs.
     * 
     * @param matrix la matrice 2D de couleurs.
     * @return une image BufferedImage créée à partir de la matrice de couleurs.
     */
    public BufferedImage genereImage(Color[][] matrix) {
        int hauteur = matrix.length;
        int largeur = matrix[0].length;
        BufferedImage image = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (matrix[y][x] != null) {
                    Color color = matrix[y][x];
                    int rgb = color.getRGB();
                    image.setRGB(x, y, rgb);
                }
            }
        }
        return image;
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
            if (pointActuel.getY() == p.getY() && dernierPoint.getY() == p.getY() && 
                ((pointActuel.getX() <= p.getX() && p.getX() <= dernierPoint.getX()) || (dernierPoint.getX() <= p.getX() && p.getX() <= pointActuel.getX()))) {
                return true; // Le point est sur un segment horizontal
            }
    
            if ((pointActuel.getY() < p.getY() && dernierPoint.getY() > p.getY()) || 
                (pointActuel.getY() > p.getY() && dernierPoint.getY() < p.getY())) {
                if (pointActuel.getY() == dernierPoint.getY()) {
                    // Cas de segment horizontal déjà traité plus haut
                } else {
                    double denom = (pointActuel.getY() - dernierPoint.getY());
                    if (denom != 0) {
                        double pointIntersectX = dernierPoint.getX() + (pointActuel.getX() - dernierPoint.getX()) * ((p.getY() - dernierPoint.getY()) / denom);
                        if (p.getX() < pointIntersectX) {
                            compteur++;
                        }
                    }
                }
            } else if (pointActuel.getY() == p.getY() && dernierPoint.getY() == p.getY()) {
                // Ignorer les segments horizontaux
            } else if (pointActuel.getY() == p.getY() || dernierPoint.getY() == p.getY()) {
                if (p.getX() < Math.min(pointActuel.getX(), dernierPoint.getX()) || p.getX() > Math.max(pointActuel.getX(), dernierPoint.getX())) {
                    continue; // Point p est en dehors du segment horizontal
                }
                // Compteur pour segments verticaux
                if (pointActuel.getX() == p.getX() && dernierPoint.getX() == p.getX()) {
                    return true; // Le point est sur un segment vertical
                }
            }
            dernierPoint = pointActuel;
        }
        return compteur % 2 != 0;
    }

    /**
     * Cherche une couleur à l'intérieur d'un polygone dans une matrice de couleurs.
     * 
     * @param matrix la matrice 2D de couleurs.
     * @param pts la liste des points définissant le polygone.
     * @return la couleur trouvée à l'intérieur du polygone, ou null si aucune couleur n'est trouvée.
     */
    public Color chercheCouleur(Color[][] matrix, List<Point> pts) {
        int hauteur = matrix.length;
        int largeur = matrix[0].length;
        Point p = new Point();

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                p.setX(x);
                p.setY(y);
                if (estDomaine(pts, p)) {
                    //afficher la couleur trouvée
                    System.out.println("Couleur trouvée : " + matrix[y][x]);
                    return matrix[y][x];
                }
            }
        }
        
        return null;
    }

    /**
     * Cherche une couleur à l'extérieur d'un polygone dans une matrice de couleurs.
     * 
     * @param matrix la matrice 2D de couleurs.
     * @param pts la liste des points définissant le polygone.
     * @return la couleur trouvée à l'extérieur du polygone, ou null si aucune couleur n'est trouvée.
     */
    public Color chercheAutreCouleur(Color[][] matrix, List<Point> pts) {
        int hauteur = matrix.length;
        int largeur = matrix[0].length;
        Point p = new Point();

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                p.setX(x);
                p.setY(y);
                if (!estDomaine(pts, p)) {
                    return matrix[y][x];
                }
            }
        }
        return null;
    }
}

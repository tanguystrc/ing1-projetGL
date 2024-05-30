package projet.traitement;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import projet.gif.GifSequenceWriter;

/**
 * Classe représentant une forme avec des points de contrôle pour le morphing.
 */
public class Forme {
    
    protected PointDeControle pointsDeControle;
    protected BufferedImage image1;
    protected BufferedImage image2;
    protected int nbFrame;
    private javafx.scene.paint.Color couleurSelectionne;
    protected File ajouteGIF;
    protected boolean avant;

    /**
     * Constructeur de la classe Forme.
     * @param pointsDeControle Les points de contrôle de la forme.
     * @param image1 L'image de départ.
     * @param image2 L'image d'arrivée.
     * @param nbFrame Le nombre de frames pour l'animation.
     * @param ajouteGIF fichier du gif s'il faut en ajouter un
     * @param avant : vrai si le Gif a ajouter est avant celui actuel
     */
    public Forme(PointDeControle pointsDeControle, BufferedImage image1, BufferedImage image2, int nbFrame, File ajouteGIF, boolean avant) {
        this.pointsDeControle = pointsDeControle;
        this.image1 = image1;
        this.image2 = image2;
        this.nbFrame = nbFrame;
        this.ajouteGIF = ajouteGIF;
        this.avant = avant;
    }

    /**
     * Définit la couleur sélectionnée pour le morphing.
     * @param couleurSelectionne La couleur sélectionnée.
     */
    public void setCouleurSelectionne(javafx.scene.paint.Color couleurSelectionne) {
        this.couleurSelectionne = couleurSelectionne;
    }

    /**
     * Calcule le vecteur de déplacement entre deux points.
     * @param p1 Le premier point.
     * @param p2 Le deuxième point.
     * @return Le vecteur de déplacement entre p1 et p2.
     */
    public static Point calculerVecteur(Point p1, Point p2) {
        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        return new Point(deltaX, deltaY);
    }

    /**
     * Génère une liste de vecteurs de déplacement pour chaque point de contrôle.
     * @param pointsDeControle Les points de contrôle.
     * @param nbFrame Le nombre de frames pour l'animation.
     * @return La liste des vecteurs de déplacement pour chaque point de contrôle.
     */
    public List<Point> listIndice(PointDeControle pointsDeControle, int nbFrame) {
        List<Point> p = new ArrayList<>();
        for (Couple<Point, Point> couple : pointsDeControle.getPointsList()) {
            Point pointA = couple.getA();
            Point pointB = couple.getB();
            Point indice = calculerVecteur(pointA, pointB);
            indice.setX(indice.getX() / nbFrame);
            indice.setY(indice.getY() / nbFrame);
            p.add(indice);
        }
        return p;
    }

    /**
     * Effectue le morphing entre deux images en générant une animation GIF.
     * @param image1 L'image de départ.
     * @param pointsDeControle Les points de contrôle.
     * @param nbFrame Le nombre de frames pour l'animation.
     * @param dureeGIF La durée du GIF en secondes.
     * @param barreChargement Fonction pour mettre à jour la progression de l'animation.
     * @throws IOException En cas d'erreur lors de l'écriture du GIF.
     */
    public void morphisme(BufferedImage image1, PointDeControle pointsDeControle, int nbFrame, int dureeGIF, BiConsumer<Integer, Integer> barreChargement) throws IOException {
        // Calcule les vecteurs de déplacement pour chaque point entre chaque image de transition :
        List<Point> listIndice = listIndice(pointsDeControle, nbFrame); 
        // Récupère les points de contrôle de l'image de départ
        List<Point> pointsKeys = new ArrayList<>();
        for (Couple<Point, Point> couple : pointsDeControle.getPointsList()) {
            pointsKeys.add(couple.getA());
        }

        // Détermine les couleurs à utiliser pour le remplissage et l'arrière-plan :
        int couleur = (couleurSelectionne != null) ? new java.awt.Color((int)(couleurSelectionne.getRed() * 255), (int)(couleurSelectionne.getGreen() * 255), (int)(couleurSelectionne.getBlue() * 255)).getRGB() : chercheCouleur(image1, pointsKeys);
        int autreCouleur = chercheAutreCouleur(image1, pointsKeys);

        ImageOutputStream output = new FileImageOutputStream(new File("animation.gif"));
        GifSequenceWriter gifWriter = new GifSequenceWriter(output, image1.getType(), (dureeGIF * 1000) / nbFrame, true);


        int hauteur = image1.getHeight();
        int largeur = image1.getWidth();
        // Crée chaque frame de l'animation :
        for (int i = 0; i < nbFrame; i++) {
            List<Point> listPoint = new ArrayList<>(); 
            for (int j = 0; j < pointsKeys.size(); j++) {
                Point p = pointsKeys.get(j);
                Point indice = listIndice.get(j);
                double x = Math.max(0, Math.min(largeur - 1, p.getX() + indice.getX() * i));
                double y = Math.max(0, Math.min(hauteur - 1, p.getY() + indice.getY() * i));
                Point p1 = new Point(x, y); 
                listPoint.add(p1);
            }
            // Remplit la nouvelle image en utilisant les nouveaux points de controles :
            BufferedImage frameImage = morphismeRemplissage(image1, couleur, autreCouleur, listPoint);
            gifWriter.writeToSequence(frameImage, (dureeGIF * 1000) / nbFrame);
            barreChargement.accept(i + 1, nbFrame);
        }

        // Génère la dernière image en utilisant les derniers points de controles :
        List<Point> listPointArrivee = new ArrayList<>();
        for (Couple<Point, Point> couple : pointsDeControle.getPointsList()) {
            listPointArrivee.add(couple.getB());
        }
        BufferedImage imageArrivee = morphismeRemplissage(image1, couleur, autreCouleur, listPointArrivee);
        gifWriter.writeToSequence(imageArrivee, (dureeGIF * 1000) / nbFrame);
        
        // Terminé, ferme le GIF :
        gifWriter.close();
        output.close();
    }

    /**
     * Remplit l'image en fonction des points de contrôle et des couleurs spécifiées.
     * @param image L'image de base.
     * @param couleur La couleur de la forme.
     * @param autreCouleur La couleur de fond.
     * @param points Les points de contrôle.
     * @return L'image remplie.
     */
    public BufferedImage morphismeRemplissage(BufferedImage image, int couleur, int autreCouleur, List<Point> points) {
        int hauteur = image.getHeight();
        int largeur = image.getWidth();
        BufferedImage newImage = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_ARGB);
    
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (estDomaine(points, new Point(x, y))) {
                    newImage.setRGB(x, y, couleur);
                } else {
                    newImage.setRGB(x, y, autreCouleur);
                }
            }
        }
    
        return newImage;
    }

    /**
     * Détermine si un point est à l'intérieur d'un polygone défini par une liste de points de contrôle.
     * @param listePoint La liste des points de contrôle définissant le polygone.
     * @param p Le point à tester.
     * @return true si le point est à l'intérieur du polygone, false sinon.
     */
    public boolean estDomaine(List<Point> listePoint, Point p) {
        int compteur = 0;
        int nbPts = listePoint.size();
        Point dernierPoint = listePoint.get(nbPts - 1);
    
        for (Point pointActuel : listePoint) {
            // Vérifie si les points sont sur la même ligne horizontale pour traiter ce cas a part (gestion d'erreur)
            if (pointActuel.getY() == p.getY() && dernierPoint.getY() == p.getY()) {
                // Vérifie si le point p est entre les points pointActuel et dernierPoint sur l'axe X
                if ((pointActuel.getX() <= p.getX() && p.getX() <= dernierPoint.getX()) || 
                    (dernierPoint.getX() <= p.getX() && p.getX() <= pointActuel.getX())) {
                    return true; 
                }
            }else {
                // Vérifie si la ligne horizontale passant par p croise le segment formé par pointActuel et dernierPoint et ajoute au compteur
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
        return compteur % 2 != 0; // Si le compteur est impair, le point est à l'intérieur du polygone
    }

    /**
     * Cherche une couleur différente de la couleur de fond à l'intérieur de la forme pour si les points ne sont pas parfaitement placés
     * @param image L'image de base.
     * @param pts Les points de contrôle définissant la forme.
     * @return La couleur trouvée.
     */
    public int chercheCouleur(BufferedImage image, List<Point> pts) {
        int hauteur = image.getHeight();
        int largeur = image.getWidth();
        Point p = new Point();
        int autreCouleur = chercheAutreCouleur(image, pts);
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                p.setX(x);
                p.setY(y);
                if (autreCouleur != image.getRGB(x, y) && estDomaine(pts, p)) {
                    return image.getRGB(x + 5, y + 5);
                }
            }
        }
        return 0; 
    }

    /**
     * Cherche une couleur de fond à l'extérieur de la forme.
     * @param image L'image de base.
     * @param pts Les points de contrôle définissant la forme.
     * @return La couleur de fond trouvée.
     */
    public int chercheAutreCouleur(BufferedImage image, List<Point> pts) {
        int hauteur = image.getHeight();
        int largeur = image.getWidth();
        Point p = new Point();
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                p.setX(x);
                p.setY(y);
                if (!estDomaine(pts, p)) {
                    return image.getRGB(x, y);
                }
            }
        }
        return 0; 
    }
}

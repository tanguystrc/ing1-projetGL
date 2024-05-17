package src.projet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.util.List;
import java.awt.Color;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Arrays;

public class FormeLineaire extends Forme {
    private Color[][] matrix1;
    private Color[][] matrix2;
    private javafx.scene.paint.Color selectedColor;

    public FormeLineaire(PointDeControle pointsDeControle, int nbFrame, Color[][] matrix1, Color[][] matrix2) {
        super(pointsDeControle, null, null, nbFrame);
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
    }

    public void setSelectedColor(javafx.scene.paint.Color selectedColor) {
        this.selectedColor = selectedColor;
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
    /**
     * Applique le morphisme simple pour générer une animation GIF.
     * @param image1 l'image source
     * @param pointsDeControle les points de contrôle
     * @param nbFrame le nombre de frames
     * @throws IOException si une erreur d'entrée/sortie se produit
     */
    public void morphismeSimple(BufferedImage image1, PointDeControle pointsDeControle, int nbFrame) throws IOException {
        Color[][] matrix = genererMatrice(image1);
        List<Point> listIndice = listIndice(pointsDeControle, nbFrame); 
        List<Point> pointsKeys = new ArrayList<>(pointsDeControle.getPointsMap().keySet());
        Color couleur = (selectedColor != null) ? new Color((int)(selectedColor.getRed() * 255), (int)(selectedColor.getGreen() * 255), (int)(selectedColor.getBlue() * 255)) : chercheCouleur(matrix, pointsKeys);
        Color autreCouleur = chercheAutreCouleur(matrix, pointsKeys);
    
        ImageOutputStream output = new FileImageOutputStream(new File("animation.gif"));
        GifSequenceWriter gifWriter = new GifSequenceWriter(output, image1.getType(), 100, true);
    
        int hauteur = matrix.length;
        int largeur = matrix[0].length;
    
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
            BufferedImage frameImage = morphismeRemplissage(matrix, couleur, autreCouleur, listPoint);
            
            gifWriter.writeToSequence(frameImage);
        }
    
        // Ajouter l'image d'arrivée en utilisant les valeurs de la map en entrée
        List<Point> listPointArrivee = new ArrayList<>();
        for (Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            listPointArrivee.add(entry.getValue());
        }
        BufferedImage imageArrivee = morphismeRemplissage(matrix, couleur, autreCouleur, listPointArrivee);
        gifWriter.writeToSequence(imageArrivee);
    
        gifWriter.close();
        output.close();
    }
    
    

    
}

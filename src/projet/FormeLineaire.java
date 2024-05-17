package src.projet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.util.List;
import java.awt.Color;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

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
     * Calcule le vecteur entre deux points.
     * @param p1 le premier point
     * @param p2 le deuxième point
     * @return le vecteur résultant du calcul
     */
    public static Point calculerVecteur(Point p1, Point p2) {
        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        // Afficher les coordonnées du vecteur
        System.out.println("Vector coordinates: (" + deltaX + ", " + deltaY + ")");
        return new Point(deltaX, deltaY);
    }

    /**
     * Calcule les indices pour chaque paire de points de contrôle.
     * @param pointsDeControle les points de contrôle
     * @param nbFrame le nombre de frames
     * @return une liste de points représentant les indices
     */
    public List<Point> listIndice(PointDeControle pointsDeControle, int nbFrame) {
        List<Point> p = new ArrayList<>();
        for (Map.Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            Point keyPoint = entry.getKey();
            Point valuePoint = entry.getValue();
            Point indice = calculerVecteur(keyPoint, valuePoint);
            indice.setX(indice.getX() / nbFrame);
            indice.setY(indice.getY() / nbFrame);
            p.add(indice);
        }
        return p;
    }

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
            BufferedImage frameImage = morphismeSimpleRemplissage(matrix, couleur, autreCouleur, listPoint);
            
            gifWriter.writeToSequence(frameImage);
        }
    
        // Ajouter l'image d'arrivée en utilisant les valeurs de la map en entrée
        List<Point> listPointArrivee = new ArrayList<>();
        for (Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            listPointArrivee.add(entry.getValue());
        }
        BufferedImage imageArrivee = morphismeSimpleRemplissage(matrix, couleur, autreCouleur, listPointArrivee);
        gifWriter.writeToSequence(imageArrivee);
    
        gifWriter.close();
        output.close();
    }
    
    
    /**
     * Remplit l'image en utilisant le morphisme simple.
     * @param matrix la matrice de couleurs
     * @param couleur la couleur à utiliser pour remplir l'intérieur du polygone
     * @param autreCouleur la couleur à utiliser pour remplir l'extérieur du polygone
     * @param points la liste des points
     * @return l'image remplie
     */
    public BufferedImage morphismeSimpleRemplissage(Color[][] matrix, Color couleur, Color autreCouleur, List<Point> points) {
        int hauteur = matrix.length;
        int largeur = matrix[0].length;
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (estDomaine(points, new Point(x, y))) {
                    matrix[y][x] = couleur; // Correction : matrix[y][x] au lieu de matrix[x][y]
                } else {
                    matrix[y][x] = autreCouleur; // Correction : matrix[y][x] au lieu de matrix[x][y]
                }
            }
        }
        return genereImage(matrix);
    }
}

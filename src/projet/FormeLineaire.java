package src.projet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
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

    public static Point calculerVecteur(Point p1, Point p2) {
        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        System.out.println("Vector coordinates: (" + deltaX + ", " + deltaY + ")");
        return new Point(deltaX, deltaY);
    }

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

    public List<BufferedImage> morphismeSimple(BufferedImage image1, PointDeControle pointsDeControle, int nbFrame) throws IOException {
        Color[][] matrix = genererMatrice(image1);
        List<Point> listIndice = listIndice(pointsDeControle, nbFrame); 
        List<Point> pointsKeys = new ArrayList<>(pointsDeControle.getPointsMap().keySet());

        Color couleur = chercheCouleur(matrix, new ArrayList<>(pointsDeControle.getPointsMap().keySet()));
        Color autreCouleur = chercheAutreCouleur(matrix, new ArrayList<>(pointsDeControle.getPointsMap().keySet()));

        // Vérifiez les couleurs trouvées
        System.out.println("Couleur trouvée: " + couleur);
        System.out.println("Autre couleur trouvée: " + autreCouleur);

        List<BufferedImage> frames = new ArrayList<>();
        int hauteur = matrix.length;
        int largeur = matrix[0].length;

        for (int i = 0; i < nbFrame; i++) {
            List<Point> listPoint = new ArrayList<>(); 
            for (int j = 0; j < pointsKeys.size(); j++) {
                Point p = pointsKeys.get(j);
                Point indice = listIndice.get(j);
                double x = Math.max(0, Math.min(largeur - 1, p.getX() + indice.getX() * i));
                double y = Math.max(0, Math.min(hauteur - 1, p.getY() + indice.getY() * i));
                System.out.println("Frame " + i + ", Point " + j + " coordinates: (" + x + ", " + y + ")");
                Point p1 = new Point(x, y); 
                listPoint.add(p1);
            }
            BufferedImage frameImage = morphismeSimpleRemplissage(matrix, couleur, autreCouleur, listPoint);
            frames.add(frameImage);
            System.out.println("Frame " + i + " generated with dimensions: " + frameImage.getWidth() + "x" + frameImage.getHeight());
        }

        return frames;
    }

    public BufferedImage morphismeSimpleRemplissage(Color[][] matrix, Color couleur, Color autreCouleur, List<Point> points) {
        int hauteur = matrix.length;
        int largeur = matrix[0].length;
        Color[][] newMatrix = new Color[hauteur][largeur];

        // Debug: Clear newMatrix with a default color
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                newMatrix[y][x] = new Color(255, 255, 255, 255); // white color
            }
        }

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (estDomaine(points, new Point(x, y))) {
                    newMatrix[y][x] = couleur;
                } else {
                    newMatrix[y][x] = autreCouleur;
                }
            }
        }

        // Debug: Verify matrix content
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (newMatrix[y][x] == null) {
                    System.out.println("Null color at (" + x + ", " + y + ")");
                }
            }
        }

        return genereImage(newMatrix);
    }

    public void generateGif(List<BufferedImage> frames, String filePath, int delay) throws IOException {
        ImageOutputStream output = new FileImageOutputStream(new File(filePath));
        GifSequenceWriter gifWriter = new GifSequenceWriter(output, frames.get(0).getType(), delay, true);

        for (BufferedImage frame : frames) {
            gifWriter.writeToSequence(frame);
        }

        gifWriter.close();
        output.close();
    }
}

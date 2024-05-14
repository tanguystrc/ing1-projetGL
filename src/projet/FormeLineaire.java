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
        return new Point(p1.getX() - p2.getX(), p1.getY() - p2.getY());
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

    public void morphismeSimple(BufferedImage image1, PointDeControle pointsDeControle, int nbFrame) throws IOException {
        Color[][] matrix = genererMatrice(image1);
        List<Point> listIndice = listIndice(pointsDeControle, nbFrame); 
        List<Point> pointsKeys = new ArrayList<>(pointsDeControle.getPointsMap().keySet());

        ImageOutputStream output = new FileImageOutputStream(new File("animation.gif"));
        GifSequenceWriter gifWriter = new GifSequenceWriter(output, image1.getType(), 100, true);

        for (int i = 0; i < nbFrame; i++) {
            List<Point> listPoint = new ArrayList<>(); 
            for (int j = 0; j < pointsKeys.size(); j++) {
                Point p = pointsKeys.get(j);
                Point indice = listIndice.get(j);
                Point p1 = new Point(p.getX() + indice.getX(), p.getY() + indice.getY()); 
                listPoint.add(p1);
            }
            BufferedImage frameImage = morphismeSimpleRemplissage(matrix, listPoint); 
            gifWriter.writeToSequence(frameImage);
        }

        gifWriter.close();
        output.close();
    }

    public BufferedImage morphismeSimpleRemplissage(Color[][] matrix, List<Point> points) {
        int hauteur = matrix.length;
        int largeur = matrix[0].length;
        Color couleur = chercheCouleur(matrix, points);
        Color autreCouleur = chercheAutreCouleur(matrix, points);
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (estDomaine(points, new Point(x, y))) {
                    matrix[x][y] = couleur;
                } else {
                    matrix[x][y] = autreCouleur;
                }
            }
        }
        return genereImage(matrix);
    }
}

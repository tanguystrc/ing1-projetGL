package src.projet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import java.awt.Color;


public abstract class Forme {
    
    protected PointDeControle pointsDeControle;
    protected BufferedImage image1;
    protected BufferedImage image2;
    protected int nbFrame;
    private javafx.scene.paint.Color selectedColor;

    public Forme(PointDeControle pointsDeControle, BufferedImage image1, BufferedImage image2, int nbFrame) {
        this.pointsDeControle = pointsDeControle;
        this.image1 = image1;
        this.image2 = image2;
        this.nbFrame = nbFrame;
    }

    public void setSelectedColor(javafx.scene.paint.Color selectedColor) {
        this.selectedColor = selectedColor;
    }


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

    public static Point calculerVecteur(Point p1, Point p2) {
        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
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


    public void morphisme(BufferedImage image1, PointDeControle pointsDeControle, int nbFrame) throws IOException {
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
    
        List<Point> listPointArrivee = new ArrayList<>();
        for (Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            listPointArrivee.add(entry.getValue());
        }
        BufferedImage imageArrivee = morphismeRemplissage(matrix, couleur, autreCouleur, listPointArrivee);
        gifWriter.writeToSequence(imageArrivee);
    
        gifWriter.close();
        output.close();
    }
    
    public BufferedImage morphismeRemplissage(Color[][] matrix, Color couleur, Color autreCouleur, List<Point> points) {
        int hauteur = matrix.length;
        int largeur = matrix[0].length;
        Color[][] newMatrix = new Color[hauteur][largeur];
    
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (estDomaine(points, new Point(x, y))) {
                    newMatrix[y][x] = couleur;
                } else {
                    newMatrix[y][x] = autreCouleur;
                }
            }
        }
    
        return genereImage(newMatrix);
    }
    
    

    public abstract boolean estDomaine(List<Point> listePoint, Point p) ;

    
    public Color chercheCouleur(Color[][] matrix, List<Point> pts) {
        int hauteur = matrix.length;
        int largeur = matrix[0].length;
        Point p = new Point();

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                p.setX(x);
                p.setY(y);
                if (estDomaine(pts, p)) {
                    System.out.println("Couleur trouvée : " + matrix[y][x]);
                    return matrix[y][x];
                }
            }
        }
        
        return null;
    }

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

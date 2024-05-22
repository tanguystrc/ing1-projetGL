package src.projet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class Forme {
    
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

   public void morphisme(BufferedImage image1, PointDeControle pointsDeControle, int nbFrame, int dureeGIF, BiConsumer<Integer, Integer> progressUpdater) throws IOException {
        List<Point> listIndice = listIndice(pointsDeControle, nbFrame); 
        List<Point> pointsKeys = new ArrayList<>(pointsDeControle.getPointsMap().keySet());
        int couleur = (selectedColor != null) ? new java.awt.Color((int)(selectedColor.getRed() * 255), (int)(selectedColor.getGreen() * 255), (int)(selectedColor.getBlue() * 255)).getRGB() : chercheCouleur(image1, pointsKeys);
        int autreCouleur = chercheAutreCouleur(image1, pointsKeys);

        ImageOutputStream output = new FileImageOutputStream(new File("animation.gif"));
        GifSequenceWriter gifWriter = new GifSequenceWriter(output, image1.getType(), (dureeGIF*1000)/nbFrame, true);

        int hauteur = image1.getHeight();
        int largeur = image1.getWidth();

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
            BufferedImage frameImage = morphismeRemplissage(image1, couleur, autreCouleur, listPoint);
            gifWriter.writeToSequence(frameImage);
            progressUpdater.accept(i + 1, nbFrame); // Update progress here
        }

        List<Point> listPointArrivee = new ArrayList<>();
        for (Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            listPointArrivee.add(entry.getValue());
        }
        BufferedImage imageArrivee = morphismeRemplissage(image1, couleur, autreCouleur, listPointArrivee);
        gifWriter.writeToSequence(imageArrivee);

        gifWriter.close();
        output.close();
    }

    
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
    
    public boolean estDomaine(List<Point> listePoint, Point p) {
        int compteur = 0;
        int nbPts = listePoint.size();
        Point dernierPoint = listePoint.get(nbPts - 1);
    
        for (Point pointActuel : listePoint) {
            if (pointActuel.getY() == p.getY() && dernierPoint.getY() == p.getY()) {
                if ((pointActuel.getX() <= p.getX() && p.getX() <= dernierPoint.getX()) || 
                    (dernierPoint.getX() <= p.getX() && p.getX() <= pointActuel.getX())) {
                    return true; 
                }
            } else if (pointActuel.getX() == p.getX() && dernierPoint.getX() == p.getX()) {
                if ((pointActuel.getY() <= p.getY() && p.getY() <= dernierPoint.getY()) || 
                    (dernierPoint.getY() <= p.getY() && p.getY() <= pointActuel.getY())) {
                    return true; 
                }
            } else {
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
        return 0; // Default color if none found
    }
    public int chercheCouleur(BufferedImage image, List<Point> pts) {
        int hauteur = image.getHeight();
        int largeur = image.getWidth();
        Point p = new Point();
        int autreCouleur = chercheAutreCouleur(image, pts);
        int couleur;

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                p.setX(x);
                p.setY(y);
                couleur = image.getRGB(x+5, y+5);
                if (autreCouleur!=couleur && estDomaine(pts, p)) {
                    return couleur;
                }
            }
        }
        return 0; // Default color if none found
    }
}

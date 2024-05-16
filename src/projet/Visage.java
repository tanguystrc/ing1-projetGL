package src.projet;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 
 * Classe pour le morphing d'image
*/
public class Visage {
     
    private BufferedImage imageSource;
    private BufferedImage imageDestination;
    private Set<PointDeControle> segments;

    public Visage(BufferedImage imageSource, BufferedImage imageDestination, Set<PointDeControle> segments) {
        this.imageSource = imageSource;
        this.imageDestination = imageDestination;
        this.segments = segments;
    }

    public BufferedImage getImageSource() {
        return imageSource;
    }

    public void setImageSource(BufferedImage imageSource) {
        this.imageSource = imageSource;
    }

    public BufferedImage getImageDestination() {
        return imageDestination;
    }

    public void setImageDestination(BufferedImage imageDestination) {
        this.imageDestination = imageDestination;
    }

    public Set<PointDeControle> getSegments() {
        return segments;
    }

    public void setSegments(Set<PointDeControle> segments) {
        this.segments = segments;
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

    public 

}

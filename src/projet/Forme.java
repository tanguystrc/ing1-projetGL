package src.projet;
import java.awt.image.BufferedImage;
import java.util.List;
import java.awt.Color;

public abstract class Forme {
	
    protected List<PointDeControle> pointsDeControle;
    protected BufferedImage image1;
    protected BufferedImage image2;
    protected int nbFrame;

    
    public Forme(List<PointDeControle> pointsDeControle, BufferedImage image1, BufferedImage image2, int nbFrame) {
        this.pointsDeControle = pointsDeControle;
        this.image1 = image1;
        this.image2 = image2;
        this.nbFrame = nbFrame;
    }

    public List<PointDeControle> getPointsDeControle() {
        return pointsDeControle;
    }

    public void setPointsDeControle(List<PointDeControle> pointsDeControle) {
        this.pointsDeControle = pointsDeControle;
    }

    public BufferedImage getImage1() {
        return image1;
    }

    public void setImage1(BufferedImage image1) {
        this.image1 = image1;
    }

    public BufferedImage getImage2() {
        return image2;
    }

    public void setImage2(BufferedImage image2) {
        this.image2 = image2;
    }

    public int getNbFrame() {
        return nbFrame;
    }

    public void setNbFrame(int nbFrame) {
        this.nbFrame = nbFrame;
    }
    public Color[][] genererMatrice(BufferedImage image) {
        int largeur = image1.getWidth();
        int hauteur = image1.getHeight();
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
    public boolean estDomaine(PointDeControle points, Point p) {
        int compteur = 0; 
        int nbPts = points.listePoint.size();
        Point dernierPoint = points.listePoint.get(nbPts - 1);
    
        for (Point pointActuel : points.listePoint) { 
            if ((pointActuel.getY() < p.getY() && dernierPoint.getY() > p.getY()) || 
                (pointActuel.getY() > p.getY() && dernierPoint.getY() < p.getY())) {
                if (pointActuel.getY() == dernierPoint.getY()) {
                    if (pointActuel.getX() > p.getX() || dernierPoint.getX() > p.getX()) {
                        compteur++;
                    }
                } else {
                    double denom = (pointActuel.getY() - dernierPoint.getY());
                    if (denom != 0) { 
                        double pointIntersectX = dernierPoint.getX() + (pointActuel.getX() - dernierPoint.getX()) * ((p.getY() - dernierPoint.getY()) / denom);
                        if (p.getX() < pointIntersectX) {
                            compteur++;
                        }
                    }
                }
            }
            dernierPoint = pointActuel;
        }
        return compteur % 2 != 0;
    }
}


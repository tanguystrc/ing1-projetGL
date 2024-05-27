package src.projet.traitement;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import javafx.application.Platform;
import src.projet.gif.GifSequenceWriter;

/**
 * Classe pour le morphing d'image avec la méthode des segments.
 */
public class Visage {

    private List<PointDeControle> segments;
    protected BufferedImage image1;
    protected BufferedImage image2;
    protected int nbFrame;

    public Visage(BufferedImage image1, BufferedImage image2, List<PointDeControle> segments, int nbFrame) {
        this.image1 = resizeImage(image1, 600, 600);
        this.image2 = resizeImage(image2, 600, 600);
        this.segments = segments;
        this.nbFrame = nbFrame;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    /**
     * Retourne les vecteurs de déplacement pour chaque point entre chaques images de transition
     * @param nbFrame Nombre d'images total
     * @return La liste des listes de vecteurs déplacement
     */
    public List<List<Point>> listIndice(int nbFrame) {
        List<List<Point>> p = new ArrayList<>();
        for (PointDeControle segment : segments) {
            List<Point> lp = new ArrayList<>();
            for (Couple<Point, Point> couple : segment.getPointsList()) {
                Point keyPoint = couple.getA();
                Point valuePoint = couple.getB();
                Point indice = valuePoint.soustraction(keyPoint);
                indice.setX(indice.getX() / (nbFrame - 1));
                indice.setY(indice.getY() / (nbFrame - 1));
                lp.add(indice);
            }
            p.add(lp);
        }
        return p;
    }

    /**
     * Forme les ensembles des segments pour le demiMorphisme
     * @param pSImage1 Ensemble de pair de segment initialisé mais vide comptant les segments de l'image1 et ceux de l'image intermédiaire n
     * @param pSImage2 Ensemble de pair de segment initialisé mais vide comptant les segments de l'image2 et ceux de l'image intermédiaire n
     * @param n Frame actuelle
     * @param listIndice Liste des indices de déplacement
     */
    public void ensemblePairSegment(Set<PairSegment> pSImage1, Set<PairSegment> pSImage2, int n, List<List<Point>> listIndice) {
        for (int i = 0; i < segments.size(); i++) {
            List<Point> indices = listIndice.get(i);
            List<Couple<Point, Point>> pointsDeControle = new ArrayList<>(segments.get(i).getPointsList());
            for (int j = 0; j < pointsDeControle.size() - 1; j++) {
                Point debut = pointsDeControle.get(j).getA();
                Point fin = pointsDeControle.get(j + 1).getA();
                Segment s1 = new Segment(debut, fin);
                Segment s2 = new Segment(pointsDeControle.get(j).getB(), pointsDeControle.get(j + 1).getB());
                Segment sn = new Segment(debut.somme(indices.get(j).produit(n).pixel()), fin.somme(indices.get(j + 1).produit(n).pixel()));
                pSImage1.add(new PairSegment(s1, sn));
                pSImage2.add(new PairSegment(s2, sn));
            }
        }
    }

    /**
     * Applique le morphisme depuis les image sources et destination, renvoyant deux liste de BufferedImage devant être assemblées dans morph().
     * @param morphs1 Liste des images intermédiaires pour l'image source
     * @param morphs2 Liste des images intermédiaires pour l'image destination
     * @param nbFrame Nombre de frames
     */
    public void demiMorph(List<BufferedImage> morphs1, List<BufferedImage> morphs2, int nbFrame) {
        morphs1.clear();
        morphs2.clear();
        List<List<Point>> listIndice = listIndice(nbFrame);
        for (int k = 1; k < nbFrame - 1; k++) {
            BufferedImage morphImage1 = new BufferedImage(600, 600, image1.getType());
            BufferedImage morphImage2 = new BufferedImage(600, 600, image2.getType());
            Set<PairSegment> sPImage1 = new HashSet<>();
            Set<PairSegment> sPImage2 = new HashSet<>();

            ensemblePairSegment(sPImage1, sPImage2, k, listIndice);

            for (int i = 0; i < morphImage1.getWidth(); i++) {
                for (int j = 0; j < morphImage1.getHeight(); j++) {
                    Point p = new Point(i, j);
                    morphImage1.setRGB(i, j, getInterpolatedColor(image1, sPImage1, p));
                    morphImage2.setRGB(i, j, getInterpolatedColor(image2, sPImage2, p));
                }
            }

            morphs1.add(morphImage1);
            morphs2.add(morphImage2);
        }
    }

    private int getInterpolatedColor(BufferedImage image, Set<PairSegment> sPImage, Point p) {
        Point depSomme = new Point(0, 0);
        double poidsSomme = 0;
        for (PairSegment sp : sPImage) {
            Point dep = sp.deplacementPointSource(p);
            double poids = sp.poids(p);
            depSomme = depSomme.somme(dep.produit(poids));
            poidsSomme += poids;
        }
        Point xS = p.somme(depSomme.produit(1 / poidsSomme));
        return getSafeRGB(image, xS);
    }

    private int getSafeRGB(BufferedImage image, Point p) {
        int x = (int) Math.round(p.getX());
        int y = (int) Math.round(p.getY());
        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
            return getBoundaryColor(image, x, y);
        }
        return image.getRGB(x, y);
    }

    private int getBoundaryColor(BufferedImage image, int x, int y) {
        x = Math.max(0, Math.min(image.getWidth() - 1, x));
        y = Math.max(0, Math.min(image.getHeight() - 1, y));
        return image.getRGB(x, y);
    }

    /**
     * Calcule la couleur intermédiaire entre deux couleurs RGB en fonction d'un pourcentage.
     * @param rgb1 Première couleur en format entier RGB
     * @param rgb2 Deuxième couleur en format entier RGB
     * @param percentage Pourcentage de la première couleur (0.0 à 1.0)
     * @return Couleur intermédiaire en format entier RGB
     */
    public static int getIntermediateColor(int rgb1, int rgb2, double percentage) {
        if (percentage < 0.0 || percentage > 1.0) {
            throw new IllegalArgumentException("Percentage must be between 0.0 and 1.0");
        }
        Color color1 = new Color(rgb1);
        Color color2 = new Color(rgb2);

        int r = (int) (color2.getRed() * percentage + color1.getRed() * (1.0 - percentage));
        int g = (int) (color2.getGreen() * percentage + color1.getGreen() * (1.0 - percentage));
        int b = (int) (color2.getBlue() * percentage + color1.getBlue() * (1.0 - percentage));

        return new Color(r, g, b).getRGB();
    }

    /**
     * Fonction pour le morphisme avec les droites
     * @param nbFrame Nombre total de frames
     * @throws IOException
     */
    public void morph(int dureeGIF, BiConsumer<Integer, Integer> progressUpdater) throws IOException {
        List<BufferedImage> morphFinal = new ArrayList<>();
        List<BufferedImage> morphs1 = new ArrayList<>();
        List<BufferedImage> morphs2 = new ArrayList<>();

        ImageOutputStream output = new FileImageOutputStream(new File("animation.gif"));
        GifSequenceWriter gifWriter = new GifSequenceWriter(output, image1.getType(), (dureeGIF * 1000) / this.nbFrame, true);

        demiMorph(morphs1, morphs2, nbFrame);

        morphFinal.add(image1);
        gifWriter.writeToSequence(image1);

        for (int k = 1; k < nbFrame - 1; k++) {
            BufferedImage image = new BufferedImage(600, 600, image1.getType());
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    image.setRGB(i, j, getIntermediateColor(morphs1.get(k - 1).getRGB(i, j), morphs2.get(k - 1).getRGB(i, j), (double) k / (double) (nbFrame - 1)));
                }
            }
            morphFinal.add(image);
            gifWriter.writeToSequence(image);
            final int progress = k + 1;
            Platform.runLater(() -> progressUpdater.accept(progress, this.nbFrame));
        }

        morphFinal.add(image2);
        gifWriter.writeToSequence(image2);

        gifWriter.close();
        output.close();
    }
}

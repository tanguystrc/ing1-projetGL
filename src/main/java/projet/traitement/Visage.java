package projet.traitement;

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
import projet.gif.GifSequenceWriter;

/**
 * Classe pour le morphing d'image avec la méthode des lignes de champs d'influance.
 */
public class Visage {   

    private List<PointDeControle> listePoint;
    private BufferedImage image1;
    private BufferedImage image2;
    private int nbFrame;
    protected File ajouteGIF;
    protected boolean avant;
    private int progress;
    
    /**
     * Constructeur de la classe Visage
     * @param image1 Première image du morphing
     * @param image2 Seconde image du morphing
     * @param listePoint Liste de PointDeControle
     * @param nbFrame Nombre d'image totale avec les deux principales et celles générées
     * @param ajouteGIF fichier du gif s'il faut en ajouter un
     * @param avant : vrai si le Gif a ajouter est avant celui actuel
     */
    public Visage(BufferedImage image1, BufferedImage image2, List<PointDeControle> listePoint, int nbFrame, File ajouteGIF, boolean avant) {
        this.image1 = redimensionnerImage(image1, 600, 600);
        this.image2 = redimensionnerImage(image2, 600, 600);
        this.listePoint = listePoint;
        this.nbFrame = nbFrame;
        this.ajouteGIF = ajouteGIF;
        this.avant = avant;
        this.progress = 1;
    }

    /**
     * Getteur de listePoint
     * @return Une liste de PointDeControle
     */
    public List<PointDeControle> getListePoint (){
        return listePoint;
    }

    /**
     * Setteur de listePoint
     * @param listePoint Une liste de PointDeControle
     */
    public void setListePoint(List<PointDeControle> listePoint){
        this.listePoint=listePoint;
    }

    /**
     * Getteur de image1
     * @return Une BufferedImage
     */
    public BufferedImage getImage1 (){
        return image1;
    }

    /**
     * Setteur de image1
     * @param image1 BufferedImage
     */
    public void setImage1(BufferedImage image1){
        this.image1=image1;
    }

    /**
     * Getteur de image2
     * @return Une BufferedImage
     */
    public BufferedImage getImage2 (){
        return image2;
    }

    /**
     * Setteur de image2
     * @param image2 BufferedImage
     */
    public void setListePoint(BufferedImage image2){
        this.image2=image2;
    }

    /**
     * Getteur de nbFrame
     * @return Un entier correspondant au nombre totale d'image
     */
    public int getNbFrame (){
        return nbFrame;
    }

    /**
     * Setteur de NbFrame
     * @param nbFrame int
     */
    public void setListePoint(int nbFrame){
        this.nbFrame=nbFrame;
    }

    /**
     * Redimensionne une BufferedImage
     * @param imageOriginale BufferedImage
     * @param largeur int
     * @param hauteur int
     * @return Une BufferedImage redimentionnée
     */
    private BufferedImage redimensionnerImage(BufferedImage imageOriginale, int largeur, int hauteur) {
        BufferedImage imageRedimentionnee = new BufferedImage(largeur, hauteur, imageOriginale.getType());
        Graphics2D g = imageRedimentionnee.createGraphics();
        g.drawImage(imageOriginale, 0, 0, largeur, hauteur, null);
        g.dispose();
        return imageRedimentionnee;
    }

    /**
     * Retourne les vecteurs de déplacement pour chaque point entre chaques images intermédiaires
     * @param nbFrame Nombre d'images total
     * @return La liste des listes de vecteurs déplacement
     */
    public List<List<Point>> listIndice(int nbFrame) {
        List<List<Point>> p = new ArrayList<>();
        for (PointDeControle segment : listePoint) {
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
     * Forme les ensembles de segment pour le demiMorphisme
     * @param pSImage1 Ensemble de pair de segment initialisé mais vide comptant les listePoint de l'image1 et ceux de l'image intermédiaire n
     * @param pSImage2 Ensemble de pair de segment initialisé mais vide comptant les listePoint de l'image2 et ceux de l'image intermédiaire n
     * @param n Numéro de l'image acctuelle
     * @param listIndice Liste des indices de déplacement
     */
    public void ensemblePairSegment(Set<PairSegment> pSImage1, Set<PairSegment> pSImage2, int n, List<List<Point>> listIndice) {
        for (int i = 0; i < listePoint.size(); i++) {
            List<Point> indices = listIndice.get(i);
            List<Couple<Point, Point>> pointsDeControle = new ArrayList<>(listePoint.get(i).getPointsList());
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
     * @param nbFrame Nombre d'image
     */
    public void demiMorph(List<BufferedImage> morphs1, List<BufferedImage> morphs2, int nbFrame,BiConsumer<Integer, Integer> barreChargement) {
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
                    morphImage1.setRGB(i, j, trouverCouleur(image1, sPImage1, p));
                    morphImage2.setRGB(i, j, trouverCouleur(image2, sPImage2, p));
                }
            }

            morphs1.add(morphImage1);
            morphs2.add(morphImage2);

            // Pour la barre de chargement :
            progress++;
            Platform.runLater(() -> barreChargement.accept(progress, this.nbFrame));    
        }
    }

    /**
     * Trouve la couleur du pixel source associé au pixel destination par la méthode des lignes de champs d'influance
     * @param image BufferedImage source
     * @param sPImage Ensemble de PairSegment correspondant au mouvement des lignes entre l'image source et la destination
     * @param p Pixel destination
     * @return La couleur pour le pixel destination
     */
    private int trouverCouleur(BufferedImage image, Set<PairSegment> sPImage, Point p) {
        Point depSomme = new Point(0, 0);
        double poidsSomme = 0;
        for (PairSegment sp : sPImage) {
            Point dep = sp.deplacementPointSource(p);
            double poids = sp.poids(p);
            depSomme = depSomme.somme(dep.produit(poids));
            poidsSomme += poids;
        }
        Point xS = p.somme(depSomme.produit(1 / poidsSomme));
        return recupererCouleur(image, xS);
    }

    /**
     * Récupère la couleur du pixel source p
     * @param image BufferedImage source
     * @param p Point source
     * @return Le RGB attitré au pixel destination
     */
    private int recupererCouleur(BufferedImage image, Point p) {
        int x = (int) Math.round(p.getX());
        int y = (int) Math.round(p.getY());
        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
            return recupererCouleurProche(image, x, y);
        }
        return image.getRGB(x, y);
    }

    /**
     * En cas de pixel non présent dans l'image source, récupère la couleur du pixel le plus proche appartenant à l'image
     * @param image BufferedImage
     * @param x Abscisse du pixel sortant
     * @param y Ordonne du pixel sortant
     * @return Couleur du nouveau pixel source
     */
    private int recupererCouleurProche(BufferedImage image, int x, int y) {
        x = Math.max(0, Math.min(image.getWidth() - 1, x));
        y = Math.max(0, Math.min(image.getHeight() - 1, y));
        return image.getRGB(x, y);
    }

    /**
     * Calcule la couleur intermédiaire entre deux couleurs RGB en fonction d'un pourcentage.
     * @param rgb1 Première couleur en format entier RGB
     * @param rgb2 Deuxième couleur en format entier RGB
     * @param pourcentage Pourcentage de la première couleur (0.0 à 1.0)
     * @return Couleur intermédiaire en format entier RGB
     */
    public static int trouverCouleurIntermediaire(int rgb1, int rgb2, double pourcentage) {
        if (pourcentage < 0.0 || pourcentage > 1.0) {
            throw new IllegalArgumentException("Le pourcentage doit être entre 0 et 1.");
        }
        Color couleur1 = new Color(rgb1);
        Color couleur2 = new Color(rgb2);

        int r = (int) (couleur2.getRed() * pourcentage + couleur1.getRed() * (1.0 - pourcentage));
        int g = (int) (couleur2.getGreen() * pourcentage + couleur1.getGreen() * (1.0 - pourcentage));
        int b = (int) (couleur2.getBlue() * pourcentage + couleur1.getBlue() * (1.0 - pourcentage));

        return new Color(r, g, b).getRGB();
    }

    /**
     * Fonction pour le morphisme avec les droites
     * @param nbFrame Nombre total de frames
     * @throws IOException
     */
    public void morph(int dureeGIF, BiConsumer<Integer, Integer> barreChargement) throws IOException {
        List<BufferedImage> morphFinal = new ArrayList<>();
        List<BufferedImage> morphs1 = new ArrayList<>();
        List<BufferedImage> morphs2 = new ArrayList<>();

        ImageOutputStream output = new FileImageOutputStream(new File("animation.gif"));
        GifSequenceWriter gifWriter = new GifSequenceWriter(output, image1.getType(), (dureeGIF * 1000) / nbFrame, true);

        demiMorph(morphs1, morphs2, nbFrame, barreChargement);

        morphFinal.add(image1);
        gifWriter.writeToSequence(image1,dureeGIF * 1000 / nbFrame);

        for (int k = 1; k < nbFrame - 1; k++) {
            BufferedImage image = new BufferedImage(600, 600, image1.getType());
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    image.setRGB(i, j, trouverCouleurIntermediaire(morphs1.get(k - 1).getRGB(i, j), morphs2.get(k - 1).getRGB(i, j), (double) k / (double) (nbFrame - 1)));
                }
            }
            morphFinal.add(image);
            gifWriter.writeToSequence(image,dureeGIF * 1000 / nbFrame);  
        }

        // Pour la barre de chargement :
        progress++;
        Platform.runLater(() -> barreChargement.accept(progress, this.nbFrame));    

        morphFinal.add(image2);
        gifWriter.writeToSequence(image2,dureeGIF * 1000 / nbFrame);

        gifWriter.close();
        output.close();
    }
}

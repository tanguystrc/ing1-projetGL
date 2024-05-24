package src.projet.traitement;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import src.projet.gif.GifSequenceWriter;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

/** 
 * Classe pour le morphing d'image avec la méthode des ségments
*/
public class Visage {
     
    private List<PointDeControle> segments;
    protected BufferedImage image1;
    protected BufferedImage image2;
    protected int nbFrame;

    public Visage(BufferedImage image1, BufferedImage image2, List<PointDeControle> segments, int nbFrame) {
        this.image1 = image1;
        this.image2 = image2;
        this.nbFrame = nbFrame;
        this.segments = segments;          
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

    public List<PointDeControle> getSegments() {
        return segments;
    }

    public void setSegments(List<PointDeControle> segments) {
        this.segments = segments;
    }

    /**
     * Retourne les vecteurs de déplacement pour chaque point entre chaques images de transition
     * @param nbFrame Nombre d'image totale
     * @return La liste des listes de vecteurs déplacement
     */
    public List<List<Point>> listIndice(int nbFrame) {
        List<List<Point>> p = new ArrayList<>();
        for (int i=0;i < segments.size();i++){
        	List<Point> lp = new ArrayList<>();
            for (Point keyPoint : segments.get(i).getPointsMap().keySet()) {
                Point valuePoint = segments.get(i).getPointsMap().get(keyPoint);
                Point indice = valuePoint.soustraction(keyPoint);
                indice.setX(indice.getX() / (nbFrame-1));//En supposant nbFrame en le nombre d'image en tout comptant la première
                indice.setY(indice.getY() / (nbFrame-1));
                lp.add(indice);
            }
            p.add(lp);
        }
        
        return p;
    }

    /**
     * Forme les ensembles des segments pour le demiMorphisme
     * @param pSImage1 Ensemble de pair de segment initialisé mais vide comptenant les segments de l'image1 et ceux de l'image intermédiaire n
     * @param pSImage2  Ensemble de pair de segment initialisé mais vide comptenant les segments de l'image2 et ceux de l'image intermédiaire n
     * @param n
     * @param listIndice
     */
    public void ensemblePairSegment(Set<PairSegment> pSImage1, Set<PairSegment> pSImage2, int n, List<List<Point>> listIndice){
        for(int i = 0; i<this.getSegments().size();i++){
            List<Point> indices = listIndice.get(i);
            Map<Point,Point> pointDeControle = new HashMap<>(this.getSegments().get(i).getPointsMap());
            List<Point> pointsList = new ArrayList<>(pointDeControle.keySet());
            for(int j=0;j<pointsList.size()-1;j++){
                Point debut = pointsList.get(j);
                Point fin = pointsList.get(j+1);
                Segment s1 = new Segment(debut,fin);
                Segment s2 = new Segment(pointDeControle.get(debut),pointDeControle.get(fin));
                Segment sn = new Segment(debut.somme(indices.get(j).produit(n).pixel()),fin.somme(indices.get(j+1).produit(n).pixel()));
                pSImage1.add(new PairSegment(s1, sn));
                pSImage2.add(new PairSegment(s2,sn));
            }
        }

    }

    /**
     * Applique le morphisme depuis les image sources et destination, renvoyant deux liste de BufferedImage devant être assembler dans morph().
     * @param morphsSource
     * @param morphsDestination
     * @param nbFrame
     */
    public void demiMorph(List<BufferedImage> morphs1, List<BufferedImage>  morphs2, int nbFrame){

        //Initialisation
        morphs1.clear();
        morphs2.clear();
        List<List<Point>> listIndice = this.listIndice(nbFrame);

        // Traitement de chaque frame avec un parallèle le traitement depuis l'image source et l'image destination
        for (int k=1;k<nbFrame-1;k++){

            BufferedImage morphImage1 = new BufferedImage(image1.getWidth(),image1.getHeight(),image1.getType());
            Set<PairSegment> sPImage1 = new HashSet<>();

            BufferedImage morphImage2 = new BufferedImage(image1.getWidth(),image1.getHeight(),image1.getType());
            Set<PairSegment> sPImage2 = new HashSet<>();

            this.ensemblePairSegment(sPImage1,sPImage2,k,listIndice);

            for (int i=0;i < morphImage1.getWidth();i++) {
                for (int j=0;j<morphImage1.getHeight();j++) {

                    Point p = new Point(i,j);

                    //Traitement depuis l'image 1

                    Point depSomme1 = new Point(0,0);
                    double poidsSomme1 = 0;

                    for (PairSegment sp : sPImage1) {
                            Point dep = sp.deplacementPointSource(p);
                            double poids = sp.poids(p);
                            depSomme1 = depSomme1.somme(dep.produit(poids));
                            poidsSomme1 += poids;
                    }
                    
                    Point xS = p.somme(depSomme1.produit(1/poidsSomme1));
                    Point pixS = new Point((double)Math.round(xS.getX()),(double)Math.round(xS.getY()));
                    if(pixS.getX()<image1.getWidth()&&pixS.getX()>=0&&pixS.getY()<image1.getHeight()&&pixS.getY()>=0) {
                        morphImage1.setRGB(i, j, this.image1.getRGB((int)pixS.getX(),(int)pixS.getY()));
                    }

                    //Traitement depuis l'image 2

                    Point depSomme2 = new Point(0,0);
                    double poidsSomme2 = 0;

                    for (PairSegment sp : sPImage2) {
                        Point dep = sp.deplacementPointSource(p);
                        double poids = sp.poids(p);
                        depSomme2 = depSomme2.somme(dep.produit(poids));
                        poidsSomme2 += poids;
                    }
                    
                    xS = p.somme(depSomme2.produit(2/poidsSomme2));
                    pixS = new Point((double)Math.round(xS.getX()),(double)Math.round(xS.getY()));
                    if(pixS.getX()<image2.getWidth()&&pixS.getX()>=0&&pixS.getY()<image2.getHeight()&&pixS.getY()>=0) {
                        morphImage2.setRGB(i, j, this.image2.getRGB((int)pixS.getX(),(int)pixS.getY()));
                    }
                }
            }
            morphs1.add(morphImage1);
            morphs2.add(morphImage2);
        }
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
     * @param nbFrame Nombre totale de frame
     * @return Une liste de BufferedImage
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public void morph() throws FileNotFoundException, IOException{
        List<BufferedImage> morphFinal = new ArrayList<>();
        List<BufferedImage> morphs1 = new ArrayList<>();
        List<BufferedImage> morphs2 = new ArrayList<>();

        //Variable pour le gif
        ImageOutputStream output = new FileImageOutputStream(new File("animation.gif"));
        GifSequenceWriter gifWriter = new GifSequenceWriter(output, image1.getType(), 100, true);

        demiMorph(morphs1, morphs2, nbFrame);

        //Ajout de la première image
        morphFinal.add(image1);
        gifWriter.writeToSequence(image1);

        //Assemblage des images
        for(int k=1; k<nbFrame-1;k++){
            BufferedImage image = new BufferedImage(image1.getWidth(), image1.getHeight(), image1.getType());
            for(int i=0; i<image.getWidth();i++){
                for(int j=0; j<image.getHeight();j++){
                    image.setRGB(i, j, getIntermediateColor(morphs1.get(k-1).getRGB(i, j),morphs2.get(k-1).getRGB(i, j),((double)k/(double)(nbFrame-1))));
                }
            }
            morphFinal.add(image);
            gifWriter.writeToSequence(image);
        }

        //Ajout de l'image finale
        morphFinal.add(image2);
        gifWriter.writeToSequence(image2);

        //Finalisation du gif
        gifWriter.close();
        output.close();

        //return morphFinal;
    }

    
    
} 

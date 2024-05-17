package src.projet;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 
 * Classe pour le morphing d'image
*/
public class Visage {
     
    private BufferedImage image1;
    private BufferedImage image2;
    private List<PointDeControle> segments;

    public Visage(BufferedImage image1, BufferedImage image2, List<PointDeControle> segments) {
        this.image1 = image1;
        this.image2 = image2;
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

    //TO DO morphisme visage (bufferedimage*2 list(point de controle) nb frame)
    
} 

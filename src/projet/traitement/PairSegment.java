package src.projet.traitement;

/**
 * Paire de Segment utilisé dans la classe Visage
 */
public class PairSegment {
	
	private Segment segmentSource;
	private Segment segmentDestination;
	
	/**
	 * Constructeur de la classe PairSegment
	 * @param segmentSource Segment sur l'image source
	 * @param segmentDestination Segment sur l'image destination
	 */
	public PairSegment (Segment segmentSource, Segment segmentDestination) {
		this.segmentSource = segmentSource;
		this.segmentDestination = segmentDestination;
	}

	/**
	 * Getteur du Segment source
	 * @return Segment sur l'image source
	 */
	public Segment getSegmentSource() {
		return segmentSource;
	}

	/**
	 * Setteur du Segment source
	 * @param segmentSource Segment sur l'image source
	 */
	public void setSegmentSource(Segment segmentSource) {
		this.segmentSource = segmentSource;
	}

	/**
	 * Getteur du Segment destination
	 * @return Segment sur l'image destination
	 */
	public Segment getSegmentDestination() {
		return segmentDestination;
	}

	/**
	 * Setteur du segment destination
	 * @param segmentDestination Segement sur l'image destination
	 */
	public void setSegmentDestination(Segment segmentDestination) {
		this.segmentDestination = segmentDestination;
	}
	
	/**
	 * Fonction pour retrouver le point correspondant à un point dans l'image destination à partir du mouvement induite par la pair de segment
	 * @param xD Point destination
	 * @return xS le point de l'image source correspondant
	 */
	public Point trouverPointSource(Point xD) {
		Point pS = segmentSource.getDebut();
		Point qS = segmentSource.getFin();
		Point pD = segmentDestination.getDebut();
		Point qD = segmentDestination.getFin();
		
		double u = xD.soustraction(pD).produit(qD.soustraction(pD))/(Math.pow(qD.soustraction(pD).norme(), 2));
		double v = xD.soustraction(pD).produit(qD.soustraction(pD).perp())/(qD.soustraction(pD).norme());
		Point xS = pS.somme(qS.soustraction(pS).produit(u)).somme(qS.soustraction(pS).perp().produit(v/(qS.soustraction(pS).norme())));
		return xS;
	}

	/**
	 * Renvoie le vecteur déplacement entre le point source et le point destination
	 * @param xD Point destination
	 * @return le vecteur déplacement
	 */
	public Point deplacementPointSource(Point xD) {
		Point xS = this.trouverPointSource(xD);
		return xS.soustraction(xD);
	}
	

	/**
	 * renvoie la distance entre le point xD et le segment sur l'image destination
	 * @param xD
	 * @return La distance sous forme d'un double
	 */
	public double distance(Point xD) {
		Point pD = segmentDestination.getDebut();
		Point qD = segmentDestination.getFin();
		
		double u = xD.soustraction(pD).produit(qD.soustraction(pD))/(Math.pow(qD.soustraction(pD).norme(), 2));
		if(u<1&&u>0) {
			double v = xD.soustraction(pD).produit(qD.soustraction(pD).perp())/(qD.soustraction(pD).norme());
			return Math.abs(v);
		}else if(u<0){
			return xD.soustraction(pD).norme();
		}else {
			return xD.soustraction(qD).norme();
		}
	}
	
	/**
	 * Renoie le poids pour le morphing utilisant les lignes de champs d'influance
	 * @param xD Point destination
	 * @return Le poid sous forme d'un double
	 */
	public double poids(Point xD) {
		Point pD = segmentDestination.getDebut();
		Point qD = segmentDestination.getFin();
		
		double taille = pD.soustraction(qD).norme();
		double dist = this.distance(xD);
		double p = 1;//Entre 0 et 1, à 0 toute les lignes on la mm influance peut importe leur longeur.
		double a = 0.000000000000000001;//Trés proche de 0 pour que on ne divise jamais par 0
		double b = 2;//Dans l'idéal entre 0.5 et 2 plus il est petit plus le pixel sont affecté par les segments les plus lointain.
		
		return Math.pow(Math.pow(taille, p)/(a+dist), b);
	}
}

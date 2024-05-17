package src.projet;

public class PairSegment {
	
	private Segment segmentSource;
	private Segment segmentDestination;
	
	public PairSegment (Segment segmentSource, Segment segmentDestination) {
		this.segmentSource = segmentSource;
		this.segmentDestination = segmentDestination;
	}

	public Segment getSegmentSource() {
		return segmentSource;
	}

	public void setSegmentSource(Segment segmentSource) {
		this.segmentSource = segmentSource;
	}

	public Segment getSegmentDestination() {
		return segmentDestination;
	}

	public void setSegmentDestination(Segment segmentDestination) {
		this.segmentDestination = segmentDestination;
	}
	
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

	public Point deplacementPointSource(Point xD) {
		Point xS = this.trouverPointSource(xD);
		return xS.soustraction(xD);
	}
	
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
	
	public double poids(Point xD) {
		Point pD = segmentDestination.getDebut();
		Point qD = segmentDestination.getFin();
		
		double taille = pD.soustraction(qD).norme();
		double dist = this.distance(xD);
		double p = 1;//Entre 0 et 1, à 0 toute les lignes on la mm influance peut importe leur longeur.
		double a = 0.000000000000000001;//Trés proche de 0 pour que on ne divise jamais par 0
		double b = 0.5;//Dans l'idéal entre 0.5 et 2 plus il est petit plus le pixel sont affecté par les segments les plus lointain.
		
		return Math.pow(Math.pow(taille, p)/(a+dist), b);
	}
}

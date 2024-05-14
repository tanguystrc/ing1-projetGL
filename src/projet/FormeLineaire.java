package src.projet;

import java.awt.image.BufferedImage;
import java.util.List;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class FormeLineaire extends Forme{
	private Color[][] matrix1;
	private Color[][] matrix2;
	
	public FormeLineaire(PointDeControle pointsDeControle, int nbFrame, Color[][] matrix1, Color[][] matrix2) {
        super(pointsDeControle, null, null, nbFrame);
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
    }

	public Color[][] getMatrix1() {
		return matrix1;
	}

	public void setMatrix1(Color[][] matrix1) {
		this.matrix1 = matrix1;
	}

	public Color[][] getMatrix2() {
		return matrix2;
	}

	public void setMatrix2(Color[][] matrix2) {
		this.matrix2 = matrix2;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) {
	        return true;
	    }
	    if (!super.equals(obj)) {
	        return false;
	    }
	    if (getClass() != obj.getClass()) {
	        return false;
	    }
	    FormeLineaire other = (FormeLineaire) obj;
	    return Arrays.deepEquals(matrix1, other.matrix1) && Arrays.deepEquals(matrix2, other.matrix2);
	}
	public static Point calculerVecteur(Point p1, Point p2) {
        return new Point(p1.getX() - p2.getX(), p1.getY() - p2.getY());
    }
	public List<Point> listIndice(Map<Point, Point> pointsMap,int nbFrame) {
    	List<Point> p = new ArrayList<>();
    	for (Map.Entry<Point, Point> entre : pointsMap.entrySet()) {
    		Point keyPoint = entre.getKey();
        	Point valuePoint = entre.getValue();
        	Point indice = calculerVecteur(keyPoint, valuePoint);
			indice.setX(indice.getX()/nbFrame);
			indice.setY(indice.getY()/nbFrame);
        	p.add(indice);
    	}
    	return p;
	}
	public void calculerPointDeControle(Color[][] matrix, Map<Point, Point> pointsMap, int nbFrame) {
		List<Point> listIndice = listIndice(pointsMap, nbFrame); 
		for (int i = 0; i < nbFrame; i++) {
			List<Point> listPoint = new ArrayList<>(); 
			for (Point p : pointsMap.keySet()) {
				Point indice = listIndice.get(i);
				Point p1 = new Point(p.getX() + indice.getX(), p.getY() + indice.getY()); 
				listPoint.add(p1);
			}
			morphismeSimple(matrix, listPoint); 
		}
	}
	public BufferedImage morphismeSimple(Color[][] matrix, List<Point> points){
		int hauteur = matrix.length;
        int largeur = matrix[0].length;
		Color couleur=chercheCouleur(matrix,points);
		Color autreCouleur=chercheAutreCouleur(matrix,points);
		for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
				if (estDomaine(points,new Point(x,y))){
					matrix[x][y]=couleur;
				}else{
					matrix[x][y]=autreCouleur;
				}
			}
		}
		return genereImage(matrix);
	}

	
}

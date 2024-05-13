package src.projet;
import java.util.List;
import java.awt.Color;
import java.util.Arrays;

public class FormeLineaire extends Forme{
	private Color[][] matrix1;
	private Color[][] matrix2;
	
	public FormeLineaire(List<PointDeControle> pointsDeControle, int nbFrame, Color[][] matrix1, Color[][] matrix2) {
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

	



}

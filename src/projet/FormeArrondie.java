package src.projet;

import java.util.List;
import java.awt.Color;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

public class FormeArrondie extends Forme {

    private Color[][] matrix1;
    private Color[][] matrix2;

    public FormeArrondie(PointDeControle pointsDeControle, int nbFrame, Color[][] matrix1, Color[][] matrix2) {
        super(pointsDeControle, null, null, nbFrame);
        this.matrix1 = matrix1;
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
        FormeArrondie other = (FormeArrondie) obj;
        return Arrays.deepEquals(matrix1, other.matrix1) && Arrays.deepEquals(matrix2, other.matrix2);
    }

    public Point[] calculateBezierCurve(double t) {
        Point[] controlPoints = pointsDeControle.getPointsMap().keySet().toArray(new Point[0]);
        int n = controlPoints.length - 1;
        Point[] newControlPoints = new Point[n + 1];
    
        for (int i = 0; i <= n; i++) {
            newControlPoints[i] = calculateNewControlPoint(controlPoints, i, t);
        }
    
        int[] binomialCoefficients = calculateBinomialCoefficients(n);
    
        double x = 0;
        double y = 0;
        for (int i = 0; i <= n; i++) {
            double coeff = binomialCoefficients[i] * Math.pow(1 - t, n - i) * Math.pow(t, i);
            x += coeff * newControlPoints[i].getX();
            y += coeff * newControlPoints[i].getY();
        }
        return new Point[] { new Point(x, y) };
    }
    
    // Calculate new control point based on de Casteljau's algorithm
    private Point calculateNewControlPoint(Point[] controlPoints, int index, double t) {
        if (controlPoints.length == 1) {
            return controlPoints[0];
        }
        Point[] newControlPoints = new Point[controlPoints.length - 1];
        for (int i = 0; i < controlPoints.length - 1; i++) {
            double x = (1 - t) * controlPoints[i].getX() + t * controlPoints[i + 1].getX();
            double y = (1 - t) * controlPoints[i].getY() + t * controlPoints[i + 1].getY();
            newControlPoints[i] = new Point(x, y);
        }
        return calculateNewControlPoint(newControlPoints, index, t);
    }
    
    
    
    
    
    private int[] calculateBinomialCoefficients(int n) {
        int[] coefficients = new int[n + 1];
        coefficients[0] = 1;
        for (int i = 1; i <= n; i++) {
            coefficients[i] = coefficients[i - 1] * (n - i + 1) / i;
        }
        return coefficients;
    }

    public PointDeControle getPointsDeControle() {
        return pointsDeControle;
    }

    private double binomial(int n, int k) {
        if (k < 0 || k > n) return 0;
        if (k == 0 || k == n) return 1;
        long result = 1;
        for (int i = 1; i <= k; ++i) {
            result *= n - i + 1;
            result /= i;
        }
        return result;
    }
}
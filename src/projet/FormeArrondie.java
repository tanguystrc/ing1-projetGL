package src.projet;

import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import java.util.List;

public class FormeArrondie {
    private Color[][] matrice1;
    private Color[][] matrice2;
    private List<CubicCurve> curves;

    public FormeArrondie(Color[][] matrice1, Color[][] matrice2, List<CubicCurve> curves) {
        this.matrice1 = matrice1;
        this.matrice2 = matrice2;
        this.curves = curves;
    }

    public Color[][] getMatrice1() {
        return matrice1;
    }

    public void setMatrice1(Color[][] matrice1) {
        this.matrice1 = matrice1;
    }

    public Color[][] getMatrice2() {
        return matrice2;
    }

    public void setMatrice2(Color[][] matrice2) {
        this.matrice2 = matrice2;
    }

    public List<CubicCurve> getCurves() {
        return curves;
    }

    public void setCurves(List<CubicCurve> curves) {
        this.curves = curves;
    }

    public boolean estDomaine(List<CubicCurve> curves, Point p) {
        int crossings = 0;

        for (CubicCurve curve : curves) {
            if (crossesCurve(curve, p)) {
                crossings++;
            }
        }

        return (crossings % 2 != 0);
    }

    private boolean crossesCurve(CubicCurve curve, Point p) {
        // Simplification : on traite la courbe comme un segment pour la vérification
        // Vous pouvez améliorer cela en calculant l'intersection réelle avec la courbe de Bézier
        double x1 = curve.getStartX();
        double y1 = curve.getStartY();
        double x2 = curve.getEndX();
        double y2 = curve.getEndY();

        if (((y1 <= p.getY() && p.getY() < y2) || (y2 <= p.getY() && p.getY() < y1)) &&
            p.getX() < ((x2 - x1) * (p.getY() - y1) / (y2 - y1) + x1)) {
            return true;
        }

        return false;
    }
}


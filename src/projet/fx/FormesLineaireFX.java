package src.projet.fx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import src.projet.traitement.Point;
import src.projet.traitement.PointDeControle;
import src.projet.traitement.Couple;
import javafx.scene.paint.Color;

public class FormesLineaireFX extends FormesFX {

    public FormesLineaireFX(Canvas zonePointsA, Canvas zonePointsB, PointDeControle pointsDeControle) {
        super(zonePointsA, zonePointsB, pointsDeControle);
    }

    @Override
    public void redessinerPoints() {
        zonePointsA.getGraphicsContext2D().clearRect(0, 0, 600, 600);
        zonePointsB.getGraphicsContext2D().clearRect(0, 0, 600, 600);

        int index = 0;
        for (Couple<Point, Point> couple : pointsDeControle.getPointsList()) {
            Point key = couple.getA();
            Point value = couple.getB();
            dessiner(zonePointsA.getGraphicsContext2D(), key.getX(), key.getY(), true, index);
            dessiner(zonePointsB.getGraphicsContext2D(), value.getX(), value.getY(), false, index);
            index++;
        }
    }

    private void dessiner(GraphicsContext gc, double x, double y, boolean estImageA, int index) {
        // lettre de l'alphabet au début, chiffres après
        String pointLabel = (index < 26) ? Character.toString((char) (asciiDuA + index)) : Integer.toString(index - 26);

        gc.setStroke(estImageA ? Color.RED : Color.BLUE);
        gc.strokeText("." + pointLabel, x, y);  
        
        // On a un point précédent du même groupe, on le lie avec le nouveau
        if (index > 0) {
            gc.setStroke(estImageA ? Color.BLUE : Color.RED);        	
            Point previousPoint = getPointFromIndex(index - 1, estImageA);
            if (previousPoint != null) {
                gc.strokeLine(previousPoint.getX(), previousPoint.getY(), x, y);
            }
        }  
    }
}

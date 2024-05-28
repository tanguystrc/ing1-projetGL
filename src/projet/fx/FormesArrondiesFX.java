package src.projet.fx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import src.projet.traitement.Point;
import src.projet.traitement.PointDeControle;
import src.projet.traitement.Couple;
import javafx.scene.paint.Color;

public class FormesArrondiesFX extends FormesFX {

    public FormesArrondiesFX(Canvas zonePointsA, Canvas zonePointsB, PointDeControle pointsDeControle) {
        super(zonePointsA, zonePointsB, pointsDeControle);
    }

    public void redessinerPoints() {
        zonePointsA.getGraphicsContext2D().clearRect(0, 0, 600, 600);
        zonePointsB.getGraphicsContext2D().clearRect(0, 0, 600, 600);

        GraphicsContext gcA = zonePointsA.getGraphicsContext2D();
        GraphicsContext gcB = zonePointsB.getGraphicsContext2D();

        gcA.setStroke(Color.RED);
        gcB.setStroke(Color.RED);

        int index = 0;
        for (Couple<Point, Point> couple : pointsDeControle.getPointsList()) {
            Point key = couple.getA();
            Point value = couple.getB();
            gcA.strokeText("." + (index + 1), key.getX(), key.getY());
            gcB.strokeText("." + (index + 1), value.getX(), value.getY());
            index++;
        }

        dessiner(gcA, true);
        dessiner(gcB, false);
    }

    private void dessiner(GraphicsContext gc, boolean estImageA) {
        int index = 0;
        Point[] points = new Point[4];

        for (Couple<Point, Point> couple : pointsDeControle.getPointsList()) {
            points[index % 4] = estImageA ? couple.getA() : couple.getB();
            index++;
            if (index % 4 == 0 && index >= 4) {
                gc.beginPath();
                gc.moveTo(points[0].getX(), points[0].getY());
                gc.bezierCurveTo(points[1].getX(), points[1].getY(), points[2].getX(), points[2].getY(), points[3].getX(), points[3].getY());
                gc.stroke();
                points[0] = points[3]; // Le dernier point devient le premier de la nouvelle courbe
                points[1] = null;
                points[2] = null;
                points[3] = null;
                index = 1; 
            }
        }
    }
}

package projet.fx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import projet.traitement.Point;
import projet.traitement.PointDeControle;
import projet.traitement.Couple;
import javafx.scene.paint.Color;

/**
 * Classe correspondant au FX du mode des formes unies et arrondies
 */
public class FormesArrondiesFX extends FormesFX {

    /**
     * Constructeur
     * @param zonePointsA : canvas de l'image de début (A)
     * @param zonePointsB : canvas de l'image de fin (B)
     * @param pointsDeControle : information stockée des points de controle du groupe actuel
     */
    public FormesArrondiesFX(Canvas zonePointsA, Canvas zonePointsB, PointDeControle pointsDeControle) {
        super(zonePointsA, zonePointsB, pointsDeControle);
    }

    /**
     * Actualise l'affichage des points sur les canvas
     */
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

    /**
     * Dessine le point sur le canvas et les potentielles courbes
     * @param gc : informations du canvas
     * @param estImageA : vrai si point de l'image de début (A)
     */
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

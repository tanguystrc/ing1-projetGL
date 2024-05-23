package src.projet.fx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import src.projet.traitement.Point;
import src.projet.traitement.PointDeControle;
import javafx.scene.paint.Color;
import java.util.Map.Entry;

public class FormesLineaireFX extends FormesFX {

    public FormesLineaireFX(Canvas canvasA, Canvas canvasB, PointDeControle pointsDeControle) {
        super(canvasA, canvasB, pointsDeControle);
    
    }

    public void redrawPoints() {
        canvasA.getGraphicsContext2D().clearRect(0, 0, 600, 600);
        canvasB.getGraphicsContext2D().clearRect(0, 0, 600, 600);

        int index = 0;
        for (Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            Point key = entry.getKey();
            Point value = entry.getValue();
            draw(canvasA.getGraphicsContext2D(), key.getX(), key.getY(), true, index);
            draw(canvasB.getGraphicsContext2D(), value.getX(), value.getY(), false, index);
            index++;
        }
    }

    private void draw(GraphicsContext gc, double mouseX, double mouseY, boolean isImageA, int index) {
        // lettre de l'alphabet au début, chiffres après
    	String pointLabel = (index < 26) ? Character.toString((char) (asciiDuA + index)) : Integer.toString(index - 26);

        gc.setStroke(isImageA ? Color.RED : Color.BLUE);
        gc.strokeText("." + pointLabel, mouseX, mouseY);  
        
        //On a un point précédant du même groupe, on le lie avec le nouveau :
        if(index > 0 ) {      	       	
        	gc.setStroke(isImageA ? Color.BLUE : Color.RED);        	
        	gc.strokeLine(getPointFromIndex(index-1, isImageA).getX(),getPointFromIndex(index-1, isImageA).getY(),mouseX,mouseY);
        }  
    }
}

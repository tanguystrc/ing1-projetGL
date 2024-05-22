package src.projet;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;

public abstract class FormesFX {
    protected Canvas canvasA;
    protected Canvas canvasB;
    protected PointDeControle pointsDeControle;
    protected int asciiDuA = 65;

    public FormesFX(Canvas canvasA, Canvas canvasB, PointDeControle pointsDeControle) {
        this.canvasA = canvasA;
        this.canvasB = canvasB;
        this.pointsDeControle = pointsDeControle;
    }

    public abstract void handleMousePressed(MouseEvent mouseEvent, boolean isImageA);

    public abstract void handleMouseDragged(MouseEvent mouseEvent, boolean isImageA);

    public abstract void handleMouseReleased(boolean isImageA);

    public abstract void handleMouseClicked(MouseEvent mouseEvent, boolean isImageA);

    public abstract void resetPoints();

    public abstract void showDeletePointDialog();

    public abstract void redrawPoints();
}

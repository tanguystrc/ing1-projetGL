package src.projet;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import java.util.Map.Entry;

public class FormesArrondiesFX extends FormesFX {

    private Point selectedPoint = null;
    private boolean isDragging = false;
    private boolean isMousePressed = false;

    public FormesArrondiesFX(Canvas canvasA, Canvas canvasB, PointDeControle pointsDeControle) {
        super(canvasA, canvasB, pointsDeControle);
    }

    @Override
    public void handleMousePressed(MouseEvent mouseEvent, boolean isImageA) {
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();
        isMousePressed = true;
        
        for (Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            Point point = isImageA ? entry.getKey() : entry.getValue();
            if (point.distance(new Point(mouseX, mouseY)) < 10) { 
                selectedPoint = point;
                isDragging = true;
                break;
            }
        }
    }

    @Override
    public void handleMouseDragged(MouseEvent mouseEvent, boolean isImageA) {
        if (isDragging && selectedPoint != null) {
            double mouseX = Math.max(0, Math.min(600, mouseEvent.getX())); 
            double mouseY = Math.max(0, Math.min(600, mouseEvent.getY())); 
            try {
                selectedPoint.setX(mouseX);
                selectedPoint.setY(mouseY);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return;
            }
            redrawPoints();
        }
    }

    @Override
    public void handleMouseReleased(boolean isImageA) {
        if (isDragging) {
            isDragging = false;
            selectedPoint = null;
            redrawPoints();
        }
        isMousePressed = false;
    }

    @Override
    public void handleMouseClicked(MouseEvent mouseEvent, boolean isImageA) {
        if (!isMousePressed) { // Ensure this is not a drag
            double mouseX = Math.max(0, Math.min(600, mouseEvent.getX())); 
            double mouseY = Math.max(0, Math.min(600, mouseEvent.getY())); 
            Point point;
            try {
                point = new Point(mouseX, mouseY);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return;
            }

            if (isImageA) {
                pointsDeControle.ajouter(point, new Point(mouseX, mouseY)); 
                redrawPoints();
            }
        }
    }

    @Override
    public void resetPoints() {
        pointsDeControle.getPointsMap().clear();
        redrawPoints();
    }

    @Override
    public void showDeletePointDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Supprimer un point");

        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        int index = 0;
        for (Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            Point key = entry.getKey();
            Point value = entry.getValue();
            String pointInfo = String.format("Points %c%d: A(%.1f, %.1f) - B(%.1f, %.1f)",
                    (index < 26) ? (char) (asciiDuA + index) : Integer.toString(index - 26),
                    index + 1, key.getX(), key.getY(), value.getX(), value.getY());
            listView.getItems().add(pointInfo);
            index++;
        }

        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");
        deleteButton.setOnAction(e -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1 && selectedIndex < pointsDeControle.getPointsMap().size()) {
                Point point = getPointFromIndex(selectedIndex, true);
                pointsDeControle.supprimer(point);
                redrawPoints();
                dialog.close();
            }
        });

        VBox dialogVBox = new VBox(20, listView, deleteButton);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setAlignment(Pos.CENTER);

        Scene dialogScene = new Scene(dialogVBox, 300, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void redrawPoints() {
        canvasA.getGraphicsContext2D().clearRect(0, 0, 600, 600);
        canvasB.getGraphicsContext2D().clearRect(0, 0, 600, 600);

        GraphicsContext gcA = canvasA.getGraphicsContext2D();
        GraphicsContext gcB = canvasB.getGraphicsContext2D();

        gcA.setStroke(Color.RED);
        gcB.setStroke(Color.RED);

        Point startPointA = null, startPointB = null;
        Point controlPoint1A = null, controlPoint1B = null;
        Point controlPoint2A = null, controlPoint2B = null;
        Point endPointA = null, endPointB = null;

        int index = 0;
        for (Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            Point key = entry.getKey();
            Point value = entry.getValue();

            if (index == 0) {
                startPointA = key;
                startPointB = value;
            } else if (index == 1) {
                controlPoint1A = key;
                controlPoint1B = value;
            } else if (index == 2) {
                controlPoint2A = key;
                controlPoint2B = value;
            } else if (index == 3) {
                endPointA = key;
                endPointB = value;
            }

            gcA.strokeText("." + (index + 1), key.getX(), key.getY());
            gcB.strokeText("." + (index + 1), value.getX(), value.getY());
            index++;
        }

        if (startPointA != null && controlPoint1A != null && controlPoint2A != null && endPointA != null) {
            gcA.beginPath();
            gcA.moveTo(startPointA.getX(), startPointA.getY());
            gcA.bezierCurveTo(controlPoint1A.getX(), controlPoint1A.getY(), controlPoint2A.getX(), controlPoint2A.getY(), endPointA.getX(), endPointA.getY());
            gcA.stroke();
        }

        if (startPointB != null && controlPoint1B != null && controlPoint2B != null && endPointB != null) {
            gcB.beginPath();
            gcB.moveTo(startPointB.getX(), startPointB.getY());
            gcB.bezierCurveTo(controlPoint1B.getX(), controlPoint1B.getY(), controlPoint2B.getX(), controlPoint2B.getY(), endPointB.getX(), endPointB.getY());
            gcB.stroke();
        }
    }

    private Point getPointFromIndex(int index, boolean isImageA) {
        int i = 0;
        for (Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            if (i == index) {
                return isImageA ? entry.getKey() : entry.getValue();
            }
            i++;
        }
        return null;
    }
}

package src.projet.fx;


import java.util.Map.Entry;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import src.projet.traitement.Point;
import src.projet.traitement.PointDeControle;


public abstract class FormesFX {
    protected Canvas canvasA;
    protected Canvas canvasB;
    protected PointDeControle pointsDeControle;
    protected int asciiDuA = 65;

    protected Point selectedPoint = null;
    protected boolean isDragging = false;
    protected boolean isMousePressed = false;
    protected boolean isDragged = false;

    public abstract void redrawPoints();

    public FormesFX(Canvas canvasA, Canvas canvasB, PointDeControle pointsDeControle) {
        this.canvasA = canvasA;
        this.canvasB = canvasB;
        this.pointsDeControle = pointsDeControle;
    }

    public void handleMousePressed(MouseEvent mouseEvent, boolean isImageA) {
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();
        isMousePressed = true;
        isDragged = false;
        
        for (Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            Point point = isImageA ? entry.getKey() : entry.getValue();
            if (point.distance(new Point(mouseX, mouseY)) < 10) { 
                selectedPoint = point;
                isDragging = true;
                break;
            }
        }
    }

    public void handleMouseDragged(MouseEvent mouseEvent, boolean isImageA) {
        isDragged = true;
        if (isDragging && selectedPoint != null) {
            double mouseX = Math.max(0, Math.min(600, mouseEvent.getX())); 
            double mouseY = Math.max(0, Math.min(600, mouseEvent.getY())); 
            try {
                selectedPoint.setX(mouseX);
                selectedPoint.setY(mouseY);
                checkForProximityAndMerge(mouseX, mouseY, isImageA);

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return;
            }
            redrawPoints();
        }
    }

    public void handleMouseReleased(boolean isImageA) {
        if (isDragging) {
            isDragging = false;
            selectedPoint = null;
            redrawPoints();
        }
        isMousePressed = false;
    }

    public void handleMouseClicked(MouseEvent mouseEvent, boolean isImageA) {
        if (!isDragged && !isMousePressed) { // Ensure this is not a drag
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

    public void resetPoints() {
        pointsDeControle.getPointsMap().clear();
        redrawPoints();
    }

    //TODO : normalement ya toujours le truc comme quoi 
    //on peut pas supp un couple de point si on a déplacé le point de l'img A
    // -> à résoudre lorsqu'on passera des matrices <point,point> à des listes pour les ptn de controles
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

        Scene dialogScene = new Scene(dialogVBox, 330, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    public void checkForProximityAndMerge(double mouseX, double mouseY, boolean isImageA) {
        for (Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            Point point = isImageA ? entry.getKey() : entry.getValue();
            if (point != selectedPoint && point.distance(new Point(mouseX, mouseY)) < 10) { 
                selectedPoint.setX(point.getX());
                selectedPoint.setY(point.getY());
                return;
            }
        }
    }

    public Point getPointFromIndex(int index, boolean isImageA) {
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

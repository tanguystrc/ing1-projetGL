package src.projet.fx;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import src.projet.traitement.Couple;
import src.projet.traitement.Point;
import src.projet.traitement.PointDeControle;

public class PhotoFX extends FormesFX {

    private List<PointDeControle> pointsDeControleLies;
    private int nbPointsDeControleAutreGroupe;

    public PhotoFX(Canvas canvasA, Canvas canvasB, PointDeControle pointsDeControle, List<PointDeControle> pointsDeControleLies) {
        super(canvasA, canvasB, pointsDeControle);
        this.pointsDeControleLies = pointsDeControleLies;
        this.nbPointsDeControleAutreGroupe = 0;
    }

    @Override
    public void resetPoints() {
        isDragging = false;
        isMousePressed = false;
        pointsDeControle.getPointsList().clear();
        pointsDeControleLies.clear();
        pointsDeControleLies.add(pointsDeControle);
        redrawPoints();
    }

    @Override
    public void handleMousePressed(MouseEvent mouseEvent, boolean isImageA) {
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        for (PointDeControle groupe : pointsDeControleLies) {
            for (Couple<Point, Point> couple : groupe.getPointsList()) {
                Point point = isImageA ? couple.getA() : couple.getB();
                if (point.distance(new Point(mouseX, mouseY)) < 10) { // zone de 10 pixels autour du point pour la sélection
                    selectedPoint = point;
                    isDragging = true;
                    isClickValid = false;
                    break;
                }
            }
        }
    }

    @Override
    public void checkForProximityAndMerge(double mouseX, double mouseY, boolean isImageA) {
        for (Couple<Point, Point> couple : pointsDeControle.getPointsList()) {
            Point point = isImageA ? couple.getA() : couple.getB();
            if (point != selectedPoint && point.distance(new Point(mouseX, mouseY)) < 10) { // Merge points within 10 pixels
                selectedPoint.setX(point.getX());
                selectedPoint.setY(point.getY());
                return;
            }
        }
    }

    @Override
    public void showDeletePointDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Supprimer un couple de point");

        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        int index = 0;
        int nbGroupe = 0;

        // Affichage
        for (PointDeControle groupe : pointsDeControleLies) {
            for (Couple<Point, Point> couple : groupe.getPointsList()) {
                Point key = couple.getA();
                Point value = couple.getB();
                String pointInfo = String.format("G%d : Points %c: A(%.1f, %.1f) - B(%.1f, %.1f)", nbGroupe,
                        (index < 26) ? (char) (asciiDuA + index) : Integer.toString(index - 26),
                        key.getX(), key.getY(), value.getX(), value.getY());
                listView.getItems().add(pointInfo);
                index++;
            }
            nbGroupe++;
        }

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();

            if (selectedIndex != -1 && selectedIndex < calculerNbTotalPoint()) {
                Point point = getPointFromIndexTotal(selectedIndex, true);
                pointsDeControleLies.get(calculerNumGroupe(selectedIndex)).supprimer(point);
                redrawPoints();
                dialog.close();
            }
        });

        VBox dialogVBox = new VBox(20, listView, deleteButton);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setAlignment(Pos.CENTER);

        Scene dialogScene = new Scene(dialogVBox, 370, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @Override
    public void redrawPoints() {
        canvasA.getGraphicsContext2D().clearRect(0, 0, 600, 600);
        canvasB.getGraphicsContext2D().clearRect(0, 0, 600, 600);

        int index;
        int numGroupe = 0;

        for (PointDeControle groupe : pointsDeControleLies) {
            nbPointsDeControleAutreGroupe = 0;
            for (int j = 0; j < numGroupe; j++) {
                nbPointsDeControleAutreGroupe += pointsDeControleLies.get(j).getPointsList().size();
            }
            index = 0 + nbPointsDeControleAutreGroupe;

            for (Couple<Point, Point> couple : groupe.getPointsList()) {
                Point key = couple.getA();
                Point value = couple.getB();
                draw(canvasA.getGraphicsContext2D(), key.getX(), key.getY(), true, index, numGroupe);
                draw(canvasB.getGraphicsContext2D(), value.getX(), value.getY(), false, index, numGroupe);
                index++;
            }
            numGroupe++;
        }
    }

    private void draw(GraphicsContext gc, double mouseX, double mouseY, boolean isImageA, int index, int numGroupe) {
        // lettre de l'alphabet au début, chiffres après
        String pointLabel = (index < 26) ? Character.toString((char) (asciiDuA + index)) : Integer.toString(index - 26);

        gc.setStroke(isImageA ? Color.RED : Color.BLUE);
        gc.strokeText("." + pointLabel, mouseX, mouseY);

        // On a un point précédent du même groupe, on le lie avec le nouveau
        if (index > 0 && !(index == nbPointsDeControleAutreGroupe)) {
            gc.setStroke(isImageA ? Color.BLUE : Color.RED);
            Point previousPoint = getPointFromIndex(index - 1, isImageA, numGroupe);
            if (previousPoint != null) {
                gc.strokeLine(previousPoint.getX(), previousPoint.getY(), mouseX, mouseY);
            }
        }
    }

    private Point getPointFromIndex(int index, boolean isImageA, int numGroupe) {
        int i = 0;
        for (Couple<Point, Point> couple : pointsDeControleLies.get(numGroupe).getPointsList()) {
            if (i == index - nbPointsDeControleAutreGroupe) {
                return isImageA ? couple.getA() : couple.getB();
            }
            i++;
        }
        return null;
    }

    private Point getPointFromIndexTotal(int index, boolean isImageA) {
        int i = 0;
        for (PointDeControle groupe : pointsDeControleLies) {
            for (Couple<Point, Point> couple : groupe.getPointsList()) {
                if (i == index) {
                    return isImageA ? couple.getA() : couple.getB();
                }
                i++;
            }
        }
        return null;
    }

    public int calculerNbTotalPoint() {
        int res = 0;
        for (PointDeControle groupe : pointsDeControleLies) {
            res += groupe.getPointsList().size();
        }
        return res;
    }

    public int calculerNumGroupe(int indexTotal) {
        int i = 0;
        int res = 0;
        for (PointDeControle groupe : pointsDeControleLies) {
            for (int j = 0; j < groupe.getPointsList().size(); j++) {
                if (indexTotal == i) {
                    return res;
                }
                i++;
            }
            res++;
        }
        return res;
    }
}

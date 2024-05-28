package src.projet.fx;

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
import src.projet.traitement.Couple;


public abstract class FormesFX {
    protected Canvas zonePointsA;
    protected Canvas zonePointsB;
    protected PointDeControle pointsDeControle;
    protected int asciiDuA = 65;

    protected Point pointSelectionne = null;
    protected boolean seDeplace = false;
    protected boolean sourisClicEnfonce = false;
    protected boolean leClicEstValide = true;

    public abstract void redessinerPoints();

    public FormesFX(Canvas zonePointsA, Canvas zonePointsB, PointDeControle pointsDeControle) {
        this.zonePointsA = zonePointsA;
        this.zonePointsB = zonePointsB;
        this.pointsDeControle = pointsDeControle;
    }

    public void sourisCliquee(MouseEvent mouseEvent, boolean estImageA) {        
        if (leClicEstValide) { // Ensure this is not a drag            
            double x = Math.max(0, Math.min(600, mouseEvent.getX())); 
            double y = Math.max(0, Math.min(600, mouseEvent.getY())); 
            Point point;
            try {
                point = new Point(x, y);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return;
            }

            if (estImageA) {
                pointsDeControle.ajouter(point, new Point(x, y)); 
                redessinerPoints();
            }
        }
        leClicEstValide = true;
    }

    public void sourisAppuyee(MouseEvent mouseEvent, boolean estImageA) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        sourisClicEnfonce = true;

        for (Couple<Point, Point> couple : pointsDeControle.getPointsList()) {
            Point point = estImageA ? couple.getA() : couple.getB();
            if (point.distance(new Point(x, y)) < 10) { 
                pointSelectionne = point;
                seDeplace = true;
                leClicEstValide = false;
                break;
            }
        }
    }

    public void sourisGlissée(MouseEvent mouseEvent, boolean estImageA) {
        if (seDeplace && pointSelectionne != null) {
            double x = Math.max(0, Math.min(600, mouseEvent.getX())); 
            double y = Math.max(0, Math.min(600, mouseEvent.getY())); 
            try {
                pointSelectionne.setX(x);
                pointSelectionne.setY(y);
                verifSuperposerPoint(x, y, estImageA);

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return;
            }
            redessinerPoints();
        }
    }

    public void sourisRelachee(boolean estImageA) {
        if (seDeplace) {
            seDeplace = false;            
            pointSelectionne = null;
            redessinerPoints();
        }
        sourisClicEnfonce = false;
    }

    public void reinitialiserPoints() {
        seDeplace = false;
        sourisClicEnfonce = false;
        pointsDeControle.getPointsList().clear();
        redessinerPoints();
    }

    public void fenetreSuppressionPoints() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Supprimer un point");

        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        int index = 0;
        for (Couple<Point, Point> couple : pointsDeControle.getPointsList()) {
            Point key = couple.getA();
            Point value = couple.getB();
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
            if (selectedIndex != -1 && selectedIndex < pointsDeControle.getPointsList().size()) {
                Couple<Point, Point> couple = pointsDeControle.getPointsList().get(selectedIndex);
                pointsDeControle.supprimer(couple.getA());
                redessinerPoints();
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

    public void verifSuperposerPoint(double x, double y, boolean estImageA) {
        for (Couple<Point, Point> couple : pointsDeControle.getPointsList()) {
            Point point = estImageA ? couple.getA() : couple.getB();
            if (point != pointSelectionne && point.distance(new Point(x, y)) < 10) { 
                pointSelectionne.setX(point.getX());
                pointSelectionne.setY(point.getY());
                return;
            }
        }
    }

    public Point getPointFromIndex(int index, boolean estImageA) {
        int i = 0;
        for (Couple<Point, Point> couple : pointsDeControle.getPointsList()) {
            if (i == index) {
                return estImageA ? couple.getA() : couple.getB();
            }
            i++;
        }
        return null;
    }
}

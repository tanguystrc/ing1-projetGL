package projet.fx;

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
import projet.traitement.Point;
import projet.traitement.PointDeControle;
import projet.traitement.Couple;

/**
 * Classe abstraite correspondant au FX des 3 modes (formes linéaires, arrondies et photo)
 */
public abstract class FormesFX {
    protected Canvas zonePointsA;
    protected Canvas zonePointsB;
    protected PointDeControle pointsDeControle;
    protected int asciiDuA = 65;

    // Informations du clic :
    protected Point pointSelectionne = null;
    protected boolean seDeplace = false;
    protected boolean sourisClicEnfonce = false;
    protected boolean leClicEstValide = true;

    /**
     * Actualise l'affichage des points sur les canvas
     */
    public abstract void redessinerPoints();

    /**
     * Constructeur
     * @param zonePointsA : canvas de l'image de début (A)
     * @param zonePointsB : canvas de l'image de fin (B)
     * @param pointsDeControle : information stockée des points de controle du groupe actuel
     */
    public FormesFX(Canvas zonePointsA, Canvas zonePointsB, PointDeControle pointsDeControle) {
        this.zonePointsA = zonePointsA;
        this.zonePointsB = zonePointsB;
        this.pointsDeControle = pointsDeControle;
    }

    /**
     * On efface tous les points stockés et on actualise les canvas
     */
    public void reinitialiserPoints() {
        seDeplace = false;
        sourisClicEnfonce = false;
        pointsDeControle.getPointsList().clear();
        redessinerPoints();
    }
    
    /**
     * Traitement si souris simplement cliquée, création du point
     * @param mouseEvent : pour récupérer les coordonnées du clic
     * @param estImageA : vrai si clic sur l'image de début (A)
     */
    public void sourisCliquee(MouseEvent mouseEvent, boolean estImageA) {        
        if (leClicEstValide) { // Pour vérifier que ce n'est PAS un déplacement de point, et bien une création            
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
                pointsDeControle.ajouter(point, new Point(x, y)); // Stock le nouveau point
                redessinerPoints(); // Affiche le nouveau point
            }
        }
        leClicEstValide = true;
    }

    /**
     * Traitement si souris enfoncée, on vérifie si on a cliqué sur un point existant ( = pour ensuite le déplacer)
     * @param mouseEvent : pour récupérer les coordonnées du clic
     * @param estImageA : vrai si clic sur l'image de début (A)
     */
    public void sourisAppuyee(MouseEvent mouseEvent, boolean estImageA) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        sourisClicEnfonce = true;        
        for (Couple<Point, Point> couple : pointsDeControle.getPointsList()) {
            Point point = estImageA ? couple.getA() : couple.getB();
            if (point.distance(new Point(x, y)) < 10) { // Zone de 10 pixels autour du point
                pointSelectionne = point;
                seDeplace = true;
                leClicEstValide = false;
                break;
            }
        }
    }

    /**
     * Traitement si souris glissé, on déplace un point
     * @param mouseEvent : pour récupérer les coordonnées du clic
     * @param estImageA : vrai si clic sur l'image de début (A)
     */
    public void sourisGlissee(MouseEvent mouseEvent, boolean estImageA) {
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

    /**
     * Traitement si souris relachée, clic fini, on réinitialise les informations du clic
     * @param mouseEvent : pour récupérer les coordonnées du clic
     * @param estImageA : vrai si clic sur l'image de début (A)
     */
    public void sourisRelachee(boolean estImageA) {
        if (seDeplace) {
            seDeplace = false;            
            pointSelectionne = null;
            redessinerPoints();
        }
        sourisClicEnfonce = false;
    }

    /**
     * Affiche et gère le traitement de la fenetre pour supprimer un point spécifique
     */
    public void fenetreSuppressionPoints() {
        // Initialisation du FX :
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Supprimer un point");
        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        // Récup les points existants pour l'affichage :
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

        // Supprime le point correspondant et actualise le canvas
        Button boutonSupprimer = new Button("Supprimer");
        boutonSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");
        boutonSupprimer.setOnAction(e -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1 && selectedIndex < pointsDeControle.getPointsList().size()) {
                Couple<Point, Point> couple = pointsDeControle.getPointsList().get(selectedIndex);
                pointsDeControle.supprimer(couple.getA());
                redessinerPoints();
                dialog.close();
            }
        });

        VBox dialogVBox = new VBox(20, listView, boutonSupprimer);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setAlignment(Pos.CENTER);

        Scene dialogScene = new Scene(dialogVBox, 330, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Vérifie si le point déplacé s'approche d'un autre point déjà existant pour bien le superposer directement 
     * @param x : coordonnée x du point traité
     * @param y : coordonnée y du point traité
     * @param estImageA : vrai si clic dans l'image de début (A)
     */
    public void verifSuperposerPoint(double x, double y, boolean estImageA) {
        for (Couple<Point, Point> couple : pointsDeControle.getPointsList()) {
            Point point = estImageA ? couple.getA() : couple.getB();
            if (point != pointSelectionne && point.distance(new Point(x, y)) < 10) { // Avec un rayon de 10 pixels autour
                pointSelectionne.setX(point.getX());
                pointSelectionne.setY(point.getY());
                return;
            }
        }
    }

    /**
     * Pour récupérer un point avec son index dans le pointsDeControle actuel
     * @param index : commence à 0
     * @param estImageA : vrai si point de controle de l'image de début (A)
     * @return Point : le point correspondant
     */
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

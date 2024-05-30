package projet.fx;

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
import projet.traitement.Couple;
import projet.traitement.Point;
import projet.traitement.PointDeControle;

/**
 * Classe correspondant au FX du mode des formes unies et abstraites
 */
public class PhotoFX extends FormesFX {

    private List<PointDeControle> pointsDeControleLies;
    private int nbPointsDeControleAutreGroupe;

    /**
     * Constructeur
     * @param zonePointsA : canvas de l'image de début (A)
     * @param zonePointsB : canvas de l'image de fin (B)
     * @param pointsDeControle : information stockée des points de controle du groupe actuel
     * @param pointsDeControleLies : information des différents groupes de points de controle
     */
    public PhotoFX(Canvas zonePointsA, Canvas zonePointsB, PointDeControle pointsDeControle, List<PointDeControle> pointsDeControleLies) {
        super(zonePointsA, zonePointsB, pointsDeControle);
        this.pointsDeControleLies = pointsDeControleLies;
        this.nbPointsDeControleAutreGroupe = 0;
    }

    /**
     * On efface tous les points stockés et on actualise les canvas
     * En Override car on a ici différents groupes
     */
    @Override
    public void reinitialiserPoints() {
        seDeplace = false;
        sourisClicEnfonce = false;
        pointsDeControle.getPointsList().clear();
        pointsDeControleLies.clear();
        pointsDeControleLies.add(pointsDeControle);
        redessinerPoints();
    }

    /**
     * Traitement si souris enfoncée, on vérifie si on a cliqué sur un point existant ( = pour ensuite le déplacer)
     * En Override car on a ici différents groupes
     * @param mouseEvent : pour récupérer les coordonnées du clic
     * @param estImageA : vrai si clic sur l'image de début (A)
     */
    @Override
    public void sourisAppuyee(MouseEvent mouseEvent, boolean estImageA) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        for (PointDeControle groupe : pointsDeControleLies) {
            for (Couple<Point, Point> couple : groupe.getPointsList()) {
                Point point = estImageA ? couple.getA() : couple.getB();
                if (point.distance(new Point(x, y)) < 10) { // zone de 10 pixels autour du point pour la sélection
                    pointSelectionne = point;
                    seDeplace = true;
                    leClicEstValide = false;
                    break;
                }
            }
        }
    }

    /**
     * Vérifie si le point déplacé s'approche d'un autre point déjà existant pour bien le superposer directement 
     * En Override car on a ici différents groupes
     * @param x : coordonnée x du point traité
     * @param y : coordonnée y du point traité
     * @param estImageA : vrai si clic dans l'image de début (A)
     */
    @Override
    public void verifSuperposerPoint(double x, double y, boolean estImageA) {
        for (Couple<Point, Point> couple : pointsDeControle.getPointsList()) {
            Point point = estImageA ? couple.getA() : couple.getB();
            if (point != pointSelectionne && point.distance(new Point(x, y)) < 10) { // Merge points within 10 pixels
                pointSelectionne.setX(point.getX());
                pointSelectionne.setY(point.getY());
                return;
            }
        }
    }

    /**
     * Affiche et gère le traitement de la fenetre pour supprimer un point spécifique
     * En Override car on a ici différents groupes
     */
    @Override
    public void fenetreSuppressionPoints() {
        // Initialisation du FX :
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Supprimer un couple de point");

        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        int index = 0;
        int nbGroupe = 0;
        System.out.println(pointsDeControleLies);
        // Récup les points existants des divers groupes pour l'affichage :
        for (PointDeControle groupe : pointsDeControleLies) {
            for (Couple<Point, Point> couple : groupe.getPointsList()) {
                Point key = couple.getA();
                Point value = couple.getB();
                String pointInfo = String.format("G%d : Points %c: A(%.1f, %.1f) - B(%.1f, %.1f)", nbGroupe,
                        (index < 26) ? (char) (asciiDuA + index) : (char) (index - 26),
                        key.getX(), key.getY(), value.getX(), value.getY());
                listView.getItems().add(pointInfo);
                index++;
            }
            nbGroupe++;
        }
        System.out.println("B");
        // Supprime le point correspondant et actualise le canvas
        Button boutonSupprimer = new Button("Supprimer");
        boutonSupprimer.setOnAction(e -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();

            if (selectedIndex != -1 && selectedIndex < calculerNbTotalPoint()) {
                Point point = getPointFromIndexTotal(selectedIndex, true);
                pointsDeControleLies.get(calculerNumGroupe(selectedIndex)).supprimer(point);
                redessinerPoints();
                dialog.close();
            }
        });
        System.out.println("C");
        VBox dialogVBox = new VBox(20, listView, boutonSupprimer);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setAlignment(Pos.CENTER);

        Scene dialogScene = new Scene(dialogVBox, 370, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Actualise l'affichage des points sur les canvas
     * En Override car on a ici différents groupes
     */
    @Override
    public void redessinerPoints() {
        zonePointsA.getGraphicsContext2D().clearRect(0, 0, 600, 600);
        zonePointsB.getGraphicsContext2D().clearRect(0, 0, 600, 600);

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
                dessiner(zonePointsA.getGraphicsContext2D(), key.getX(), key.getY(), true, index, numGroupe);
                dessiner(zonePointsB.getGraphicsContext2D(), value.getX(), value.getY(), false, index, numGroupe);
                index++;
            }
            numGroupe++;
        }
    }

    /**
     * Dessine le point sur le canvas
     * @param gc : informations du canvas
     * @param x : coordonnée x du point à dessiner
     * @param y : coordonnée y du point à dessiner
     * @param estImageA : vrai si point de l'image de début (A)
     * @param index : index du point dans le pointsDeControle, pour savoir s'il faut le lier à un point précédent ou non
     * @param numGroupe : numéro du groupe du point a traiter
     */
    private void dessiner(GraphicsContext gc, double x, double y, boolean estImageA, int index, int numGroupe) {
        // lettre de l'alphabet au début, chiffres après
        String pointLabel = (index < 26) ? Character.toString((char) (asciiDuA + index)) : Integer.toString(index - 26);

        gc.setStroke(estImageA ? Color.RED : Color.BLUE);
        gc.strokeText("." + pointLabel, x, y);

        // On a un point précédent du même groupe, on le lie avec le nouveau
        if (index > 0 && !(index == nbPointsDeControleAutreGroupe)) {
            if(numGroupe!=(pointsDeControleLies.size()-1)){
                gc.setStroke(Color.GREY);
            }else if(estImageA){
                gc.setStroke(Color.BLUE);
            }else{
                gc.setStroke(Color.RED);
            }            
            Point previousPoint = getPointFromIndex(index - 1, estImageA, numGroupe);
            if (previousPoint != null) {
                gc.strokeLine(previousPoint.getX(), previousPoint.getY(), x, y);
            }
        }
    }

    /**
     * Pour récupérer un point avec son index dans le pointsDeControle correspondant
     * @param index : commence à 0
     * @param estImageA : vrai si point de controle de l'image de début (A)
     * @param numGroupe : numéro du groupe pour savoir le pointsDeControle correspodnant
     * @return Point : le point correspondant
     */
    private Point getPointFromIndex(int index, boolean estImageA, int numGroupe) {
        int i = 0;
        for (Couple<Point, Point> couple : pointsDeControleLies.get(numGroupe).getPointsList()) {
            if (i == index - nbPointsDeControleAutreGroupe) {
                return estImageA ? couple.getA() : couple.getB();
            }
            i++;
        }
        return null;
    }

    /**
     * Pour récupérer un point avec son index TOTAl, en prenant en compte tous les groupes différents
     * @param index : commence à 0
     * @param estImageA : vrai si point de controle de l'image de début (A)
     * @return Point : le point correspondant
     */
    private Point getPointFromIndexTotal(int index, boolean estImageA) {
        int i = 0;
        for (PointDeControle groupe : pointsDeControleLies) {
            for (Couple<Point, Point> couple : groupe.getPointsList()) {
                if (i == index) {
                    return estImageA ? couple.getA() : couple.getB();
                }
                i++;
            }
        }
        return null;
    }

    /**
     * Calcul le nombre total de points
     * @return le nombre total de points parmis tous les groupes
     */
    public int calculerNbTotalPoint() {
        int res = 0;
        for (PointDeControle groupe : pointsDeControleLies) {
            res += groupe.getPointsList().size();
        }
        return res;
    }

    /**
     * Récupère le numéro de groupe correspondant à l'index TOTAL d'un point
     * @param indexTotal : index total du point correspondant
     * @return le numéro du groupe correspondant
     */
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

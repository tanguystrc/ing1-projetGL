package src.projet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

public class Hello extends Application {

    private static int asciiDuA = 65;
    private List<Point2D> pointsDeControle;
    private int nbPointsDeControle;
    private Point2D selectedPoint = null;
    private int selectedPointIndex = -1;
    private Canvas canvasA;
    private Canvas canvasB;

    private ImageView createImageView() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(600);
        imageView.setFitHeight(600);
        return imageView;
    }

    private Button createImageButton(String label) {
        Button button = new Button(label);
        button.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2; -fx-background-radius: 0;");
        button.setPrefSize(200, 50);
        return button;
    }

    private StackPane imgDansPane(ImageView i, boolean isImageA) {
        StackPane pane = new StackPane();
        pane.getChildren().add(i);
        pane.setStyle("-fx-border-color: #000000; -fx-border-width: 1px;");

        // canvas des points :
        Canvas canvas = new Canvas(600, 600);
        if (isImageA) {
            canvasA = canvas;
        } else {
            canvasB = canvas;
        }
        GraphicsContext gc = canvas.getGraphicsContext2D();

        canvas.setOnMouseClicked(mouseEvent -> {
            double mouseX = mouseEvent.getX();
            double mouseY = mouseEvent.getY();

            if (selectedPoint != null) {
                pointsDeControle.set(selectedPointIndex, new Point2D(mouseX, mouseY));
                selectedPoint = null;
                selectedPointIndex = -1;
                redrawPoints();
            } else {
                if (isImageA && nbPointsDeControle % 2 == 0) {
                    pointsDeControle.add(new Point2D(mouseX, mouseY));
                    draw(gc, mouseX, mouseY, isImageA);
                    nbPointsDeControle++;
                } else if (!isImageA && nbPointsDeControle % 2 != 0) {
                    pointsDeControle.add(new Point2D(mouseX, mouseY));
                    draw(gc, mouseX, mouseY, isImageA);
                    nbPointsDeControle++;
                }
            }
        });

        StackPane.setAlignment(canvas, Pos.TOP_LEFT); 
        pane.getChildren().add(canvas);

        return pane;
    }

    // Dessine un point de contrôle
    private void draw(GraphicsContext gc, double mouseX, double mouseY, boolean isImageA) {
        int index = (nbPointsDeControle) / 2;
        String pointLabel = (index < 26) ? Character.toString((char) (asciiDuA + index)) : Integer.toString(index - 26);

        gc.setStroke(Color.RED);
        gc.strokeText("." + pointLabel, mouseX, mouseY);
    }
    // Redessine les points de contrôle
    private void redrawPoints() {
        canvasA.getGraphicsContext2D().clearRect(0, 0, 600, 600);
        canvasB.getGraphicsContext2D().clearRect(0, 0, 600, 600);

        nbPointsDeControle = 0;
        for (int i = 0; i < pointsDeControle.size(); i++) {
            Point2D point = pointsDeControle.get(i);
            boolean isImageA = (i % 2 == 0);
            draw(isImageA ? canvasA.getGraphicsContext2D() : canvasB.getGraphicsContext2D(),
                    point.getX(), point.getY(), isImageA);
            nbPointsDeControle++;
        }
    }
    // Affiche une boîte de dialogue pour modifier un point de contrôle
    private void showModifyPointDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Modifier un point");

        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        for (int i = 0; i < pointsDeControle.size(); i++) {
            Point2D point = pointsDeControle.get(i);
            String pointInfo = String.format("Point %c%d: (%.1f, %.1f) - %s",
                    (i < 52) ? (char) (asciiDuA + (i / 2) % 26) : '0' + (i / 2 - 26),
                    i / 2 + 1, point.getX(), point.getY(), (i % 2 == 0) ? "Image A" : "Image B");
            listView.getItems().add(pointInfo);
        }

        Button modifyButton = new Button("Modifier");
        modifyButton.setOnAction(e -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                selectedPointIndex = selectedIndex;
                selectedPoint = pointsDeControle.get(selectedIndex);
                System.out.println("Point sélectionné pour modification : " + selectedPoint);
                dialog.close();
            }
        });

        VBox dialogVBox = new VBox(20, listView, modifyButton);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setAlignment(Pos.CENTER);

        Scene dialogScene = new Scene(dialogVBox, 300, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }
    // Réinitialise les points de contrôle
    private void resetPoints() {
        pointsDeControle.clear();
        nbPointsDeControle = 0;
        redrawPoints();
    }

    private void showDeletePointDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Supprimer un point");

        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        for (int i = 0; i < pointsDeControle.size(); i += 2) {
            Point2D pointA = pointsDeControle.get(i);
            Point2D pointB = pointsDeControle.get(i + 1);
            String pointInfo = String.format("Points %c%d: A(%.1f, %.1f) - B(%.1f, %.1f)",
                    (i < 52) ? (char) (asciiDuA + (i / 2) % 26) : '0' + (i / 2 - 26),
                    i / 2 + 1, pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());
            listView.getItems().add(pointInfo);
        }
        // Supprime le point sélectionné
        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                int index = selectedIndex * 2;
                pointsDeControle.remove(index + 1); 
                pointsDeControle.remove(index); 
                selectedPoint = null;
                selectedPointIndex = -1;
                nbPointsDeControle -= 2;
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
    
    @Override
    public void start(Stage primaryStage) {
        this.pointsDeControle = new ArrayList<>();
        this.nbPointsDeControle = 0;

        // Texte d'instruction :
        Text texteInstruction = new Text();
        texteInstruction.setFont(new Font(14));
        texteInstruction.setWrappingWidth(1000);
        texteInstruction.setTextAlignment(TextAlignment.JUSTIFY);
        texteInstruction.setText("Vos images doivent être de même dimension.\n"
                + "Cliquez sur la 1ère image pour ajouter un nouveau point de controle là où vous le souhaitez, puis sur la seconde pour son second emplacement."
                + "Cliquez sur Valider en suivant, après avoir précisé le nombre de frames souhaité pour le GIF.");

        // Configuration des zones des images A et B :
        ImageView startImageView = createImageView();
        ImageView endImageView = createImageView();

        StackPane paneA = imgDansPane(startImageView, true);
        StackPane paneB = imgDansPane(endImageView, false);

        // Conteneur d'images
        HBox imageBox = new HBox(20, paneA, paneB);
        imageBox.setAlignment(Pos.CENTER);

        // Boutons de chargement/changement d'image
        Button selectStartImageButton = createImageButton("Select Image A");
        Button selectEndImageButton = createImageButton("Select Image B");
        HBox buttonBox1 = new HBox(10, selectStartImageButton, selectEndImageButton);
        buttonBox1.setAlignment(Pos.CENTER);

        // Bouton pour modifier un point
        Button selectPointButton = new Button("Modifier un point");
        selectPointButton.setOnAction(e -> showModifyPointDialog());

        // Bouton pour réinitialiser les points
        Button resetButton = new Button("Réinitialiser");
        resetButton.setOnAction(e -> resetPoints());

        // Bouton pour supprimer une paire de points
        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> showDeletePointDialog());

        // Champ de texte pour le nombre de frames
        TextField framesTextField = new TextField();
        framesTextField.setPromptText("Frames (5-144)");
        framesTextField.setMaxWidth(120);

        Label framesLabel = new Label("Nombre de frames");
        VBox textFieldBox = new VBox(5, framesLabel, framesTextField);
        textFieldBox.setAlignment(Pos.CENTER);

        // Boutons
        Button startButton = new Button("Start");
        Button saveSettingsButton = new Button("Save settings");
        HBox buttonBox2 = new HBox(10, startButton, saveSettingsButton, selectPointButton, resetButton, deleteButton);
        buttonBox2.setAlignment(Pos.CENTER);

        // Configuration du BorderPane
        VBox vBox = new VBox();
        vBox.getChildren().addAll(texteInstruction, imageBox, buttonBox1, textFieldBox, buttonBox2);
        vBox.setPadding(new Insets(20));
        vBox.setSpacing(15);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: #eeeeee;");

        // Scène et affichage
        Scene scene = new Scene(vBox, 1450, 950);
        primaryStage.setTitle("PROJET GL");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Ajout des gestionnaires d'événements pour les boutons de chargement/changement d'images
        FileChooser fileChooser = new FileChooser();
        selectStartImageButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                startImageView.setImage(new Image("file:" + file.getAbsolutePath(), 600, 600, true, true));
            }
        });

        selectEndImageButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                endImageView.setImage(new Image("file:" + file.getAbsolutePath(), 600, 600, true, true));
            }
        });
    }

    // main method
    public static void main(String[] args) {
        launch(args);
    }
}

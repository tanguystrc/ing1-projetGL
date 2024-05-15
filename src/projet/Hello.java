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
import javafx.scene.input.MouseEvent;
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
    private boolean isDragging = false;
    private boolean isClickValid = true;

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

        canvas.setOnMousePressed(mouseEvent -> handleMousePressed(mouseEvent, isImageA));
        canvas.setOnMouseDragged(mouseEvent -> handleMouseDragged(mouseEvent));
        canvas.setOnMouseReleased(mouseEvent -> handleMouseReleased());
        canvas.setOnMouseClicked(mouseEvent -> handleMouseClicked(mouseEvent, isImageA));

        StackPane.setAlignment(canvas, Pos.TOP_LEFT); // Pour bien aligner le Canvas en haut à gauche (et superposer)
        pane.getChildren().add(canvas);

        return pane;
    }

    private void handleMousePressed(MouseEvent mouseEvent, boolean isImageA) {
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        for (int index = 0; index < pointsDeControle.size(); index++) {
            if ((isImageA && index % 2 == 0) || (!isImageA && index % 2 != 0)) {
                Point2D point = pointsDeControle.get(index);
                if (point.distance(mouseX, mouseY) < 10) { // assuming a tolerance of 10 pixels for selection
                    selectedPoint = point;
                    selectedPointIndex = index;
                    isDragging = true;
                    isClickValid = false;
                    break;
                }
            }
        }
    }

    private void handleMouseDragged(MouseEvent mouseEvent) {
        if (isDragging && selectedPoint != null) {
            double mouseX = mouseEvent.getX();
            double mouseY = mouseEvent.getY();
            pointsDeControle.set(selectedPointIndex, new Point2D(mouseX, mouseY));
            redrawPoints();
        }
    }

    private void handleMouseReleased() {
        if (isDragging) {
            isDragging = false;
            selectedPoint = null;
            selectedPointIndex = -1;
        }
    }

    private void handleMouseClicked(MouseEvent mouseEvent, boolean isImageA) {
        if (isClickValid) {
            double mouseX = mouseEvent.getX();
            double mouseY = mouseEvent.getY();

            if (isImageA && nbPointsDeControle % 2 == 0) {
                pointsDeControle.add(new Point2D(mouseX, mouseY));
                pointsDeControle.add(new Point2D(mouseX, mouseY)); // Add corresponding point in image B
                nbPointsDeControle += 2;
                redrawPoints();
            }
        }
        isClickValid = true;
    }

    private void draw(GraphicsContext gc, double mouseX, double mouseY, boolean isImageA, int index) {
        String pointLabel = (index < 26) ? Character.toString((char) (asciiDuA + index)) : Integer.toString(index - 26);

        gc.setStroke(Color.RED);
        gc.strokeText("." + pointLabel, mouseX, mouseY);
    }

    private void redrawPoints() {
        canvasA.getGraphicsContext2D().clearRect(0, 0, 600, 600);
        canvasB.getGraphicsContext2D().clearRect(0, 0, 600, 600);

        for (int i = 0; i < pointsDeControle.size(); i++) {
            Point2D point = pointsDeControle.get(i);
            boolean isImageA = (i % 2 == 0);
            draw(isImageA ? canvasA.getGraphicsContext2D() : canvasB.getGraphicsContext2D(),
                    point.getX(), point.getY(), isImageA, i / 2);
        }
    }

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
            if (i + 1 < pointsDeControle.size()) {
                Point2D pointA = pointsDeControle.get(i);
                Point2D pointB = pointsDeControle.get(i + 1);
                String pointInfo = String.format("Points %c%d: A(%.1f, %.1f) - B(%.1f, %.1f)",
                        (i / 2 < 26) ? (char) (asciiDuA + (i / 2)) : Integer.toString((i / 2) - 26),
                        (i / 2) + 1, pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());
                listView.getItems().add(pointInfo);
            }
        }

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1 && selectedIndex * 2 + 1 < pointsDeControle.size()) {
                int index = selectedIndex * 2;
                pointsDeControle.remove(index + 1); // Supprimer le point correspondant dans l'image B
                pointsDeControle.remove(index); // Supprimer le point dans l'image A
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

    private void showSuperposePointDialog(boolean isImageA) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Superposer un point");

        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        for (int i = 0; i < pointsDeControle.size(); i++) {
            if ((isImageA && i % 2 == 0) || (!isImageA && i % 2 != 0)) {
                Point2D point = pointsDeControle.get(i);
                String pointInfo = String.format("Point %c%d: (%.1f, %.1f) - %s",
                        (i / 2 < 26) ? (char) (asciiDuA + (i / 2)) : Integer.toString((i / 2) - 26),
                        (i / 2) + 1, point.getX(), point.getY(), (i % 2 == 0) ? "Image A" : "Image B");
                listView.getItems().add(pointInfo);
            }
        }

        Button superposeButton = new Button("Superposer");
        superposeButton.setOnAction(e -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1 && selectedIndex * 2 < pointsDeControle.size()) {
                int index = (selectedIndex * 2) + (isImageA ? 0 : 1);
                Point2D point = pointsDeControle.get(index);
                pointsDeControle.add(new Point2D(point.getX(), point.getY())); // Ajouter un nouveau point superposé à la même position
                pointsDeControle.add(new Point2D(point.getX(), point.getY())); // Add corresponding point in other image
                nbPointsDeControle += 2;
                redrawPoints();
                dialog.close();
            }
        });

        VBox dialogVBox = new VBox(20, listView, superposeButton);
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

        // Bouton pour réinitialiser les points
        Button resetButton = new Button("Réinitialiser");
        resetButton.setOnAction(e -> resetPoints());

        // Bouton pour supprimer une paire de points
        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> showDeletePointDialog());

        // Bouton pour superposer un point
        Button superposePointButton = new Button("Superposer un point");
        superposePointButton.setOnAction(e -> {
            boolean isImageA = (nbPointsDeControle % 2 == 0);
            showSuperposePointDialog(isImageA);
        });

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
        HBox buttonBox2 = new HBox(10, startButton, saveSettingsButton, resetButton, deleteButton, superposePointButton);
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

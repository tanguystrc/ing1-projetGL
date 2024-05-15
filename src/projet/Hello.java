package src.projet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.control.TextFormatter;
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
import javafx.util.Duration;

public class Hello extends Application {

    private static int asciiDuA = 65;
    private PointDeControle pointsDeControle;
    private int nbPointsDeControle;
    private Point selectedPoint = null;
    private int selectedPointIndex = -1;
    private Canvas canvasA;
    private Canvas canvasB;
    private boolean isDragging = false;
    private boolean isClickValid = true;
    private List<Text> pointLabels = new ArrayList<>();

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
        canvas.setOnMouseMoved(mouseEvent -> handleMouseMoved(mouseEvent, isImageA, gc));

        StackPane.setAlignment(canvas, Pos.TOP_LEFT); // Pour bien aligner le Canvas en haut à gauche (et superposer)
        pane.getChildren().add(canvas);

        return pane;
    }

    private void handleMousePressed(MouseEvent mouseEvent, boolean isImageA) {
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        for (int index = 0; index < pointsDeControle.getPointsMap().size(); index++) {
            Point point = getPointFromIndex(index, isImageA);
            if (point != null && point.distance(new Point(mouseX, mouseY)) < 10) { // assuming a tolerance of 10 pixels for selection
                selectedPoint = point;
                selectedPointIndex = index;
                isDragging = true;
                isClickValid = false;
                break;
            }
        }
    }

    private void handleMouseDragged(MouseEvent mouseEvent) {
        if (isDragging && selectedPoint != null) {
            double mouseX = mouseEvent.getX();
            double mouseY = mouseEvent.getY();
            selectedPoint.setX(mouseX);
            selectedPoint.setY(mouseY);
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
            Point point = new Point(mouseX, mouseY);

            if (isImageA) {
                pointsDeControle.ajouter(point, point); // Add corresponding point in image B
                nbPointsDeControle++;
                redrawPoints();
            }
        }
        isClickValid = true;
    }

    private void handleMouseMoved(MouseEvent mouseEvent, boolean isImageA, GraphicsContext gc) {
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        for (int index = 0; index < pointsDeControle.getPointsMap().size(); index++) {
            Point point = getPointFromIndex(index, isImageA);
            if (point != null && point.distance(new Point(mouseX, mouseY)) < 10) { // assuming a tolerance of 10 pixels for hover
                showAnimatedLabels(mouseX, mouseY, index);
                return;
            }
        }
        removeAnimatedLabels();
    }

    private void showAnimatedLabels(double mouseX, double mouseY, int index) {
        for (Text label : pointLabels) {
            label.setVisible(false);
        }
        pointLabels.clear();

        String pointLabel = (index < 26) ? Character.toString((char) (asciiDuA + index)) : Integer.toString(index - 26);

        Text labelA = new Text("." + pointLabel);
        labelA.setFont(new Font(12));
        labelA.setFill(Color.RED);
        labelA.setX(mouseX + 10);
        labelA.setY(mouseY - 10);

        Text labelB = new Text("." + pointLabel);
        labelB.setFont(new Font(12));
        labelB.setFill(Color.RED);
        labelB.setX(mouseX - 10);
        labelB.setY(mouseY + 10);

        pointLabels.add(labelA);
        pointLabels.add(labelB);

        animateLabel(labelA, mouseX + 10, mouseY - 10);
        animateLabel(labelB, mouseX - 10, mouseY + 10);

        canvasA.getGraphicsContext2D().getCanvas().getParent().getChildrenUnmodifiable().addAll(labelA, labelB);
    }

    private void animateLabel(Text label, double startX, double startY) {
        KeyValue keyValueX = new KeyValue(label.translateXProperty(), startX + 20);
        KeyValue keyValueY = new KeyValue(label.translateYProperty(), startY + 20);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(500), keyValueX, keyValueY);

        Timeline timeline = new Timeline(keyFrame);
        timeline.setAutoReverse(true);
        timeline.setCycleCount(2);
        timeline.play();
    }

    private void removeAnimatedLabels() {
        for (Text label : pointLabels) {
            label.setVisible(false);
        }
        pointLabels.clear();
    }

    private Point getPointFromIndex(int index, boolean isImageA) {
        List<Point> points = new ArrayList<>(pointsDeControle.getPointsMap().keySet());
        if (index < points.size()) {
            Point key = points.get(index);
            Point value = pointsDeControle.getPointsMap().get(key);
            if (isImageA) {
                return key;
            } else {
                return value;
            }
        }
        return null;
    }

    private void draw(GraphicsContext gc, double mouseX, double mouseY, boolean isImageA, int index) {
        String pointLabel = (index < 26) ? Character.toString((char) (asciiDuA + index)) : Integer.toString(index - 26);

        gc.setStroke(Color.RED);
        gc.strokeText("." + pointLabel, mouseX, mouseY);
    }

    private void redrawPoints() {
        canvasA.getGraphicsContext2D().clearRect(0, 0, 600, 600);
        canvasB.getGraphicsContext2D().clearRect(0, 0, 600, 600);

        int index = 0;
        for (Point key : pointsDeControle.getPointsMap().keySet()) {
            Point value = pointsDeControle.getPointsMap().get(key);
            draw(canvasA.getGraphicsContext2D(), key.getX(), key.getY(), true, index);
            draw(canvasB.getGraphicsContext2D(), value.getX(), value.getY(), false, index);
            index++;
        }
    }

    private void resetPoints() {
        pointsDeControle.getPointsMap().clear();
        nbPointsDeControle = 0;
        redrawPoints();
    }

    private void showDeletePointDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Supprimer un point");

        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        int index = 0;
        for (Point key : pointsDeControle.getPointsMap().keySet()) {
            Point value = pointsDeControle.getPointsMap().get(key);
            String pointInfo = String.format("Points %c%d: A(%.1f, %.1f) - B(%.1f, %.1f)",
                    (index < 26) ? (char) (asciiDuA + index) : Integer.toString(index - 26),
                    index + 1, key.getX(), key.getY(), value.getX(), value.getY());
            listView.getItems().add(pointInfo);
            index++;
        }

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1 && selectedIndex < pointsDeControle.getPointsMap().size()) {
                Point point = getPointFromIndex(selectedIndex, true);
                pointsDeControle.supprimer(point);
                nbPointsDeControle--;
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
        int index = 0;
        for (Point key : pointsDeControle.getPointsMap().keySet()) {
            if ((isImageA && index % 2 == 0) || (!isImageA && index % 2 != 0)) {
                String pointInfo = String.format("Point %c%d: (%.1f, %.1f) - %s",
                        (index < 26) ? (char) (asciiDuA + index) : Integer.toString(index - 26),
                        index + 1, key.getX(), key.getY(), (index % 2 == 0) ? "Image A" : "Image B");
                listView.getItems().add(pointInfo);
            }
            index++;
        }

        Button superposeButton = new Button("Superposer");
        superposeButton.setOnAction(e -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1 && selectedIndex < pointsDeControle.getPointsMap().size()) {
                Point point = getPointFromIndex(selectedIndex, isImageA);
                Point newPoint = new Point(point.getX(), point.getY());
                pointsDeControle.ajouter(newPoint, newPoint); // Ajouter un nouveau point superposé à la même position
                nbPointsDeControle++;
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
        this.pointsDeControle = new PointDeControle();
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

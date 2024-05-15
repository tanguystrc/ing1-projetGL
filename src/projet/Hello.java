package src.projet;

import java.io.File;
import java.util.Map;
import javafx.application.Application;
import javafx.geometry.Insets;
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

    private PointDeControle pointsDeControle;
    private int nbPointsDeControle;
    private Point selectedPoint = null;
    private int selectedPointIndex = -1;
    private Canvas canvasA;
    private Canvas canvasB;
    private boolean isAddingPoint = false;

    public Hello() {
        this.pointsDeControle = new PointDeControle();
    }

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

    private void enableDrag(Button button) {
        final Delta dragDelta = new Delta();
        button.setOnMousePressed(event -> {
            dragDelta.x = button.getLayoutX() - event.getSceneX();
            dragDelta.y = button.getLayoutY() - event.getSceneY();
            button.setCursor(javafx.scene.Cursor.MOVE);
        });
        button.setOnMouseReleased(event -> button.setCursor(javafx.scene.Cursor.HAND));
        button.setOnMouseDragged(event -> {
            button.setLayoutX(event.getSceneX() + dragDelta.x);
            button.setLayoutY(event.getSceneY() + dragDelta.y);
        });
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

        canvas.setOnMousePressed(event -> {
            if (!isAddingPoint) {
                Point mousePoint = new Point(event.getX(), event.getY());
                for (Map.Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
                    Point pointA = entry.getKey();
                    Point pointB = entry.getValue();
                    if (isImageA && pointA.distance(mousePoint) < 5) {
                        selectedPoint = pointA;
                        selectedPointIndex = getIndexByPoint(pointA, true);
                        break;
                    } else if (!isImageA && pointB.distance(mousePoint) < 5) {
                        selectedPoint = pointB;
                        selectedPointIndex = getIndexByPoint(pointB, false);
                        break;
                    }
                }
            }
        });

        canvas.setOnMouseDragged(event -> {
            if (selectedPoint != null) {
                selectedPoint.setX(event.getX());
                selectedPoint.setY(event.getY());
                redrawPoints();
            }
        });

        canvas.setOnMouseReleased(event -> {
            selectedPoint = null;
            selectedPointIndex = -1;
        });

        canvas.setOnMouseClicked(event -> {
            if (isAddingPoint) {
                double mouseX = event.getX();
                double mouseY = event.getY();
                if (isImageA && nbPointsDeControle % 2 == 0) {
                    pointsDeControle.ajouter(new Point(mouseX, mouseY), new Point(mouseX, mouseY));
                    draw(gc, mouseX, mouseY, isImageA, nbPointsDeControle / 2);
                    draw(canvasB.getGraphicsContext2D(), mouseX, mouseY, false, nbPointsDeControle / 2);
                    nbPointsDeControle += 2;
                    isAddingPoint = false;
                }
            }
        });

        StackPane.setAlignment(canvas, Pos.TOP_LEFT); // Pour bien aligner le Canvas en haut à gauche (et superposer)
        pane.getChildren().add(canvas);

        return pane;
    }

    private int getIndexByPoint(Point point, boolean isImageA) {
        int index = 0;
        for (Map.Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            Point pointA = entry.getKey();
            Point pointB = entry.getValue();
            if (isImageA && pointA.equals(point)) {
                return index;
            } else if (!isImageA && pointB.equals(point)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private void draw(GraphicsContext gc, double mouseX, double mouseY, boolean isImageA, int index) {
        String pointLabel = (index < 26) ? Character.toString((char) (asciiDuA + index)) : Integer.toString(index - 26);

        gc.setStroke(Color.RED);
        gc.strokeText("." + pointLabel, mouseX, mouseY);
    }

    private void redrawPoints() {
        canvasA.getGraphicsContext2D().clearRect(0, 0, 600, 600);
        canvasB.getGraphicsContext2D().clearRect(0, 0, 600, 600);

        nbPointsDeControle = 0;
        int i = 0;
        for (Map.Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            Point pointA = entry.getKey();
            Point pointB = entry.getValue();
            if (pointA != null) {
                draw(canvasA.getGraphicsContext2D(), pointA.getX(), pointA.getY(), true, i);
            }
            if (pointB != null) {
                draw(canvasB.getGraphicsContext2D(), pointB.getX(), pointB.getY(), false, i);
            }
            i++;
        }
        nbPointsDeControle = pointsDeControle.getPointsMap().size() * 2;
    }

    private void resetPoints() {
        pointsDeControle = new PointDeControle();
        nbPointsDeControle = 0;
        redrawPoints();
    }

    private void showDeletePointDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Supprimer un point");

        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        int i = 0;
        for (Map.Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            Point pointA = entry.getKey();
            Point pointB = entry.getValue();
            String pointInfo = String.format("Points %c%d: A(%.1f, %.1f) - B(%.1f, %.1f)",
                    (i < 52) ? (char) (asciiDuA + (i / 2) % 26) : '0' + (i / 2 - 26),
                    i / 2 + 1, pointA.getX(), pointA.getY(), pointB != null ? pointB.getX() : 0, pointB != null ? pointB.getY() : 0);
            listView.getItems().add(pointInfo);
            i++;
        }

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                int index = selectedIndex / 2;
                Point key = pointsDeControle.getPointsMap().keySet().toArray(new Point[0])[index];
                pointsDeControle.getPointsMap().remove(key);
                selectedPoint = null;
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
        int i = 0;
        for (Map.Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            Point pointA = entry.getKey();
            Point pointB = entry.getValue();
            String pointInfo = (isImageA)
                    ? String.format("Point %c%d: (%.1f, %.1f) - Image A", (i < 52) ? (char) (asciiDuA + i % 26) : '0' + (i / 2 - 26), i / 2 + 1, pointA.getX(), pointA.getY())
                    : String.format("Point %c%d: (%.1f, %.1f) - Image B", (i < 52) ? (char) (asciiDuA + i % 26) : '0' + (i / 2 - 26), i / 2 + 1, pointB.getX(), pointB.getY());
            listView.getItems().add(pointInfo);
            i++;
        }

        Button superposeButton = new Button("Superposer");
        superposeButton.setOnAction(e -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                int index = selectedIndex / 2;
                Point point = (isImageA)
                        ? pointsDeControle.getPointsMap().keySet().toArray(new Point[0])[index]
                        : pointsDeControle.getPointsMap().values().toArray(new Point[0])[index];
                pointsDeControle.ajouter(point, point);
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

        // Bouton pour ajouter un point
        Button addPointButton = createImageButton("Ajouter un point");
        addPointButton.setOnAction(e -> isAddingPoint = true);

        // Bouton pour arrêter d'ajouter des points
        Button stopAddPointButton = createImageButton("Arrêter d'ajouter des points");
        stopAddPointButton.setOnAction(e -> isAddingPoint = false);

        // Bouton pour réinitialiser les points
        Button resetButton = createImageButton("Réinitialiser");
        resetButton.setOnAction(e -> resetPoints());

        // Bouton pour supprimer une paire de points
        Button deleteButton = createImageButton("Supprimer");
        deleteButton.setOnAction(e -> showDeletePointDialog());

        // Bouton pour superposer un point
        Button superposePointButton = createImageButton("Superposer un point");
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
        Button startButton = createImageButton("Start");
        Button saveSettingsButton = createImageButton("Save settings");

        // Enable dragging for buttons
        enableDrag(addPointButton);
        enableDrag(stopAddPointButton);
        enableDrag(resetButton);
        enableDrag(deleteButton);
        enableDrag(superposePointButton);
        enableDrag(startButton);
        enableDrag(saveSettingsButton);

        HBox buttonBox2 = new HBox(10, startButton, saveSettingsButton, addPointButton, stopAddPointButton, resetButton, deleteButton, superposePointButton);
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

    // Simple class to hold x and y coordinates for dragging
    class Delta {
        double x, y;
    }
}

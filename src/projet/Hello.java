package src.projet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Hello extends Application {

    private static int asciiDuA = 65;

    private PointDeControle pointsDeControle;
    private int nbPointsDeControle;
    private Point selectedPoint = null;
    private Canvas canvasA;
    private Canvas canvasB;
    private boolean isDragging = false;
    private boolean isClickValid = true;
    private Image startImage;
    private Image endImage;

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
        
        canvas.setOnMousePressed(mouseEvent -> handleMousePressed(mouseEvent, isImageA));
        canvas.setOnMouseDragged(mouseEvent -> handleMouseDragged(mouseEvent, isImageA));
        canvas.setOnMouseReleased(mouseEvent -> handleMouseReleased());
        canvas.setOnMouseClicked(mouseEvent -> handleMouseClicked(mouseEvent, isImageA));

        StackPane.setAlignment(canvas, Pos.TOP_LEFT); // Pour bien aligner le Canvas en haut à gauche (et superposer)
        pane.getChildren().add(canvas);

        return pane;
    }

    private void handleMousePressed(MouseEvent mouseEvent, boolean isImageA) {
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        for (Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            Point point = isImageA ? entry.getKey() : entry.getValue();
            if (point.distance(new Point(mouseX, mouseY)) < 10) { // assuming a tolerance of 10 pixels for selection
                selectedPoint = point;
                isDragging = true;
                isClickValid = false;
                break;
            }
        }
    }

private void handleMouseDragged(MouseEvent mouseEvent, boolean isImageA) {
    if (isDragging && selectedPoint != null) {
        double mouseX = Math.max(0, Math.min(600, mouseEvent.getX())); // Limiting the x-coordinate
        double mouseY = Math.max(0, Math.min(600, mouseEvent.getY())); // Limiting the y-coordinate
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


    private void handleMouseReleased() {
        if (isDragging) {
            isDragging = false;
            selectedPoint = null;
        }
    }

    private void handleMouseClicked(MouseEvent mouseEvent, boolean isImageA) {
        if (isClickValid) {
            double mouseX = Math.max(0, Math.min(600, mouseEvent.getX())); // Limiting the x-coordinate
            double mouseY = Math.max(0, Math.min(600, mouseEvent.getY())); // Limiting the y-coordinate
            Point point;
            try {
                point = new Point(mouseX, mouseY);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return;
            }
    
            if (isImageA) {
                pointsDeControle.ajouter(point, new Point(mouseX, mouseY)); // Add corresponding point in image B
                nbPointsDeControle++;
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

        int index = 0;
        for (Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            Point key = entry.getKey();
            Point value = entry.getValue();
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
        for (Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            Point key = entry.getKey();
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
                pointsDeControle.ajouter(newPoint, new Point(point.getX(), point.getY())); // Ajouter un nouveau point superposé à la même position
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
        startButton.setOnAction(e -> {
            //afficher la map de points de controle
            System.out.println(pointsDeControle.toString());
            int nbFrames;
            try {
                nbFrames = Integer.parseInt(framesTextField.getText());
                if (nbFrames < 5 || nbFrames > 144) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                System.out.println("Le nombre de frames doit être compris entre 5 et 144.");
                return;
            }
            
            if (startImage != null) {
                try {
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(startImage, null);
                    FormeLineaire formeLineaire = new FormeLineaire(pointsDeControle, nbFrames, null, null);
                    formeLineaire.morphismeSimple(bufferedImage, pointsDeControle, nbFrames);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

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
                startImage = new Image("file:" + file.getAbsolutePath(), 600, 600, true, true);
                startImageView.setImage(startImage);
            }
        });

        selectEndImageButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                endImage = new Image("file:" + file.getAbsolutePath(), 600, 600, true, true);
                endImageView.setImage(endImage);
            }
        });
    }

    // main method
    public static void main(String[] args) {
        launch(args);
    }
}

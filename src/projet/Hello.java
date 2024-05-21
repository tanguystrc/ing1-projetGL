package src.projet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.scene.layout.BorderPane;
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
import javafx.scene.Cursor;
import javafx.scene.shape.Rectangle;

public class Hello extends Application {

    private static int asciiDuA = 65;

    private PointDeControle pointsDeControle;
    private Point selectedPoint = null;
    private Canvas canvasA;
    private Canvas canvasB;
    private boolean isDragging = false;
    private boolean isClickValid = true;
    private boolean isPipetteMode = false;
    private Color selectedColor;
    private Image startImage;
    private Image endImage;
    private Rectangle colorDisplay;

    private FormesFX currentForme;

    private ImageView createImageView() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(600);
        imageView.setFitHeight(600);
        return imageView;
    }

    private Button createImageButton(String label) {
        Button button = new Button(label);
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");
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
        canvas.setOnMouseReleased(mouseEvent -> handleMouseReleased(isImageA));
        canvas.setOnMouseClicked(mouseEvent -> handleMouseClicked(mouseEvent, isImageA));

        StackPane.setAlignment(canvas, Pos.TOP_LEFT); // Pour bien aligner le Canvas en haut à gauche (et superposer)
        pane.getChildren().add(canvas);

        return pane;
    }

    private void handleMousePressed(MouseEvent mouseEvent, boolean isImageA) {
        if (currentForme != null) {
            currentForme.handleMousePressed(mouseEvent, isImageA);
        }
    }

    private void handleMouseDragged(MouseEvent mouseEvent, boolean isImageA) {
        if (currentForme != null) {
            currentForme.handleMouseDragged(mouseEvent, isImageA);
        }
    }

    private void handleMouseReleased(boolean isImageA) {
        if (currentForme != null) {
            currentForme.handleMouseReleased(isImageA);
        }
    }

    private void handleMouseClicked(MouseEvent mouseEvent, boolean isImageA) {
        if (isPipetteMode) {
            pickColor(mouseEvent, isImageA);
            isPipetteMode = false; // Désactiver le mode pipette après utilisation
            canvasA.setCursor(Cursor.DEFAULT); // Reset cursor
            canvasB.setCursor(Cursor.DEFAULT); // Reset cursor
        } else if (isClickValid && currentForme != null) {
            currentForme.handleMouseClicked(mouseEvent, isImageA);
        }
        isClickValid = true;
    }

    private void pickColor(MouseEvent mouseEvent, boolean isImageA) {
        Image image = isImageA ? startImage : endImage;
        if (image != null) {
            int x = (int) mouseEvent.getX();
            int y = (int) mouseEvent.getY();
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            java.awt.Color color = new java.awt.Color(bufferedImage.getRGB(x, y));
            selectedColor = Color.rgb(color.getRed(), color.getGreen(), color.getBlue());
            colorDisplay.setFill(selectedColor);
            System.out.println("Selected Color: " + selectedColor);
        }
    }

    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #2c3e50; -fx-padding: 20px;");
        
        Button linearButton = new Button("Formes Linéaires");
        linearButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #2980b9; -fx-border-width: 1px; -fx-cursor: hand;");
        linearButton.setOnAction(e -> {
            currentForme = new FormesLineaireFX(canvasA, canvasB, pointsDeControle); 
        });

        Button roundedButton = new Button("Formes Arrondies");
        roundedButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #2980b9; -fx-border-width: 1px; -fx-cursor: hand;");
        roundedButton.setOnAction(e -> {
            currentForme = new FormesArrondiesFX(canvasA, canvasB, pointsDeControle); 
        });

        menu.getChildren().addAll(linearButton, roundedButton);
        return menu;
    }

    @Override
    public void start(Stage primaryStage) {
        this.pointsDeControle = new PointDeControle();
        this.currentForme = new FormesLineaireFX(canvasA, canvasB, pointsDeControle); 

        colorDisplay = new Rectangle(30, 30, Color.TRANSPARENT);
        colorDisplay.setStroke(Color.BLACK);

        Text texteInstruction = new Text();
        texteInstruction.setFont(new Font(14));
        texteInstruction.setWrappingWidth(1000);
        texteInstruction.setTextAlignment(TextAlignment.JUSTIFY);
        texteInstruction.setText("Vos images doivent être de même dimension.\n"
                + "Cliquez sur la 1ère image pour ajouter un nouveau point de controle là où vous le souhaitez, puis sur la seconde pour son second emplacement."
                + "Cliquez sur Valider en suivant, après avoir précisé le nombre de frames souhaité pour le GIF.");

        ImageView startImageView = createImageView();
        ImageView endImageView = createImageView();

        StackPane paneA = imgDansPane(startImageView, true);
        StackPane paneB = imgDansPane(endImageView, false);

        HBox imageBox = new HBox(20, paneA, paneB);
        imageBox.setAlignment(Pos.CENTER);

        Button selectStartImageButton = createImageButton("Select Image A");
        Button selectEndImageButton = createImageButton("Select Image B");

        HBox buttonBox1 = new HBox(10, selectStartImageButton, selectEndImageButton);
        buttonBox1.setAlignment(Pos.CENTER);

        Button resetButton = new Button("Réinitialiser");
        resetButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");
        resetButton.setOnAction(e -> currentForme.resetPoints());

        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");
        deleteButton.setOnAction(e -> currentForme.showDeletePointDialog());

        Button pipetteButton = new Button("Pipette");
        pipetteButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");
        pipetteButton.setOnAction(e -> {
            isPipetteMode = true;
            canvasA.setCursor(Cursor.CROSSHAIR); 
            canvasB.setCursor(Cursor.CROSSHAIR); 
        });

        TextField framesTextField = new TextField();
        framesTextField.setPromptText("Frames (5-144)");
        framesTextField.setMaxWidth(120);
        framesTextField.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        Label framesLabel = new Label("Nombre de frames");
        framesLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");
        VBox textFieldBox = new VBox(5, framesLabel, framesTextField);
        textFieldBox.setAlignment(Pos.CENTER);

        Button startButton = new Button("Start");
        startButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");
        startButton.setOnAction(e -> {
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
                    formeLineaire.setSelectedColor(selectedColor); 
                    formeLineaire.morphisme(bufferedImage, pointsDeControle, nbFrames);
                    Platform.runLater(() -> {
                        try {
                            GIFViewer.display("GIF Viewer", "animation.gif");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        HBox buttonBox2 = new HBox(10, startButton, resetButton, deleteButton, pipetteButton, colorDisplay);
        buttonBox2.setAlignment(Pos.CENTER);

        VBox menu = createMenu();
        VBox mainContent = new VBox();
        mainContent.getChildren().addAll(texteInstruction, imageBox, buttonBox1, textFieldBox, buttonBox2);
        mainContent.setPadding(new Insets(20));
        mainContent.setSpacing(15);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setStyle("-fx-background-color: #ecf0f1;");

        BorderPane root = new BorderPane();
        root.setLeft(menu);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1600, 1000);
        primaryStage.setTitle("PROJET GL");
        primaryStage.setScene(scene);
        primaryStage.show();

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

    public static void main(String[] args) {
        launch(args);
    }
}

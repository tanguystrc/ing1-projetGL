package src.projet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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

    private static final String DEFAULT_STYLE = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #2980b9; -fx-border-width: 1px; -fx-cursor: hand;";
    private static final String SELECTED_STYLE = "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #27ae60; -fx-border-width: 1px; -fx-cursor: hand;";

    private PointDeControle pointsDeControle;
    private Canvas canvasA;
    private Canvas canvasB;
    private boolean isClickValid = true;
    private boolean isPipetteMode = false;
    private Color selectedColor;
    private Image startImage;
    private Image endImage;
    private Rectangle colorDisplay;
    private FormesFX currentForme;

    private Button linearButton;
    private Button roundedButton;
    private ImageView startImageView;
    private ImageView endImageView;

    private ImageView createImageView() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(600);
        imageView.setFitHeight(600);
        return imageView;
    }

    private Button createImageButton(String label) {
        Button button = new Button(label);
        button.setStyle(DEFAULT_STYLE);
        button.setPrefSize(200, 50);
        return button;
    }

    private StackPane imgDansPane(ImageView i, boolean isImageA) {
        StackPane pane = new StackPane();
        pane.getChildren().add(i);
        pane.setStyle("-fx-border-color: #000000; -fx-border-width: 1px;");

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

        StackPane.setAlignment(canvas, Pos.TOP_LEFT);
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
            isPipetteMode = false;
            canvasA.setCursor(Cursor.DEFAULT);
            canvasB.setCursor(Cursor.DEFAULT);
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

    private void updateButtonStyles(Button selectedButton) {
        linearButton.setStyle(DEFAULT_STYLE);
        roundedButton.setStyle(DEFAULT_STYLE);
        selectedButton.setStyle(SELECTED_STYLE);
    }

    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #2c3e50; -fx-padding: 20px;");

        linearButton = new Button("Formes Linéaires");
        linearButton.setStyle(DEFAULT_STYLE);
        linearButton.setOnAction(e -> {
            currentForme = new FormesLineaireFX(canvasA, canvasB, pointsDeControle);
            currentForme.resetPoints();
            updateButtonStyles(linearButton);
        });

        roundedButton = new Button("Formes Arrondies");
        roundedButton.setStyle(DEFAULT_STYLE);
        roundedButton.setOnAction(e -> {
            currentForme = new FormesArrondiesFX(canvasA, canvasB, pointsDeControle);
            currentForme.resetPoints();
            updateButtonStyles(roundedButton);
        });

        Button exampleButton = new Button("Exemple");
        exampleButton.setStyle(DEFAULT_STYLE);
        exampleButton.setOnAction(e -> loadExample());

        menu.getChildren().addAll(linearButton, roundedButton, exampleButton);
        return menu;
    }

    private void loadExample() {
        if (currentForme instanceof FormesLineaireFX) {
            startImage = new Image("file:./src/projet/img/carre.png", 600, 600, true, true);
            endImage = new Image("file:./src/projet/img/triangle.png", 600, 600, true, true);

            pointsDeControle.getPointsMap().clear();
            pointsDeControle.ajouter(new Point(88.0, 97), new Point(301, 100));
            pointsDeControle.ajouter(new Point(497, 97), new Point(301, 100));
            pointsDeControle.ajouter(new Point(499, 492), new Point(509, 474));
            pointsDeControle.ajouter(new Point(85, 490), new Point(93, 474));
        } else if (currentForme instanceof FormesArrondiesFX) {
            startImage = new Image("file:./src/projet/img/coeur.png", 600, 600, true, true);
            endImage = new Image("file:./src/projet/img/croissant.png", 600, 600, true, true);

            pointsDeControle.getPointsMap().clear();
            pointsDeControle.ajouter(new Point(298.0, 204.0), new Point(394.0, 32.0));
            pointsDeControle.ajouter(new Point(402.0, 8.0), new Point(311.0, 111.0));
            pointsDeControle.ajouter(new Point(583.0, 154.0), new Point(284.0, 170.0));
            pointsDeControle.ajouter(new Point(508.0, 296.0), new Point(277.0, 267.0));
            pointsDeControle.ajouter(new Point(478.0, 368.0), new Point(273.0, 339.0));
            pointsDeControle.ajouter(new Point(407.0, 437.0), new Point(290.0, 444.0));
            pointsDeControle.ajouter(new Point(299.0, 510.0), new Point(396.0, 540.0));
            pointsDeControle.ajouter(new Point(166.0, 434.0), new Point(181.0, 539.0));
            pointsDeControle.ajouter(new Point(40.0, 297.0), new Point(124.0, 288.0));
            pointsDeControle.ajouter(new Point(75.0, 181.0), new Point(179.0, 182.0));
            pointsDeControle.ajouter(new Point(93.0, 116.0), new Point(199.0, 130.0));
            pointsDeControle.ajouter(new Point(223.0, 52.0), new Point(272.0, 30.0));
            pointsDeControle.ajouter(new Point(298.1, 204.0), new Point(394.0, 32.0));
        }

        startImageView.setImage(startImage);
        endImageView.setImage(endImage);

        currentForme.redrawPoints();
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
                + "Cliquez sur la 1ère image pour ajouter un nouveau point de contrôle là où vous le souhaitez, puis sur la seconde pour son second emplacement."
                + "Cliquez sur Valider en suivant, après avoir précisé le nombre de frames souhaité pour le GIF.");

        startImageView = createImageView();
        endImageView = createImageView();

        StackPane paneA = imgDansPane(startImageView, true);
        StackPane paneB = imgDansPane(endImageView, false);

        HBox imageBox = new HBox(20, paneA, paneB);
        imageBox.setAlignment(Pos.CENTER);

        Button selectStartImageButton = createImageButton("Select Image A");
        Button selectEndImageButton = createImageButton("Select Image B");

        HBox buttonBox1 = new HBox(10, selectStartImageButton, selectEndImageButton);
        buttonBox1.setAlignment(Pos.CENTER);

        Button resetButton = new Button("Réinitialiser");
        resetButton.setStyle(DEFAULT_STYLE);
        resetButton.setOnAction(e -> currentForme.resetPoints());

        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle(DEFAULT_STYLE);
        deleteButton.setOnAction(e -> currentForme.showDeletePointDialog());

        Button pipetteButton = new Button("Pipette");
        pipetteButton.setStyle(DEFAULT_STYLE);
        pipetteButton.setOnAction(e -> {
            isPipetteMode = true;
            canvasA.setCursor(Cursor.CROSSHAIR);
            canvasB.setCursor(Cursor.CROSSHAIR);
        });

        TextField framesTextField = new TextField();
        framesTextField.setPromptText("Frames (5-144)");
        framesTextField.setMaxWidth(120);
        framesTextField.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        TextField durationTextField = new TextField();
        durationTextField.setPromptText("Durée (s)");
        durationTextField.setMaxWidth(120);
        durationTextField.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        Label framesLabel = new Label("Nombre de frames");
        framesLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");

        Label durationLabel = new Label("Durée du GIF");
        durationLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");

        VBox textFieldBox = new VBox(5, framesLabel, framesTextField, durationLabel, durationTextField);
        textFieldBox.setAlignment(Pos.CENTER);

        Button startButton = new Button("Start");
        startButton.setStyle(SELECTED_STYLE);
        startButton.setOnAction(e -> {
            System.out.println(pointsDeControle.toString());
            int nbFrames;
            int duration;
            try {
                nbFrames = Integer.parseInt(framesTextField.getText());
                if (nbFrames < 5 || nbFrames > 144) {
                    throw new NumberFormatException();
                }
                duration = Integer.parseInt(durationTextField.getText());
                if (duration < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                System.out.println("Le nombre de frames doit être compris entre 5 et 144 et la durée doit être positive.");
                return;
            }

            if (startImage != null) {
                
                Stage loadingStage = createLoadingDialog(primaryStage);

                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        updateProgress(0, nbFrames);

                        Forme forme;
                        if (currentForme instanceof FormesLineaireFX) {
                            forme = new Forme(pointsDeControle, null, null, nbFrames);
                        } else {
                            forme = new FormeArrondie(pointsDeControle, nbFrames);
                        }
                        forme.setSelectedColor(selectedColor);

                        try {
                            forme.morphisme(SwingFXUtils.fromFXImage(startImage, null), pointsDeControle, nbFrames, duration, this::updateProgress);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }

                        Platform.runLater(() -> {
                            try {
                                GIFViewer.display("GIF Viewer", "animation.gif");
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        });

                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        loadingStage.close();
                    }

                    @Override
                    protected void failed() {
                        super.failed();
                        loadingStage.close();
                    }
                };

                ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(task.progressProperty());
        VBox vbox = new VBox(10, new Label("GIF en cours de création..."), progressBar);
        vbox.setAlignment(Pos.CENTER);  
        vbox.setPadding(new Insets(20));
        Scene loadingScene = new Scene(vbox, 300, 100);
        loadingStage.setScene(loadingScene);

        new Thread(task).start();
        loadingStage.show();
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

    private Stage createLoadingDialog(Stage primaryStage) {
        Stage loadingStage = new Stage();
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setTitle("Loading");

        ProgressBar progressBar = new ProgressBar();
        VBox vbox = new VBox(new Label("Loading..."), progressBar);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 300, 100);
        loadingStage.setScene(scene);

        return loadingStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

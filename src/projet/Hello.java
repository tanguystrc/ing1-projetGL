package src.projet;
import java.io.File;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;


public class Hello extends Application {

    private Button createImagePlaceholder() {
        Button button = new Button();
        button.setPrefSize(600, 600);
        button.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2; -fx-background-radius: 0;");
        ImageView plusIcon = new ImageView(new Image("file: data/Downloads/plus.png")); 
        plusIcon.setFitHeight(100);
        plusIcon.setFitWidth(100);
        button.setGraphic(plusIcon);
        return button;
    }

    @Override
    public void start(Stage primaryStage) {
        // Configuration des boutons d'image
        Button startImageButton = createImagePlaceholder();
        Button endImageButton = createImagePlaceholder();

        Label startLabel = new Label("Image de départ", startImageButton);
        Label endLabel = new Label("Image d'arrivée", endImageButton);
        startLabel.setContentDisplay(ContentDisplay.TOP);
        endLabel.setContentDisplay(ContentDisplay.TOP);

        // Conteneur d'images
        HBox imageBox = new HBox(20, startLabel, endLabel);
        imageBox.setAlignment(Pos.CENTER);

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
        HBox buttonBox = new HBox(10, startButton, saveSettingsButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Configuration du BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(imageBox);
        borderPane.setCenter(framesTextField);
        borderPane.setBottom(buttonBox);
        borderPane.setPadding(new Insets(20));
        borderPane.setStyle("-fx-background-color: #eeeeee;");

        // Scène et affichage
        Scene scene = new Scene(borderPane, 1350, 750);
        primaryStage.setTitle("PROJET GL");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Ajout des gestionnaires d'événements pour les boutons d'image
        FileChooser fileChooser = new FileChooser();
        startImageButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                ImageView imgView = new ImageView(new Image("file:" + file.getAbsolutePath(), 600, 600, true, true));
                startImageButton.setGraphic(imgView);
            }
        });

        endImageButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                ImageView imgView = new ImageView(new Image("file:" + file.getAbsolutePath(), 600, 600, true, true));
                endImageButton.setGraphic(imgView);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

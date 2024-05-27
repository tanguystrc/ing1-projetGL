package src.projet;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class Redimensionneur extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Redimensionner");
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);

        Button uploadButton = new Button("Ajouter image");
        uploadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                Image image = new Image(file.toURI().toString());
                gererImage(imageView, image);
            }
        });

        StackPane root = new StackPane();
        root.getChildren().addAll(imageView, uploadButton);
        StackPane.setAlignment(uploadButton, Pos.TOP_CENTER);

        Scene scene = new Scene(root, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void gererImage(ImageView imageView, Image image) {
        double largeur = image.getWidth();
        double hauteur = image.getHeight();
        double largeurV = 600;
        double hauteurV = 600;

        if (largeur > largeurV || hauteur > hauteurV) {
            Image imageRed = redimensionner(image, largeurV, hauteurV);
            imageView.setImage(imageRed);
        } else {
            imageView.setFitWidth(largeurV);
            imageView.setFitHeight(hauteurV);
            imageView.setImage(image);
        }
        if (largeur < largeurV || hauteur < hauteurV) {
            imageView.setFitWidth(largeurV);
            imageView.setFitHeight(hauteurV);
        }
    }

    private Image redimensionner(Image image, double largeurV, double hauteurV) {
        double largeur = image.getWidth();
        double hauteur = image.getHeight();

        double cropWidth = Math.min(largeur, largeurV);
        double cropHeight = Math.min(hauteur, hauteurV);

        double x = (largeur - cropWidth) / 2;
        double y = (hauteur - cropHeight) / 2;

        PixelReader reader = image.getPixelReader();
        WritableImage croppedImage = new WritableImage(reader, (int)x, (int)y, (int)cropWidth, (int)cropHeight);

        return croppedImage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

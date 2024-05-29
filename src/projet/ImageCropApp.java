package src.projet;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCropApp extends Application {

    private ImageView imageView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Image Cropper");

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);

        imageView = new ImageView();
        root.setCenter(imageView);

        Button button = new Button("Choose Image");
        button.setOnAction(e -> chooseImage(primaryStage));
        root.setBottom(button);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void chooseImage(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(selectedFile);
                BufferedImage croppedBufferedImage = cropRightHalf(bufferedImage);
                Image croppedImage = SwingFXUtils.toFXImage(croppedBufferedImage, null);
                imageView.setImage(croppedImage);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private BufferedImage cropRightHalf(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int cropWidth = width / 4;

        return image.getSubimage(2*cropWidth, 0, cropWidth, height);
    }
}

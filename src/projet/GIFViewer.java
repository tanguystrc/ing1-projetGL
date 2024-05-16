package src.projet;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GIFViewer extends Application {

    private static String gifFilePath;

    public static void display(String title, String filePath) throws IOException {
        gifFilePath = filePath;
        Platform.runLater(() -> {
            try {
                new GIFViewer().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("GIF Viewer");

        File file = new File(gifFilePath);
        Image gifImage = new Image(file.toURI().toString());
        ImageView imageView = new ImageView(gifImage);

        StackPane root = new StackPane();
        root.getChildren().add(imageView);

        Scene scene = new Scene(root, gifImage.getWidth(), gifImage.getHeight());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

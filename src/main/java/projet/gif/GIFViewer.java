package projet.gif;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Bibliothèque pour la visualisation de gif provenant de ce lien :
 * https://openimaj.org/openimaj-demos/sandbox/xref/org/openimaj/demos/sandbox/image/gif/GifSequenceWriter.html
 * Le code a été modifié en fonction de nos besoins présents.
 */
public class GIFViewer extends Application {

    private static String gifFilePath;
    private List<Image> gifFrames;
    private ImageView imageView;
    private Slider slider;
    private boolean isSliderMode = false;

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
        primaryStage.setTitle("Lecteur de GIF");
        primaryStage.setAlwaysOnTop(true);

        File file = new File(gifFilePath);
        gifFrames = extractGifFrames(file);
        if (gifFrames.isEmpty()) {
            System.out.println("No frames extracted from the GIF.");
            return;
        }

        imageView = new ImageView(new Image(file.toURI().toString()));

        slider = new Slider(0, gifFrames.size() - 1, 0);
        slider.setMajorTickUnit(1);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);
        slider.setDisable(true);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (isSliderMode) {
                imageView.setImage(gifFrames.get(newVal.intValue()));
            }
        });

        Button switchModeButton = new Button("Passez au mode slider");
        switchModeButton.setOnAction(e -> {
            isSliderMode = !isSliderMode;
            if (isSliderMode) {
                imageView.setImage(gifFrames.get((int) slider.getValue()));
                switchModeButton.setText("Passez au mode lecture");
                slider.setDisable(false);
            } else {
                imageView.setImage(new Image(file.toURI().toString()));
                switchModeButton.setText("Passez au mode slider");
                slider.setDisable(true);
            }
        });

        Button downloadButton = new Button("Télécharger");
        downloadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le GIF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GIF files (*.gif)", "*.gif"));
            File saveFile = fileChooser.showSaveDialog(primaryStage);
            if (saveFile != null) {
                try {
                    saveGif(file, saveFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        HBox controls = new HBox(10, switchModeButton, downloadButton);
        controls.setPadding(new Insets(10));
        controls.setAlignment(Pos.CENTER);

        StackPane imagePane = new StackPane(imageView);
        BorderPane root = new BorderPane();
        root.setCenter(imagePane);
        root.setBottom(slider);
        root.setTop(controls);
        BorderPane.setMargin(slider, new Insets(10));
        BorderPane.setMargin(controls, new Insets(10));

        Scene scene = new Scene(root, gifFrames.get(0).getWidth(), gifFrames.get(0).getHeight() + 100);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private List<Image> extractGifFrames(File gifFile) throws IOException {
        List<Image> frames = new ArrayList<>();
        try (ImageInputStream stream = ImageIO.createImageInputStream(gifFile)) {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                reader.setInput(stream);
                int numFrames = reader.getNumImages(true);
                for (int i = 0; i < numFrames; i++) {
                    BufferedImage frame = reader.read(i);
                    frames.add(SwingFXUtils.toFXImage(frame, null));
                }
                reader.dispose();
            }
        }
        return frames;
    }

    private void saveGif(File sourceFile, File targetFile) throws IOException {
        try (ImageInputStream stream = ImageIO.createImageInputStream(sourceFile)) {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                reader.setInput(stream);
    
                ImageOutputStream output = ImageIO.createImageOutputStream(targetFile);
                int numFrames = reader.getNumImages(true);
                int initialDelay = extractFrameDelay(reader, 0);
    
                GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, initialDelay, true);
    
                for (int i = 0; i < numFrames; i++) {
                    BufferedImage frame = reader.read(i);
                    int delay = extractFrameDelay(reader, i);
                    writer.writeToSequence(frame, delay);
                }
                writer.close();
                output.close();
            }
        }
    }
    

    private int extractFrameDelay(ImageReader reader, int frameIndex) throws IOException {
        IIOMetadata metadata = reader.getImageMetadata(frameIndex);
        String[] metaFormatNames = metadata.getMetadataFormatNames();
        for (String formatName : metaFormatNames) {
            IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(formatName);
            IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
            if (graphicsControlExtensionNode != null) {
                String delayTime = graphicsControlExtensionNode.getAttribute("delayTime");
                return Integer.parseInt(delayTime) * 10; // Convert to milliseconds
            }
        }
        return 100; // Default delay time if not specified
    }
    

    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName) == 0) {
                return ((IIOMetadataNode) rootNode.item(i));
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return node;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

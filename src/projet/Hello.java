package src.projet;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.FileChooser;


public class Hello extends Application {
	
	private static int asciiDuA = 65;
	
	private List<Point2D> pointsDeControle;
	private int nbPointsDeControle;
	
	
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
    
    // TODO (maybe) : faire en sorte que la couleur des points soit en fct de la couleur de l'image pour que ce soit VISIBLE
    // (mettre juste du rouge ça marche pas forcément tout le temps)(idk)
    private StackPane imgDansPane(ImageView i, boolean isImageA) {
    	StackPane pane = new StackPane();
    	pane.getChildren().add(i);
    	pane.setStyle("-fx-border-color: #000000; -fx-border-width: 1px;");
    	
    	// canvas des points :
    	Canvas canvas = new Canvas(600,600);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		canvas.setOnMouseClicked(mouseEvent -> {
			double mouseX = mouseEvent.getX();
	        double mouseY = mouseEvent.getY();
			pointsDeControle.add(new Point2D(mouseX, mouseY));			
	        System.out.println("Point de controle n°"+nbPointsDeControle+" : x = "+mouseX+", y = "+mouseY);
			draw(gc, mouseX, mouseY,isImageA);
		});	
		StackPane.setAlignment(canvas, javafx.geometry.Pos.TOP_LEFT); // Pour bien aligner le Canvas en haut à gauche (et superposer)
		pane.getChildren().add(canvas);
		
    	return pane;
    }
    /**
     * 
     * @param gc
     * @param mouseX : coordonnées du x du clic de la souris dans l'image
     * @param mouseY : coordonnées du Y du clic de la souris dans l'image
     */
    private void draw(GraphicsContext gc, double mouseX, double mouseY, boolean isImageA) {
		gc.setFill(Color.TRANSPARENT);
	    gc.fillRect(0, 0, 600,600);
	    gc.setStroke(Color.RED);
	    int n = nbPointsDeControle/2;
	    
	    // Dessiner le nv point
	    if (mouseX != -1 && mouseY != -1) {
	    	System.out.println("is img A : "+isImageA+"  nbPointsDeControle % 2 : "+(nbPointsDeControle % 2));
	    	if( (isImageA && nbPointsDeControle % 2 == 0) || (!isImageA && nbPointsDeControle % 2 != 0)) {
	    		if(nbPointsDeControle<26) {
		    		gc.strokeText("." + (char)(asciiDuA+n), mouseX, mouseY);
		    	}else {
		    		gc.strokeText("." + (n-26), mouseX, mouseY);
		    	}
		        pointsDeControle.add(new Point2D(mouseX, mouseY));
		        nbPointsDeControle++;
	    	}    			    	
	    }
	}
    
    // TODO : bouton pour effacer tous les points ou le dernier couple de point
    @Override
    public void start(Stage primaryStage) {
    	this.pointsDeControle = new ArrayList<Point2D>();
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
        
        
        
        
        // Champ de texte pour le nombre de frames
        TextField framesTextField = new TextField();
        framesTextField.setPromptText("Frames (5-144)");
        framesTextField.setMaxWidth(120);

        Label framesLabel = new Label("Nombre de frames");
        VBox textFieldBox = new VBox(5, framesLabel, framesTextField);
        textFieldBox.setAlignment(Pos.CENTER);
     

        // Boutons
        /* TODO : pour le gestionnaire d'event de "start" : 
         * vérif qu'on a bien les 2 images chargées, 
         * verif qu'on a pas aucun point de controle
         * verif que le nb total de point de controle soit pair (moitié dans image A, moitié dans image B quoi)
         * */
        Button startButton = new Button("Start");
        Button saveSettingsButton = new Button("Save settings");
        HBox buttonBox2 = new HBox(10, startButton, saveSettingsButton);
        buttonBox2.setAlignment(Pos.CENTER);
        
        // Configuration du BorderPane
        VBox vBox = new VBox();
        vBox.getChildren().addAll(texteInstruction, imageBox,buttonBox1,framesTextField,buttonBox2);
        vBox.setPadding(new Insets(20));
        vBox.setSpacing(15);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: #eeeeee;");

        // Scène et affichage
        Scene scene = new Scene(vBox, 1350, 800);
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
    //main method
    public static void main(String[] args) {
        launch(args);
    }
}

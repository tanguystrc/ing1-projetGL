package src.projet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
    private int nbPointsDeControleAutreGroupe;
    private List<PointDeControle> pointsDeControleLies;

    private Canvas canvasA;
    private Canvas canvasB;
    private Color selectedColor;
    private Rectangle colorDisplay;
    private Image startImage;
    private Image endImage;

    private boolean isDragging = false;
    private boolean isClickValid = true;
    private boolean isPipetteMode = false;
    private Point selectedPoint = null;

    

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
        canvas.setOnMouseReleased(mouseEvent -> handleMouseReleased(isImageA));
        canvas.setOnMouseClicked(mouseEvent -> handleMouseClicked(mouseEvent, isImageA));

        StackPane.setAlignment(canvas, Pos.TOP_LEFT); // Pour bien aligner le Canvas en haut à gauche (et superposer)
        pane.getChildren().add(canvas);

        return pane;
    }

    private void handleMousePressed(MouseEvent mouseEvent, boolean isImageA) {
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        for (PointDeControle groupe : pointsDeControleLies){
        	for (Entry<Point, Point> entry : groupe.getPointsMap().entrySet()) {
                Point point = isImageA ? entry.getKey() : entry.getValue();
                if (point.distance(new Point(mouseX, mouseY)) < 10) { // zone de 10pixels autour du point pour la selection
                    selectedPoint = point;
                    isDragging = true;
                    isClickValid = false;
                    break;
                }
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
                checkForProximityAndMerge(mouseX, mouseY, isImageA);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return;
            }
            redrawPoints();
        }
    }

    private void checkForProximityAndMerge(double mouseX, double mouseY, boolean isImageA) {
        for (Entry<Point, Point> entry : pointsDeControle.getPointsMap().entrySet()) {
            Point point = isImageA ? entry.getKey() : entry.getValue();
            if (point != selectedPoint && point.distance(new Point(mouseX, mouseY)) < 10) { // Merge points within 10 pixels
                selectedPoint.setX(point.getX());
                selectedPoint.setY(point.getY());
                return;
            }
        }
    }

    private void handleMouseReleased(boolean isImageA) {
        if (isDragging) {
            isDragging = false;
            selectedPoint = null;
            redrawPoints();
        }
    }

    private void handleMouseClicked(MouseEvent mouseEvent, boolean isImageA) {
        if (isPipetteMode) {
            pickColor(mouseEvent, isImageA);
            isPipetteMode = false; // Désactiver le mode pipette après utilisation
            canvasA.setCursor(Cursor.DEFAULT); // Reset cursor
            canvasB.setCursor(Cursor.DEFAULT); // Reset cursor
        } else if (isClickValid) {
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
                redrawPoints();
            }
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
    
    private void generateFace() {
    	resetPoints();
    	// Coordonnées :
    	LinkedList<LinkedList<Point>> listeCoord = new LinkedList<>();
    	LinkedList<Point> g1 = new LinkedList<>(Arrays.asList(new Point(200.0,299.0), new Point(267.0,308.0)));
    	LinkedList<Point> g2 = new LinkedList<>(Arrays.asList(new Point(415.0,297.0), new Point(345.0,307.0)));
    	LinkedList<Point> g3 = new LinkedList<>(Arrays.asList(new Point(181.0,262.0), new Point(201.0,252.0),new Point(259.0,263.0)));
    	LinkedList<Point> g4 = new LinkedList<>(Arrays.asList(new Point(429.0,258.0), new Point(407.0,247.0),new Point(349.0,266.0)));
    	LinkedList<Point> g5 = new LinkedList<>(Arrays.asList(new Point(272.0,406.0), new Point(305.0,320.0),new Point(337.0,404.0)));
    	LinkedList<Point> g6 = new LinkedList<>(Arrays.asList(new Point(255.0,461.0), new Point(358.0,458.0)));
    	LinkedList<Point> g7 = new LinkedList<>(Arrays.asList(new Point(158.0,220.0), new Point(197.0,139.0),new Point(422.0,139.0),new Point(449.0,220.0)));
    	LinkedList<Point> g8 = new LinkedList<>(Arrays.asList(new Point(169.0,392.0), new Point(190.0,458.0),new Point(212.0,501.0),new Point(270.0,541.0),
    														  new Point(329.0,545.0), new Point(388.0,504.0),new Point(413.0,461.0),new Point(438.0,386.0)));
    	LinkedList<Point> g9 = new LinkedList<>(Arrays.asList(new Point(198.0,502.0), new Point(194.0,561.0),new Point(163.0,591.0)));
    	LinkedList<Point> g10 = new LinkedList<>(Arrays.asList(new Point(403.0,509.0), new Point(406.0,552.0),new Point(436.0,591.0)));
    	listeCoord.addAll(Arrays.asList(g1,g2,g3,g4,g5,g6,g7,g8,g9,g10));
    	
    	for(LinkedList<Point> groupe : listeCoord) {
    		for(Point p : groupe) {
    			pointsDeControle.ajouter(p, new Point(p.getX(), p.getY()));
    		}
    		pointsDeControleLies.remove(pointsDeControle);
            pointsDeControleLies.add(new PointDeControle(pointsDeControle));
            pointsDeControle.getPointsMap().clear();
            pointsDeControleLies.add(pointsDeControle);    		
    	}    	
    	redrawPoints();    	
    }
    

    private void draw(GraphicsContext gc, double mouseX, double mouseY, boolean isImageA, int index, int numGroupe) {
        // lettre de l'alphabet au début, chiffres après
    	String pointLabel = (index < 26) ? Character.toString((char) (asciiDuA + index)) : Integer.toString(index - 26);

        gc.setStroke(isImageA ? Color.RED : Color.BLUE);
        gc.strokeText("." + pointLabel, mouseX, mouseY);  
        
        //On a un point précédant du même groupe, on le lie avec le nouveau :
        if(index > 0 && !(index == nbPointsDeControleAutreGroupe)) {      	       	
        	gc.setStroke(isImageA ? Color.BLUE : Color.RED);        	
        	gc.strokeLine(getPointFromIndex(index-1, isImageA,numGroupe).getX(),getPointFromIndex(index-1, isImageA,numGroupe).getY(),mouseX,mouseY);
        }   
        
    }


    private void redrawPoints() {
        canvasA.getGraphicsContext2D().clearRect(0, 0, 600, 600);
        canvasB.getGraphicsContext2D().clearRect(0, 0, 600, 600);

        int index;        
        int numGroupe = 0;
        
        for (PointDeControle groupe : pointsDeControleLies){
        	nbPointsDeControleAutreGroupe = 0;
        	for(int j = 0 ; j < numGroupe ; j++) {
        		nbPointsDeControleAutreGroupe += pointsDeControleLies.get(j).getPointsMap().size();
        	}
        	index = 0 + nbPointsDeControleAutreGroupe;
        	
        	for (Entry<Point, Point> entry : groupe.getPointsMap().entrySet()) {
                Point key = entry.getKey();
                Point value = entry.getValue();
                draw(canvasA.getGraphicsContext2D(), key.getX(), key.getY(), true, index,numGroupe );
                draw(canvasB.getGraphicsContext2D(), value.getX(), value.getY(), false, index,numGroupe );
                index++;                
            }   
        	numGroupe++;       	
        }        
    }

    private void resetPoints() {
        pointsDeControle.getPointsMap().clear();
        pointsDeControleLies.clear();
        pointsDeControleLies.add(pointsDeControle);
        redrawPoints();
    }

    private void showDeletePointDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Supprimer un couple de point");

        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        int index = 0;
        int nbGroupe = 0;
        
        // Affichage :
        for (PointDeControle groupe : pointsDeControleLies){
        	for (Entry<Point, Point> entry : groupe.getPointsMap().entrySet()) {
                Point key = entry.getKey();
                Point value = entry.getValue();
                String pointInfo = String.format("G%d : Points %c: A(%.1f, %.1f) - B(%.1f, %.1f)",nbGroupe,
                        (index < 26) ? (char) (asciiDuA + index) : Integer.toString(index - 26),
                        key.getX(), key.getY(), value.getX(), value.getY());
                listView.getItems().add(pointInfo);
                index++;
            }
        	nbGroupe++;
        }

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> {
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            
            if (selectedIndex != -1 && selectedIndex < calculerNbTotalPoint() ) {           	
                Point point = getPointFromIndexTotal(selectedIndex,true);
                System.out.println("Suppression de "+point+" du groupe "+calculerNumgroupe(selectedIndex));
                
                pointsDeControleLies.get(calculerNumgroupe(selectedIndex)).supprimer(point);
                redrawPoints();
                dialog.close();
            }
        });

        VBox dialogVBox = new VBox(20, listView, deleteButton);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setAlignment(Pos.CENTER);

        Scene dialogScene = new Scene(dialogVBox, 370, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private Point getPointFromIndex(int index, boolean isImageA, int numGroupe) {
        int i = 0;
        for (Entry<Point, Point> entry : pointsDeControleLies.get(numGroupe).getPointsMap().entrySet()) {
            if (i == index-nbPointsDeControleAutreGroupe) {
                return isImageA ? entry.getKey() : entry.getValue();
            }
            i++;
        }        
        return null;
    }

    private Point getPointFromIndexTotal(int index, boolean isImageA) {
        int i = 0; 
    	for (PointDeControle groupe : pointsDeControleLies){
    		for (Entry<Point, Point> entry : groupe.getPointsMap().entrySet()) {
                if (i == index) {
                    return isImageA ? entry.getKey() : entry.getValue();
                }
                i++;
            }  
        }        
        return null;
    }

    public int calculerNbTotalPoint(){
    	int res = 0;
    	for (PointDeControle groupe : pointsDeControleLies){
    		res += groupe.getPointsMap().size();       	
        }  
    	return res;
    }
    
    public int calculerNumgroupe(int indexTotal) {
    	int i = 0;
    	int res = 0;
    	for (PointDeControle groupe : pointsDeControleLies){
    		for (int j=0 ; j < groupe.getPointsMap().size() ; j++) {
    			if (indexTotal == i) {
    				return res;
    			}    			  
    			i++;
    		}
    		res++;
        }
    	return res;
    }

    
    
    @Override
    public void start(Stage primaryStage) {
        this.pointsDeControle = new PointDeControle();
        this.nbPointsDeControleAutreGroupe = 0;
        
        this.pointsDeControleLies = new LinkedList<>();    
        // il aura tjrs pointsDeControle, qui est la dernière liste
        pointsDeControleLies.add(pointsDeControle);

    
        // Rectangle montrant la couleur selectionnée
        colorDisplay = new Rectangle(30, 30, Color.TRANSPARENT);
        colorDisplay.setStroke(Color.BLACK);
    
        // Texte d'instruction :
        Text texteInstruction = new Text();
        texteInstruction.setFont(new Font(14));
        texteInstruction.setWrappingWidth(1000);
        texteInstruction.setTextAlignment(TextAlignment.JUSTIFY);
        texteInstruction.setText("Vos images doivent être de même dimension.\n"
                + "Cliquez sur la 1ère image pour creer un nouveau couple de point. Vous pouvez ensuite les déplacer à votre guise."
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
        selectStartImageButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");
    
        Button selectEndImageButton = createImageButton("Select Image B");
        selectEndImageButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");
    
        HBox buttonBox1 = new HBox(10, selectStartImageButton, selectEndImageButton);
        buttonBox1.setAlignment(Pos.CENTER);
    
        // Bouton pour réinitialiser les points
        Button resetButton = new Button("Réinitialiser");
        resetButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");
        resetButton.setOnAction(e -> resetPoints());
    
        // Bouton pour supprimer une paire de points
        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");
        deleteButton.setOnAction(e -> showDeletePointDialog());

        // Bouton pour creer un nouveau groupe de points :
        Button nvGroupePointsButton = new Button("Nouveau Groupe de point");
        nvGroupePointsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");
        nvGroupePointsButton.setOnAction(e -> {
        	if (!pointsDeControle.getPointsMap().isEmpty()) {
        		System.out.println("NOUVEAU GROUPE !");      
        		// Copie profonde du groupe actuel terminé :
        		pointsDeControleLies.remove(pointsDeControle);
                pointsDeControleLies.add(new PointDeControle(pointsDeControle));
                // Nouveau groupe :
                pointsDeControle.getPointsMap().clear();
                pointsDeControleLies.add(pointsDeControle);
                
                System.out.println(pointsDeControleLies);
                
            }
        });
        
        // Bouton pour creer automatiquement des groupes de PointsDeControle pour les visages :
        Button faceGroupPoints = new Button("Visage");
        faceGroupPoints.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");
        faceGroupPoints.setOnAction(e -> {
        	generateFace();
        });
        
        
        // Bouton pipette
        Button pipetteButton = new Button("Pipette");
        pipetteButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");
        pipetteButton.setOnAction(e -> {
            isPipetteMode = true;
            canvasA.setCursor(Cursor.CROSSHAIR); // Set cursor to pipette
            canvasB.setCursor(Cursor.CROSSHAIR); // Set cursor to pipette
        });
    
        // Champ de texte pour le nombre de frames
        TextField framesTextField = new TextField();
        framesTextField.setPromptText("Frames (5-144)");
        framesTextField.setMaxWidth(120);
        framesTextField.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
    
        Label framesLabel = new Label("Nombre de frames");
        framesLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");
        VBox textFieldBox = new VBox(5, framesLabel, framesTextField);
        textFieldBox.setAlignment(Pos.CENTER);
    
        // Boutons
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
                    formeLineaire.setSelectedColor(selectedColor); // Appliquer la couleur sélectionnée
                    formeLineaire.morphismeSimple(bufferedImage, pointsDeControle, nbFrames);
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
    
    
        HBox buttonBox2 = new HBox(10, startButton, resetButton, deleteButton, nvGroupePointsButton, faceGroupPoints, pipetteButton, colorDisplay);
        buttonBox2.setAlignment(Pos.CENTER);
    
        // Configuration du BorderPane
        VBox vBox = new VBox();
        vBox.getChildren().addAll(texteInstruction, imageBox, buttonBox1, textFieldBox, buttonBox2);
        vBox.setPadding(new Insets(20));
        vBox.setSpacing(15);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: #ecf0f1;");
    
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
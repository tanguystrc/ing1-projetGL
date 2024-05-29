package src.projet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
import javafx.stage.FileChooser.ExtensionFilter;

import src.projet.fx.FormesArrondiesFX;
import src.projet.fx.FormesFX;
import src.projet.fx.FormesLineaireFX;
import src.projet.fx.PhotoFX;
import src.projet.gif.GIFViewer;
import src.projet.traitement.Forme;
import src.projet.traitement.FormeArrondie;
import src.projet.traitement.Visage;
import src.projet.traitement.Couple;
import src.projet.traitement.Point;
import src.projet.traitement.PointDeControle;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Cursor;
import javafx.scene.shape.Rectangle;

public class Hello extends Application {

    // CSS :
    private static final String DEFAULT_STYLE = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #2980b9; -fx-border-width: 1px; -fx-cursor: hand;";
    private static final String SELECTIONNE_STYLE = "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #27ae60; -fx-border-width: 1px; -fx-cursor: hand;";
    private static final String STYLE_JAUNE = "-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);";
    private static final String STYLE_ZONE_TEXTE = "-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-background-radius: 5px;";

    private PointDeControle pointsDeControle;
    private List<PointDeControle> pointsDeControleLies;
    private FormesFX formeActuelle;

    private Canvas zonePointsA;
    private Canvas zonePointsB;
    private boolean leClicEstValide = true;
    private boolean enModePipette = false;
    private Color couleurSelectionne;
    private Rectangle rectangleCouleur;

    private Image imageDebut;
    private Image imageFin;
    private ImageView imageViewDebut;
    private ImageView imageViewFin;

    private File ajouteGif;
    private boolean avant;
    
    private Button boutonFormeLi;
    private Button boutonFormeArr;
    private Button boutonPhoto;
    private Button nvGroupePointsButton;
    private Button boutonPointsVisage;
    
    /**
     * Créé l'ImageView de l'interface
     * @return ImageView (A ou B)
     */
    private ImageView creerImageView() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(600);
        imageView.setFitHeight(600);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    /**
     * Crée les boutons permetatnt de choisir les images A et B
     * @param label
     * @return
     */
    private Button creerBoutonChoixImage(String label) {
        Button button = new Button(label);
        button.setStyle(DEFAULT_STYLE);
        button.setPrefSize(200, 50);
        return button;
    }

    /**
     * Crée le StackPane contenant l'ImageView avec son canvas (zone où il y a les points) superposé par dessus
     * @param i : ImageView A ou B
     * @param estImageA : vrai si c'est l'ImageView A
     * @return StackPane
     */
    private StackPane imgDansPane(ImageView i, boolean estImageA) {
        StackPane pane = new StackPane();
        pane.getChildren().add(i);
        pane.setStyle("-fx-border-color: #000000; -fx-border-width: 1px;");
        
        Canvas canvas = new Canvas(600, 600);// Zone qui contiendra les dessins des points, traits etc.
        if (estImageA) {
            zonePointsA = canvas;
        } else {
            zonePointsB = canvas;
        }

        canvas.setOnMousePressed(mouseEvent -> sourisAppuyee(mouseEvent, estImageA));
        canvas.setOnMouseDragged(mouseEvent -> sourisGlissée(mouseEvent, estImageA));
        canvas.setOnMouseReleased(mouseEvent -> sourisRelachee(estImageA));
        canvas.setOnMouseClicked(mouseEvent -> sourisCliquee(mouseEvent, estImageA));

        StackPane.setAlignment(canvas, Pos.TOP_LEFT); // Pour bien superposer
        pane.getChildren().add(canvas);

        return pane;
    }

    /**
     * Gestion de l'evenement clic de la souris enfoncé
     * @param mouseEvent : permet  d'avoir les coordonnées du curseur
     * @param estImageA : vrai si le clic est sur l'image A
     */
    private void sourisAppuyee(MouseEvent mouseEvent, boolean estImageA) {
        if (formeActuelle != null) {         
            formeActuelle.sourisAppuyee(mouseEvent, estImageA);
        }
    }

    /**
     * Gestion de l'evenement on fait un déplacement avec la souris (=déplacer un point)
     * @param mouseEvent : permet  d'avoir les coordonnées du curseur
     * @param estImageA : vrai si le clic est sur l'image A
     */
    private void sourisGlissée(MouseEvent mouseEvent, boolean estImageA) {
        if (formeActuelle != null) {
            formeActuelle.sourisGlissée(mouseEvent, estImageA);
        }
    }

    /**
     * Gestion de l'evenement si la souris est relachée
     * @param estImageA : vrai si le clic est sur l'image A
     */
    private void sourisRelachee(boolean estImageA) {
        if (formeActuelle != null) {
            formeActuelle.sourisRelachee(estImageA);
        }
    }

    /**
     * Gestion de l'evenement si souris simplement cliquée
     * @param mouseEvent : permet  d'avoir les coordonnées du clic
     * @param estImageA : vrai si le clic est sur l'image A
     */
    private void sourisCliquee(MouseEvent mouseEvent, boolean estImageA) {
        if (enModePipette) {
            pipette(mouseEvent, estImageA);
            enModePipette = false;
            zonePointsA.setCursor(Cursor.DEFAULT);
            zonePointsB.setCursor(Cursor.DEFAULT);
        } else if (leClicEstValide && formeActuelle != null) {
            formeActuelle.sourisCliquee(mouseEvent, estImageA);
        }
        leClicEstValide = true;
    }

    /**
     * Mode pipette permettant de récuperer la couleur du pixel cliqué
     * @param mouseEvent
     * @param estImageA
     */
    private void pipette(MouseEvent mouseEvent, boolean estImageA) {
        Image image = estImageA ? imageDebut : imageFin;
        if (image != null) {
            int x = (int) mouseEvent.getX();
            int y = (int) mouseEvent.getY();
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            java.awt.Color color = new java.awt.Color(bufferedImage.getRGB(x, y));
            couleurSelectionne = Color.rgb(color.getRed(), color.getGreen(), color.getBlue());
            rectangleCouleur.setFill(couleurSelectionne);
            System.out.println("Couleur : " + couleurSelectionne);
        }
    }

    /**
     * Réinitialise les styles CSS des boutons du Menu de gauche
     * @param boutonSelectionne
     */
    private void reinitialiserStyleBoutons(Button boutonSelectionne) {
        boutonFormeLi.setStyle(DEFAULT_STYLE);
        boutonFormeArr.setStyle(DEFAULT_STYLE);
        boutonPhoto.setStyle(DEFAULT_STYLE);
        boutonSelectionne.setStyle(SELECTIONNE_STYLE);
    }

    /**
     * Créé le menu de gauche contenant les différents choix des modes + boutons exemple
     * @return Vbox contenant les boutons du menu
     */
    private VBox creerMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #2c3e50; -fx-padding: 20px;");

        boutonFormeLi = new Button("Formes Linéaires");
        boutonFormeLi.setStyle(DEFAULT_STYLE);
        boutonFormeLi.setOnAction(e -> {
            boutonPointsVisage.setVisible(false);
            nvGroupePointsButton.setVisible(false);
            formeActuelle = new FormesLineaireFX(zonePointsA, zonePointsB, pointsDeControle);
            formeActuelle.reinitialiserPoints();
            reinitialiserStyleBoutons(boutonFormeLi);
        });

        boutonFormeArr = new Button("Formes Arrondies");
        boutonFormeArr.setStyle(DEFAULT_STYLE);
        boutonFormeArr.setOnAction(e -> {
            boutonPointsVisage.setVisible(false);
            nvGroupePointsButton.setVisible(false);
            formeActuelle = new FormesArrondiesFX(zonePointsA, zonePointsB, pointsDeControle);
            formeActuelle.reinitialiserPoints();
            reinitialiserStyleBoutons(boutonFormeArr);
        });

        boutonPhoto = new Button("Photo");
        boutonPhoto.setStyle(DEFAULT_STYLE);
        boutonPhoto.setOnAction(e -> {
            boutonPointsVisage.setVisible(true);
            nvGroupePointsButton.setVisible(true);
            formeActuelle = new PhotoFX(zonePointsA, zonePointsB, pointsDeControle, pointsDeControleLies);
            formeActuelle.reinitialiserPoints();
            reinitialiserStyleBoutons(boutonPhoto);
        });

        Button boutonExemple = new Button("Exemple");
        boutonExemple.setStyle(STYLE_JAUNE);
        boutonExemple.setOnAction(e -> chargerExemple());

        menu.getChildren().addAll(boutonFormeLi, boutonFormeArr, boutonPhoto, boutonExemple);
        return menu;
    }

    /**
     * Charge le mode exemple : fournit des images début et fin du dossier img et leur points de controle correspondants
     */
    private void chargerExemple() {
        if (formeActuelle instanceof FormesLineaireFX) {
            imageDebut = rognerImage(new Image("file:./src/projet/img/carre.png", 600, 600, true, true));
            imageFin = rognerImage(new Image("file:./src/projet/img/triangle.png", 600, 600, true, true));

            pointsDeControle.getPointsList().clear();
            pointsDeControle.ajouter(new Point(90, 100), new Point(319, 108));
            pointsDeControle.ajouter(new Point(510, 100), new Point(319, 108));
            pointsDeControle.ajouter(new Point(510, 490), new Point(537, 470));
            pointsDeControle.ajouter(new Point(90, 490), new Point(103, 470));
        } else if (formeActuelle instanceof FormesArrondiesFX) {
            imageDebut = rognerImage(new Image("file:./src/projet/img/coeur.png", 600, 600, true, true));
            imageFin = rognerImage(new Image("file:./src/projet/img/croissant.png", 600, 600, true, true));

            pointsDeControle.getPointsList().clear();
            pointsDeControle.ajouter(new Point(319.0, 205.0), new Point(432.0, 31.0));
            pointsDeControle.ajouter(new Point(400.0, 49.0), new Point(343.0, 112.0));
            pointsDeControle.ajouter(new Point(574.0, 113.0), new Point(311.0, 174.0));
            pointsDeControle.ajouter(new Point(564.0, 227.0), new Point(305.0, 268.0));
            pointsDeControle.ajouter(new Point(562.0, 350.0), new Point(306.0, 336.0));
            pointsDeControle.ajouter(new Point(405.0, 451.0), new Point(313.0, 445));
            pointsDeControle.ajouter(new Point(322.0, 509.0), new Point(431.0, 538.0));
            pointsDeControle.ajouter(new Point(175.0, 425.0), new Point(195.0, 521.0));
            pointsDeControle.ajouter(new Point(49.0, 292.0), new Point(147.0, 307.0));
            pointsDeControle.ajouter(new Point(78.0, 191.0), new Point(190.0, 199.0));
            pointsDeControle.ajouter(new Point(95.0, 115.0), new Point(216.0, 138.0));
            pointsDeControle.ajouter(new Point(240.0, 55.0), new Point(285.0, 38.0));
            pointsDeControle.ajouter(new Point(319.1, 205.0), new Point(432.0, 31.0));
        } else if (formeActuelle instanceof PhotoFX) {            
            imageDebut = rognerImage(new Image("file:./src/projet/img/visage1.png", 600, 600, true, true));
            imageFin = rognerImage(new Image("file:./src/projet/img/visage2.png", 600, 600, true, true));            
            pointsDeControle.getPointsList().clear();
            genererPointsVisage(true);
        }

        imageViewDebut.setImage(imageDebut);
        imageViewFin.setImage(imageFin);

        formeActuelle.redessinerPoints();
    }

    /**
     * Affiche les points pré-placés formant un visage pour plus de rapidité
     * @param deuxTypes : boolean, true si c'est pour le bouton exemple (donc les points de l'image B sont differents), false sinon
     */
    private void genererPointsVisage(boolean deuxTypes) {
        formeActuelle.reinitialiserPoints();
        // Coordonnées :
        LinkedList<LinkedList<Couple<Point, Point>>> listeCoord = new LinkedList<>();
        LinkedList<Couple<Point, Point>> g1 = new LinkedList<>(Arrays.asList(
            new Couple<>(new Point(200.0, 299.0), new Point(209.0, 252.0)),
            new Couple<>(new Point(267.0, 308.0), new Point(268.0, 263.0))
        ));
        LinkedList<Couple<Point, Point>> g2 = new LinkedList<>(Arrays.asList(
            new Couple<>(new Point(415.0, 297.0), new Point(392.0, 251.0)),
            new Couple<>(new Point(345.0, 307.0), new Point(336.0, 264.0))
        ));
        LinkedList<Couple<Point, Point>> g3 = new LinkedList<>(Arrays.asList(
            new Couple<>(new Point(181.0, 262.0), new Point(192.0, 218.0)),
            new Couple<>(new Point(201.0, 252.0), new Point(216.0, 199.0)),
            new Couple<>(new Point(259.0, 263.0), new Point(277.0, 219.0))
        ));
        LinkedList<Couple<Point, Point>> g4 = new LinkedList<>(Arrays.asList(
            new Couple<>(new Point(429.0, 258.0), new Point(415.0, 210.0)),
            new Couple<>(new Point(407.0, 247.0), new Point(395.0, 193.0)),
            new Couple<>(new Point(349.0, 266.0), new Point(336.0, 211.0))
        ));
        LinkedList<Couple<Point, Point>> g5 = new LinkedList<>(Arrays.asList(
            new Couple<>(new Point(272.0, 406.0), new Point(271.0, 336.0)),
            new Couple<>(new Point(305.0, 320.0), new Point(302.0, 268.0)),
            new Couple<>(new Point(337.0, 404.0), new Point(339.0, 334.0))
        ));
        LinkedList<Couple<Point, Point>> g6 = new LinkedList<>(Arrays.asList(
            new Couple<>(new Point(255.0, 461.0), new Point(261.0, 406.0)),
            new Couple<>(new Point(358.0, 458.0), new Point(349.0, 405.0))
        ));
        LinkedList<Couple<Point, Point>> g7 = new LinkedList<>(Arrays.asList(
            new Couple<>(new Point(158.0, 220.0), new Point(180.0, 184.0)),
            new Couple<>(new Point(197.0, 139.0), new Point(208.0, 122.0)),
            new Couple<>(new Point(422.0, 139.0), new Point(397.0, 122.0)),
            new Couple<>(new Point(449.0, 220.0), new Point(429.0, 176.0))
        ));
        LinkedList<Couple<Point, Point>> g8 = new LinkedList<>(Arrays.asList(
            new Couple<>(new Point(169.0, 392.0), new Point(188.0, 361.0)),
            new Couple<>(new Point(190.0, 458.0), new Point(197.0, 411.0)),
            new Couple<>(new Point(212.0, 501.0), new Point(231.0, 451.0)),
            new Couple<>(new Point(270.0, 541.0), new Point(289.0, 493.0)),
            new Couple<>(new Point(329.0, 545.0), new Point(328.0, 492.0)),
            new Couple<>(new Point(388.0, 504.0), new Point(378.0, 455.0)),
            new Couple<>(new Point(413.0, 461.0), new Point(413.0, 414.0)),
            new Couple<>(new Point(438.0, 386.0), new Point(423.0, 360.0))
        ));
        LinkedList<Couple<Point, Point>> g9 = new LinkedList<>(Arrays.asList(
            new Couple<>(new Point(198.0, 502.0), new Point(224.0, 461.0)),
            new Couple<>(new Point(194.0, 561.0), new Point(208.0, 536.0)),
            new Couple<>(new Point(163.0, 591.0), new Point(139.0, 578.0))
        ));
        LinkedList<Couple<Point, Point>> g10 = new LinkedList<>(Arrays.asList(
            new Couple<>(new Point(403.0, 509.0), new Point(384.0, 467.0)),
            new Couple<>(new Point(406.0, 552.0), new Point(388.0, 515.0)),
            new Couple<>(new Point(436.0, 591.0), new Point(445.0, 566.0))
        ));
        listeCoord.addAll(Arrays.asList(g1, g2, g3, g4, g5, g6, g7, g8, g9, g10));
        
        if (deuxTypes) {
            for (LinkedList<Couple<Point, Point>> groupe : listeCoord) {
                for (Couple<Point, Point> p : groupe) {
                    pointsDeControle.ajouter(p.getA(), p.getB());
                }
                pointsDeControleLies.remove(pointsDeControle);
                pointsDeControleLies.add(new PointDeControle(pointsDeControle));
                pointsDeControle.getPointsList().clear();
                pointsDeControleLies.add(pointsDeControle);          
            }    
        } else {
            for (LinkedList<Couple<Point, Point>> groupe : listeCoord) {
                for (Couple<Point, Point> p : groupe) {
                    pointsDeControle.ajouter(p.getA(), p.getA());
                }
                pointsDeControleLies.remove(pointsDeControle);
                pointsDeControleLies.add(new PointDeControle(pointsDeControle));
                pointsDeControle.getPointsList().clear();
                pointsDeControleLies.add(pointsDeControle);          
            }    
        }
            
        formeActuelle.redessinerPoints();     
    }

    /**
     * Start
     * @param Stage principal
     */
    @Override
    public void start(Stage primaryStage) {
        /* - - Initialisation de l'interface - - */
        this.pointsDeControle = new PointDeControle();        
        this.pointsDeControleLies = new LinkedList<>();         
        this.pointsDeControleLies.add(pointsDeControle); // il aura tjrs pointsDeControle dans pointsDeControleLies, qui est la *dernière* liste
        
        rectangleCouleur = new Rectangle(30, 30, Color.TRANSPARENT);
        rectangleCouleur.setStroke(Color.BLACK);

        Text texteInstruction = new Text();
        texteInstruction.setFont(new Font(14));
        texteInstruction.setWrappingWidth(1200);
        texteInstruction.setTextAlignment(TextAlignment.JUSTIFY);
        texteInstruction.setText("Privilégiez les images en carré étant données qu'elles seront redimenssionées en 600x600.\n"
                + "Cliquez sur la zone de gauche pour creer un point sur les deux images, que vous pourrez ensuite déplacer à votre guise. "
                + "Si ce n'est déjà fait, merci de ne pas oublier de préciser à l'aide de la pipette la couleur correspondant à votre forme unie. "
                + "Cliquez sur Valider en suivant, après avoir précisé le nombre de frames souhaité pour le GIF - il s'affichera dès la fin de son traitement.");

        imageViewDebut = creerImageView();
        imageViewFin = creerImageView();

        StackPane paneA = imgDansPane(imageViewDebut, true);
        StackPane paneB = imgDansPane(imageViewFin, false);

        HBox imageBox = new HBox(20, paneA, paneB);
        imageBox.setAlignment(Pos.CENTER);

        Button boutonChoisirImageDebut = creerBoutonChoixImage("Select Image A");
        Button boutonChoisirImageFin = creerBoutonChoixImage("Select Image B");

        HBox zoneBouton1 = new HBox(10, boutonChoisirImageDebut, boutonChoisirImageFin);
        zoneBouton1.setAlignment(Pos.CENTER);

        Button boutonReinitialiser = new Button("Réinitialiser");
        boutonReinitialiser.setStyle(DEFAULT_STYLE);
        boutonReinitialiser.setOnAction(e -> formeActuelle.reinitialiserPoints());

        Button boutonSupprimer = new Button("Supprimer");
        boutonSupprimer.setStyle(DEFAULT_STYLE);
        boutonSupprimer.setOnAction(e -> formeActuelle.fenetreSuppressionPoints());

        // Bouton pour creer un nouveau groupe de points :
        nvGroupePointsButton = new Button("Nouveau Groupe de point");
        nvGroupePointsButton.setStyle(STYLE_JAUNE);
        nvGroupePointsButton.setOnAction(e -> {
            if (!pointsDeControle.getPointsList().isEmpty()) {
                System.out.println("NOUVEAU GROUPE !");
                // Copie profonde du groupe actuel terminé :
                pointsDeControleLies.remove(pointsDeControle);
                pointsDeControleLies.add(new PointDeControle(pointsDeControle));
                // Nouveau groupe :
                pointsDeControle.getPointsList().clear();
                pointsDeControleLies.add(pointsDeControle);  
                formeActuelle.redessinerPoints();              
            }
        });
        nvGroupePointsButton.setVisible(false);

        // Bouton pour creer automatiquement des groupes de PointsDeControle pour les visages :
        boutonPointsVisage = new Button("Visage");
        boutonPointsVisage.setStyle(STYLE_JAUNE);
        boutonPointsVisage.setOnAction(e -> {
            genererPointsVisage(false);
        });
        boutonPointsVisage.setVisible(false);

        Button pipetteButton = new Button("Pipette");
        pipetteButton.setStyle(DEFAULT_STYLE);
        pipetteButton.setOnAction(e -> {
            enModePipette = true;
            zonePointsA.setCursor(Cursor.CROSSHAIR);
            zonePointsB.setCursor(Cursor.CROSSHAIR);
        });

        TextField zoneTexteFrames = new TextField();
        zoneTexteFrames.setPromptText("Frames (5-144)");
        zoneTexteFrames.setMaxWidth(120);
        zoneTexteFrames.setStyle(STYLE_ZONE_TEXTE);

        TextField zoneTexteDuree = new TextField();
        zoneTexteDuree.setPromptText("Durée (s)");
        zoneTexteDuree.setMaxWidth(120);
        zoneTexteDuree.setStyle(STYLE_ZONE_TEXTE);

        Label framesLabel = new Label("Nombre de frames");
        framesLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");

        Label dureeLabel = new Label("Durée du GIF");
        dureeLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");
        
        VBox zoneTexte = new VBox(5, framesLabel, zoneTexteFrames, dureeLabel, zoneTexteDuree);
        zoneTexte.setAlignment(Pos.CENTER);

        Button boutonAjouterGif = new Button("Ajouter un GIF à celui-ci");
        boutonAjouterGif.setStyle(DEFAULT_STYLE);        
        ToggleGroup groupeRb = new ToggleGroup();
        RadioButton rb1 = new RadioButton("Avant");
        rb1.setToggleGroup(groupeRb);
        rb1.setSelected(true);
        RadioButton rb2 = new RadioButton("Après");
        rb2.setToggleGroup(groupeRb);
        rb2.setSelected(false);
        Label nomFichierLabel = new Label("GIF : aucune fichier selectionné.");
        nomFichierLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
        HBox zoneAjouterGif = new HBox(15, boutonAjouterGif,rb1,rb2,nomFichierLabel);
        zoneAjouterGif.setAlignment(Pos.CENTER);

        /* - - - START - - - */
        Button boutonStart = new Button("Start");
        boutonStart.setStyle(SELECTIONNE_STYLE);
        boutonStart.setOnAction(e -> {
            int nbFrames;
            int duree;
            try {
                nbFrames = Integer.parseInt(zoneTexteFrames.getText());
                if (nbFrames < 5 || nbFrames > 144) {
                    throw new NumberFormatException();
                }
                duree = Integer.parseInt(zoneTexteDuree.getText());
                if (duree < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                Text t = new Text();        
                t.setTextAlignment(TextAlignment.JUSTIFY);
                t.setText("Le nombre de frames doit être compris entre 5 et 144\net la durée doit être positive.\nVos valeurs doivent être entière.");
                VBox tmpvbox = new VBox();
                tmpvbox.setAlignment(Pos.CENTER);  
                tmpvbox.setPadding(new Insets(20));
                tmpvbox.getChildren().add(t);
                Scene ErrorScene = new Scene(tmpvbox, 450, 100);
                Stage ErrorStage = new Stage();
                ErrorStage.setTitle("Erreur");
                ErrorStage.setResizable(false);
                ErrorStage.setScene(ErrorScene);
                ErrorStage.show();
                return;
            }

            if (imageDebut != null) {
                System.out.println(pointsDeControleLies);
                Stage stageChargement = creerFenetreChargement(primaryStage);
                avant = rb1.isSelected();
                
                // On peut commencer ; traitement des images, affichage du gif, affichage de la progression :
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        updateProgress(0, nbFrames);
                        
                        if (formeActuelle instanceof PhotoFX) {
                            // Cas mode Photo :
                            Visage visage; 
                            System.out.println("Traitement d'une photo");
                            visage = new Visage(SwingFXUtils.fromFXImage(imageDebut, null),SwingFXUtils.fromFXImage(imageFin, null),pointsDeControleLies,nbFrames,ajouteGif,avant);
                            try {
                                visage.morph(duree, this::updateProgress);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        } else if (formeActuelle instanceof FormesArrondiesFX) {
                            // Cas mode forme unie et arrondie :
                            FormeArrondie forme;
                            System.out.println("Traitement d'une forme unie arrondie");
                            forme = new FormeArrondie(pointsDeControle, nbFrames,ajouteGif,avant);
                            forme.setSelectedColor(couleurSelectionne);
                            try {
                                forme.morphisme(SwingFXUtils.fromFXImage(imageDebut, null), pointsDeControle, nbFrames, duree, this::updateProgress);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        } else {
                            // Cas mode forme unie et linéaire :
                            Forme forme;
                            System.out.println("Traitement d'une forme unie linéaire");
                            forme = new Forme(pointsDeControle, null, null, nbFrames,ajouteGif,avant);
                            forme.setSelectedColor(couleurSelectionne);
                            try {
                                forme.morphisme(SwingFXUtils.fromFXImage(imageDebut, null), pointsDeControle, nbFrames, duree, this::updateProgress);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }

                        // Pour afficher le gif lorsque le traitement est terminé :
                        Platform.runLater(() -> {
                            try {
                                GIFViewer.display("GIF Viewer", "animation.gif");
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        });

                        return null;
                    }

                    // Fenetre de chargement :
                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        stageChargement.close();
                    }
                    @Override
                    protected void failed() {
                        super.failed();
                        stageChargement.close();
                    }
                };

                // Pour la barre de progression en temps réel :
                ProgressBar barreProgression = new ProgressBar();
                barreProgression.progressProperty().bind(task.progressProperty());
                VBox vbox = new VBox(10, new Label("GIF en cours de création..."), barreProgression);
                vbox.setAlignment(Pos.CENTER);  
                vbox.setPadding(new Insets(20));
                Scene loadingScene = new Scene(vbox, 300, 100);
                stageChargement.setScene(loadingScene);

                new Thread(task).start();
                stageChargement.show();
            }
        });

        HBox buttonBox2 = new HBox(10, boutonStart, boutonReinitialiser, boutonSupprimer, pipetteButton, rectangleCouleur, nvGroupePointsButton, boutonPointsVisage);
        buttonBox2.setAlignment(Pos.CENTER);

        VBox menu = creerMenu();
        VBox mainContent = new VBox();
        mainContent.getChildren().addAll(texteInstruction, imageBox, zoneBouton1, zoneTexte, zoneAjouterGif, buttonBox2);
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

        /* Ajouts des fichiers correspondant à l'image de début, de fin, et potentiellement un GIF : */
        FileChooser fileChooserIMG = new FileChooser();
        fileChooserIMG.getExtensionFilters().addAll(
            new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        boutonChoisirImageDebut.setOnAction(e -> {
            File file = fileChooserIMG.showOpenDialog(primaryStage);
            if (file != null) {
                // Redimension et recadrage de l'image pour l'avoir en 600x600 :
                imageDebut = rognerImage(new Image("file:" + file.getAbsolutePath()));
                imageViewDebut.setImage(imageDebut);
            }
        });
        boutonChoisirImageFin.setOnAction(e -> {
            File file = fileChooserIMG.showOpenDialog(primaryStage);
            if (file != null) {
                 // Redimension et recadrage de l'image pour l'avoir en 600x600 :
                 imageFin = rognerImage(new Image("file:" + file.getAbsolutePath()));
                 imageViewFin.setImage(imageFin);
            }
        });
        FileChooser fileChooserGIF = new FileChooser();
        fileChooserGIF.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("GIF Files", "*.gif")
        );
        boutonAjouterGif.setOnAction(e -> {
            ajouteGif = fileChooserGIF.showOpenDialog(primaryStage);
            if (ajouteGif != null) {
                nomFichierLabel.setText("GIF : " + ajouteGif.getName());
            }
        });
    }

    /**
     * Crée la fenêtre affichant une barre de chargement lors du traitement des images
     * @return Stage correspondant
     */
    private Stage creerFenetreChargement(Stage primaryStage) {
        Stage stageChargement = new Stage();
        stageChargement.initModality(Modality.APPLICATION_MODAL);
        stageChargement.setTitle("Chargement");

        ProgressBar barreProgression = new ProgressBar();
        VBox vbox = new VBox(new Label("Chargement..."), barreProgression);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 300, 100);
        stageChargement.setScene(scene);

        return stageChargement;
    }
          
    /**
     * Permet de redimenssionner et rogner une image
     * @param image à rogner
     * @return l'image rognée
     */
    public static Image rognerImage(Image image) {
        double ratio = Math.max(600 / image.getHeight(), 600 / image.getWidth());
        Image imageRes = new Image(image.getUrl(),(int) image.getWidth() * ratio,(int) image.getHeight() * ratio, true, true);

        double width = imageRes.getWidth();
        double height = imageRes.getHeight();
        double targetSize = 600;

        if (width > targetSize && height <= targetSize) {
            double cropAmount = (width - targetSize) / 2;
            Rectangle2D viewport = new Rectangle2D(cropAmount, 0, width - 2*cropAmount, height);
            return rognerImage2(imageRes, viewport);
        } else if (height > targetSize && width <= targetSize) {            
            double cropAmount = (height - targetSize) / 2;
            Rectangle2D viewport = new Rectangle2D(0, cropAmount, width, height - 2*cropAmount);
            return rognerImage2(imageRes, viewport);
        }
        else{
            return imageRes;
        }
    }

    /**
     * Permet de rogner une image
     * @param image
     * @param viewport
     * @return
     */
    private static Image rognerImage2(Image image, Rectangle2D viewport) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        int x = (int) viewport.getMinX();
        int y = (int) viewport.getMinY();
        int w = (int) viewport.getWidth();
        int h = (int) viewport.getHeight();
        BufferedImage imageARogner = bufferedImage.getSubimage(x, y, w, h);
        return (SwingFXUtils.toFXImage(imageARogner, null));
    }


    public static void main(String[] args) {
        launch(args);
    }
}

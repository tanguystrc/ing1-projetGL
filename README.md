
# PROJET MORPHING ING 1 GI

## Authors

- AURE Nolhan
- BOURILLON Gwenn
- LAKOMICKI Laura
- LEBRIN-VENTAJA Mahault
- SOUTRIC Tanguy

## Description du projet

Ce projet de morphing d'images permet de créer des animations de transition entre deux images en utilisant différentes techniques de transformation. Les utilisateurs peuvent choisir parmi plusieurs formes de transformations, telles que des formes linéaires, arrondies, ou des transformations basées sur des photos.

## Instructions pour lancer l'application

### Option 1 : Utiliser une JDK existante
#### Prérequis

- Une JDK doit être présente sur votre appareil (**passez directement à l'étape 3**)
- Placez vous dans le dossier dézipé

1. **Installer une JDK sur Linux et Windows :**

   **Sous Linux :**

   - Ouvrez un terminal.
   - Mettez à jour votre liste de paquets :
     ```sh
     sudo apt update
     ```
   - Installez la JDK (par exemple, OpenJDK 19) :
     ```sh
     sudo apt install openjdk-19-jdk
     ```
   - Vérifiez l'installation :
     ```sh
     java -version
     ```

   **Sous Windows :**

   - Téléchargez la JDK depuis le site officiel d'Oracle ou adoptium.net.
   - Exécutez l'installateur et suivez les instructions à l'écran.
   - Ajoutez le chemin de la JDK à la variable d'environnement PATH :
     1. Ouvrez le Panneau de configuration.
     2. Allez dans Système et sécurité > Système > Paramètres système avancés.
     3. Cliquez sur Variables d'environnement.
     4. Dans la section Variables système, trouvez la variable PATH, et cliquez sur Modifier.
     5. Ajoutez le chemin du répertoire `bin` de la JDK (par exemple, `C:\Program Files\Java\jdk-17in`).
   - Vérifiez l'installation en ouvrant une invite de commandes et en tapant :
     ```sh
     java -version
     ```

2. **Dézipper le dossier JavaFX SDK :**

   Assurez-vous que le dossier `lib` contenant `javafx-sdk-22-linux` ou `javafx-sdk-22-windows` est dézippé dans votre répertoire de travail.

3. **Compiler et exécuter l'application :**

   **Pour Linux :**
   ```sh
   javac --module-path lib/javafx-sdk-22-linux/lib --add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.media,javafx.swing,javafx.web -d out $(find src -name "*.java")
   java --module-path lib/javafx-sdk-22-linux/lib --add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.media,javafx.swing,javafx.web -cp out projet.Hello
   ```

   **Pour Windows :**
   ```sh
   dir /s /b src\*.java > sources.txt

   javac --module-path lib\javafx-sdk-22-windows\lib --add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.media,javafx.swing,javafx.web -d out @sources.txt

   java --module-path lib\javafx-sdk-22-windows\lib --add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.media,javafx.swing,javafx.web -cp out projet.Hello
   ```

### Option 2 : Créer une Custom JRE avec Maven et JLink (LINUX SEULEMENT)

#### Prérequis

- Maven doit être installé sur votre machine. ```sudo apt install maven ```

- Placez vous dans le dossier dézipé

1. **Copier les dépendances :**
   ```sh
   mvn dependency:copy-dependencies
   ```

2. **Générer le classpath :**
   ```sh
   mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt
   ```

3. **Créer une Custom JRE :**
   ```sh
   jlink --module-path $JAVA_HOME/jmods:$(cat classpath.txt) --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.media,javafx.swing,javafx.web --output custom-jre
   ```

4. **Lancer l'application :**
   ```sh
   custom-jre/bin/java -jar target/javafx-project-1.0-SNAPSHOT.jar
   ```

## Comment utiliser l'application
 - **Si vous n'avez pas vos propres images, cliquez sur l'une des 3 parties puis sur le bouton "Exemple", ensuite remplissez le nombre de frames voulues ainsi que la durée du gif et lancez avec "Start"**
### Formes linéaires

Après avoir importé vos images et placé les points principaux sur la première, ajusté les points formés en miroir sur la seconde puis ajoutez le nombre d'images désirées ainsi que la durée du gif.
Sélectionnez la couleur de votre forme avec l'outil pipette puis appuyez sur Start pour commencer la création.

### Formes arrondies

La marche à suivre est presque identique à celle pour les formes linéaires hormis le fait que vous devez ajuster la courbe formée par chaque segment à l'aide des deux points supplémentaires.
Petite subtilité, un segment se forme maintenant en 4 clic au lieu de deux.

### Transformations de photo

Après avoir importé vos images et sélectionner le mode Photo de l'application, détourez la première image en cliquant pour former et déplacer les points caractéristiques qui seront reliés par des segments. 
Pour former un nouveau groupe de point, appuyez sur le bouton Nouveau Groupe De Point. Ajustez ensuite vos points sur la seconde image. Sélectionnez le nombre d'images et la durée du gif puis appuyez sur start.

### GifViewer
Une fois la barre de progression terminée, la fenetre d'affichage du gif apparaît qui est par défaut en mode lecture , vous pouvez passez en mode slider pour voir en détail chaque image ou téléchargez le gif pour pouvoir faire des enchaînements par la suite

## Arborescence du projet

**Voici une description de l'arborescence du projet, en expliquant le rôle de chaque répertoire et fichier important :**


- `src/` : Contient toutes les classes réparties en packages. Le dossier `main/java` contient les fichiers source de l'application principale et ses modules.
- `lib/` : Contient les fichiers JAR de JavaFX nécessaires pour l'exécution de l'application.
- `docs/` : Contient la documentation Javadoc. Ouvrez `index.html` pour accéder à la documentation générée.
- `out/` : Contient tous les fichiers `.class` générés après compilation. Le sous-dossier `production` contient les classes compilées de l'application principale.
- `target/` : Contient les fichiers compilés et les artefacts générés par Maven selon le fichier `pom.xml`.
- `pom.xml` : Fichier de configuration de Maven qui spécifie les dépendances du projet, les plugins utilisés et les instructions de build.

Assurez-vous de bien configurer votre environnement de développement pour inclure les bibliothèques JavaFX présentes dans le répertoire `lib` pour exécuter l'application correctement.
## Sources

- Bibliothèque pour le traitement des GIF : [OpenIMAJ GifSequenceWriter](https://openimaj.org/openimaj-demos/sandbox/xref/org/openimaj/demos/sandbox/image/gif/GifSequenceWriter.html)
- Article pour le morphing avec ligne de champs d’influence : [Morphing with Field Line Influence](https://www.cs.princeton.edu/courses/archive/fall00/cs426/papers/beier92.pdf)


## Notes

- Assurez-vous que `JAVA_HOME` est défini dans votre environnement.
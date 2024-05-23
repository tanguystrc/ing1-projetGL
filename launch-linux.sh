#!/bin/bash

# Crée le répertoire .vscode s'il n'existe pas
mkdir -p .vscode

# Écrit le contenu dans settings.json
cat <<EOL > .vscode/settings.json
{
    "java.project.referencedLibraries": [
        "lib/**/*.jar",
        "./lib/javafx-sdk-22-linux/**/*.jar"
    ]
}
EOL

# Écrit le contenu dans launch.json
cat <<EOL > .vscode/launch.json
{
    // Use IntelliSense to learn about possible attributes.
    // Hover to view descriptions of existing attributes.
    // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Hello",
            "request": "launch",
            "mainClass": "src.projet.Hello",
            "vmArgs": "--module-path \"./lib/javafx-sdk-22-linux/lib\" --add-modules javafx.controls,javafx.fxml"
        }
    ]
}
EOL

echo "Les fichiers .vscode/settings.json et .vscode/launch.json ont été créés et remplis."

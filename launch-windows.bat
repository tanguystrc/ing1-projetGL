@echo off
setlocal

REM Crée le répertoire .vscode s'il n'existe pas
if not exist ".vscode" (
    mkdir .vscode
)

REM Écrit le contenu dans settings.json
(
echo {
echo     "java.project.referencedLibraries": [
echo         "lib/**/*.jar",
echo         "./lib/javafx-sdk-22-winodws/**/*.jar"
echo     ]
echo }
) > .vscode\settings.json

REM Écrit le contenu dans launch.json
(
echo {
echo     // Use IntelliSense to learn about possible attributes.
echo     // Hover to view descriptions of existing attributes.
echo     // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
echo     "version": "0.2.0",
echo     "configurations": [
echo         {
echo             "type": "java",
echo             "name": "Hello",
echo             "request": "launch",
echo             "mainClass": "src.projet.Hello",
echo             "projectName": "ing1-projetGL_3d0b975c",
echo             "vmArgs": "--module-path \"./lib/javafx-sdk-22-winodws/lib\" --add-modules javafx.controls,javafx.fxml"
echo         }
echo     ]
echo }
) > .vscode\launch.json

echo Les fichiers .vscode/settings.json et .vscode/launch.json ont été créés et remplis.

endlocal

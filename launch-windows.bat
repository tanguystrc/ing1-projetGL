@echo off
set JAVA_HOME=%~dp0\lib\javafx-sdk-22-windows
set PATH=%JAVA_HOME%\bin;%PATH%
start "" "%ProgramFiles%\Java\jdk-22\bin\java.exe" --module-path "%JAVA_HOME%\lib" --add-modules javafx.controls,javafx.fxml -cp "%~dp0\src" src.projet.Hello

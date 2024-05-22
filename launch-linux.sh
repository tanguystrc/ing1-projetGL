#!/bin/bash
SCRIPT_DIR=$(dirname "$0")
export JAVA_HOME=$SCRIPT_DIR/lib/javafx-sdk-22-linux
export PATH=$JAVA_HOME/bin:$PATH
java --module-path "$JAVA_HOME/lib" --add-modules javafx.controls,javafx.fxml -cp "$SCRIPT_DIR/src" src.projet.Hello

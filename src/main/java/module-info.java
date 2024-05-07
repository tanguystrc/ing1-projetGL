module ing1gl {
    requires javafx.controls;
    requires javafx.fxml;

    opens ing1gl to javafx.fxml;
    exports ing1gl;
}

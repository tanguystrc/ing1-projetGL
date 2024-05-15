package src.projet;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;

/**
 * Classe permettant d'afficher un carré de la couleur correspondante dans le ComboBox
 * au lieu de simplement avoir le code
 * @author cytech
 *
 */
public class ColorCell extends javafx.scene.control.ListCell<Color> {
    @Override
    protected void updateItem(Color color, boolean empty) {
        super.updateItem(color, empty);

        if (empty || color == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(color.toString());
            Rectangle rect = new Rectangle(15, 15);
            rect.setFill(color);
            setGraphic(rect);
        }
    }
}

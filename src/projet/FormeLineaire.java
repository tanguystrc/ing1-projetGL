package src.projet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.util.List;
import java.awt.Color;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Arrays;

public class FormeLineaire extends Forme {
    private Color[][] matrix1;
    private Color[][] matrix2;
    private javafx.scene.paint.Color selectedColor;

    public FormeLineaire(PointDeControle pointsDeControle, int nbFrame, Color[][] matrix1, Color[][] matrix2) {
        super(pointsDeControle, null, null, nbFrame);
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
    }

    public void setSelectedColor(javafx.scene.paint.Color selectedColor) {
        this.selectedColor = selectedColor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FormeLineaire other = (FormeLineaire) obj;
        return Arrays.deepEquals(matrix1, other.matrix1) && Arrays.deepEquals(matrix2, other.matrix2);
    }
}

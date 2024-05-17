package src.projet;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CubicBezierInteractive extends Application {

    private List<Double> controlPointsX = new ArrayList<>();
    private List<Double> controlPointsY = new ArrayList<>();
    private boolean dragging = false;

    @Override
    public void start(Stage primaryStage) {
        int width = 600; 
        int height = 600;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        canvas.setOnMousePressed(event -> {
            controlPointsX.add(event.getX());
            controlPointsY.add(event.getY());
            dragging = true;
        });

        canvas.setOnMouseDragged(event -> {
            if (dragging) {
                controlPointsX.set(controlPointsX.size() - 1, event.getX());
                controlPointsY.set(controlPointsY.size() - 1, event.getY());


                gc.clearRect(0, 0, width, height);
                drawCubicBezierCurve(gc);
            }
        });

        canvas.setOnMouseReleased(event -> {
            dragging = false;
        });


        StackPane root = new StackPane();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root, width, height);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Interactive Cubic Bézier Curve");
        primaryStage.show();
    }

    private void drawCubicBezierCurve(GraphicsContext gc) {
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);


        int n = controlPointsX.size() - 1;
        if (n >= 1) {
            for (int i = 0; i <= n - 3; i += 3) {
                double x0 = controlPointsX.get(i);
                double y0 = controlPointsY.get(i);
                double x1 = controlPointsX.get(i + 1);
                double y1 = controlPointsY.get(i + 1);
                double x2 = controlPointsX.get(i + 2);
                double y2 = controlPointsY.get(i + 2);
                double x3 = controlPointsX.get(i + 3);
                double y3 = controlPointsY.get(i + 3);

                for (double t = 0; t <= 1; t += 0.01) {
                    double x = cubicBezierPoint(x0, x1, x2, x3, t);
                    double y = cubicBezierPoint(y0, y1, y2, y3, t);
                    double nextX = cubicBezierPoint(x0, x1, x2, x3, t + 0.01);
                    double nextY = cubicBezierPoint(y0, y1, y2, y3, t + 0.01);
                    gc.strokeLine(x, y, nextX, nextY);
                }
            }
        }

 
        gc.setFill(Color.RED);
        for (int i = 0; i < controlPointsX.size(); i++) {
            gc.fillOval(controlPointsX.get(i) - 5, controlPointsY.get(i) - 5, 10, 10);
        }
    }

    private double cubicBezierPoint(double p0, double p1, double p2, double p3, double t) {
        return Math.pow(1 - t, 3) * p0 + 3 * Math.pow(1 - t, 2) * t * p1 + 3 * (1 - t) * Math.pow(t, 2) * p2 + Math.pow(t, 3) * p3;
    }

    public static void main(String[] args) {
        launch(args);
    }
}





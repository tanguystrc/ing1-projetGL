package src.projet;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class BSplineInteractive extends Application {

    private List<Double> controlPointsX = new ArrayList<>();
    private List<Double> controlPointsY = new ArrayList<>();
    private boolean dragging = false;

    @Override
    public void start(Stage primaryStage) {
        int width = 600; // Width of the canvas
        int height = 600; // Height of the canvas

        // Create a canvas to draw on
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();


        // Add mouse event handlers
        canvas.setOnMousePressed(event -> {
            controlPointsX.add(event.getX());
            controlPointsY.add(event.getY());
            dragging = true;
        });

        canvas.setOnMouseDragged(event -> {
            if (dragging) {
                controlPointsX.set(controlPointsX.size() - 1, event.getX());
                controlPointsY.set(controlPointsY.size() - 1, event.getY());

                // Redraw the curve
                gc.clearRect(0, 0, width, height);
                drawBSpline(gc);
            }
        });

        canvas.setOnMouseReleased(event -> {
            dragging = false;
        });

        // Create a stack pane to hold the canvas
        StackPane root = new StackPane();
        root.getChildren().add(canvas);

        // Set up the scene
        Scene scene = new Scene(root, width, height);

        // Set up the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Interactive B-Spline Curve");
        primaryStage.show();
    }

    // Method to draw the B-Spline curve
    private void drawBSpline(GraphicsContext gc) {
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);

        // Draw the curve using the control points
        int n = controlPointsX.size() - 1;
        if (n >= 3) {
            for (int i = 0; i <= n - 3; i++) {
                for (double t = 0; t <= 1; t += 0.01) {
                    double x = calculateBSplinePoint(i, t, controlPointsX);
                    double y = calculateBSplinePoint(i, t, controlPointsY);
                    double nextX = calculateBSplinePoint(i, t + 0.01, controlPointsX);
                    double nextY = calculateBSplinePoint(i, t + 0.01, controlPointsY);
                    gc.strokeLine(x, y, nextX, nextY);
                }
            }
        }

        // Draw control points
        gc.setFill(Color.RED);
        for (int i = 0; i < controlPointsX.size(); i++) {
            gc.fillOval(controlPointsX.get(i) - 5, controlPointsY.get(i) - 5, 10, 10);
        }
    }

    // Calculate a point on the B-Spline curve segment using the De Boor's algorithm
    private double calculateBSplinePoint(int i, double t, List<Double> points) {
        double p0 = points.get(i);
        double p1 = points.get(i + 1);
        double p2 = points.get(i + 2);
        double p3 = points.get(i + 3);

        double b0 = (1 - t) * (1 - t) * (1 - t) / 6;
        double b1 = (3 * t * t * t - 6 * t * t + 4) / 6;
        double b2 = (-3 * t * t * t + 3 * t * t + 3 * t + 1) / 6;
        double b3 = t * t * t / 6;

        return b0 * p0 + b1 * p1 + b2 * p2 + b3 * p3;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

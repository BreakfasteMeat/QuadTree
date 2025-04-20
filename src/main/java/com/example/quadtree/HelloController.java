package com.example.quadtree;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class HelloController {
    public AnchorPane anchor_pane;

    @FXML
    private Canvas canvas;
    @FXML
    private Label info_label;

    private AnimationTimer timer;
    private QuadTree quadTree;
    private List<GameObject> gameObjects = new ArrayList<>();
    private List<RangeCircle> rangeCircles = new ArrayList<>();

    public void initialize() {
        javafx.application.Platform.runLater(() -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();
            QuadRectangle boundary = new QuadRectangle(0, 0, width, height);
            quadTree = new QuadTree(boundary, 1);
            System.out.println("Boundary: " + boundary);

            timer = new AnimationTimer() {
                private long lastTime = 0;
                private int frameCount = 0;
                private long lastReportTime = 0;

                @Override
                public void handle(long now) {
                    if (lastTime == 0) {
                        lastTime = now;
                        lastReportTime = now;
                    }

                    frameCount++;
                    if (now - lastReportTime >= 1_000_000_000L) {
                        info_label.setText("FPS: " + frameCount + "\nNumber of Entities: " + gameObjects.size());
                        frameCount = 0;
                        lastReportTime = now;
                    }

                    quadTree.clear();
                    for (GameObject gameObject : gameObjects) {
                        quadTree.insert(gameObject);
                    }

                    quadCheckCollisions();
                    checkInRanges();
                    movePoints();
                    draw();
                    clearCollisions();

                }
            };
            timer.start();
        });
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawTree(gc, quadTree);
        drawRanges(gc);
    }

    private void drawTree(GraphicsContext gc, QuadTree node) {
        if (node == null) return;

        QuadRectangle boundary = node.bounds;
        gc.setStroke(Color.LIGHTGRAY);
        gc.strokeRect(boundary.x, boundary.y, boundary.w, boundary.h);

        for (GameObject obj : node.objects) {
            if (obj.collided) {
                gc.setFill(Color.RED);
            } else {
                gc.setFill(Color.DARKGRAY);
            }
            gc.fillOval(obj.x - obj.radius, obj.y - obj.radius, obj.radius * 2, obj.radius * 2);
        }

        if (node.divided) {
            drawTree(gc, node.northeast);
            drawTree(gc, node.northwest);
            drawTree(gc, node.southeast);
            drawTree(gc, node.southwest);
        }
    }

    private void drawRanges(GraphicsContext gc) {
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(1);
        for (RangeCircle rangeCircle : rangeCircles) {
            gc.strokeOval(rangeCircle.x - rangeCircle.radius, rangeCircle.y - rangeCircle.radius,
                    rangeCircle.radius * 2, rangeCircle.radius * 2);
        }
    }

    private void movePoints() {
        for (GameObject point : gameObjects) {
            point.move();
            if(point.x >= quadTree.bounds.w || point.x <= 0) {
                point.invert_x_velocity();
            }
            if(point.y >= quadTree.bounds.h || point.y <= 0) {
                point.invert_y_velocity();
            }
        }
    }

    private void checkInRanges() {
        List<GameObject> points = new ArrayList<>();
        for (RangeCircle rangeCircle : rangeCircles) {
            points.addAll(quadTree.query(rangeCircle));
        }
        for (GameObject gameObject : points) {
            gameObject.collided = true;
        }
    }

    private void clearCollisions() {
        for (GameObject gameObject : gameObjects) {
            gameObject.collided = false;
        }
    }

    public void quadCheckCollisions() {
        for (GameObject obj : gameObjects) {
            RangeCircle circle = new RangeCircle(obj.x, obj.y, obj.radius + 1);
            List<GameObject> collision = quadTree.query(circle);
            for (GameObject gameObject : collision) {
                if (obj != gameObject) obj.collidingWith(gameObject);
            }
        }
    }

    public void onAddPoint(ActionEvent actionEvent) {
        for (int i = 0; i < 100; i++) {
            double x = Math.random() * quadTree.bounds.w;
            double y = Math.random() * quadTree.bounds.h;
            GameObject object = new GameObject(x, y, Math.random() * 4 - 2, Math.random() * 4 - 2,5);
            gameObjects.add(object);
        }
    }

    public void onAddRange(ActionEvent actionEvent) {
        RangeCircle c = new RangeCircle(Math.random() * quadTree.bounds.w,
                Math.random() * quadTree.bounds.h, Math.random() * 100);
        rangeCircles.add(c);
    }
}

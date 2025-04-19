package com.example.quadtree;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class HelloController {
    public AnchorPane anchor_pane;
    public Group group_pane;

    @FXML
    private Label info_label;
    private AnimationTimer timer;

    private QuadTree quadTree;
    private List<GameObject> gameObjects = new ArrayList<>();
    private List<RangeCircle> rangeCircles = new ArrayList<>();


    public void initialize() {
        javafx.application.Platform.runLater(() -> {
            double width = anchor_pane.getWidth();
            double height = anchor_pane.getHeight();
            QuadRectangle boundary = new QuadRectangle(0, 0, width, height);
            quadTree = new QuadTree(boundary,1);
            System.out.println("Boundary: " + boundary);
            // Start render loop
            timer = new AnimationTimer() {
                private long lastTime = 0;
                private int frameCount = 0;
                private long lastReportTime = 0;
                @Override
                public void handle(long now) {
                    // FPS tracking
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
                    for(GameObject gameObject : gameObjects) {
                        quadTree.insert(gameObject);
                    }


                    //checkCollisions();
                    quadCheckCollisions();
                    checkInRanges();
//                    drawTree(quadTree);
//                    drawRanges();
                    movePoints();
                    clearCollisions();
                }
            };
            timer.start();
        });

    }

    private void checkInRanges() {
        List<GameObject> points = new ArrayList<>();
        for(RangeCircle rangeCircle : rangeCircles) {
             points.addAll(quadTree.query(rangeCircle));
        }
        for(GameObject gameObject : points) {
            gameObject.collided = true;
        }

    }

    private void clearCollisions() {
        for(GameObject gameObject : gameObjects) {
            gameObject.collided = false;
        }
    }

    private void drawRanges(){
        for(RangeCircle rangeCircle : rangeCircles) {
            Circle c = new Circle(rangeCircle.x,rangeCircle.y, rangeCircle.radius, Color.TRANSPARENT);
            c.setStroke(Color.GREEN);
            group_pane.getChildren().add(c);
        }
    }

    private void movePoints() {
        double maxDelta = 2.0; // Max pixels to move in one direction

        for (GameObject point : gameObjects) {
            double dx = (Math.random() * 2 - 1) * maxDelta; // range: -maxDelta to +maxDelta
            double dy = (Math.random() * 2 - 1) * maxDelta;

            point.x += dx;
            point.y += dy;
        }
    }

    private void drawTree(QuadTree tree) {
        QuadRectangle boundary = tree.bounds;
        Rectangle rect = new Rectangle(boundary.x, boundary.y, boundary.w, boundary.h);
        rect.setStroke(Color.GRAY);
        rect.setFill(Color.TRANSPARENT);
        rect.setStrokeWidth(1);
        group_pane.getChildren().clear();
        group_pane.getChildren().add(rect);
        drawNode(tree, group_pane);
    }

    private void drawNode(QuadTree node, Group group) {
        if(node == null){
            return;
        }
//        QuadRectangle boundary = node.bounds;
//        Rectangle rect = new Rectangle(boundary.x, boundary.y, boundary.w, boundary.h);
//        rect.setStroke(Color.GRAY);
//        rect.setFill(Color.TRANSPARENT);
//        rect.setStrokeWidth(1);
//        group.getChildren().add(rect);
        for (GameObject obj : node.objects) {
            Circle dot = new Circle(obj.x, obj.y, obj.radius, Color.GRAY);
            if(obj.collided) dot.setFill(Color.RED);
            group.getChildren().add(dot);
        }
        if (node.divided) {
            drawNode(node.northeast, group);
            drawNode(node.northwest, group);
            drawNode(node.southeast, group);
            drawNode(node.southwest, group);
        }
    }

    public void checkCollisions(){
        for(GameObject obj : gameObjects){
            for(GameObject gameObject : gameObjects){
                if(obj != gameObject) obj.collidingWith(gameObject);
            }
        }
    }
    public void quadCheckCollisions(){
        for(GameObject obj : gameObjects){
            RangeCircle circle = new RangeCircle(obj.x, obj.y, obj.radius + 1);
            List<GameObject> collision = quadTree.query(circle);
            for(GameObject gameObject : collision){
                if(obj != gameObject) gameObject.collidingWith(gameObject);
            }
        }
    }




    public void onAddPoint(ActionEvent actionEvent) {
        for(int i = 0;i < 100;i++){
            double x = Math.random() * quadTree.bounds.w;
            double y = Math.random() * quadTree.bounds.h;
            GameObject object = new GameObject(x, y, 1);
            gameObjects.add(object);
        }
    }

    public void onAddRange(ActionEvent actionEvent) {
        RangeCircle c = new RangeCircle(Math.random() * quadTree.bounds.w,Math.random() * quadTree.bounds.h,Math.random() * 100);
        rangeCircles.add(c);
    }
}
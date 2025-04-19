package com.example.quadtree;

public class GameObject implements Collidable<GameObject>{
    double x;
    double y;
    double radius;
    boolean collided;

    public GameObject(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    @Override
    public boolean collidingWith(GameObject other) {
        if(this.distance(other) <= this.radius + other.radius) {
            handleCollision(other);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void handleCollision(GameObject other) {
        this.collided = true;
        other.collided = true;
    }

    public double distance(GameObject other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

}

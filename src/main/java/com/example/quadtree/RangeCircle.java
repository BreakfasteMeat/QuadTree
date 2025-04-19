package com.example.quadtree;

public class RangeCircle {
    double x,y,radius;
    public RangeCircle(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
    public boolean canContain(GameObject other) {
        return this.distance(other) <= this.radius + other.radius;
    }
    public double distance(GameObject other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}

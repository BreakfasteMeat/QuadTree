package com.example.quadtree;

public class GameObject implements Collidable<GameObject>{
    double x;
    double y;
    double mass;
    double x_velocity;
    double y_velocity;
    double radius;
    boolean collided;

    public GameObject(double x, double y, double x_velocity, double y_velocity, double radius) {
        this.x = x;
        this.y = y;
        this.x_velocity = x_velocity;
        this.y_velocity = y_velocity;
        this.radius = radius;
        collided = false;
        mass = Math.PI * radius * radius;
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

        resolveElasticCollision(other);
    }

    public void resolveElasticCollision(GameObject other) {
        // Step 1: Position correction (resolve overlap)
        Vector p1 = new Vector(x, y);
        Vector p2 = new Vector(other.x, other.y);
        Vector delta = p1.subtract(p2);

        double distance = delta.magnitude();
        double minDistance = this.radius + other.radius;

        if (distance == 0) {
            // Objects are at the same position: force a small separation
            delta = new Vector(1, 0);
            distance = 1e-6;
        }

        double overlap = minDistance - distance;

        if (overlap > 0) {
            Vector correction = delta.normalize().multiply(overlap / 2);

            // Move both objects away from each other
            this.x += correction.x;
            this.y += correction.y;
            other.x -= correction.x;
            other.y -= correction.y;
        }
        p1 = new Vector(x,y);
        p2 = new Vector(other.x,other.y);
        Vector v1 = new Vector(x_velocity, y_velocity);
        Vector v2 = new Vector(other.x_velocity, other.y_velocity);


        Vector impact = p1.subtract(p2); // Direction of collision
        Vector vDiff = v1.subtract(v2);  // Relative velocity

        double distanceSq = impact.magnitudeSquared();
        if (distanceSq == 0) {
            System.out.println("WARNING: Distance squared is zero — skipping collision");
            return;
        }

        double dot = vDiff.dot(impact);
        if (dot >= 0) {
            // They're moving apart
            return;
        }

        double scalar = (2 * other.mass / (mass + other.mass)) * (dot / distanceSq);

        Vector deltaV = impact.multiply(scalar);
        if (Double.isNaN(deltaV.x) || Double.isNaN(deltaV.y)) {
            System.out.println("NaN detected! scalar: " + scalar + ", impact: " + impact + ", dot: " + dot + ", distSq: " + distanceSq);
            return;
        }

        x_velocity -= deltaV.x;
        y_velocity -= deltaV.y;
        other.x_velocity += deltaV.x;
        other.y_velocity += deltaV.y;
    }




    public double distance(GameObject other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public void move() {
        x = x + x_velocity;
        y = y + y_velocity;
    }
    public void invert_x_velocity(){
        x_velocity = -x_velocity;
    }
    public void invert_y_velocity(){
        y_velocity = -y_velocity;
    }
}

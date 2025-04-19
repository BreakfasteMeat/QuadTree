package com.example.quadtree;

public interface Collidable<T> {
    public boolean collidingWith(T other);
    public void handleCollision(T other);
}

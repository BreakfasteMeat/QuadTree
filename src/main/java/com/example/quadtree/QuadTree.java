package com.example.quadtree;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    int capacity;
    List<GameObject> objects;
    QuadRectangle bounds;
    QuadTree northwest;
    QuadTree southwest;
    QuadTree northeast;
    QuadTree southeast;
    boolean divided;

    public QuadTree(QuadRectangle bounds, int capacity) {
        objects = new ArrayList<GameObject>();
        this.capacity = capacity;
        this.bounds = bounds;
        northwest = null;
        southwest = null;
        northeast = null;
        southeast = null;
    }

    public void subdivide() {

        QuadRectangle nw = new QuadRectangle(bounds.x, bounds.y, bounds.w/2, bounds.h/2);

        QuadRectangle sw = new QuadRectangle(bounds.x,bounds.y + bounds.h/2, bounds.w/2, bounds.h/2);

        QuadRectangle ne = new QuadRectangle(bounds.x + bounds.w/2, bounds.y, bounds.w/2, bounds.h/2);

        QuadRectangle se = new QuadRectangle(bounds.x + bounds.w/2, bounds.y + bounds.h/2, bounds.w/2, bounds.h/2);

        northwest = new QuadTree(nw, capacity);
        southwest = new QuadTree(sw, capacity);
        northeast = new QuadTree(ne, capacity);
        southeast = new QuadTree(se, capacity);
        for (GameObject obj : objects) {
            insertIntoQuads(obj);
        }
        divided = true;

    }

    private void insertIntoQuads(GameObject obj) {
        if (northwest.bounds.canContain(obj)) {
            northwest.insert(obj);
        } else if (northeast.bounds.canContain(obj)) {
            northeast.insert(obj);
        } else if (southwest.bounds.canContain(obj)) {
            southwest.insert(obj);
        } else if (southeast.bounds.canContain(obj)) {
            southeast.insert(obj);
        }
    }

    public void insert(GameObject object) {
        if(!bounds.canContain(object)) {
            return;
        }
        if(objects.size() < capacity && !divided) {
            objects.add(object);
        } else {
            if(!divided) {
                subdivide();
            }
            insertIntoQuads(object);
        }
    }

    public List<GameObject> query(RangeCircle c){
        List<GameObject> found = new ArrayList<>();
        if(!bounds.inRange(c)){
            return found;
        } else {
            for(GameObject object : objects) {
                if(c.canContain(object)) {
                    found.add(object);
                }
            }
            if(divided) {
                found.addAll(northeast.query(c));
                found.addAll(southeast.query(c));
                found.addAll(northwest.query(c));
                found.addAll(southwest.query(c));
            }

            return found;
        }
    }

    public void clear() {
        if (northwest != null) northwest.clear();
        if (northeast != null) northeast.clear();
        if (southwest != null) southwest.clear();
        if (southeast != null) southeast.clear();

        northwest = null;
        northeast = null;
        southwest = null;
        southeast = null;
        divided = false;

        objects.clear();
    }

}

package fr.insalyon.mxyns.collinsa.physics.collisions;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

/**
 * Axis Aligned Bounding Box, un rectangle avec ses côtés horizontaux / verticaux qui englobe une entité pour la détection des collisions en broad-phase
 */
public class AABB {

    public float x;
    public float y;
    public float w;
    public float h;

    public AABB(Entity entity) {

        this.x = entity.getPos().x;
        this.y = entity.getPos().y;
    }

    public AABB(float x, float y, float w, float h) {

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public float getX() {

        return x;
    }

    public void setX(float x) {

        this.x = x;
    }

    public float getY() {

        return y;
    }

    public void setY(float y) {

        this.y = y;
    }

    public float getWidth() {

        return w;
    }

    public void setWidth(float w) {

        this.w = w;
    }

    public float getHeight() {

        return h;
    }
    public void setHeight(float h) {

        this.h = h;
    }

    public boolean intersects(AABB aabb) {

        return x < aabb.x + aabb.w &&
               x + w > aabb.x &&
               y < aabb.y + aabb.h &&
               h + y > aabb.y;
    }
}

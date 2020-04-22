package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Graphics2D;

/**
 * Entitée circulaire, la plus simple possible, permet de modéliser un liquide ou une particule par exemple
 */
public class Circle extends Entity {

    /**
     * Rayon du cercle en mètres
     */
    private float r;

    /**
     * Crée un cercle à partir d'un vecteur position et d'un rayon
     * @param pos vecteur position du centre du cercle
     * @param r rayon du cercle
     */
    public Circle(Vec2f pos, float r) {

        super(pos);
        this.r = r;

        getInertia().update(this);
        updateAABB();
    }

     /**
     * Crée un cercle à partir des coordonnées (x, y) du centre du cercle et d'un rayon
     * @param x coordonnée x du centre du cercle
     * @param y coordonnée y du centre du cercle
     * @param r rayon du cercle
     */
    public Circle(double x, double y, float r) {

        super(x, y);

        this.r = r;

        getInertia().update(this);
        updateAABB();
    }

    @Override
    public void render(Renderer renderer, Graphics2D g) {

        renderer.renderCircle(this, g);
    }

    @Override
    public float computeJ() {

        return 0.5f * getInertia().getMass() * this.r * this.r;
    }

    @Override
    public float getVolume() {

        return (float) (Math.PI * this.r * this.r * 1);
    }

    @Override
    public double getMaximumSize() {

        return r;
    }

    @Override
    public void updateAABB() {

        this.aabb.x = pos.x - r;
        this.aabb.y = pos.y - r;
        this.aabb.w = 2 * r;
        this.aabb.h = this.aabb.w;
    }

    @Override
    public short cardinal() {

        return 0;
    }

    public float getR() {

        return r;
    }

    public void setR(float r) {

        this.r = r;
        this.getInertia().update(this);
    }

    public String toString() {

        return "Circle["+pos+", "+r+"]";
    }
}

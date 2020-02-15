package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2;

import java.awt.Graphics2D;

/**
 * Entitée circulaire, la plus simple possible, permet de modéliser un liquide ou une particule par exemple
 */
public class Circle extends Entity {

    public double r;

    public Circle(Vec2 pos, double r) {

        super(pos);
        this.r = r;

        updateAABB();
    }

    public Circle(double x, double y, double r) {

        super(x, y);
        this.r = r;

        updateAABB();
    }

    public String toString() {

        return "Circle["+pos+", "+r+"]";
    }

    @Override
    public void render(Renderer renderer, Graphics2D g) {

        renderer.renderCircle(this, g);
    }

    @Override
    public double getMaximumSize() {

        return r;
    }

    @Override
    public void updateAABB() {

        this.aabb.x = pos.x - r;
        this.aabb.y = pos.y - r;
        this.aabb.width = 2 * r;

        //noinspection SuspiciousNameCombination, permet d'éviter de faire 2 fois le calcul
        this.aabb.height = this.aabb.width;
    }
}

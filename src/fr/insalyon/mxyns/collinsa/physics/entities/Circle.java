package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * Entitée circulaire, la plus simple possible, permet de modéliser un liquide ou une particule par exemple
 */
public class Circle extends Entity {

    public double r;

    public Circle(Vec2 pos, double r) {

        super(pos);
        this.r = r;
    }

    public Circle(double x, double y, double r) {

        super(x, y);
        this.r = r;
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
    public Rectangle2D.Double getAABB() {

        return new Rectangle.Double(pos.x - r, pos.y - r, r, r);
    }
}

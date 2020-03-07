package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Graphics2D;

/**
 * Polygone concave à décomposer en polygones convexes pour analyse
 */
public class ConcavePoly extends Polygon {

    public ConcavePoly(Vec2f pos) {

        super(pos);
    }

    public ConcavePoly(double x, double y) {

        super(x, y);
    }

    @Override
    public void render(Renderer renderer, Graphics2D g) {

    }

    @Override
    public double getMaximumSize() {

        return 0;
    }

    @Override
    public void updateAABB() {

    }

}

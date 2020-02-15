package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2;

import java.awt.Graphics2D;

/**
 * Polygone convexe simple, points ordonnés dans le sens de l'angle (avec l'axe Ox) croissant (trigo)
 */
public class ConvexPoly extends Polygon {

    public ConvexPoly(Vec2 pos) {

        super(pos);
    }

    public ConvexPoly(double x, double y) {

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

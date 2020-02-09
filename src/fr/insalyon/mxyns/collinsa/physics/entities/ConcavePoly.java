package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.utils.geo.Vec2;

/**
 * Polygone concave à décomposer en polygones convexes pour analyse
 */
public class ConcavePoly extends Polygon {

    public ConcavePoly(Vec2 pos) {

        super(pos);
    }

    public ConcavePoly(double x, double y) {

        super(x, y);
    }
}

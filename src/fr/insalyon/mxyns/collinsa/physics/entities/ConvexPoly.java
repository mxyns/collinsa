package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

/**
 * Polygone convexe simple, points ordonnés dans le sens de l'angle (avec l'axe Ox) croissant (trigo)
 */
public class ConvexPoly extends Polygon {

    public ConvexPoly(Vec2f pos, int n) {

        super(pos, n);
    }
    public ConvexPoly(Vec2f pos, int n, float r) {

        super(pos, n, r);
    }
    public ConvexPoly(Vec2f pos, Vec2f... vertices) {

        super(pos, vertices);
    }
}

package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

/**
 * Polygone convexe simple, points ordonnés dans le sens de l'angle (avec l'axe Ox) décroissant (horaire)
 */
public class ConvexPoly extends Polygon {

    /**
     * Constructeur pour Polygone convexe régulier vide
     * @param pos position du polygone
     * @param n nombre de sommets (donc de côtés)
     */
    public ConvexPoly(Vec2f pos, int n) {

        super(pos, n);
    }

    /**
     * Constructeur pour Polygone convexe régulier avec un circumcircle-radius
     * @param pos position du polygone
     * @param n nombre de points
     * @param r circumcircle-radius
     */
    public ConvexPoly(Vec2f pos, int n, float r) {

        this(pos, n);

        local_vertices = new Vec2f[n];

        float a = 360f / n;
        for (int i = 0; i < n; ++i)
            local_vertices[i] = new Vec2f(r, 0).rotate((float) Math.toRadians(-a * i));

        updateVertices();
        getInertia().update();
    }

    /**
     * Constructeur pour polygone convexe irrégulier.
     * Les points sont à donner dans le sens horaire. Et doivent donner un polygone convexe.
     *
     * @param pos vecteur position
     * @param vertices position des points par rapport au centre du polygone
     */
    public ConvexPoly(Vec2f pos, Vec2f... vertices) {

        super(pos, vertices);
    }
}

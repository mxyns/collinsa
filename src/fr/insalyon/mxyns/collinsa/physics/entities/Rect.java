package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Geometry;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Graphics2D;

/**
 * Entitée rectangulaire, simplifie les détections de collisions pour les objets simples
 */
public class Rect extends ConvexPoly {

    /**
     * Vec2f contenant la taille du Rect : (width, height)
     */
    private Vec2f size;

    /**
     * Crée un rectangle à partir d'un vecteur position (centre), et un vecteur taille
     * @param pos Vecteur position
     * @param size Vecteur taille: (largeur ; hauteur)
     */
    public Rect(Vec2f pos, Vec2f size) {

        super(pos, 4);
        this.size = size;
        this.local_vertices = Geometry.getRectangeLocalCorners(this);

        getInertia().update();
        updateVertices();
    }

    /**
     * Crée un rectangle à partir des coordonnées du centre et de la taille
     * @param x position x du coin
     * @param y position y du coin
     * @param w largeur
     * @param h hauteur
     */
    public Rect(double x, double y, double w, double h) {

        this(new Vec2d(x, y).toFloat(), new Vec2f((float)w, (float)h));
    }

    /**
     * Renvoie le vecteur (w, h) représentant la taille du Rect
     * @return size
     */
    public Vec2f getSize() {

        return size;
    }
    /**
     * Change la taille du rectangle et met à jour son inertie en fonction des nouvelles dimensions
     * @param size (w, h)
     */
    public void setSize(Vec2f size) {

        this.size = size;
        this.local_vertices = Geometry.getRectangeLocalCorners(this);

        getInertia().update();
    }

    @Override
    public void render(Renderer renderer, Graphics2D g) {

        renderer.renderRect(this, g);
    }

    /**
     * Calcule le moment d'inertie d'un rectangle autour de son centre d'inertie
     * Formule : J = (w² + h²) * m / 12
     * @link https://en.wikipedia.org/wiki/List_of_moments_of_inertia
     * @return (w² + h²) * m / 12
     */
    @Override
    public float computeJ() {

        if (size != null)
            return getInertia().getMass() * getSize().squaredMag() / 12;

        return 0;
    }

    /**
     * Volume d'un pavé droit de profondeur 1m
     * @return w * h * 1 (depth)
     */
    @Override
    public float getVolume() {

        if (size != null)
            return size.x * size.y * 1;

        return 0;
    }

    /**
     * Taille de la grande diagonale
     * @return (w² + h²)^0.5
     */
    @Override
    public double getMaximumSize() {

        return size.mag();
    }

    @Override
    public short cardinal() {

        return 1;
    }

    public String toString() {

        return "Rect[center=" + pos + ", size=" + size + "]";
    }

    @Override
    public Entity copy() {

        Rect copy = new Rect(pos, size);
        copyTo(copy);

        return copy;
    }
}

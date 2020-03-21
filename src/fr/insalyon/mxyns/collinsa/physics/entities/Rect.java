package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Geometry;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Graphics2D;

/**
 * Entitée rectangulaire, simplifie les détections de collisions pour les objets simples
 */
public class Rect extends Entity {


    /**
     * Tableau de Vec2f contenant les coins du rectangle
     */
    private Vec2f[] corners;

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

        super(pos);
        this.size = size;

        getInertia().update(this);
        updateAABB();
    }

    /**
     * Crée un rectangle à partir des coordonnées du centre et de la taille
     * @param x position x du coin
     * @param y position y du coin
     * @param w largeur
     * @param h hauteur
     */
    public Rect(double x, double y, double w, double h) {

        super(x, y);
        size = new Vec2f((float)w, (float)h);

        getInertia().update(this);
        updateAABB();
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
        getInertia().update(this);
    }

    /**
     * Renvoie le tableau contenant la position des 4 coins du Rect
     * @return corners
     */
    public Vec2f[] getCorners() {

        return corners;
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
    public void updateAABB() {

        this.corners = Geometry.getRectangleCorners(this);
        Vec2f minCorner = Geometry.getMinPos(corners);
        Vec2f maxCorner = Geometry.getMaxPos(corners);

        this.aabb.x = minCorner.x;
        this.aabb.y = minCorner.y;
        this.aabb.w = maxCorner.x - minCorner.x;
        this.aabb.h = maxCorner.y - minCorner.y;
    }

    public String toString() {

        return "Rect[center=" + pos + ", size=" + size + "]";
    }
}

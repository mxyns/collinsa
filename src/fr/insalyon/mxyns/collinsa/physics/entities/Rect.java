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
    public Vec2f size;

    /**
     * Crée un rectangle à partir d'un vecteur position (centre), et un vecteur taille
     * @param pos Vecteur position
     * @param size Vecteur taille: (largeur ; hauteur)
     */
    public Rect(Vec2f pos, Vec2f size) {

        super(pos);
        this.size = size;

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

        updateAABB();
    }

    public Vec2f getSize() {

        return size;
    }

    public Vec2f[] getCorners() {

        return corners;
    }

    public void setCorners(Vec2f[] corners) {

        this.corners = corners;
    }


    public String toString() {

        return "Rect[center=" + pos + ", size=" + size + "]";
    }

    @Override
    public void render(Renderer renderer, Graphics2D g) {

        renderer.renderRect(this, g);
    }

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


}

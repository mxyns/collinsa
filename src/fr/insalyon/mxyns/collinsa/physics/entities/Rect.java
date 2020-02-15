package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2;

import java.awt.Graphics2D;

/**
 * Entitée rectangulaire, simplifie les détections de collisions pour les objets simples
 */
public class Rect extends Entity {

    /**
     * Vec2 contenant la taille du Rect : (width, height)
     */
    public Vec2 size;

    /**
     * Crée un rectangle à partir d'un vecteur position (centre), et un vecteur taille
     * @param pos Vecteur position
     * @param size Vecteur taille: (largeur ; hauteur)
     */
    public Rect(Vec2 pos, Vec2 size) {

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
        size = new Vec2(w, h);

        updateAABB();
    }

    public String toString() {

        return "Rect[center=" + pos + ", size=" + size + "]";
    }

    @Override
    public void render(Renderer renderer, Graphics2D g) {

        renderer.renderRect(this, g);
    }

    //TODO Manage Rect rotation
    @Override
    public double getMaximumSize() {

        return size.mag();
    }

    //TODO Manage Rect rotation
    @Override
    public void updateAABB() {

        this.aabb.x = pos.x - size.x/2;
        this.aabb.y = pos.y - size.y/2;
        this.aabb.width = size.x;
        this.aabb.height = size.y;
    }


}

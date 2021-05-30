package fr.insalyon.mxyns.collinsa.physics.collisions;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

/**
 * Axis Aligned Bounding Box, un rectangle avec ses côtés horizontaux / verticaux qui englobe une entité pour la détection des collisions en broad-phase
 */
public class AABB {

    /**
     * Coordonnée x du coin supérieur gauche de l'AABB
     */
    public float x;

    /**
     * Coordonnée y du coin supérieur gauche de l'AABB
     */
    public float y;

    /**
     * Largeur du coin supérieur gauche de l'AABB
     */
    public float w;

    /**
     * Hauteur du coin supérieur gauche de l'AABB
     */
    public float h;

    /**
     * Crée une AABB à partir d'une entité et définit sa taille à partir de la taille maximale de l'entité.
     * @param entity entité pour laquelle on crée une AABB
     */
    public AABB(Entity entity) {

        this.x = entity.getPos().x;
        this.y = entity.getPos().y;
        this.w = (float) entity.getMaximumSize();
        this.h = (float) entity.getMaximumSize();
    }

    /**
     * Crée une AABB à partir de coordonnées (x, y) et d'une taille (w, h)
     * Toutes ces dimensions sont en mètres
     * @param x coordonnée x du coin supérieur gauche de l'AABB
     * @param y coordonnée y du coin supérieur gauche de l'AABB
     * @param w largeur de l'AABB
     * @param h hauteur de l'AABB
     */
    public AABB(float x, float y, float w, float h) {

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * Renvoie la coordonnée x du coin supérieur gauche de l'AABB
     * @return x
     */
    public float getX() {

        return x;
    }

    /**
     * Redéfinit la coordonnée x du coin supérieur gauche de l'AABB
     * @param x nouvelle coordonnée x
     */
    public void setX(float x) {

        this.x = x;
    }

    /**
     * Renvoie la coordonnée y du coin supérieur gauche de l'AABB
     * @return y
     */
    public float getY() {

        return y;
    }

    /**
     * Redéfinit la coordonnée y du coin supérieur gauche de l'AABB
     * @param y nouvelle coordonnée y
     */
    public void setY(float y) {

        this.y = y;
    }

    /**
     * Renvoie la largeur de l'AABB
     * @return w
     */
    public float getWidth() {

        return w;
    }

    /**
     * Redéfinit la largeur de l'AABB
     * @param w nouvelle largeur
     */
    public void setWidth(float w) {

        this.w = w;
    }

    /**
     * Renvoie la hauteur de l'AABB
     * @return h
     */
    public float getHeight() {

        return h;
    }

    /**
     * Redéfinit la hauteur de l'AABB
     * @param h nouvelle hauteur
     */
    public void setHeight(float h) {

        this.h = h;
    }

    /**
     * Détermine si deux AABB sont en intersection
     * @param other AABB avec laquelle vérifier l'intersection
     * @return true si les deux AABB sont en intersection
     */
    public boolean intersects(AABB other) {

        return x < other.x + other.w &&
               x + w > other.x &&
               y < other.y + other.h &&
               h + y > other.y;
    }

    public String toString() {

        return "AABB (" + x + ", " + y + ") -> " + w + "x" + h;
    }
}

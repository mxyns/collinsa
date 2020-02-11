package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public abstract class Entity {

    /**
     * Vec2 position de l'entité
     */
    Vec2 pos;

    /**
     * Vec2 vitesse de l'entité
     */
    private Vec2 vel;


    /**
     * Vec2 acceleration de l'entité
     */
    private Vec2 acc;

    /**
     * Constructeur global
     */
    private Entity() {

        vel = Vec2.zero();
        acc = Vec2.zero();
    }

    /**
     * Constructeur avec vecteur position
     * @param pos vecteur position
     */
    public Entity(Vec2 pos) {

        this();
        this.pos = pos.copy();
    }

    /**
     * Constructeur avec coordonnées position
     * @param x position en x
     * @param y position en y
     */
    public Entity(double x, double y) {

        this();
        this.pos = new Vec2(x, y);
    }

    /**
     * Méthode abstraite de rendu. Unique à chaque type d'entité
     * @param renderer renderer utilisé pour le rendu
     * @param g graphics associé au panel pour dessin
     */
    public abstract void render(Renderer renderer, Graphics2D g);

    /**
     * Méthode abstraite donnant la taille maximale d'une entité (permet de définir la taille des Chunks)
     * @return maximum size of an entity, even after rotation
     */
    public abstract double getMaximumSize();

    /**
     * Méthode abstraite renvoyant la AABB (Axis Aligned Bounding Box) de l'entité
     * @return AABB de l'entité sous forme de Rectangle
     */
    public abstract Rectangle2D.Double getAABB();

    /**
     * Permet de mettre à jour la position, l'angle de rotation, etc. d'une entité
     */
    public void update() {

        vel.add(acc);
        pos.add(vel);
    }


    /**
     * Renvoie le vecteur position de l'entité
     * @return vecteur position
     */
    public Vec2 getPos() {

        return pos;
    }

    /**
     * Redéfinit le vecteur position de l'entité
     */
    public void setPos(Vec2 pos) {

        this.pos = pos;
    }

    /**
     * Renvoie le vecteur vitesse de l'entité
     * @return vecteur vitesse
     */
    public Vec2 getVel() {

        return vel;
    }

    /**
     * Redéfinit le vecteur vitesse de l'entité
     */
    public void setVel(Vec2 vel) {

        this.vel = vel;
    }

    /**
     * Renvoie le vecteur accélération de l'entité
     * @return vecteur accélération
     */
    public Vec2 getAcc() {

        return acc;
    }

    /**
     * Redéfinit le vecteur accélération de l'entité
     */
    public void setAcc(Vec2 acc) {

        this.acc = acc;
    }
}

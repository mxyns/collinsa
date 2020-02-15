package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * Représente une Entité (un objet ou un élément de la simulation) de manière générale
 */
public abstract class Entity {

    /**
     * Vec2 position de l'entité
     */
    protected Vec2 pos;

    /**
     * Vec2 vitesse de l'entité
     */
    private Vec2 vel;


    /**
     * Vec2 acceleration de l'entité
     */
    private Vec2 acc;

    /**
     * Rectangle.Double (car en mètres) correspondant à l'AABB de l'entité
     * TODO: replace by own AABB class
     */
    protected Rectangle.Double aabb;

    /**
     * Couleur de l'entité
     */
    private Color color;

    /**
     * Constructeur global
     */
    private Entity() {

        vel = Vec2.zero();
        acc = Vec2.zero();
        aabb = new Rectangle2D.Double(0, 0, 0, 0);
        color = Color.black;
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
     * Méthode renvoyant la AABB (Axis Aligned Bounding Box) de l'entité
     * @return AABB de l'entité sous forme de Rectangle
     */
    public Rectangle.Double getAABB() {

        return aabb;
    }

    /**
     * Méthode abstraite mettant à jour la AABB (Axis Aligned Bounding Box) de l'entité
     */
    public abstract void updateAABB();


    /**
     * Permet de mettre à jour la position, l'angle de rotation, etc. d'une entité
     */
    public void updateMillis(long elapsed) {

        vel.add(acc, elapsed * 1e-3f);
        pos.add(vel, elapsed * 1e-3f);

        updateAABB();
    }

    /**
     * Permet de mettre à jour la position, l'angle de rotation, etc. d'une entité
     */
    public void updateNano(long elapsed) {

        vel.add(acc, elapsed * 1e-9f);
        pos.add(vel, elapsed * 1e-9f);
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
     * Redéfinit le vecteur position de l'entité
     */
    public void setPos(float x, float y) {

        this.pos.set(x, y);
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

    /**
     * Renvoie la couleur de l'entité
     * @return couleur de l'entité
     */
    public Color getColor() {

        return color;
    }

    /**
     * Redéfini la couleur de l'entité
     * @param color nouvelle couleur
     */
    public void setColor(Color color) {

        this.color = color;
    }

}

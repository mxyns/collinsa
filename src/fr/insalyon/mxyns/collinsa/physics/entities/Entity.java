package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.physics.collisions.AABB;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Représente une Entité (un objet ou un élément de la simulation) de manière générale
 */
public abstract class Entity {

    /**
     * Vec2f position de l'entité
     */
    protected Vec2f pos;

    /**
     * Vec2f vitesse de l'entité
     */
    private Vec2f vel;

    /**
     * Vec2f acceleration de l'entité
     */
    private Vec2f acc;

    /**
     * float angle de l'entité (rotation autour de son centre)
     */
    private float rot;

    /**
     * Vitesse angulaire de l'entité (rotation autour de son centre)
     */
    private float angVel;

    /**
     * Accélération angulaire de l'entité
     */
    private float angAcc;

    /**
     * AABB (dims. en mètres) correspondant à l'AABB de l'entité
     */
    protected AABB aabb;

    /**
     * Couleur de l'entité
     */
    private Color color;

    /**
     * Constructeur global
     */
    private Entity() {

        vel = Vec2f.zero();
        acc = Vec2f.zero();
        aabb = new AABB(0, 0, 0, 0);
        color = Color.black;
    }

    /**
     * Constructeur avec vecteur position
     * @param pos vecteur position
     */
    public Entity(Vec2f pos) {

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
        this.pos = new Vec2f((float)x, (float)y);
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
    public AABB getAABB() {

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

        angVel += angAcc * elapsed;
        rot += angVel * elapsed;

        updateAABB();
    }

    /**
     * Permet de mettre à jour la position, l'angle de rotation, etc. d'une entité
     */
    public void updateNano(long elapsed) {

        vel.add(acc, elapsed * 1e-9f);
        pos.add(vel, elapsed * 1e-9f);

        angVel += angAcc * elapsed;
        rot += angVel * elapsed;

        updateAABB();
    }


    /**
     * Renvoie le vecteur position de l'entité
     * @return vecteur position
     */
    public Vec2f getPos() {

        return pos;
    }

    /**
     * Redéfinit le vecteur position de l'entité
     */
    public void setPos(Vec2f pos) {

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
    public Vec2f getVel() {

        return vel;
    }

    /**
     * Redéfinit le vecteur vitesse de l'entité
     */
    public void setVel(Vec2f vel) {

        this.vel = vel;
    }

    /**
     * Renvoie le vecteur accélération de l'entité
     * @return vecteur accélération
     */
    public Vec2f getAcc() {

        return acc;
    }

    /**
     * Redéfinit le vecteur accélération de l'entité
     */
    public void setAcc(Vec2f acc) {

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


    public float getRot() {

        return rot;
    }

    public void setRot(float rot) {

        this.rot = rot;
    }

    public float getAngVel() {

        return angVel;
    }

    public void setAngVel(float angVel) {

        this.angVel = angVel;
    }

    public float getAngAcc() {

        return angAcc;
    }

    public void setAngAcc(float angAcc) {

        this.angAcc = angAcc;
    }
}

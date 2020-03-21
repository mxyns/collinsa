package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.physics.Inertia;
import fr.insalyon.mxyns.collinsa.physics.Material;
import fr.insalyon.mxyns.collinsa.physics.collisions.AABB;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision.CollisionType;
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
     * Type de collision, détermine si l'objet aura des collisions elastiques ou non avec les autres objets
     */
    private CollisionType collisionType = CollisionType.CLASSIC;

    /**
     * AABB (dims. en mètres) correspondant à l'AABB de l'entité
     */
    final protected AABB aabb;

    /**
     * Matériau de l'entité
     */
    private Material material;

    /**
     * Inertie de l'entité, contient ses informations de masse, et permet de calculer / stocker son moment d'inertie
     */
    final private Inertia inertia;

    /**
     * Couleur de l'entité
     */
    private Color color;

    /**
     * Défini si une entité est activée, si elle ne l'est pas, sa vitesse, position, etc. ne sont pas mis à jour et elle ignore les collisions
     */
    private boolean activated = true;

    /**
     * Constructeur global
     */
    private Entity() {

        vel = Vec2f.zero();
        acc = Vec2f.zero();
        aabb = new AABB(0, 0, 0, 0);
        color = Color.black;

        inertia = new Inertia();
        setMaterial(Material.DUMMY.copy());
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
     * Calcule le moment d'inertie autour du centre de rotation
     * @return moment d'inertie de l'entité autour de son centre de rotation
     */
    public abstract float computeJ();

    /**
     * Méthode qui permet de calculer le volume d'une entité
     * @return volume en m^3
     */
    public abstract float getVolume();

    /**
     * Méthode abstraite mettant à jour la AABB (Axis Aligned Bounding Box) de l'entité
     */
    public abstract void updateAABB();

    /**
     * Permet de mettre à jour la position, l'angle de rotation, etc. d'une entité
     * @param elapsed temps en ms
     */
    public void updateMillis(long elapsed) {

        vel.add(acc, elapsed * 1e-3f);
        pos.add(vel, elapsed * 1e-3f);

        angVel += angAcc * elapsed * 1e-3f;
        rot += angVel * elapsed * 1e-3f;

        updateAABB();
    }

    /**
     * Permet de mettre à jour la position, l'angle de rotation, etc. d'une entité
     * @param elapsed temps en ns
     */
    public void updateNano(long elapsed) {

        vel.add(acc, elapsed * 1e-9f);
        pos.add(vel, elapsed * 1e-9f);

        angVel += angAcc * elapsed;
        rot += angVel * elapsed;

        updateAABB();
    }


    // GETTERS & SETTERS

    /**
     * Méthode renvoyant la AABB (Axis Aligned Bounding Box) de l'entité
     * @return AABB de l'entité sous forme de Rectangle
     */
    public AABB getAABB() {

        return aabb;
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
     * Redéfinit le vecteur vitesse de l'entité
     */
    public void setVel(float x, float y) {

        this.vel.x = x;
        this.vel.y = y;
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
     * Redéfinit le vecteur accélération de l'entité
     */
    public void setAcc(float x, float y) {

        this.acc.x = x;
        this.acc.y = y;
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

    /**
     * Renvoie l'angle de rotation de l'entité autour de son centre (pos)
     * @return rot
     */
    public float getRot() {

        return rot;
    }

    /**
     * Redéfinit l'angle de rotation de l'entité autour de son centre (pos)
     * @param rot nouvel angle
     */
    public void setRot(float rot) {

        this.rot = rot;
    }

    /**
     * Renvoie la vitesse angulaire de l'entité autour de son centre (pos)
     * @return angVel
     */
    public float getAngVel() {

        return angVel;
    }

    /**
     * Redéfinit la vitesse angulaire de l'entité autour de son centre (pos)
     * @param angVel nouvelle vitesse angulaire
     */
    public void setAngVel(float angVel) {

        this.angVel = angVel;
    }

    /**
     * Renvoie l'accélération angulaire de l'entité autour de son centre (pos)
     * @return angAcc
     */
    public float getAngAcc() {

        return angAcc;
    }

    /**
     * Redéfinit l'accélération angulaire de l'entité autour de son centre (pos)
     * @param angAcc nouvelle accélération angulaire
     */
    public void setAngAcc(float angAcc) {

        this.angAcc = angAcc;
    }

    /**
     * Renvoie true si l'entité est un objet cinématique. C'est à dire qu'il ne réagit pas aux collisions avec d'autres objets et son déplacement ne dépend que de sa vitesse & accélération
     * @return true si l'entité est cinématique
     */
    public boolean isKinematic() {

        return collisionType == CollisionType.KINEMATIC;
    }

    /**
     * Renvoie le type de collision créé par cette entité
     * @return collisionType
     */
    public CollisionType getCollisionType() {

        return collisionType;
    }

    /**
     * Redéfinit le type de collision que l'entité génèrera
     * @param collisionType nouveau type de collision
     */
    public void setCollisionType(CollisionType collisionType) {

        this.collisionType = collisionType;
    }

    public Material getMaterial() {

        return material;
    }

    public void setMaterial(Material material) {

        this.material = material;
        this.color = material.getColor();
        this.inertia.update(this);
    }

    public Inertia getInertia() {

        return inertia;
    }

    public boolean isActivated() {

        return activated;
    }

    public void setActivated(boolean activated) {

        this.activated = activated;
    }
}

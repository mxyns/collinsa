package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Inertia;
import fr.insalyon.mxyns.collinsa.physics.Material;
import fr.insalyon.mxyns.collinsa.physics.collisions.AABB;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision.CollisionType;
import fr.insalyon.mxyns.collinsa.physics.collisions.CollisionListener;
import fr.insalyon.mxyns.collinsa.render.Renderable;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Color;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Représente une Entité (un objet ou un élément de la simulation) de manière générale
 */
public abstract class Entity implements Renderable {

    /**
     * Unique identifier, used in order to find the corresponding Entity in different Ticks
     */
    public final UUID uuid;

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
     * Durée vécue et durée de vie de l'entité
     */
    public double lived = 0, lifespan = Double.POSITIVE_INFINITY;

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
     * Couleur de l'entité (outline => bord, fill => intérieur)
     */
    private Color outlineColor, fillColor;

    /**
     * Défini si une entité est activée, si elle ne l'est pas, sa vitesse, position, etc. ne sont pas mis à jour et elle ignore les collisions
     */
    private boolean activated = true;

    /**
     * Liste des listeners associés à l'entité. LinkedList car on la parcourt de proche en proche (pas d'accès aléatoire).
     */
    final private LinkedHashSet<CollisionListener> listeners = new LinkedHashSet<>();

    /**
     * Constructeurs globaux
     */
    private Entity() {

        this(UUID.randomUUID());
    }
    protected Entity(UUID uid) {

        this.uuid = uid == null ? UUID.randomUUID() : uid;

        vel = Vec2f.zero();
        acc = Vec2f.zero();
        aabb = new AABB(0, 0, 0, 0);
        outlineColor = Color.black;
        fillColor = null;

        inertia = new Inertia(this);
        setMaterial(Material.DUMMY.copy());
    }
    protected Entity(UUID uid, Vec2f pos) {

        this(uid);
        this.pos = pos.copy();
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
     * Donne le numéro identifiant le type d'Entité (utilisé dans Collider)
     */
    public abstract short cardinal();

    /**
     * Permet de mettre à jour la position, l'angle de rotation, etc. d'une entité
     * @param elapsed temps en sec
     */
    public boolean update(double elapsed) {

        if ((lived += elapsed) > lifespan)
            return false;

        vel.add(acc, elapsed);
        pos.add(vel, elapsed);

        angVel += angAcc * elapsed;
        rot += angVel * elapsed;

        updateAABB();

        return true;
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
    public Color getOutlineColor() {

        return outlineColor;
    }

    /**
     * Renvoie la couleur de l'entité
     * @return couleur de l'entité
     */
    public Color getFillColor() {

        return fillColor;
    }

    /**
     * Redéfini la couleur de l'entité
     * @param color nouvelle couleur
     */
    public void setOutlineColor(Color color) {

        this.outlineColor = color;
    }
     /**
     * Redéfini la couleur de l'entité
     * @param color nouvelle couleur
     */
    public void setFillColor(Color color) {

        this.fillColor = color;
    }

    /**
     * Donne une couleur de remplissage et de contour à partir d'une couleur
     * La couleur de bordure est la couleur de remplissage maisen plus clair
     * @param color nouvelle couleur de fond
     */
    public void setColor(Color color) {

        this.fillColor = color;
        this.outlineColor = color.brighter().brighter();
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

    /**
     * Renvoie le matériau de l'entité
     * @return material
     */
    public Material getMaterial() {

        return material;
    }

    /**
     * Redéfinit le matériau de l'entité et applique les modifications nécessaires (nouvelles couleurs, mise à jour de l'inertie pour prendre en compte la nouvelle densité)
     *
     * @param material nouveau matériau
     */
    public void setMaterial(Material material) {

        this.material = material;
        this.outlineColor = material.getOutlineColor();
        this.fillColor = material.getFillColor();
        this.inertia.update();
    }

    /**
     * Renvoie l'objet Inertia contenant les informations d'inertie (masse, moment d'inertie, etc.) de l'entité
     *
     * @return inertia
     */
    public Inertia getInertia() {

        return inertia;
    }

    /**
     * Informe de si l'entité l'entité est activée ou non
     *
     * @return activated
     */
    public boolean isActivated() {

        return activated;
    }

    /**
     * Active ou désactive l'entité
     *
     * @param activated nouvel état
     */
    public void setActivated(boolean activated) {

        this.activated = activated;
    }

    /**
     * Ajoute un CollisionListener à la liste des listeners de l'entité
     *
     * @param listener listener à ajouter
     */
    public void addCollisionListener(CollisionListener listener) {

        listeners.add(listener);
    }

    /**
     * Renvoie la liste des listeners actifs sur l'entité
     *
     * @return listeners
     */
    public Set<CollisionListener> getCollisionListeners() {

        return listeners;
    }

    public abstract Entity copy();

    protected Entity copyTo(Entity dest) {

        dest.pos.set(pos);
        dest.vel.set(vel);
        dest.acc.set(acc);
        dest.setRot(rot);
        dest.setAngVel(angVel);
        dest.setAngAcc(angAcc);
        if (fillColor != null)
            dest.setFillColor(new Color(fillColor.getRGB()));
        if (outlineColor != null)
            dest.setOutlineColor(new Color(outlineColor.getRGB()));
        dest.setCollisionType(collisionType);
        dest.material = material.copy();
        dest.inertia.setMass(inertia.getMass());
        dest.inertia.setJ(inertia.getJ());
        dest.setActivated(activated);
        dest.lifespan = lifespan;
        dest.lived = lived;

        dest.listeners.addAll(listeners);

        if (Collinsa.INSTANCE.getMonitoring().entityMonitoring.isMonitored(this))
            Collinsa.INSTANCE.getMonitoring().monitor(dest);

        dest.updateAABB();

        return dest;
    }

    public String toString() {

        return String.format("[" +
                             "activated=%b\n" +
                             "pos=%s\n" +
                             "vel=%s\n" +
                             "acc=%s\n" +
                             "rot=%s\n" +
                             "angVel=%s\n" +
                             "angAcc=%s\n" +
                             "lived=%e until it reaches lifespan=%e\n" +
                             "collisionType=%s\n" +
                             "material=%s\n" +
                             "inertia=%s\n" +
                             "colors : outline=%s, fill=%s\n",
                             activated, pos, vel, acc, rot, angVel, angAcc, lived, lifespan, collisionType, material, inertia, outlineColor, fillColor);
    }
}

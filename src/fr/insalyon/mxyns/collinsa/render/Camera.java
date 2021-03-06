package fr.insalyon.mxyns.collinsa.render;

import fr.insalyon.mxyns.collinsa.physics.Chunk;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import javax.swing.JPanel;

/**
 * Une camera permettant le rendu de la scene, elle a toujours le même ratio que le JPanel sur lequel elle est rendue
 * Elle est associée à son JPanel par un Renderer. C'est lui qui la contrôle, les autres classes peuvent seulement lire les paramètres de la Camera.
 */
public class Camera {

    /**
     * La caméra appartient au monde donc toutes les dimensions doivent être en mètres
     * pos est la position du coin supérieur-gauche de la caméra dans le monde
     * width la largeur de la caméra
     * height la hauteur
     */
    private Vec2f pos;
    private double width, height;

    /**
     * Ratio largeur / hauteur de la caméra (e.g: 16:9)
     */
    private double ratio;

    /**
     * Entité suivie par la caméra
     */
    private Entity followedEntity = null;

    public Camera() {

        this.pos = new Vec2f(0, 0);
        this.width = 0;
        this.height = 0;
        this.ratio = 1;
    }
    /**
     * Crée une caméra à partir de dimensions en mètres, doivent avoir le même rapport largeur/hauteur que le Panel sur lequel elle est dessinée.
     * @param size dimensions de la caméra en mètres
     */
    public Camera(Vec2d size) {

        this();
        setSize(size);
    }
    /**
     * Crée une caméra à partir de dimensions en mètres, doivent avoir le même rapport largeur/hauteur que le Panel sur lequel elle est dessinée.
     * @param pos position de la caméra en mètres
     * @param size dimensions de la caméra en mètres
     */
    public Camera(Vec2d pos, Vec2d size) {

        this();
        setPos(pos.toFloat());
        setSize(size);
    }
    /**
     * Crée une caméra à partir de dimensions en mètres, doivent avoir le même rapport largeur/hauteur que le Panel sur lequel elle est dessinée.
     * @param width largeur de la caméra en mètres
     * @param height largeur de la caméra en mètres
     */
    public Camera(double width, double height) {

        this(new Vec2d(width, height));
    }
    /**
     * Crée une caméra à partir d'un panel. La caméra aura initialement la même taille que le panel (converti dans le monde via le scale)
     * @param panel dimensions de la caméra en mètres
     * @param scale échelle en px/m
     */
    public Camera(JPanel panel, float scale) {

        this(new Vec2d(panel.getSize().getWidth() / scale, panel.getSize().getHeight() / scale));
    }


    /**
     * Vérifie si le chunk est visible ou non
     * @param chunk chunk à tester
     * @return true si le chunk est visible
     */
    boolean sees(Chunk chunk) {

        return pos.x < chunk.bounds.getX() + chunk.bounds.getWidth() &&
               pos.x + width > chunk.bounds.getX() &&
               pos.y < chunk.bounds.getY() + chunk.bounds.getHeight() &&
               height + pos.y > chunk.bounds.getY();
    }

    /**
     * Renvoie la position actuelle de la Camera dans le monde
     * @return Vec2f position (en mètres)
     */
    public Vec2f getPos() {

        return pos;
    }

    /**
     * Définit la position du coin supérieur-gauche de la Camera dans le monde
     * @param x coordonnée x en mètres
     * @param y coordonée y en mètres
     */
    void setPos(float x, float y) {

        this.pos.set(x, y);
    }

    /**
     * Définit la position du coin supérieur-gauche de la Camera dans le monde
     * @param pos Vec2f contenant les coordonnées en mètres de la Camera
     */
    void setPos(Vec2f pos) {

        this.pos = pos;
    }

    /**
     * Déplace la camera dans une certaine direction
     * @param dir direction de mouvement, la norme du vecteur est la longueur du déplacement
     */
    void move(Vec2f dir) {

        this.pos.add(dir);
    }

    /**
     * Déplace la camera dans une certaine direction
     * @param x déplacement selon x
     * @param y déplacement selon y
     */
    void move(int x , int y) {

        this.pos.add(x, y);
    }
    /**
     * Déplace la camera dans une certaine direction
     * @param x déplacement selon x
     * @param y déplacement selon y
     */
    void move(float x , float y) {

        this.pos.add(x, y);
    }

    /**
     * Définit la position x du coin supérieur-gauche de la Camera dans le monde.
     * @param x coordonnée x en mètres
     */
    void setX(float x) {

        this.pos.x = x;
    }

    /**
     * Définit la position y du coin supérieur-gauche de la Camera dans le monde.
     * @param y coordonnée y en mètres
     */
    void setY(float y) {

        this.pos.y = y;
    }

    /**
     * Renvoie la largeur de la zone capturée par la Camera
     * @return largeur de la camera
     */
    public double getWidth() {

        return width;
    }

    /**
     * Renvoie la hauteur de la zone capturée par la Camera
     * @return hauteur de la camera
     */
    public double getHeight() {

        return height;
    }

    /**
     * Initialise la taille et le ratio w/h caméra (peut être fait après son instanciation)
     * @param size dimensions de la caméra en mètres
     */
    void setSize(Vec2d size) {

        this.width = size.x;
        this.height = size.y;
        this.ratio = width/height;
    }
    /**
     * Initialise la taille et le ratio w/h caméra (peut être fait après son instanciation)
     * @param width largeur de la caméra en mètres
     * @param height hauteur de la caméra en mètres
     */
    void setSize(double width, double height) {

        this.width = width;
        this.height = height;
        this.ratio = width/height;
    }

    /**
     * Définit la taille de la caméra en mètres, conserve le ratio w/h
     * @param height hauteur de la caméra en mètres
     */
    void setHeight(double height) {

        this.width = ratio * height;
        this.height = height;
    }

    /**
     * Renvoie le ratio largeur/hauteur de la Camera qui est le même que celui de son panel associé
     * @return rapport largeur/hauteur
     */
    public double getRatio() {

        return ratio;
    }

    /**
     * Renvoie l'entité suivie par la caméra
     *
     * @return followedEntity or null
     */
    public Entity getFollowedEntity() {

        return followedEntity;
    }

    /**
     * Indique à la caméra de suivre une entité
     *
     * @param entity entité à suivre. si null, la caméra est libre
     */
    public void follow(Entity entity) {

        this.followedEntity = entity;
    }

    public String toString() {

        return "Camera[pos=" + pos + ", size=" + new Vec2d(width, height) + ", ratio=" + ratio + "]";
    }
}

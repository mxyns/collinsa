package fr.insalyon.mxyns.collinsa.utils.geo;

/**
 * Vecteur 2D et opérations associées
 */
public class Vec2 {

    /**
     * Coordonnées x et y du Vecteur 2D
     */
    public double x, y;

    public Vec2(double x, double y) {

        this.x = x;
        this.y = y;
    }

    /**
     * Ajout d'un Vec2 au vecteur courant
     * @param other Vec2 à ajouter
     * @return vecteur courant modifié
     */
    public Vec2 add(Vec2 other) {

        x += other.x;
        y += other.y;

        return this;
    }

    /**
     * Soustraction d'un Vec2 au vecteur courant
     * @param other Vec2 à ajouter
     * @return vecteur courant modifié
     */
    public Vec2 sub(Vec2 other) {

        x -= other.x;
        y -= other.y;

        return this;
    }

    /**
     * Multiplication du vecteur courant par un facteur
     * @param mult facteur multiplicateur
     * @return vecteur courant modifié
     */
    public Vec2 mult(double mult) {

        this.x *= mult;
        this.y *= mult;

        return this;
    }

    /**
     * Produit scalaire d'un Vec2 avec le vecteur courant
     * @param other deuxième vecteur du produit scalaire
     * @return le résultat du produit scalaire
     */
    public double dot(Vec2 other) {

        return x*other.x + y*other.y;
    }

    /**
     * Produit vectoriel 2D d'un Vec2 avec le vecteur courant
     * @param other deuxième vecteur du produit vectoriel 2D
     * @return le résultat du produit vectoriel 2D
     */
    public double cross(Vec2 other) {

        return x*other.y - other.x*y;
    }

    /**
     * Multiplie le vecteur courant par -1 // Le rend égal à son opposé
     * @return vecteur courant modifié
     */
    public Vec2 neg() {

        x = -x;
        y = -y;

        return this;
    }

    /**
     * Calcule la norme au carré du Vec2. Permet d'éviter la racine si inutile, bon pour les perfs.
     * @return norme au carré
     */
    public double squaredMag() {

        return x*x + y*y;
    }

    /**
     * Calcule la norme du Vec2
     * @return norme
     */
    public double mag() {

        return Math.sqrt(x*x + y*y);
    }

    /**
     * Normalise le vecteur courant
     * @return vecteur courant normalisé
     */
    // TODO: invsqrt method
    public Vec2 normalize() {

        double mag = Math.sqrt(x*x + y*y);
        this.x /= mag;
        this.y /= mag;

        return this;
    }

    /**
     * Force la norme du vecteur
     * @param mult nouvelle norme
     * @return vecteur courant de nouvelle norme 'mult'
     */
    // TODO: invsqrt method
    public Vec2 setMag(double mult) {

        double mag = Math.sqrt(x*x+y*y);
        this.x *= mult/mag;
        this.y *= mult/mag;

        return this;
    }

    /**
     * Calcule la valeur de l'angle formé entre le vecteur courant et un autre vecteur
     * @param other autre vecteur formant l'angle
     * @return angle(rad) formé par les deux vecteurs
     */
    public double angleWith(Vec2 other) {

        return Math.atan2(x*other.x + y*other.y, x*other.y - other.x*y);
    }

    /**
     * Calcule la distance entre deux Vec2 si on les considère comme représentant des points
     * @param other autre vecteur
     * @return distance à l'autre vecteur
     */
    public double dist(Vec2 other) {

        double dx = x-other.x;
        double dy = y-other.y;

        return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * Donne le vecteur nul
     * @return vecteur nul
     */
    public static Vec2 zero() {

        return new Vec2(0,0);
    }

    /**
     * Donne une copie de ce vecteur
     * @return copie du vecteur
     */
    public Vec2 copy() {

        return new Vec2(x, y);
    }

    /**
     * Redéfini les coordonnées du vecteur
     * @param x coordoonée x
     * @param y coordoonée y
     */
    public void set(float x, float y) {

        this.x = x;
        this.y = y;
    }

    /**
     * Redéfini la coordonnée x du vecteur
     * @param x coordoonée x
     */
    public void setX(float x) {

        this.x = x;
    }

    /**
     * Redéfini la coordonnée y du vecteur
     * @param y coordoonée y
     */
    public void setY(float y) {

        this.y = y;
    }

    public String toString() {

        return "(" + x + ", " + y + ")";
    }
}

package fr.insalyon.mxyns.collinsa.utils.geo;

/**
 * Vecteur 2D et opérations associées
 */
public class Vec2f {

    /**
     * Coordonnées x et y du Vecteur 2D
     */
    public float x, y;

    public Vec2f(float x, float y) {

        this.x = x;
        this.y = y;
    }

    /**
     * Ajout d'un Vec2d au vecteur courant
     * @param other Vec2d à ajouter
     * @return vecteur courant modifié
     */
    public Vec2f add(Vec2f other) {

        x += other.x;
        y += other.y;

        return this;
    }

    /**
     * Ajout d'un Vec2d multiplié, sans le modifier, au vecteur courant
     * @param other Vec2d à ajouter
     * @return vecteur courant modifié
     */
    public Vec2f add(Vec2f other, float mult) {

        x += other.x * mult;
        y += other.y * mult;

        return this;
    }

    /**
     * Ajout de valeurs au coordonnées x et y
     * @param x valeur à ajouter à la coordonnée x
     * @param y valeur à ajouter à la coordonnée y
     * @return vecteur courant modifié
     */
    public Vec2f add(int x, int y) {

        this.x += x;
        this.y += y;

        return this;
    }
    /**
     * Ajout de valeurs au coordonnées x et y
     * @param x valeur à ajouter à la coordonnée x
     * @param y valeur à ajouter à la coordonnée y
     * @return vecteur courant modifié
     */
    public Vec2f add(float x, float y) {

        this.x += x;
        this.y += y;

        return this;
    }
    /**
     * Ajout de valeurs au coordonnées x et y
     * @param x valeur à ajouter à la coordonnée x
     * @param y valeur à ajouter à la coordonnée y
     * @return vecteur courant modifié
     */
    public Vec2f add(double x, double y) {

        this.x += x;
        this.y += y;

        return this;
    }


    /**
     * Ajout de valeur à la coordonnée x
     * @param x valeur à ajouter à la coordonnée x
     * @return vecteur courant modifié
     */
    public Vec2f addX(int x) {

        this.x += x;

        return this;
    }

    /**
     * Ajout de valeur à la coordonnée y
     * @param y valeur à ajouter à la coordonnée y
     * @return vecteur courant modifié
     */
    public Vec2f addY(int y) {

        this.y += y;

        return this;
    }

    /**
     * Soustraction d'un Vec2d au vecteur courant
     * @param other Vec2d à ajouter
     * @return vecteur courant modifié
     */
    public Vec2f sub(Vec2f other) {

        x -= other.x;
        y -= other.y;

        return this;
    }

    /**
     * Retrait de valeurs au coordonnées x et y
     * @param x valeur à retirer à la coordonnée x
     * @param y valeur à retirer à la coordonnée y
     * @return vecteur courant modifié
     */
    public Vec2f sub(int x, int y) {

        this.x -= x;
        this.y -= y;

        return this;
    }
    /**
     * Retrait de valeurs au coordonnées x et y
     * @param x valeur à retirer à la coordonnée x
     * @param y valeur à retirer à la coordonnée y
     * @return vecteur courant modifié
     */
    public Vec2f sub(float x, float y) {

        this.x -= x;
        this.y -= y;

        return this;
    }

    /**
     * Retrait de valeur à la coordonnée x
     * @param x valeur à retirer à la coordonnée x
     * @return vecteur courant modifié
     */
    public Vec2f subX(int x) {

        this.x -= x;

        return this;
    }

    /**
     * Retrait de valeur à la coordonnée y
     * @param y valeur à retirer à la coordonnée y
     * @return vecteur courant modifié
     */
    public Vec2f subY(int y) {

        this.y -= y;

        return this;
    }


    /**
     * Multiplication du vecteur courant par un facteur
     * @param mult facteur multiplicateur
     * @return vecteur courant modifié
     */
    public Vec2f mult(float mult) {

        this.x *= mult;
        this.y *= mult;

        return this;
    }
    /**
     * Multiplication du vecteur courant par un facteur différent sur x et y
     * @param multX facteur multiplicateur de x
     * @param multY facteur multiplicateur de y
     * @return vecteur courant modifié
     */
    public Vec2f mult(float multX, float multY) {

        this.x *= multX;
        this.y *= multY;

        return this;
    }
    /**
     * Multiplication du vecteur courant par un facteur différent sur x et y
     * @param multX facteur multiplicateur de x
     * @param multY facteur multiplicateur de y
     * @return vecteur courant modifié
     */
    public Vec2f mult(double multX, double multY) {

        this.x *= multX;
        this.y *= multY;

        return this;
    }


    /**
     * Produit scalaire d'un Vec2d avec le vecteur courant
     * @param other deuxième vecteur du produit scalaire
     * @return le résultat du produit scalaire
     */
    public float dot(Vec2f other) {

        return x*other.x + y*other.y;
    }

    /**
     * Produit vectoriel 2D d'un Vec2d avec le vecteur courant
     * @param other deuxième vecteur du produit vectoriel 2D
     * @return le résultat du produit vectoriel 2D
     */
    public float cross(Vec2f other) {

        return x*other.y - other.x*y;
    }

    /**
     * Multiplie le vecteur courant par -1 // Le rend égal à son opposé
     * @return vecteur courant modifié
     */
    public Vec2f neg() {

        x = -x;
        y = -y;

        return this;
    }

    /**
     * Calcule la norme au carré du Vec2d. Permet d'éviter la racine si inutile, bon pour les perfs.
     * @return norme au carré
     */
    public float squaredMag() {

        return x*x + y*y;
    }

    /**
     * Calcule la norme du Vec2d
     * @return norme
     */
    public float mag() {

        return (float)Math.sqrt(x*x + y*y);
    }

    /**
     * Normalise le vecteur courant
     * @return vecteur courant normalisé
     */
    // TODO: invsqrt method
    public Vec2f normalize() {

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
    public Vec2f setMag(float mult) {

        double  mag = Math.sqrt(x*x+y*y);
        this.x *= mult/mag;
        this.y *= mult/mag;

        return this;
    }

    /**
     * Calcule la valeur de l'angle formé entre le vecteur courant et un autre vecteur
     * @param other autre vecteur formant l'angle
     * @return angle(rad) formé par les deux vecteurs
     */
    public float angleWith(Vec2f other) {

        return (float)Math.atan2(x*other.x + y*other.y, x*other.y - other.x*y);
    }

    /**
     * Calcule la distance entre deux Vec2d si on les considère comme représentant des points
     * @param other autre vecteur
     * @return distance à l'autre vecteur
     */
    public float dist(Vec2f other) {

        float dx = x-other.x;
        float dy = y-other.y;

        return (float)Math.sqrt(dx*dx + dy*dy);
    }


    /**
     * Calcule la distance entre deux Vec2d au carré si on les considère comme représentant des points
     * @param other autre vecteur
     * @return distance à l'autre vecteur
     */
    public float sqrdDist(Vec2f other) {

        float dx = x-other.x;
        float dy = y-other.y;

        return dx*dx+dy*dy;
    }

    /**
     * Donne le vecteur nul
     * @return vecteur nul
     */
    public static Vec2f zero() {

        return new Vec2f(0, 0);
    }

    /**
     * Donne une copie de ce vecteur
     * @return copie du vecteur
     */
    public Vec2f copy() {

        return new Vec2f(x, y);
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
    /**
     * Redéfini les coordonnées du vecteur
     * @param x coordoonée x
     * @param y coordoonée y
     */
    public void set(double x, double y) {

        this.x = (float) x;
        this.y = (float) y;
    }

    /**
     * Redéfini la coordonnée x du vecteur
     * @param x coordoonée x
     */
    public void setX(double x) {

        this.x = (float) x;
    }

    /**
     * Redéfini la coordonnée y du vecteur
     * @param y coordoonée y
     */
    public void setY(double y) {

        this.y = (float) y;
    }

    public Vec2d toDouble() {

        return new Vec2d(x,y);
    }

    public String toString() {

        return "(" + x + ", " + y + ")";
    }
}

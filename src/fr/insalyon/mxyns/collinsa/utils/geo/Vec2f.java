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
     *
     * @param other Vec2d à ajouter
     *
     * @return vecteur courant modifié
     */
    public Vec2f add(Vec2f other) {

        x += other.x;
        y += other.y;

        return this;
    }
    /**
     * Ajout d'un Vec2d au vecteur courant
     *
     * @param other Vec2d à ajouter
     *
     * @return vecteur courant modifié
     */
    public Vec2f add(Vec2d other) {

        x += other.x;
        y += other.y;

        return this;
    }

    /**
     * Ajout d'un Vec2f multiplié, sans le modifier, au vecteur courant
     *
     * @param other Vec2f à ajouter
     *
     * @return vecteur courant modifié
     */
    public Vec2f add(Vec2f other, float mult) {

        x += other.x * mult;
        y += other.y * mult;

        return this;
    }
    /**
     * Ajout d'un Vec2f multiplié, sans le modifier, au vecteur courant
     *
     * @param other Vec2f à ajouter
     *
     * @return vecteur courant modifié
     */
    public Vec2f add(Vec2f other, double mult) {

        x += other.x * mult;
        y += other.y * mult;

        return this;
    }

    /**
     * Ajout d'un Vec2d multiplié, sans le modifier, au vecteur courant
     *
     * @param other Vec2d à ajouter
     *
     * @return vecteur courant modifié
     */
    public Vec2f add(Vec2d other, float mult) {

        x += other.x * mult;
        y += other.y * mult;

        return this;
    }
    /**
     * Ajout d'un Vec2d multiplié, sans le modifier, au vecteur courant
     *
     * @param other Vec2d à ajouter
     *
     * @return vecteur courant modifié
     */
    public Vec2f add(Vec2d other, double mult) {

        x += other.x * mult;
        y += other.y * mult;

        return this;
    }

    /**
     * Ajout de valeurs au coordonnées x et y
     *
     * @param x valeur à ajouter à la coordonnée x
     * @param y valeur à ajouter à la coordonnée y
     *
     * @return vecteur courant modifié
     */
    public Vec2f add(int x, int y) {

        this.x += x;
        this.y += y;

        return this;
    }

    /**
     * Ajout de valeurs au coordonnées x et y
     *
     * @param x valeur à ajouter à la coordonnée x
     * @param y valeur à ajouter à la coordonnée y
     *
     * @return vecteur courant modifié
     */
    public Vec2f add(float x, float y) {

        this.x += x;
        this.y += y;

        return this;
    }

    /**
     * Ajout de valeurs au coordonnées x et y
     *
     * @param x valeur à ajouter à la coordonnée x
     * @param y valeur à ajouter à la coordonnée y
     *
     * @return vecteur courant modifié
     */
    public Vec2f add(double x, double y) {

        this.x += x;
        this.y += y;

        return this;
    }


    /**
     * Ajout de valeur à la coordonnée x
     *
     * @param x valeur à ajouter à la coordonnée x
     *
     * @return vecteur courant modifié
     */
    public Vec2f addX(int x) {

        this.x += x;

        return this;
    }

    /**
     * Ajout de valeur à la coordonnée y
     *
     * @param y valeur à ajouter à la coordonnée y
     *
     * @return vecteur courant modifié
     */
    public Vec2f addY(int y) {

        this.y += y;

        return this;
    }

    /**
     * Soustraction d'un Vec2d au vecteur courant
     *
     * @param other Vec2d à ajouter
     *
     * @return vecteur courant modifié
     */
    public Vec2f sub(Vec2f other) {

        x -= other.x;
        y -= other.y;

        return this;
    }

    /**
     * Retrait de valeurs au coordonnées x et y
     *
     * @param x valeur à retirer à la coordonnée x
     * @param y valeur à retirer à la coordonnée y
     *
     * @return vecteur courant modifié
     */
    public Vec2f sub(int x, int y) {

        this.x -= x;
        this.y -= y;

        return this;
    }

    /**
     * Retrait de valeurs au coordonnées x et y
     *
     * @param x valeur à retirer à la coordonnée x
     * @param y valeur à retirer à la coordonnée y
     *
     * @return vecteur courant modifié
     */
    public Vec2f sub(float x, float y) {

        this.x -= x;
        this.y -= y;

        return this;
    }

    /**
     * Retrait de valeur à la coordonnée x
     *
     * @param x valeur à retirer à la coordonnée x
     *
     * @return vecteur courant modifié
     */
    public Vec2f subX(int x) {

        this.x -= x;

        return this;
    }

    /**
     * Retrait de valeur à la coordonnée y
     *
     * @param y valeur à retirer à la coordonnée y
     *
     * @return vecteur courant modifié
     */
    public Vec2f subY(int y) {

        this.y -= y;

        return this;
    }


    /**
     * Multiplication du vecteur courant par un facteur
     *
     * @param mult facteur multiplicateur
     *
     * @return vecteur courant modifié
     */
    public Vec2f mult(float mult) {

        this.x *= mult;
        this.y *= mult;

        return this;
    }
    /**
     * Multiplication du vecteur courant par un facteur
     *
     * @param mult facteur multiplicateur
     *
     * @return vecteur courant modifié
     */
    public Vec2f mult(double mult) {

        this.x *= mult;
        this.y *= mult;

        return this;
    }

    /**
     * Multiplication du vecteur courant par un facteur différent sur x et y
     *
     * @param multX facteur multiplicateur de x
     * @param multY facteur multiplicateur de y
     *
     * @return vecteur courant modifié
     */
    public Vec2f mult(float multX, float multY) {

        this.x *= multX;
        this.y *= multY;

        return this;
    }

    /**
     * Multiplication du vecteur courant par un facteur différent sur x et y
     *
     * @param multX facteur multiplicateur de x
     * @param multY facteur multiplicateur de y
     *
     * @return vecteur courant modifié
     */
    public Vec2f mult(double multX, double multY) {

        this.x *= multX;
        this.y *= multY;

        return this;
    }

    /**
     * Création d'un vecteur à partir du vecteur courant et d'un facteur multiplicateur
     *
     * @param mult facteur multiplicateur
     *
     * @return copie du vecteur courant multiplié par 'mult'
     */
    public Vec2f multOut(float mult) {

        return new Vec2f(x * mult, y * mult);
    }

    public Vec2f div(float div) {

        x /= div;
        y /= div;

        return this;
    }

    /**
     * Produit scalaire d'un Vec2d avec le vecteur courant
     *
     * @param other deuxième vecteur du produit scalaire
     *
     * @return le résultat du produit scalaire
     */
    public float dot(Vec2f other) {

        return x * other.x + y * other.y;
    }

    public static float dot(Vec2f a, Vec2f b) {

        return a.x*b.x + a.y*b.y;
    }

    /**
     * Produit vectoriel 2D d'un Vec2d avec le vecteur courant
     *
     * @param other deuxième vecteur du produit vectoriel 2D
     *
     * @return le résultat du produit vectoriel 2D
     */
    public float cross(Vec2f other) {

        return x * other.y - other.x * y;
    }


    /**
     * Multiplie le vecteur courant par -1 // Le rend égal à son opposé
     *
     * @return vecteur courant modifié
     */
    public Vec2f neg() {

        x = -x;
        y = -y;

        return this;
    }

    /**
     * Calcule la norme au carré du Vec2d. Permet d'éviter la racine si inutile, bon pour les perfs.
     *
     * @return norme au carré
     */
    public float squaredMag() {

        return x * x + y * y;
    }

    /**
     * Calcule la norme du Vec2d
     *
     * @return norme
     */
    public float mag() {

        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Normalise le vecteur courant
     *
     * @return vecteur courant normalisé
     */
    // TODO: invsqrt method
    public Vec2f normalize() {

        double mag = Math.sqrt(x * x + y * y);
        if (mag == 0) return null;

        this.x /= mag;
        this.y /= mag;

        return this;
    }

    /**
     * Force la norme du vecteur
     *
     * @param mult nouvelle norme
     *
     * @return vecteur courant de nouvelle norme 'mult'
     */
    // TODO: invsqrt method
    public Vec2f setMag(float mult) {

        double mag = Math.sqrt(x * x + y * y);

        if (mag == 0) return null;

        this.x *= mult / mag;
        this.y *= mult / mag;

        return this;
    }

    /**
     * Réoriente le vecteur dans la direction d'un autre vecteur, conserve la norme
     *
     * @param other nouveau vecteur directeur
     *
     * @return vecteur courant de nouvelle direction et de même norme
     */
    public Vec2f setDir(Vec2f other) {

        float mag = mag();
        this.set(other.x, other.y);
        setMag(mag);

        return this;
    }

    /**
     * Calcule la valeur de l'angle formé entre le vecteur courant et un autre vecteur
     *
     * @param other autre vecteur formant l'angle
     *
     * @return angle(rad) formé par les deux vecteurs
     */
    public float angleWith(Vec2f other) {

        return (float) (Math.atan2(y, x) - Math.atan2(other.y, other.x));
    }

    /**
     * Tourne le vecteur courant d'un angle 'angle' orienté dans le sens direct
     *
     * @param angle angle de rotation
     *
     * @return vecteur courant tourné
     */
    public Vec2f rotate(float angle) {

        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float lx = x;
        x = cos * x - sin * y;
        y = sin * lx + cos * y;

        return this;
    }

    /**
     * Tourne le vecteur courant d'un angle 'angle' orienté dans le sens direct
     *
     * @param angle angle de rotation
     *
     * @return vecteur courant tourné
     */
    public Vec2f rotateOut(float angle) {

        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        return new Vec2f(cos * x - sin * y, sin * x + cos * y);
    }

    /**
     * Calcule la distance entre deux Vec2d si on les considère comme représentant des points
     *
     * @param other autre vecteur
     *
     * @return distance à l'autre vecteur
     */
    public float dist(Vec2f other) {

        float dx = x - other.x;
        float dy = y - other.y;

        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calcule la distance entre deux Vec2d au carré si on les considère comme représentant des points
     *
     * @param other autre vecteur
     *
     * @return distance à l'autre vecteur
     */
    public float sqrdDist(Vec2f other) {

        float dx = x - other.x;
        float dy = y - other.y;

        return dx * dx + dy * dy;
    }

    /**
     * Donne le vecteur nul
     *
     * @return vecteur nul
     */
    public static Vec2f zero() {

        return new Vec2f(0, 0);
    }

    /**
     * Donne une copie de ce vecteur
     *
     * @return copie du vecteur
     */
    public Vec2f copy() {

        return new Vec2f(x, y);
    }

    /**
     * Redéfini les coordonnées du vecteur
     *
     * @param x coordoonée x
     * @param y coordoonée y
     */
    public Vec2f set(float x, float y) {

        this.x = x;
        this.y = y;

        return this;
    }

    public Vec2f set(Vec2f other) {

        this.x = other.x;
        this.y = other.y;

        return this;
    }

    /**
     * Redéfini la coordonnée x du vecteur
     *
     * @param x coordoonée x
     */
    public void setX(float x) {

        this.x = x;
    }

    /**
     * Redéfini la coordonnée y du vecteur
     *
     * @param y coordoonée y
     */
    public void setY(float y) {

        this.y = y;
    }

    /**
     * Redéfini les coordonnées du vecteur
     *
     * @param x coordoonée x
     * @param y coordoonée y
     */
    public void set(double x, double y) {

        this.x = (float) x;
        this.y = (float) y;
    }

    /**
     * Redéfini la coordonnée x du vecteur
     *
     * @param x coordoonée x
     */
    public void setX(double x) {

        this.x = (float) x;
    }

    /**
     * Redéfini la coordonnée y du vecteur
     *
     * @param y coordoonée y
     */
    public void setY(double y) {

        this.y = (float) y;
    }

    public Vec2f nil() {

        x = 0;
        y = 0;

        return this;
    }

    /**
     * Renvoie un Vec2d équivalent au Vec2f courant
     *
     * @return Vec2d avec les mêmes coordonées que le Vec2f courant
     */
    public Vec2d toDouble() {

        return new Vec2d(x, y);
    }

    /**
     * Renvoie la valeur sur z du produit vectoriel entre vecA = (xA, yA, 0) et vecB = (xB, yB, 0)
     *
     * @param vecA premier vecteur (0 sur z)
     * @param vecB deuxieme vecteur (0 sur z)
     *
     * @return vecA ∧ vecB = (0, 0, result)
     */
    public static float cross(Vec2f vecA, Vec2f vecB) {

        return vecA.x * vecB.y - vecB.x * vecA.y;
    }

    /**
     * Renvoie le vecteur résultant du produit vectoriel entre vec = (x, y, 0) et v = (0, 0, s)
     *
     * @param vec vecteur sans valeur sur z
     * @param s   valeur sur z du deuxième vecteur
     *
     * @return vec ∧ v = (x, y, 0) ∧ (0, 0, s) = (y*s, -s*x, 0)
     */
    public static Vec2f cross(Vec2f vec, float s) {

        return new Vec2f(vec.y * s, -s * vec.x);
    }

    /**
     * Renvoie le vecteur résultant du produit vectoriel entre v = (0, 0, s) et vec = (x, y, 0)
     *
     * @param vec vecteur sans valeur sur z
     * @param s   valeur sur z du deuxième vecteur
     *
     * @return v ∧ vec = (0, 0, s) ∧ (x, y, 0) = (-s*y, s*x, 0)
     */
    public static Vec2f cross(float s, Vec2f vec) {

        return new Vec2f(-vec.y * s, s * vec.x);
    }

    public static Vec2f[] arrayOf(int i) {

        Vec2f[] result = new Vec2f[i];
        for (int j = 0 ; j < i; ++j)
            result[j] = Vec2f.zero();

        return result;
    }

    public static Vec2f fromAngle (float angle) {

        // Normalize pas obligatoire mais corrige les erreurs
        return new Vec2f(1, 0).rotate(angle).normalize();
    }

    @Override
    public boolean equals(Object o) {

        return (o instanceof Vec2f && ((Vec2f) o).x == x && ((Vec2f) o).y == y) || (o instanceof Vec2d && (float) ((Vec2d) o).x == x && (float) ((Vec2d) o).y == y);
    }

    public String toString() {

        return "(" + x + ", " + y + ")";
    }
}

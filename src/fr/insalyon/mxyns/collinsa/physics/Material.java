package fr.insalyon.mxyns.collinsa.physics;

import java.awt.Color;

/**
 * Classe représentant un matériau et ses propriétés
 */
public class Material {

    /**
     * Matériaux par défaut
     */
    public static Material DUMMY = new Material(1, 0.7f, 0.2f, 0.1f, Color.blue);
    public static Material STICKY = new Material(1, 0.5f, 1f, 0.9f, new Color(120, 0, 0));
    public static Material BOUNCY = new Material(1, 3f, 0.05f, 0.025f, new Color(0, 120, 0));
    public static Material SLIDY = new Material(1, 0.1f, 0.05f, 0.025f, Color.cyan);

    /**
     * Coefficient de restitution, représente l'élasticité ou la "bounciness" / rebond
     * e = 1 <=> collision élastique
     */
    float restitution;

    /**
     * Coefficient de friction statique, détermine quelle vitesse il faut pour que deux objets se mettent en mouvement
     */
    float staticFriction;

    /**
     * Coefficient de friction dynamique, représente les frottements / l'accroche d'un objet aux autres quand ils sont en mouvement
     */
    float dynamicFriction;

    /**
     * Couleur par défaut d'un objet ayant ce matériau
     */
    Color color;

    /**
     * Densité utilisée pour calculer la masse
     */
    float density;

    public Material(float density, float restitution, float staticFriction, float dynamicFriction, Color color) {

        this.restitution = restitution;
        this.staticFriction = staticFriction;
        this.dynamicFriction = dynamicFriction;
        this.density = density;
        this.color = color;
    }

    /**
     * Renvoie la couleur par défaut du matériau. Peut être modifiée ou remplacée manuellement avec Entity.setColor()
     * @return couleur par défaut du matériau
     */
    public Color getColor() {

        return color;
    }

    /**
     * Renvoie la densité du matériau en kg/m^3
     * @return density
     */
    public float getDensity() {

        return density;
    }

    /**
     * Redéfinit la densité du matériau en kg/m^3.
     * Nécessite d'executer Entity.getInertia()::update pour que les changements soient appliqués à l'entité
     * @param density nouvelle densité en kg/m^3
     */
    public void setDensity(float density) {

        this.density = density;
    }

    /**
     * Renvoie le coefficient de restitution du matériau.
     * < 1 inelastic
     * = 1 elastic
     * > unreal but can be useful
     * @return restitution
     */
    public float getRestitution() {

        return restitution;
    }

    /**
     * Redéfinit la restitution du matériau
     * @param restitution nouvelle restitution
     */
    public void setRestitution(float restitution) {

        this.restitution = restitution;
    }

    /**
     * Méthode de calcul du coefficient de frottement équivalent entre deux matériaux.
     * Pour deux matériaux A et B de friction respective fA et fB on obtient √(fA²+fB²)
     * N'a pas d'explication physique puisque il n'existe aucune formule pour déterminer les coefficients de frottements d'un matériau.
     * @param frictionA coefficient de friction du premier matériau
     * @param frictionB coefficient de friction du deuxième matériau
     * @return moyenne des deux coefficients
     */
    public static float frictionAverage(float frictionA, float frictionB) {

        return (float) Math.sqrt(frictionA*frictionA + frictionB*frictionB);
    }

    /**
     * Renvoie le coefficient de friction statique (~ énergie nécessaire pour qu'un objet se mette en mouvement)
     * @return staticFriction
     */
    public float getStaticFriction() {

        return staticFriction;
    }

    /**
     * Rédefinit le coefficient de friction statique (~ énergie nécessaire pour qu'un objet se mette en mouvement)
     * @param staticFriction nouveau coefficient de frottement statique
     */
    public void setStaticFriction(float staticFriction) {

        this.staticFriction = staticFriction;
    }

    /**
     * Renvoie le coefficient de friction dynamique (frottements lors de déplacements)
     * @return staticFriction
     */
    public float getDynamicFriction() {

        return dynamicFriction;
    }

    /**
     * Rédefinit le coefficient de friction dynamique (frottements lors de déplacements)
     * @param dynamicFriction nouveau coefficient de frottement dynamique
     */
    public void setDynamicFriction(float dynamicFriction) {

        this.dynamicFriction = dynamicFriction;
    }

    /**
     * Renvoie une copie du matériau, permet de ne pas avoir tous les éléments d'un même matériau liés
     * @return copy of the current material
     */
    public Material copy() {

        return new Material(density, restitution, staticFriction, dynamicFriction, color);
    }
}

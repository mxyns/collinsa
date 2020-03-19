package fr.insalyon.mxyns.collinsa.physics;

import java.awt.Color;

/**
 * Classe représentant un matériau et ses propriétés
 */
public class Material {

    public static Material DUMMY = new Material(1, 0.7f, 0.2f, 0.1f, Color.blue);
    public static Material STICKY = new Material(1, 0.5f, 1f, 0.9f, new Color(120, 0, 0));
    public static Material BOUNCY = new Material(1, 3f, 0.05f, 0.025f, new Color(0, 120, 0));

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
     * TODO: inutilisée pour l'instant
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

    public float getDensity() {

        return density;
    }

    public void setDensity(float density) {

        this.density = density;
    }

    public float getRestitution() {

        return restitution;
    }

    public void setRestitution(float restitution) {

        this.restitution = restitution;
    }

    public static float frictionAverage(float frictionA, float frictionB) {

        return (float) Math.sqrt(frictionA*frictionA + frictionB*frictionB);
    }

    public float getStaticFriction() {

        return staticFriction;
    }

    public void setStaticFriction(float staticFriction) {

        this.staticFriction = staticFriction;
    }

    public float getDynamicFriction() {

        return dynamicFriction;
    }

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

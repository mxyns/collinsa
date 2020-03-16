package fr.insalyon.mxyns.collinsa.physics;

import java.awt.Color;

/**
 * Classe représentant un matériau et ses propriétés
 */
public class Material {

    public static Material DUMMY = new Material(1f, Color.blue);

    /**
     * Coefficient de restitution, représente l'élasticité ou la "bounciness" / rebond
     */
    float restitution;

    /**
     * Couleur par défaut d'un objet ayant ce matériau
     */
    Color color;

    /**
     * Densité utilisée pour calculer la masse
     * TODO: inutilisée pour l'instant
     */
    float density;

    public Material(float restitution, Color color) {

        this.restitution = restitution;
        this.color = color;
    }

    /**
     * Renvoie la couleur par défaut du matériau. Peut être modifiée ou remplacée manuellement avec Entity.setColor()
     * @return couleur par défaut du matériau
     */
    public Color getColor() {

        return color;
    }

    public float getRestitution() {

        return restitution;
    }

    public void setRestitution(float restitution) {

        this.restitution = restitution;
    }

    /**
     * Renvoie une copie du matériau, permet de ne pas avoir tous les éléments d'un même matériau liés
     * @return copy of the current material
     */
    public Material copy() {

        return new Material(restitution, color);
    }
}

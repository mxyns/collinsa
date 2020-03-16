package fr.insalyon.mxyns.collinsa.physics;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

/**
 * Classe contenant toutes les informations sur l'inertie d'une entité
 */
public class Inertia {

    /**
     * Masse en kg de l'entité
     */
    private float mass;

    /**
     * Inverse de la masse, doit être souvent calculé, autant le stocker
     */
    private float massInv;

    /**
     * Moment d'inertie de l'objet
     */
    float J;

    public Inertia(Entity entity) {

        setMass(1);
    }

    public float getMass() {

        return mass;
    }

    public void setMass(float mass) {

        if ((this.mass = mass) != 0)
            massInv = 1 / mass;
        else
            massInv = 0;
    }

    public float getMassInv() {

        return massInv;
    }
}

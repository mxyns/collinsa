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
     * Moment d'inertie de l'objet autour d'un axe passant par son centre
     */
    private float J;

    /**
     * Inverse du moment d'inertie de l'objet autour d'un axe passant par son centre
     */
    private float inv_J;

    public Inertia() {

        super();
    }
    public Inertia(Entity entity) {

        setMass(entity.getMaterial().getDensity() * entity.getVolume());

        // utiliser update après. pas faisable à l'initialisation à cause du fonctionnement de computeJ
    }

    public void update(Entity entity) {

        setMass(entity.getMaterial().getDensity() * entity.getVolume());
        setJ(entity.computeJ());
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

    public float getJ() {

        return J;
    }

    public void setJ(float j) {

        if ((this.J = j) != 0)
            inv_J = 1 / j;
        else
            inv_J = 0;
    }

    public float getJInv() {

        return inv_J;
    }

    public void setInvJInv(float inv_J) {

        this.inv_J = inv_J;
    }

    public String toString() {

        return "Inertia[" + "mass=" + mass + ", inv_mass= " + massInv + ", J=" + J + ", inv_J=" + inv_J + "]";
    }
}

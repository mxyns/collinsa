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

    /**
     * Met à jour la masse et le moment d'inertie d'une entité à partir de la densité de son matériau et de ses dimensions actuelles
     * @param entity entité référence pour les calculs
     */
    public void update(Entity entity) {

        setMass(entity.getMaterial().getDensity() * entity.getVolume());
        setJ(entity.computeJ());
    }

    /**
     * Renvoie la masse de l'entité
     * @return mass
     */
    public float getMass() {

        return mass;
    }

    /**
     * Force la valeur de la masse de l'entité associée à cette Inertia. Sera écrasée si update() est appelée
     * @param mass nouvelle masse si mass = 0, invMass = 0 pour une masse infinie
     */
    public void setMass(float mass) {

        if ((this.mass = mass) != 0)
            massInv = 1 / mass;
        else
            massInv = 0;
    }

    /**
     * Renvoie l'inverse de la masse de l'entité
     * @return massInv
     */
    public float getMassInv() {

        return massInv;
    }

    /**
     * Renvoie l'inverse du moment d'inertie autour du centre de rotation de l'entité
     * @return J
     */
    public float getJ() {

        return J;
    }

    /**
     * Force J le moment d'inertie autour du centre de rotation de l'entité. Sera écrasé si update() est appelée
     * @param j si J = 0, invJ = 0 pour une masse infinie
     */
    public void setJ(float j) {

        if ((this.J = j) != 0)
            inv_J = 1 / j;
        else
            inv_J = 0;
    }

    /**
     * Renvoie l'inverse du moment d'inertie
     * @return inv_J
     */
    public float getJInv() {

        return inv_J;
    }

    public String toString() {

        return "Inertia[" + "mass=" + mass + ", inv_mass= " + massInv + ", J=" + J + ", inv_J=" + inv_J + "]";
    }
}

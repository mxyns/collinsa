package fr.insalyon.mxyns.collinsa.threads;

import fr.insalyon.mxyns.collinsa.clocks.Clock;
import fr.insalyon.mxyns.collinsa.clocks.MillisClock;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collider;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

import java.awt.Color;

/**
 * Thread dédié à la mise à jour de la simulation.
 */
public class ProcessingThread extends ClockedThread {

    /**
     *  Physics représentant la simulation associée au Thread de calcul
     */
    private Physics physics;

    /**
     *  Collider représentant le détecteur de collisions associé à la simulation
     */
    private Collider collider;

    /**
     * Nombre de tick par secondes à générer visé par le Thread, s'il y arrive, il se fixe autour.
     * Short car jamais plus grand que 32,767 fps
     */
    private short refreshRate;

    /**
     * Délai de base entre chaque tick en millisecondes, si le calcul prenait un temps de calcul de 0 (ms ou ns selon précision)
     */
    private int baseDelay;

    /**
     * Crée un Thread de calcul à partir d'une simulation (Physics), avec une précision par défaut en milliseconde et un refreshRate de 0
     * @param physics Physics représentant la simulation
     */
    public ProcessingThread(Physics physics) {

        this(physics, new MillisClock());
    }
    /**
     * Crée un Thread de calcul à partir d'une simulation (Physics), et d'une Horloge, avec un refreshRate par défaut de 0
     * @param physics Physics représentant la simulation
     * @param clock horloge dédiée au Thread
     */
    public ProcessingThread(Physics physics, Clock clock) {

        this(physics, clock, 0);
    }

    /**
     * Crée un Thread de calcul à partir d'une simulation (Physics), et d'une Horloge, et d'un refreshRate
     * @param physics Physics représentant la simulation
     * @param clock horloge dédiée au Thread
     * @param refreshRate temps de rafraichissement (délai entre chaque calcul)
     */
    public ProcessingThread(Physics physics, Clock clock, int refreshRate) {

        super(clock, refreshRate);
        this.physics = physics;
        this.collider = physics.getCollider();
        this.refreshRate = (short)refreshRate;
        this.baseDelay = 1000 / refreshRate;
    }

    /**
     * Mise à jour de l'état de la simulation :
     *      Mise à jour des positions, détection de collisions, résolution des collisions, etc...
     * @param elapsedTime temps écoulé (c-à-d le temps dont il faut que la simulation avance)
     */
    // Déclarer deltaTime en dehors de tick permet de l'utiliser ailleurs et de ne pas avoir à re-allouer la mémoire nécessaire à un long
    private long deltaTime;
    @Override
    public void tick(long elapsedTime) {

        // Sélection du temps à utiliser
        deltaTime = physics.isRealtime() ? elapsedTime : physics.getFixedDeltaTime();

        // Test de mise à jour des positions, vitesses, accélérations
            for (Entity entity : physics.getEntities()) {

                // 1ère étape : mettre à jour les éléments
                // Pour le test on bloque les entités en bas de l'écran pour pas qu'elles se tirent
                if (entity.getPos().y <= physics.getHeight() && entity.getPos().y >= 0 && entity.getPos().x >= 0 && entity.getPos().x <= physics.getWidth()) {
                    if (entity.isActivated())
                        entity.updateMillis(deltaTime);
                } else
                    physics.removeEntity(entity);
            }

            for (Entity entity : physics.getEntities())
                // 2ème étape : détection de collisions
                for (Entity target : collider.getNearbyEntities(entity))
                    collider.checkForCollision(entity, target);

        // 3ème étape : résolution des collisions détectées
        if (collider.preciseResolution) {

            // TODO: do some fancy collision time calculations and resolve collisions by time order accounting for simultaneous collisions when time difference is lower than a threshold

        } else // Résout les collisions dans leur ordre de détection qui est aléatoire, rapide mais n'est pas déterministe
            for (Collision coll : collider.getRegisteredCollision()) {

                if (collider.displayCollisionColor) {
                    coll.getSource().setColor(Color.red);
                    coll.getTarget().setColor(Color.red);
                }

                coll.resolve();

                // trigger collision listeners
            }

        //System.out.println("detected " + collider.getRegisteredCollision().size() + " collisions");

            // 4ème étape : on remet à jour les Chunks
            physics.clearChunks();
            collider.clearCollisions();
            physics.spatialHashing();

            // 5ème on régule le délai
            regulateDelay(baseDelay, elapsedTime);
    }

    /**
     * Renvoie la simulation associée au Thread
     * @return physics
     */
    public Physics getPhysics() {

        return physics;
    }

    /**
     * Redéfinit la simulation associée au Thread
     * @param physics nouvelle simulation associée
     */
    public void setPhysics(Physics physics) {

        this.physics = physics;
        this.collider = physics.getCollider();
    }

    /**
     * Redéfinit le taux de rafraîchissement de la simulation (nombre d'images calculées par secondes)
     * @param refreshRate nouveau taux de rafraichissement de la simulation
     */
    public void setRefreshRate(int refreshRate) {

        this.refreshRate = (short)refreshRate;
        this.baseDelay = 1000 / refreshRate;
    }

    /**
     * Donne le taux de rafraîchissement de la simulation (nombre d'images calculées par secondes)
     * @return refreshRate taux de rafraichissement de la simulation
     */
    public int getRefreshRate() {

        return refreshRate;
    }
}
package fr.insalyon.mxyns.collinsa.threads;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.clocks.Clock;
import fr.insalyon.mxyns.collinsa.clocks.MillisClock;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collider;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.forces.Force;
import fr.insalyon.mxyns.collinsa.physics.ticks.Tick;
import fr.insalyon.mxyns.collinsa.utils.monitoring.EntityMonitoring;

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

    public static boolean stepTick = false;

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
        setName("collinsa-processing");
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

        // Le tick 0 est déjà créé à l'initialisation donc on commence par finir le tick précédent et récupérer le nouveau
        Tick readTick = physics.getTickMachine().finished(physics, deltaTime);
        Tick writeTick = physics.getTickMachine().current();

        readTick.entitiesToInsert.forEach( entity -> writeTick.entities.put(entity.uuid, entity));
        readTick.entitiesToRemove.forEach(writeTick.entities::remove);

        physics.spatialHashing(writeTick);

        // Sélection du temps à utiliser
        deltaTime = physics.isRealtime() ? elapsedTime : physics.getFixedDeltaTime();

        physics.totalElapsedTime += deltaTime;

        // TODO : access from a 'Monitoring' instance
        EntityMonitoring entityMonitoring = Collinsa.INSTANCE.getMonitoring().entityMonitoring;

        // Test de mise à jour des positions, vitesses, accélérations
        readTick.entities.forEach( (uuid, readEntity) -> {

            Entity writeEntity = writeTick.entities.get(uuid); // next state of the entity
            if (writeEntity == null) { // Entity must have been removed
                return;
            }

            // 1ère étape : mettre à jour les éléments
            // Pour le test on bloque les entités en bas de l'écran pour pas qu'elles se tirent
            if (readEntity.getPos().y <= physics.getHeight() && readEntity.getPos().y >= 0 && readEntity.getPos().x >= 0 && readEntity.getPos().x <= physics.getWidth()) {
                if (readEntity.isActivated()) {

                    if (writeEntity.update(deltaTime * clock.toSec())) {
                        if (entityMonitoring.isMonitored(readEntity)) { // TODO : fix monitoring, it's possible that it won't work bc now &writeEntity != &readEntity but writeEntity.equals(readEntity) so hashCodes are maybe different
                            entityMonitoring.logScalarInfo(writeEntity, physics.totalElapsedTime);
                            entityMonitoring.logVectorialInfo(writeEntity, physics.totalElapsedTime);
                        }
                    } else
                        physics.removeEntity(writeEntity);

                    if (!readEntity.isKinematic()) {
                        writeEntity.setAcc(0, 0);
                        writeEntity.setAngAcc(0);
                    }
                }
            } else
                physics.removeEntity(writeEntity);
        });

        // 1-bis étape : on applique les forces
        for (Force force : readTick.forces) // TODO : think about changing source & target bc now it is a copy so its not the same lol
            force.apply(readTick);

        readTick.entities.forEach( (uuid, readEntity) -> {

            // 1-ter étape : on applique les forces globales (on le fait ici pour éviter de re-parcourir une deuxième fois la liste des entités
            for (Force globalForce : readTick.globalForces) {
                if (!globalForce.affects(uuid)) { // on évite d'appliquer la force d'un objet sur lui même
                    globalForce.setTarget(writeTick.entities.get(uuid));
                    globalForce.apply(readTick);
                }
            }

            // On met à jour la AABB de l'entité en lecture puisque c'est à partir d'elle qu'on va faire les calculs
            readEntity.update(0);

            // 2ème étape : détection de collisions
            for (Entity target : collider.getNearbyEntities(readTick, readEntity))
                collider.checkForCollision(readEntity, target);
        });

     // 3ème étape : résolution des collisions détectées
        if (collider.preciseResolution) {

            // TODO: do some fancy collision time calculations and resolve collisions by time order accounting for simultaneous collisions when time difference is lower than a threshold

        } else // Résout les collisions dans leur ordre de détection qui est aléatoire (rapide mais n'est pas déterministe)

            for (Collision coll : collider.getRegisteredCollision()) {

                if (collider.displayCollisionColor) {
                    coll.getReference().setColor(Color.red);
                    coll.getIncident().setColor(Color.red);
                }

                // TODO : change way to resolve collision so that modifications are applied to writeTick's entities copies
                coll.resolve(writeTick.entities.get(coll.getReference().uuid), writeTick.entities.get(coll.getIncident().uuid));

                // trigger collision listeners (done in resolve)
            }

            // 4ème étape : on remet à jour les Chunks
            physics.clearChunks(writeTick);
            collider.clearCollisions();
            physics.spatialHashing(writeTick);

            // 5ème on régule le délai pour avoir un nombre de fps constant
            regulateDelay(baseDelay, elapsedTime);

            // TODO : also
            //    - check if changing chunk counts will cause problems or not

        stepTick = false;
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
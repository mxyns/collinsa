package fr.insalyon.mxyns.collinsa.threads;

import fr.insalyon.mxyns.collinsa.clocks.Clock;
import fr.insalyon.mxyns.collinsa.clocks.MillisClock;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

/**
 * Thread dédié à la mise à jour de la simulation.
 */
public class ProcessingThread extends ClockedThread {

    /**
     *  Physics représentant la simulation associée au Thread de calcul
     */
    private Physics physics;

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
    public ProcessingThread(Physics physics, Clock clock, long refreshRate) {

        super(clock, refreshRate);
        this.physics = physics;
    }

    /**
     * Mise à jour de l'état de la simulation :
     *      Mise à jour des positions, détection de collisions, résolution des collisions, etc...
     * @param elapsedTime temps écoulé (c-à-d le temps dont il faut que la simulation avance)
     */
    @Override
    public void tick(long elapsedTime) {

        // Test de mise à jour des positions, vitesses, accélérations
            for (Entity entity : physics.getEntities()) {
                entity.updateMillis(clock.lastElapsed);

                // Pour le test on bloque les entités en bas de l'écran pour pas qu'elles se tirent
                if (entity.getPos().y >= physics.getHeight())
                    interrupt();
            }
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
    }
}
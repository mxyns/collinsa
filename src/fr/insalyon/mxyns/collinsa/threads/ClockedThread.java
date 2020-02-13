package fr.insalyon.mxyns.collinsa.threads;

import fr.insalyon.mxyns.collinsa.clocks.Clock;

/**
 * Thread Boucle avec une horloge intégrée. N'est pas abstract car on pourrait l'instancier, il faudrait par contre redéfinir la méthode tick() lors de l'instanciation
 */
public class ClockedThread extends Thread {

    /**
     * Horloge dédiée au Thread
     * Accès est en package puisque ProcessingThread et RenderingThread doivent pouvoir y accéder. Le protected n'est pas nécessaire puisque Rendering et Processing sont dans le package 'thread'
     */
    Clock clock;

    /**
     * Délai entre chaque exécution
     * Accès est en package car ProcessingThread et RenderingThread doivent pouvoir gérer dynamiquement le délai requis
     */
    long delay;

    /**
     *  Crée un Thread avec un délai nul entre chaque exécution (à utiliser si le la tâche est longue)
     * @param clock holorge à associer au Thread
     */
    public ClockedThread(Clock clock) {

        this(clock, 0);
    }
    /**
     *  Crée un Thread avec un délai 'delay' entre chaque exécution (à utiliser si le la tâche est courte)
     * @param clock holorge à associer au Thread
     * @param delay délai entre chaque exécution
     */
    public ClockedThread(Clock clock, long delay) {

        this.clock = clock;
        this.delay = delay;
    }

    /**
     * Tâche exécutée en boucle à moins que l'état du Thread devienne 'Interrupted', qui est la seule manière propre de stopped un Thread
     */
    @Override
    public void run() {

        // Tant que le Thread n'est pas stoppé
        while(!isInterrupted()) {

            // On patiente le temps d'un delay avant 'tick' car cette méthode peut décider de mettre fin au Thread.
            // On évite alors une ThreadInterruptedException à l'appel de 'sleep' qui se produirait si on appelait 'sleep' après 'tick'
            try {
                if (delay > 0)
                    sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // On met à jour le temps
            clock.read();

            // On exécute la méthode à répéter, en lui indiquant combien de temps s'est écoulé depuis sa dernière exécution
            tick(clock.lastElapsed);
        }
    }

    /**
     * Méthode à exécuter à en boucle. Doit être redéfinie.
     * @param lastElapsed temps écoulé depuis la denière exécution de la méthode
     */
    public void tick(long lastElapsed) {}

    /**
     * Prend soin de stopper la Clock en même temps que le Thread
     */
    @Override
    public void interrupt() {

        clock.stop();
        super.interrupt();
    }

    /**
     * Prend soin de mettre en marche la Clock en même temps que le Thread
     */
    @Override
    public void start() {

        clock.start();
        super.start();
    }

    /**
     * Renvoie la Clock dédié au Thread
     */
    public Clock getClock() {

        return clock;
    }

    /**
     * Associe une nouvelle Clock au Thread. Prend soin de la mettre dans le même état que l'ancienne (running ou non)
     * Peu recommandé mais peut permettre de passer d'une MillisClock à NanoClock et inversement
     */
    public void setClock(Clock clock) {

        if (this.clock.isRunning())
            clock.start();
        else
            clock.stop();

        this.clock = clock;
    }
}

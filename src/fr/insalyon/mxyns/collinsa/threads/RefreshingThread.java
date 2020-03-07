package fr.insalyon.mxyns.collinsa.threads;

import fr.insalyon.mxyns.collinsa.clocks.Clock;
import fr.insalyon.mxyns.collinsa.clocks.MillisClock;

import java.awt.Component;

/**
 * Thread dédié au rendu (à la mise à jour de l'affichage)
 */
public class RefreshingThread extends ClockedThread {

    /**
     * Composant associé au Thread (celui qui sera repaint périodiquement)
     */
    private Component componentToRefresh;

    /**
     * Nombre d'images par secondes visé par le Thread, s'il y arrive, il se fixe autour.
     * Short car jamais plus grand que 32,767 fps
     */
    private short refreshRate;

    /**
     * Délai de base entre chaque rafraichissement en millisecondes, si le rafraichissement prenait un temps de calcul de 0 (ms ou ns selon précision)
     */
    private int baseDelay;

    /**
     * Crée un Thread de rafraichissement à partir d'un composant à rafraichir, avec une précision par défaut en milliseconde et un framerate de 60
     * @param componentToRefresh composant à rafraîchir / repaint
     */
    public RefreshingThread(Component componentToRefresh) {

        this(componentToRefresh, new MillisClock(), 60);
    }
    /**
     * Crée un Thread de rafraichissement à partir d'un composant à rafraichir, un framerate, avec une précision par défaut en milliseconde
     * @param componentToRefresh composant à rafraîchir / repaint
     * @param refreshRate nombre d'image par secondes que le Thread essayera d'atteindre
     */
    public RefreshingThread(Component componentToRefresh, int refreshRate) {

        this(componentToRefresh, new MillisClock(), refreshRate);
    }
    /**
     * Crée un Thread de rafraichissement à partir d'un composant à rafraichir et d'une Clock, avec un framerate par défaut de 60
     * @param componentToRefresh composant à rafraîchir / repaint
     * @param clock horloge dédiée au Thread
     */
    public RefreshingThread(Component componentToRefresh, Clock clock) {

        this(componentToRefresh, clock, 60);
    }
    /**
     * Crée un Thread de rafraichissement à partir d'un composant à rafraichir, d'une Clock et d'un framerate
     * @param componentToRefresh composant à rafraîchir / repaint
     * @param clock horloge dédiée au Thread
     * @param refreshRate nombre d'image par secondes que le Thread essayera d'atteindre
     */
    public RefreshingThread(Component componentToRefresh, Clock clock, int refreshRate) {

        super(clock, 1000 / refreshRate);

        this.componentToRefresh = componentToRefresh;
        this.refreshRate = (short) refreshRate;
        this.baseDelay = 1000 / refreshRate;
        this.delay = baseDelay;
    }

    /**
     * Mise à jour du composant.
     * On recalcule de delay nécessaire pour essayer d'avoir au minimum le framerate voulu
     * @param elapsedTime temps écoulé depuis le dernier affichage
     */
    @Override
    public void tick(long elapsedTime) {

        refresh();
        regulateDelay(baseDelay, elapsedTime);
    }

    /**
     * Redéfinit le composant à rafraichir
     * @param componentToRefresh nouveau renderer
     */
    public void setRefreshedComponent(Component componentToRefresh) {

        this.componentToRefresh = componentToRefresh;
    }

    /**
     * Essaye de mettre à jour l'affichage si le rendu est actif
     */
    private void refresh() {

        if (!isInterrupted())
            componentToRefresh.repaint();
    }

    /**
     * Force le rendu, que le Thread soit actif ou non, n'informe pas la Clock du rendu lorsqu'il est extérieur.
     * La frame n'est donc pas comptée dans les frames rendues lors de la dernière seconde
     */
    private void forceRefreshNoClock() {

        componentToRefresh.repaint();
    }

    /**
     * Essaye de forcer la mise à jour de l'affichage que le Thread soit actif ou pas
     */
    public void forceRefresh() {

        componentToRefresh.repaint();
        clock.read();
    }

    /**
     * Donne le refreshRate de le Thread de rendu va essayer d'atteindre, s'il y arrive, il se fixe autour.
     * @return refreshRate
     */
    public short getRefreshRate() {

        return refreshRate;
    }

    /**
     * Redéfinit le refreshRate visé par le Thread
     * @param refreshRate nouveau framerate visé
     */
    public void setRefreshRate(int refreshRate) {

        this.refreshRate = (short) refreshRate;
        this.baseDelay = 1000 / refreshRate;
    }
}

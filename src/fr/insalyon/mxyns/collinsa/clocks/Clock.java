package fr.insalyon.mxyns.collinsa.clocks;

/**
 * Horloge permettant d'accèder au temps écoulé lors de la simulation ou du rendu, permet de prendre en compte la vitesse d'écoulement du temps
 */
public abstract class Clock {

    /*
     *  - Les temps sont de type long car vu qu'ils sont en millisecondes ils peuvenet excéder la valeur maximale des Integer
     *  - Leur accès est en package puisque MillisColock et NanoClock doivent pouvoir y accéder. Le protected n'est pas nécessaire puisque Millis et Nano sont dans le package 'clocks'
     */

    /**
     * Temps actuel (au moment de la lecture)
     * Pas nécessaire mais rend la Clock plus précise car elle prend en compte le temps acquisition de l'heure et le temps de calcul du timeElapsed qui est non-négligeable en ns
     */
    long currentTime;

    /**
     * Temps enregistré après la dernière lecture
     */
    long lastTime;

    /**
     * Temps séparant les deux dernières mesures (la mesure actuelle et la précédente)
     */
    public long lastElapsed;

    /**
     * Permet de s'avoir si la Clock est active (ne fait rien en soit, sa valeur est définie mais jamais utilisée)
     */
    boolean running;


    /**
     * Constructeur par défaut, permet de créer des Clock à partir de rien.
     * Nécessaire pour les classes filles
     */
    Clock() {}

    /**
     * Permet de créer un doublon d'une Clock. Sert à transformer une MillisClock en NanoClock et inversement
     * @param other holorge à copier
     */
    public Clock(Clock other) {

        this.running = other.isRunning();
        this.currentTime = other.getCurrentTime();
        this.lastTime = other.getLastTime();
        this.lastElapsed = other.getLastElapsed();
    }

    /**
     * Execute une lecture de temps
     * @return timeElapsed entre cette mesure et la précédente
     */
    public abstract long read();

    /**
     * Démarre la Clock (initialise les temps)
     * @return start time (or -1 if already running)
     */
    public abstract long start();

    /**
     * Stoppe la Clock et enregistre l'heure de fin
     * @return timeElapsed (or -1 if not running)
     */
    public abstract long stop();

    /**
     * Donne depuis combien de temps la Clock est stoppée
     * @return durée d'arrêt de la Clock
     */
    public abstract long stoppedFor();

    /**
     * Donne le dernier temps mesuré
     * @return lastTime
     */
    public long getLastTime() {

        return lastTime;
    }

    /**
     * Donne le temps actuel lors de la dernière mesure
     * @return cuurentTime
     */
    public long getCurrentTime() {

        return currentTime;
    }

    /**
     * Donne le dernier intervalle de temps renvoyé
     * @return lastElapsed
     */
    public long getLastElapsed() {

        return lastElapsed;
    }

    /**
     * Renvoie l'état de la Clock (en marche ou stoppée)
     * @return running
     */
    public boolean isRunning() {

        return running;
    }
}

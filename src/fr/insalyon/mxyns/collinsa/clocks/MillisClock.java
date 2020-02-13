package fr.insalyon.mxyns.collinsa.clocks;

/**
 * Redéfinition d'une Clock ayant une précision d'une millisecondes (1e-3 sec ou 1e+6 ns)
 */
public class MillisClock extends Clock {

    @Override
    public long read() {

        lastElapsed = (currentTime = System.currentTimeMillis()) - lastTime;
        lastTime = System.currentTimeMillis();

        return lastElapsed;
    }

    @Override
    public long start() {

        if (!running) {
            this.running = true;
            currentTime = System.currentTimeMillis();
            lastTime = System.currentTimeMillis();
            return currentTime;
        }

        return -1;
    }

    @Override
    public long stop() {

        if (running) {
            this.running = false;
            currentTime = System.currentTimeMillis();

            return lastElapsed;
        }

        return -1;
    }

    @Override
    public long stoppedFor() {

        return lastTime - System.currentTimeMillis();
    }
}

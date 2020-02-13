package fr.insalyon.mxyns.collinsa.clocks;


/**
 * Redéfinition d'une Clock ayant une précision d'une nanosecondes (1e-9 sec ou 1e-6 ms)
 */
public class NanoClock extends Clock {

    @Override
    public long read() {

        lastElapsed = (currentTime = System.nanoTime()) - lastTime;
        lastTime = System.nanoTime();

        return lastElapsed;
    }

    @Override
    public long start() {

        if (!running) {
            this.running = true;
            currentTime = System.nanoTime();
            lastTime = System.nanoTime();
            return currentTime;
        }

        return -1;
    }

    @Override
    public long stop() {

        if (running) {
            this.running = false;
            currentTime = System.nanoTime();

            return lastElapsed;
        }

        return -1;
    }

    @Override
    public long stoppedFor() {

        return lastElapsed - System.nanoTime();
    }
}

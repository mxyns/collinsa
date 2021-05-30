package fr.insalyon.mxyns.collinsa.threads;

import fr.insalyon.mxyns.collinsa.clocks.Clock;
import fr.insalyon.mxyns.collinsa.clocks.MillisClock;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.render.Renderer;

/**
 * Thread dédié au rendu (à la création d'images à afficher)
 */
public class RenderingThread extends ClockedThread {

    /**
     * Renderer associé (celui utilisé pour le rendu par ce Thread)
     */
    private Renderer renderer;

    /**
     *  Physics représentant la simulation associée au Thread de rendu
     *  Inutile pour l'instant, le restera peut être par la suite. Il reste là en attendant
     */
    private Physics physics;

    /**
     * Nombre d'images par secondes visé par le Thread, s'il y arrive, il se fixe autour.
     * Short car jamais plus grand que 32,767 fps
     */
    private short framerate;

    /**
     * Délai de base entre chaque frame en millisecondes, si le rendu prenait un temps de calcul de 0 (ms ou ns selon précision)
     */
    private int baseDelay;

    /**
     * Crée un Thread de rendu à partir d'une simulation (Physics), et d'un Renderer, avec une précision par défaut en milliseconde et un framerate de 60
     * @param physics Physics représentant la simulation
     * @param renderer Renderer utilisé pour le rendu
     */
    public RenderingThread(Physics physics, Renderer renderer) {

        this(physics, new MillisClock(), renderer, 60);
    }
    /**
     * Crée un Thread de rendu à partir d'une simulation (Physics), et d'un Renderer, un framerate, avec une précision par défaut en milliseconde
     * @param physics Physics représentant la simulation
     * @param renderer Renderer utilisé pour le rendu
     * @param framerate nombre d'image par secondes que le Thread essayera d'atteindre
     */
    public RenderingThread(Physics physics, Renderer renderer, int framerate) {

        this(physics, new MillisClock(), renderer, framerate);
    }
    /**
     * Crée un Thread de rendu à partir d'une simulation (Physics), et d'un Renderer et d'une Clock, avec un framerate par défaut de 60
     * @param physics Physics représentant la simulation
     * @param renderer Renderer utilisé pour le rendu
     * @param clock horloge dédiée au Thread
     */
    public RenderingThread(Physics physics, Clock clock,  Renderer renderer) {

        this(physics, clock, renderer, 60);
    }
    /**
     * Crée un Thread de rendu à partir d'une simulation (Physics), et d'un Renderer, d'une Clock et d'un framerate
     * @param physics Physics représentant la simulation
     * @param renderer Renderer utilisé pour le rendu
     * @param clock horloge dédiée au Thread
     * @param framerate nombre d'image par secondes que le Thread essayera d'atteindre
     */
    public RenderingThread(Physics physics, Clock clock, Renderer renderer, int framerate) {

        super(clock, 1000 / framerate);

        this.physics = physics;
        this.renderer = renderer;
        this.framerate = (short)framerate;
        this.baseDelay = 1000 / framerate;
        this.delay = baseDelay;

        setName("collinsa-rendering");
    }

    /**
     * Rendu, création d'une image.
     * On recalcule de delay nécessaire pour essayer d'avoir au minimum le framerate voulu
     * @param elapsedTime temps écoulé depuis le dernier affichage
     */
    @Override
    public void tick(long elapsedTime) {

        renderNoClock();
        regulateDelay(baseDelay, elapsedTime);
    }

    /**
     * Redéfinit le Renderer utilisé pour le rendu
     * @param renderer nouveau renderer
     */
    public void setRenderer(Renderer renderer) {

        this.renderer = renderer;
    }

    /**
     * Essaye de mettre à jour l'affichage si le rendu est actif
     */
    private void render() {

        if (!isInterrupted())
            renderer.render(physics);
    }

    /**
     * Force le rendu, que le Thread soit actif ou non, n'informe pas la Clock du rendu lorsque la méthode est appelée de l'extérieur.
     * La frame n'est donc pas comptée dans les frames rendues lors de la dernière seconde
     */
    private void renderNoClock() {

        renderer.render(physics);
    }

    /**
     * Essaye de forcer la mise à jour de l'affichage que le rendu soit actif ou pas
     */
    public void forceRender() {

        renderer.render(physics);
        clock.read();
    }

    /**
     * Donne le framerate de le Thread de rendu va essayer d'atteindre, s'il y arrive, il se fixe autour.
     * @return framerate
     */
    public short getFramerate() {

        return framerate;
    }

    /**
     * Redéfinit le framerate visé par le Thread
     * @param framerate nouveau framerate visé
     */
    public void setFramerate(int framerate) {

        this.framerate = (short)framerate;
        this.baseDelay = 1000 / framerate;
    }
}

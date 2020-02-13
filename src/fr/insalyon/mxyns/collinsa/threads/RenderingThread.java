package fr.insalyon.mxyns.collinsa.threads;

import fr.insalyon.mxyns.collinsa.clocks.Clock;
import fr.insalyon.mxyns.collinsa.clocks.MillisClock;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.render.Renderer;

/**
 * Thread dédié au rendu (à la mise à jour de l'affichage)
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
     * Short car jamais plus quand que 32,767 fps
     */
    private short framerate;

    /**
     * Délai de base entre chaque frame en millisecondes, si la rendu prenait un temps de calcul de 0 (ms ou ns selon précision)
     */
    private int baseDelay;


    /**
     * Crée un Thread de rendu à partir d'une simulation (Physics), et d'un Renderer, avec une précision par défaut en milliseconde et un framerate de 60
     * @param physics Physics représentant la simulation
     * @param renderer Renderer utilisé pour le rendu
     */
    public RenderingThread(Physics physics, Renderer renderer) {

        this(physics, new MillisClock(), renderer, (short)60);
    }
    /**
     * Crée un Thread de rendu à partir d'une simulation (Physics), et d'un Renderer, un framerate, avec une précision par défaut en milliseconde
     * @param physics Physics représentant la simulation
     * @param renderer Renderer utilisé pour le rendu
     * @param framerate nombre d'image par secondes que le Thread essayera d'atteindre
     */
    public RenderingThread(Physics physics, Renderer renderer, short framerate) {

        this(physics, new MillisClock(), renderer, framerate);
    }
    /**
     * Crée un Thread de rendu à partir d'une simulation (Physics), et d'un Renderer et d'une Clock, avec un framerate par défaut de 60
     * @param physics Physics représentant la simulation
     * @param renderer Renderer utilisé pour le rendu
     * @param clock horloge dédiée au Thread
     */
    public RenderingThread(Physics physics, Clock clock,  Renderer renderer) {

        this(physics, clock, renderer, (short)60);
    }
    /**
     * Crée un Thread de rendu à partir d'une simulation (Physics), et d'un Renderer, d'une Clock et d'un framerate
     * @param physics Physics représentant la simulation
     * @param renderer Renderer utilisé pour le rendu
     * @param clock horloge dédiée au Thread
     * @param framerate nombre d'image par secondes que le Thread essayera d'atteindre
     */
    public RenderingThread(Physics physics, Clock clock, Renderer renderer, short framerate) {

        super(clock, (int)(1000.0f / framerate));

        this.physics = physics;
        this.renderer = renderer;
        this.framerate = framerate;
        this.baseDelay = 1000 / framerate;
        this.delay = baseDelay;
    }

    /**
     * Rendu, mise à jour de l'affichage.
     * On recalcule de delay nécessaire pour essayer d'avoir au minimum le framerate voulu
     * @param lastElapsed temps écoulé depuis le dernier affichage
     */
    @Override
    public void tick(long lastElapsed) {

        forceRender();

        // Le délai nécessaire à la prochaine frame pour garder un framerate constant vaut :
        // délaiInitial - dernierTempsDeRendu = délaiInitial - (dernierTempsTotalDeBoucle - délaiAppliqué(à la frame précédénte)
        // Soit on prend le délai calculé, soit si il est négatif on prend 0
        // Ensuite on choisit la plus petite valeur entre le délai de base et la valeur calculée car on préfère avoir plus de 60 que moins (on ne peut pas prédire combien de temps prendra le rendu de la prochaine frame donc autant prendre large)
        delay = Math.min(Math.max(0, baseDelay - clock.lastElapsed + delay), baseDelay);
    }

    /**
     * Redéfinit le Renderer utilisé pour le rendu
     * @param renderer nouveau renderer
     */
    public void setRenderer(Renderer renderer) {

        this.renderer = renderer;
    }

    /**
     * Essaye de forcer la mise à jour de l'affichage si le rendu est actif
     */
    private void render() {

        if (!isInterrupted())
            renderer.getDestination().repaint();
    }

    /**
     * Force le rendu, que le Thread soit actif ou non, n'informe pas la Clock du rendu lorsqu'il est extérieur.
     * La frame n'est donc pas comptée dans les frames rendues lors de la dernière seconde
     */
    public void forceRender() {

        renderer.getDestination().repaint();
        // TODO: manage clock time reset
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
    public void setFramerate(short framerate) {

        this.framerate = framerate;
    }
}

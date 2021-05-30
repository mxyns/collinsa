package fr.insalyon.mxyns.collinsa.physics.forces;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.ticks.Tick;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.Utils;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Force d'un ressort reliant deux objets
 */
public class Spring extends Force {

    /**
     * SPRING_CONSTANT : constante de raideur du ressort
     * REST_LENGTH : longueur au repos (à l'équilibre, sans forces appliquées) du ressort
     */
    public final double SPRING_CONSTANT, REST_LENGTH;

    public Spring(Entity source, Entity target, double springConstant, double restLength) {

        this.source = source;
        this.target = target;

        SPRING_CONSTANT = springConstant;
        REST_LENGTH = restLength;
    }

    /**
     * @return F = SPRING_CONSTANT * (longueur du ressort - REST_LENGTH)
     * @param readTick
     */
    @Override
    protected Vec2d computeValue(Tick readTick) {

        Vec2d distanceVector = source.getPos().toDouble().add(toSourceApplicationPoint).sub(target.getPos().toDouble().add(toTargetApplicationPoint));
        return distanceVector.setMag(- SPRING_CONSTANT * (REST_LENGTH - distanceVector.mag()));
    }

    /**
     * On trace les forces avec une couleur qui dépend de la tension dans le ressort puis on ajoute par dessus le visuel du ressort : une ligne pointillée avec une taille de pointillés qui dépend de la tension dans le ressort
     * On essaye de choisir des couleurs pour que ce soit pas trop moche mais c'est pas une très grande réussite
     *
     * @param renderer renderer utilisé pour le rendu
     * @param g graphics associé au panel pour dessin
     */
    @Override
    public void render(Renderer renderer, Graphics2D g) {

        if (lastValue == null)
            return;

        renderColor = Utils.lerpColor(Color.green, Color.red, .25 * (1 + lastValue.mag() / (SPRING_CONSTANT * REST_LENGTH)) + .25);
        super.render(renderer, g);

        renderer.renderSpring(source.getPos().copy().add(toSourceApplicationPoint.x, toSourceApplicationPoint.y), target.getPos().copy().add(toTargetApplicationPoint.x, toTargetApplicationPoint.y), lastValue.mag(), SPRING_CONSTANT, REST_LENGTH, g);
    }

    @Override
    public Spring copy() {

        return new Spring(this.source, this.target, this.SPRING_CONSTANT, this.REST_LENGTH);
    }
}

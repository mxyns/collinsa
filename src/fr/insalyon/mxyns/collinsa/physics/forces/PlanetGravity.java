package fr.insalyon.mxyns.collinsa.physics.forces;

import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.ticks.Tick;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;

import java.awt.Graphics2D;

public class PlanetGravity extends Force {

    /**
     * Intensité de la gravité sur Terre
     */
    private static final double EARTH_GRAVITY = 9.807;

    /**
     * Facteur multiplicateur d'intensité
     */
    public double gFactor;

    /**+
     * Crée une force verticale vers le bas valant gFactor * EARTH_GRAVITY * m(target)
     * @param gFactor multiplicateur de l'intensité de la pesanteur
     *    ~ 0,165 pour la Lune
     *    ~ 0,3784 pour Mars
     *    ~ 2,52778627510 pour Jupiter
     */
    public PlanetGravity(double gFactor) {

        this.gFactor = gFactor;
    }
    public PlanetGravity(Entity target, double gFactor) {

        this.target = target;
        this.gFactor = gFactor;
    }

    /**
     * S'applique seulement à target donc on simplifie la méthode
     * @return true si appliquée
     * @param readTick
     */
    @Override
    public boolean apply(Tick readTick) {

        if (target == null) return false;

        if (target.getCollisionType() == Collision.CollisionType.CLASSIC)
            applyForce(target, lastValue = computeValue(readTick));

        return true;
    }

    /**
     * @return F = gFactor * EARTH_GRAVITY * m(target) * Vec2f(0, 1) (vers le bas)
     * @param readTick
     */
    @Override
    protected Vec2d computeValue(Tick readTick) {

        return new Vec2d(0, EARTH_GRAVITY * gFactor * target.getInertia().getMass());
    }

    /**
     * On affiche la force seulement sur le target puisque source est tjr null
     */
    @Override
    public void render(Renderer renderer, Graphics2D g) {

        if (lastValue == null)
            return;

        renderer.renderVector(target.getPos().copy().add(toTargetApplicationPoint.x, toTargetApplicationPoint.y), lastValue, renderer.getForceFactor(), renderColor, g);
    }

    /**
     * Vaut toujours 0 car s'applique au centre de masse donc GM vaut 0
     * @return 0
     */
    @Override
    protected double computeMoment(Tick readTick, Vec2d GM, Vec2d value) {

        return 0;
    }

    @Override
    public PlanetGravity copy() {

        return this.target == null ? new PlanetGravity(gFactor) : new PlanetGravity(this.target, gFactor);
    }
}

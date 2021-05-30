package fr.insalyon.mxyns.collinsa.physics.forces;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.ticks.Tick;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;

/**
 * Force de Gravité entre deux entités de masse non-nulle (simplifiée)
 */
public class Gravity extends Force {

    /**
     * Constante gravitationnelle
     */
    private static final double GRAVITATIONAL_CONSTANT = 6.67408 * 1e-11;

    /**
     * By convention, source should be the heaviest of the two entities but it really doesn't matter
     * @param source
     * @param target
     */
    public Gravity(Entity source, Entity target) {

        this.source = source;
        this.target = target;
    }

    /**
     * Valeur de la force de gravité
     *
     * @return G * m(source) * m(target) / dist(source, target)² * Vec2d(target -> source)
     * @param readTick tick utilisé pour les calculs
     */
    @Override
    protected Vec2d computeValue(Tick readTick) {

        Entity source = readTick.entities.get(this.source.uuid);
        Entity target = readTick.entities.get(this.target.uuid);
        return source.getPos().toDouble().sub(target.getPos().x, target.getPos().y).setMag(GRAVITATIONAL_CONSTANT * target.getInertia().getMass() * source.getInertia().getMass() / target.getPos().sqrdDist(source.getPos()));
    }

    /**
     * Moment toujours nul puisqu'elle s'applique au centre de masse
     *
     * @return 0
     */
    @Override
    protected double computeMoment(Tick readTick, Vec2d GM, Vec2d value) {

        return 0;
    }

    @Override
    public Gravity copy() {

        return new Gravity(this.source, this.target);
    }
}

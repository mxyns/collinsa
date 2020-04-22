package fr.insalyon.mxyns.collinsa.physics.forces;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;

public class Gravity extends Force {

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

    @Override
    protected Vec2d computeValue() {

        return source.getPos().toDouble().sub(target.getPos().x, target.getPos().y).setMag(GRAVITATIONAL_CONSTANT * target.getInertia().getMass() * source.getInertia().getMass() / target.getPos().sqrdDist(source.getPos()));
    }

    @Override
    protected double computeMoment(Vec2d GM, Vec2d value) {

        return 0;
    }
}

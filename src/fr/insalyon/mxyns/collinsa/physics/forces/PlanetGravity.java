package fr.insalyon.mxyns.collinsa.physics.forces;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;

public class PlanetGravity extends Force {

    private static final double EARTH_GRAVITY = 9.807;
    public double gFactor = 1;

    public PlanetGravity(double gFactor) {

        this.gFactor = gFactor;
    }
    public PlanetGravity(Entity target, double gFactor) {

        this.target = target;
        this.gFactor = gFactor;
    }

    @Override
    public boolean apply() {

        if (!target.isKinematic())
            applyForce(target, computeValue());

        return true;
    }

    @Override
    protected Vec2d computeValue() {

        return new Vec2d(0, EARTH_GRAVITY * gFactor * target.getInertia().getMass());
    }

    @Override
    protected double computeMoment(Vec2d GM, Vec2d value) {

        return 0;
    }
}

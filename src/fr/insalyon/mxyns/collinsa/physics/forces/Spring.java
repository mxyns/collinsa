package fr.insalyon.mxyns.collinsa.physics.forces;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;

public class Spring extends Force {

    public final double SPRING_CONSTANT, REST_LENGTH;

    public Spring(Entity source, Entity target, double springConstant, double restLength) {

        this.source = source;
        this.target = target;

        SPRING_CONSTANT = springConstant;
        REST_LENGTH = restLength;
    }

    @Override
    protected Vec2d computeValue() {

        Vec2d distanceVector = source.getPos().toDouble().sub(target.getPos().x, target.getPos().y);
        return distanceVector.setMag(- SPRING_CONSTANT * (REST_LENGTH - distanceVector.mag()));
    }

    @Override
    protected double computeMoment(Vec2d GM, Vec2d value) {

        return 0;
    }
}

package fr.insalyon.mxyns.collinsa.physics.forces;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;

public abstract class Force {

    protected Entity target = null, source = null;
    protected Vec2d toTargetApplicationPoint, toSourceApplicationPoint;

    protected abstract Vec2d computeValue();

    protected double computeMoment(Vec2d GM, Vec2d value) {

        return Vec2d.cross(GM, value);
    }

    public boolean apply() {

        if (target == null || (target.isKinematic() && source.isKinematic())) return false;

        Vec2d force = computeValue();

        if (!target.isKinematic()) {

            applyForce(target, force);
            applyMoment(target, computeMoment(toTargetApplicationPoint, force));
        }

        if (source != null && !source.isKinematic()) {

            applyForce(source, force.neg());
            applyMoment(source, computeMoment(toSourceApplicationPoint, force));
        }

        return true;
    }

    public static void applyForce(Entity entity, Vec2d force) {

        entity.getAcc().add(force, entity.getInertia().getMassInv());
    }

    public static void applyMoment(Entity entity, double moment) {

        entity.setAngAcc((float) (entity.getAngVel() + moment * entity.getInertia().getJInv()));
    }

    public void setTarget(Entity entity) {

        target = entity;
    }
}

package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.function.Consumer;

import static fr.insalyon.mxyns.collinsa.physics.collisions.Collision.CollisionType.IGNORE;

public class EntityEmitter extends Entity {

    public final Entity body;
    public final Physics physics;
    public final ArrayList<Entity> emitted = new ArrayList<>();
    public Consumer<Entity> onEmit = null;
    public float range;
    private double stack = 0;
    public double delay = 1;

    // if false will emit all entities in #this.emitted
    public boolean randomEmit = true;

    public EntityEmitter(Physics physics, Entity body) {

        super(body.pos);

        if (body instanceof EntityEmitter) throw new IllegalArgumentException();

        this.setCollisionType(IGNORE);

        this.aabb.setWidth(0);
        this.aabb.setHeight(0);
        this.aabb.setX(Float.NEGATIVE_INFINITY);
        this.aabb.setY(Float.NEGATIVE_INFINITY);

        this.body = body;
        this.physics = physics;
        this.pos = body.pos;
    }

    private void emit(Entity entity) {

        physics.addEntity(entity);
        entity.pos.set(body.pos);

        if (range > 0)
            entity.pos.add(Vec2f.fromAngle((float) (Math.random() * 2 * Math.PI)), Math.random() * range);

        if (onEmit != null)
            onEmit.accept(entity);
    }

    @Override
    public boolean update(double secs) {

        body.update(secs);

        if (emitted.isEmpty() || (stack += secs) < delay)
            return true;

        stack = 0;
        if (randomEmit)
            emit(emitted.get((int) (Math.random() * emitted.size())).copy());
        else
            for (Entity entity : emitted)
                emit(entity);

        return true;
    }

    @Override
    public double getMaximumSize() {

        if (body == null)
            return 0;

        return body.getMaximumSize();
    }

    @Override
    public float computeJ() {

        if (body == null)
            return 0;

        return body.computeJ();
    }

    @Override
    public float getVolume() {

        if (body == null)
            return 0;

        return body.getVolume();
    }

    @Override
    public void updateAABB() {

        body.updateAABB();
    }

    @Override
    public short cardinal() {

        return body.cardinal();
    }

    @Override
    public void render(Renderer renderer, Graphics2D g) {

        body.render(renderer, g);
    }

    @Override
    public Entity copy() {

        EntityEmitter copy = new EntityEmitter(physics, body.copy());
        copyTo(copy);
        copy.emitted.addAll(emitted);
        copy.stack = stack;
        copy.delay = delay;
        copy.randomEmit = randomEmit;

        return copy;
    }
}

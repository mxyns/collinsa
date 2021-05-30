package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.entities.EntityEmitter;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Color;

public class Presets_Tests extends Preset {

    @Override
    public void setup(String[] args, Collinsa collinsa) {

        Physics physics = collinsa.getPhysics();

        Circle circle = new Circle(physics.getCenterPos(), 50);
        circle.setFillColor(null);
        circle.setCollisionType(Collision.CollisionType.IGNORE);

        EntityEmitter emitter = new EntityEmitter(physics, circle);
        Entity toEmit = new Rect(new Vec2f(10, 10), new Vec2f(20, 50));
        toEmit.setAngVel(5);
        toEmit = new Circle(0,0,10);
        toEmit.lifespan = 10;

        circle.setOutlineColor(Color.white);

        emitter.emitted.add(toEmit);
        //emitter.emitted.add(new Rect(0,0, 10, 20));
        emitter.onEmit = e -> {
            if (e instanceof Circle)
                ((Circle)e).setR((float) (10 * Math.random()));
            else
                e.setAngVel((float) (Math.random() * 10));
            e.getVel().set(Vec2f.fromAngle((float) (Math.random() * 2 * Math.PI)).mult(Math.random() * 100));

        };
        emitter.range = 10;
        emitter.delay = 1e-2;

        physics.placeEntity(circle);
        physics.placeEntity(emitter);
    }
}

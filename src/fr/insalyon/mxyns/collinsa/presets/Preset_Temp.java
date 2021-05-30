package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Polygon;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

public class Preset_Temp extends Preset {

    @Override
    public void setup(String[] args, Collinsa collinsa) {


        Physics physics = collinsa.getPhysics();

        Circle circle = new Circle(physics.getCenterPos(), 50);
        circle.setFillColor(null);
        circle.setCollisionType(Collision.CollisionType.CLASSIC);

        Circle circle2 = new Circle(physics.getCenterPos().mult(1.01f), 50);
        circle2.setFillColor(null);
        circle2.setCollisionType(Collision.CollisionType.CLASSIC);

        Polygon r = new Rect(physics.getCenterPos().mult(1.01f), new Vec2f(100, 200));


        physics.insertEntity(circle);
        physics.insertEntity(circle2);
        physics.insertEntity(r);
    }
}

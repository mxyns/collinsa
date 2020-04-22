package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.entities.Polygon;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.physics.forces.Gravity;
import fr.insalyon.mxyns.collinsa.physics.forces.PlanetGravity;
import fr.insalyon.mxyns.collinsa.physics.forces.Spring;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

public class Preset_Force extends Preset {

    @Override
    public void setup(String[] args, Collinsa collinsa) {

        Physics physics = collinsa.getPhysics();

        Circle balle1 = new Circle(new Vec2f(350, 300), 50);
        Circle balle2 = new Circle(new Vec2f(550, 300), 10);

        balle2.setVel(0, 200);
        balle1.getInertia().setMass(1e17f);
        System.out.println("balle1 inertia " + balle1.getInertia());

        Polygon balle3 = new Rect(new Vec2f(800, 300), new Vec2f(100, 100));
        Entity balle4 = new Circle(new Vec2f(900, 300), 20);
        balle4.getInertia().setMass(10);
        balle3.setCollisionType(Collision.CollisionType.KINEMATIC);

        physics.addEntity(balle1);
        physics.addEntity(balle2);
        physics.addEntity(balle3);
        physics.addEntity(balle4);
        physics.forces.add(new Gravity(balle2, balle1));
        physics.forces.add(new Spring(balle3, balle4, 10, 100));
        physics.forces.add(new PlanetGravity(balle4, 2));
    }
}

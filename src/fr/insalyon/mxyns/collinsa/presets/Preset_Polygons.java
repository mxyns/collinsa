package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Material;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.collisions.CollisionListener;
import fr.insalyon.mxyns.collinsa.physics.entities.*;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

public class Preset_Polygons extends Preset {

    float rate = .3f;
    int loop = 0;
    Polygon poly2;

    @Override
    public void setup(String[] args, Collinsa collinsa) {

        Physics physics = collinsa.getPhysics();

        Polygon poly = new ConvexPoly(new Vec2f(100, 100), 5, 60);
        //poly2 = new ConvexPoly(new Vec2f(420, 250), 3, 60);
        //Rect poly = new Rect(100, 100, 300, 100);
        poly.setVel(50,0);
        poly.setRot(0.5f);

        poly2 = new Rect(500, 100, 300, 100);

        poly2 = new ConvexPoly(new Vec2f(300, 100), 5, 60);
        poly2.setRot(0.1f);
        poly2.setCollisionType(Collision.CollisionType.KINEMATIC);

        ConvexPoly triangle = new ConvexPoly(new Vec2f(600, 200), 3, 60);
        triangle.setAcc(0, 10);
        triangle.setRot((float) Math.toRadians(60));
        triangle.setRot((float) Math.toRadians(37));

        ConvexPoly sol = new Rect(550, 400, 400, 150);
        sol.setCollisionType(Collision.CollisionType.KINEMATIC);
        sol.setRot(-0.2f);
        sol.setMaterial(Material.SLIDY);

        poly.getMaterial().setRestitution(0.2f);
        poly2.getMaterial().setRestitution(0.2f);
        sol.getMaterial().setRestitution(0.2f);
        triangle.getMaterial().setRestitution(0.2f);

        Circle boule = new Circle(triangle.getPos().copy(), 20);
        boule.getPos().add(10, -50);
        boule.setAcc(0, 10);
        //boule.setAngAcc(2f);
        boule.setMaterial(Material.STICKY);

        Entity listenerTest;
        (listenerTest = new Circle(boule.getPos().copy(), boule.getR())).getPos().add(0,-60);
        listenerTest.setAcc(-2,15);
        listenerTest.addCollisionListener(new CollisionListener() {
            @Override
            public void aabbCollided(Entity source, Entity target) {

                ((Circle) source).setR(Math.max(5, ((Circle) source).getR()-.5f));
            }
            @Override
            public void collisionDectected(Entity source, Entity target, Collision collision) {

                if (target instanceof Rect && false)
                    physics.removeEntity(target);
            }

            @Override
            public void collisionResolved(Entity source, Entity target, Collision collision) {

            }

            @Override
            public void collisionIgnored(Entity source, Entity target, Collision collision) {

            }
        });


        physics.addEntity(boule);
        physics.addEntity(listenerTest);
        physics.addEntity(sol);
        physics.addEntity(triangle);
        physics.addEntity(poly);
        physics.addEntity(poly2);
    }

    @Override
    public void loop(String[] args, Collinsa collinsa) {

        while (true) {

            if (loop <= 8) {
                //collinsa.getPhysics().removeEntity(poly2);
//
                //float oldRot = poly2.getRot();
//
                ////poly2 = new ConvexPoly(new Vec2f(380, 220), 2 + ++loop, 50);
                //poly2.setRot(oldRot);
                ////poly2.setAngVel(1);
                ////collinsa.getPhysics().addEntity(poly2);
            } else {

            }

            try {
                Thread.sleep((int) (1000 / rate));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

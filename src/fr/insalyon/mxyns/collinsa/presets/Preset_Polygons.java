package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Material;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.collisions.CollisionListener;
import fr.insalyon.mxyns.collinsa.physics.entities.*;
import fr.insalyon.mxyns.collinsa.physics.forces.PlanetGravity;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

/**
 * Preset montrant les collisions cercles / polygones et polygones / polygones ainsi que le fonctionnement des CollisionListener
 * On remarque un problème de collision entre le pentagone qui tombe et le petit triangle en bas. C'est dû à la précision des float qui mettent en échec l'algorithme de calcul de normale / points de contacts avec certains angles d'incidence faibles
 */
public class Preset_Polygons extends Preset {

    @Override
    public void setup(String[] args, Collinsa collinsa) {

        Physics physics = collinsa.getPhysics();

        Polygon poly = new ConvexPoly(new Vec2f(100, 60), 5, 60);
        //poly2 = new ConvexPoly(new Vec2f(420, 250), 3, 60);
        //Rect poly = new Rect(100, 100, 300, 100);
        poly.setVel(50, 0);
        poly.setRot(0.5f);

        Polygon poly2 = new Rect(500, 100, 300, 100);

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

        Polygon convexPoly = new ConvexPoly(new Vec2f(70, 580),
                                            new Vec2f(20, 20),
                                            new Vec2f(-40, -20),
                                            new Vec2f(-5f, 30)
        );
        convexPoly.setCollisionType(Collision.CollisionType.KINEMATIC);
        physics.placeEntity(convexPoly);

        Circle boule = new Circle(triangle.getPos().copy(), 20);
        boule.getPos().add(10, -50);
        boule.setAcc(0, 10);
        //boule.setAngAcc(2f);
        boule.setMaterial(Material.STICKY);

        Entity listenerTest;
        (listenerTest = new Circle(boule.getPos().copy(), boule.getR())).getPos().add(0, -60);
        listenerTest.setAcc(-2, 15);
        listenerTest.addCollisionListener(new CollisionListener() {
            @Override
            public void aabbCollided(Entity source, Entity target) {

                ((Circle) source).setR(Math.max(5, ((Circle) source).getR() - .5f));
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


        physics.placeEntity(boule);
        physics.placeEntity(listenerTest);
        physics.placeEntity(sol);
        physics.placeEntity(triangle);
        physics.placeEntity(poly);
        physics.placeEntity(poly2);
        physics.addGlobalForce(new PlanetGravity(1));
    }
}
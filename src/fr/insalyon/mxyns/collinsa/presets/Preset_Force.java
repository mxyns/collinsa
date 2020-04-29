package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.physics.forces.Gravity;
import fr.insalyon.mxyns.collinsa.physics.forces.PlanetGravity;
import fr.insalyon.mxyns.collinsa.physics.forces.Spring;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

/**
 * Preset qui montre l'utilisation et le fonctionnement des forces entre les entités
 */
public class Preset_Force extends Preset {

    @Override
    public void setup(String[] args, Collinsa collinsa) {

        Physics physics = collinsa.getPhysics();

        Circle balle1 = new Circle(new Vec2f(350, 300), 50);
        Circle balle2 = new Circle(new Vec2f(550, 300), 10);

        balle2.setVel(0, 200);
        balle1.getInertia().setMass(1e17f);
        System.out.println("balle1 inertia " + balle1.getInertia());

        Rect carre = new Rect(new Vec2f(800, 300), new Vec2f(20, 20));
        Entity balle4 = new Circle(new Vec2f(900, 300), 20);
        balle4.getInertia().setMass(10);
        carre.setCollisionType(Collision.CollisionType.CLASSIC);

        physics.addEntity(balle1);
        physics.addEntity(balle2);
        physics.addEntity(carre);
        physics.addEntity(balle4);
        physics.forces.add(new Gravity(balle2, balle1));
        physics.forces.add(new PlanetGravity(balle4, 2));

        Spring spring;
        physics.forces.add(spring = new Spring(carre, balle4, 10, 100));
        spring.toSourceApplicationPointLocal = new Vec2d(0, carre.getSize().y * .5f);
    }
}

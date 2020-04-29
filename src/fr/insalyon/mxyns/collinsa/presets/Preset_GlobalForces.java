package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.forces.Gravity;
import fr.insalyon.mxyns.collinsa.physics.forces.Spring;
import fr.insalyon.mxyns.collinsa.utils.Utils;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Color;

/**
 * Preset qui teste les forces globales
 * Il est franchement plutôt joli
 */
public class Preset_GlobalForces extends Preset {

    @Override
    public void setup(String[] args, Collinsa collinsa) {

        Physics physics = collinsa.getPhysics();

        physics.getChunkCount().mult(3);
        physics.resize(new Vec2f(physics.getWidth(), physics.getHeight()));

        Circle attractor = new Circle(new Vec2f(physics.getWidth() * .5f, physics.getHeight() * .5f), 50);
        //attractor.setCollisionType(Collision.CollisionType.KINEMATIC);
        attractor.setFillColor(Color.red);
        Utils.applyParameter("--m", 5f, args, attractor.getInertia()::setMass);
        physics.addEntity(attractor);

        for (int i = 0; i < Utils.getParameter("--n", 300, args); ++i) {

            Circle circle = new Circle((int) (Math.random() * collinsa.getPhysics().getWidth()), (int) (Math.random() * collinsa.getPhysics().getHeight()), 2);

            // On redéfinit la couleur du matériau
            circle.setColor(new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));

            // On teste une accélération vers le bas type gravité = 10g
            //circle.setVel(speed);
            physics.addEntity(circle);
        }

        physics.globalForces.add(new Spring(attractor, null, 100, 100));
        physics.globalForces.add(new Gravity(attractor, null));
    }
}

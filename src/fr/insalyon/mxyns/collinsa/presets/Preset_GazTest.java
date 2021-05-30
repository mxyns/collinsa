package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Material;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.ConvexPoly;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.render.Camera;
import fr.insalyon.mxyns.collinsa.utils.Utils;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Color;

/**
 * Un preset qui devait modéliser un gaz à la base. Au final on a juste quelques cercles et un élément qui les pousse (Triangle)
 * Permet de montrer le fonctionnement des collisions polygones / cercles et le fonctionnement des caméras de suivi (la caméra #159 suit le pusher)
 */
public class Preset_GazTest extends Preset {

    private Entity pusher;
    private Camera camera_2;

    @Override
    public void setup(String[] args, Collinsa collinsa) {

        float e = Material.DUMMY.getRestitution(), m = 10;
        int n = Utils.getParameter("-n", 100, args);

        //Création d'élements / entitées à ajouter à la simulation
        Physics physics = collinsa.getPhysics();


        pusher = new ConvexPoly(new Vec2f(50, collinsa.getPhysics().getHeight() / 2), 3, 100);
        pusher.setCollisionType(Collision.CollisionType.KINEMATIC);
        System.out.println("inertia " + pusher.getInertia());
        pusher.setAcc(50, 0);
        collinsa.getPhysics().placeEntity(pusher);


        for (int i = 0; i < n; ++i) {

            Circle circle = new Circle((int) (Math.random() * collinsa.getPhysics().getWidth()), (int) (Math.random() * collinsa.getPhysics().getHeight()), 2);
            Utils.applyParameter("--e", Material.DUMMY.getRestitution(), args, circle.getMaterial()::setRestitution);
            Utils.applyParameter("--m", 5f, args, circle.getInertia()::setMass);

            // On redéfinit la couleur du matériau
            circle.setColor(new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));

            // On teste une accélération vers le bas type gravité = 10g
            //circle.setVel(speed);
            physics.placeEntity(circle);
        }

        System.out.println("max size = " + pusher.getMaximumSize());
        camera_2 = new Camera(pusher.getPos().toDouble(), new Vec2d(collinsa.getRenderer().getCamera().getRatio() * 2 * pusher.getMaximumSize(), 2 * pusher.getMaximumSize()));
        for (int i = 0; i < 157; ++i)
            collinsa.getRenderer().getCameraController().addCamera(new Camera(new Vec2d(physics.getWidth() * Math.random(), physics.getHeight() * Math.random()), new Vec2d(physics.getWidth() * (0.5f + Math.random() / 3f), physics.getHeight() * (0.5f + Math.random() / 3f))));
        camera_2.follow(pusher);
        collinsa.getRenderer().getCameraController().addCamera(camera_2);
    }
}

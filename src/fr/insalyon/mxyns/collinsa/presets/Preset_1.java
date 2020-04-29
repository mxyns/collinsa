package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Color;

/**
 * Un preset avec deux gros rectangles et des cercles positionnés aléatoirement
 */
public class Preset_1 extends Preset {

    @Override
    public void setup(String[] args, Collinsa collinsa) {

        //Création d'élements / entitées à ajouter à la simulation
        Physics physics = collinsa.getPhysics();
        Rect rect = new Rect(physics.getChunkSize().x / 2, physics.getChunkSize().y / 2, 60, 100);

        // Test rotating rect-rect collisions
        Rect rect1 = new Rect(collinsa.getPhysics().getWidth() / 2, collinsa.getPhysics().getHeight() / 2, 200, 100);
        Rect rect2 = new Rect(collinsa.getPhysics().getWidth() / 2 - 150, collinsa.getPhysics().getHeight() / 2 - 130, 200, 100);
        rect1.setAngVel(3.5f);
        rect1.setCollisionType(Collision.CollisionType.KINEMATIC);
        rect2.setAngVel(-2f);
        rect2.setRot(0.2f);
        rect2.setVel(new Vec2f(0, 20));

        // Test not aligned circle-circle collision
        Circle circle1 = new Circle(rect1.getPos().x - 500, rect1.getPos().y, 20);
        Circle circle2 = new Circle(circle1.getPos().x - 30, circle1.getPos().y + 30, 20);
        circle2.setVel(new Vec2f(0, -10));

        // Test moving circle-circle
        Circle circle3 = new Circle(300, 600, 20);
        circle3.getInertia().setMass(1000);
        Circle circle4 = new Circle(20, 600, 5);
        circle4.setVel(new Vec2f(120, 0));
        circle3.setVel(new Vec2f(0, 0));

        // Test rotated circle-rect collision
        Rect rect3 = new Rect(collinsa.getPhysics().getWidth() / 2 + 300, collinsa.getPhysics().getHeight() / 2, 50, 150);
        rect3.setRot(0);

        rect3.setVel(new Vec2f(150, 0));
        Circle circle5 = new Circle(rect3.getPos().x + 150, rect3.getPos().y - 50, 15);
        //circle5.setVel(rect2.getVel().copy().mult(3));

        /*Circle caillou = new Circle(new Vec2f(10, 400), 10);
            caillou.setVel(new Vec2f(300, -100));
            caillou.setAcc(new Vec2f(0, 300));

        physics.addEntity(caillou);*/

        for (int i = 0; i < 20; ++i) {

            Circle circle = new Circle((int) (Math.random() * collinsa.getPhysics().getWidth()), (int) (Math.random() * collinsa.getPhysics().getHeight()), 5);

            // On redéfinit la couleur du matériau
            circle.setColor(new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));

            // On teste une accélération vers le bas type gravité = 10g
            circle.setVel(new Vec2f((float) (Math.random() * 200) - 100, (float) (Math.random() * 200) - 100));
            physics.addEntity(circle);
        }

        // On ajoute les entités au moteur physique
        physics.addEntity(rect1);
        physics.addEntity(rect2);
        physics.addEntity(rect3);
        physics.addEntity(circle1);
        physics.addEntity(circle2);
        physics.addEntity(circle3);
        physics.addEntity(circle4);
        physics.addEntity(circle5);

        Rect test = new Rect(0, 0, 300, 300);
        test.setRot(0.2f);

        physics.addEntity(test);
    }
}

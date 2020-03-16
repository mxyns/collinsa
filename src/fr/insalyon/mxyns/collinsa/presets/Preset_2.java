package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Material;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.utils.Utils;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Color;

public class Preset_2 extends Preset {

    @Override
    public void run(String[] args, Collinsa collinsa) {

        float e = Material.DUMMY.getRestitution(), m = 10;
        int n = 1000;

        try {
            if (Utils.lookForString("-e", args) != -1)
                e = Float.parseFloat(Utils.getArgValue("e", args));
            if (Utils.lookForString("-n", args) != -1)
                n = Integer.parseInt(Utils.getArgValue("n", args));
        } catch (NumberFormatException | NullPointerException e1) {
            System.out.println("wrong parameters format");
        }

        //Création d'élements / entitées à ajouter à la simulation
        Physics physics = collinsa.getPhysics();
        Rect moulin = new Rect(physics.getWidth() / 2, physics.getHeight() / 2, physics.getChunkSize().x / 4, physics.getChunkSize().y / 16);
        moulin.setAngVel(-1f);
        moulin.setAngAcc(-0.3f);
        moulin.setCollisionType(Collision.CollisionType.KINEMATIC);

        for (int i = 0; i < n; ++i) {

            Circle circle = new Circle((int) (Math.random() * collinsa.getPhysics().getWidth()), (int) (Math.random() * collinsa.getPhysics().getHeight()), 5);
            circle.getMaterial().setRestitution(e);

            // On redéfinit la couleur du matériau
            circle.setColor(new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));

            // On teste une accélération vers le bas type gravité = 10g
            circle.setVel(new Vec2f((float) (Math.random() * 200) - 100, (float) (Math.random() * 200) - 100));
            physics.addEntity(circle);
        }

        // On ajoute les entités au moteur physique
        physics.addEntity(moulin);
    }
}

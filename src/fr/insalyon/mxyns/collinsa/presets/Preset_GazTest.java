package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Material;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.utils.Utils;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Color;

public class Preset_GazTest extends Preset {

    Vec2f speed = Vec2f.zero();

    @Override
    public void setup(String[] args, Collinsa collinsa) {

        float e = Material.DUMMY.getRestitution(), m = 10;
        int n = 1000;

        try {
            if (Utils.lookForString("-e", args) != -1)
                e = Float.parseFloat(Utils.getArgValue("e", args));
            if (Utils.lookForString("-n", args) != -1)
                n = Integer.parseInt(Utils.getArgValue("n", args));
            if (Utils.lookForString("-m", args) != -1)
                m = Float.parseFloat(Utils.getArgValue("m", args));
        } catch (NumberFormatException | NullPointerException e1) {
            System.out.println("wrong parameters format");
        }

        //Création d'élements / entitées à ajouter à la simulation
        Physics physics = collinsa.getPhysics();

        for (int i = 0; i < n; ++i) {

            Circle circle = new Circle((int) (Math.random() * collinsa.getPhysics().getWidth()), (int) (Math.random() * collinsa.getPhysics().getHeight()), 2);
            circle.getMaterial().setRestitution(e);
            circle.getInertia().setMass(m);

            // On redéfinit la couleur du matériau
            circle.setColor(new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));

            // On teste une accélération vers le bas type gravité = 10g
            //circle.setVel(speed);
            physics.addEntity(circle);
        }

    }

    @Override
    public void loop(String[] args, Collinsa collinsa) {

        Circle circle = new Circle(10,collinsa.getPhysics().getHeight() /2, 25);
        circle.getInertia().setMass(150);
        circle.setAcc(100, 0);

        collinsa.getPhysics().addEntity(circle);

    }
}

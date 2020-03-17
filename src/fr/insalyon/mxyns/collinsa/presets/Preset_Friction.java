package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Material;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.utils.Utils;

import java.awt.Color;

public class Preset_Friction extends Preset {

    int rate = 1;
    float circleMass = 1;
    float e = Material.DUMMY.getRestitution();
    Rect mill;

    @Override
    public void setup(String[] args, Collinsa collinsa) {

       float sF = Material.DUMMY.getStaticFriction(), dF = Material.DUMMY.getDynamicFriction(), cyanMass = 5;

        try {
            if (Utils.lookForString("--millSF", args) != -1)
                sF = Float.parseFloat(Utils.getArgValue("-millSF", args));
            if (Utils.lookForString("--millDF", args) != -1)
                dF = Float.parseFloat(Utils.getArgValue("-millDF", args));
            if (Utils.lookForString("-e", args) != -1)
                e = Float.parseFloat(Utils.getArgValue("e", args));
            if (Utils.lookForString("--circleMass", args) != -1)
                circleMass = Float.parseFloat(Utils.getArgValue("-circleMass", args));
            if (Utils.lookForString("--cyanMass", args) != -1)
                cyanMass = Float.parseFloat(Utils.getArgValue("-cyanMass", args));
            if (Utils.lookForString("--rate", args) != -1)
                rate = Integer.parseInt(Utils.getArgValue("-rate", args));
        } catch (NumberFormatException | NullPointerException e1) {
            System.out.println("wrong parameters format");
        }

        //Création d'élements / entitées à ajouter à la simulation
        Physics physics = collinsa.getPhysics();

        Rect r = new Rect(40, 300, 220, 30);
        r.setRot(1.1f);
        r.setCollisionType(Collision.CollisionType.KINEMATIC);
        r.setMaterial(Material.STICKY);
        r.getInertia().setMass(1.5f);
        physics.addEntity(r);

        Rect r2 = new Rect(250, 300, 200, 30);
        r2.setMaterial(Material.BOUNCY);
        r2.getInertia().setMass(100);
        r2.setRot(.5f);
        r2.setCollisionType(Collision.CollisionType.KINEMATIC);
        physics.addEntity(r2);

        Rect r3 = new Rect(250, 500, 500, 30);
        r3.setColor(Color.cyan);
        r3.getInertia().setMass(cyanMass);
        r3.setRot(0.2f);
        r3.setCollisionType(Collision.CollisionType.KINEMATIC);
        physics.addEntity(r3);

        mill = new Rect(800, 500, 250, 15);
        mill.getMaterial().setDynamicFriction(dF);
        mill.getMaterial().setStaticFriction(sF);
        mill.setAngVel(1f);
        mill.setAngAcc(0.1f);
        mill.setCollisionType(Collision.CollisionType.KINEMATIC);
        physics.addEntity(mill);
    }

    @Override
    public void loop(String[] args, Collinsa collinsa) {

        Physics physics = collinsa.getPhysics();
        float x,y,vx,vy;
        while (true) {

            if (mill.getAngVel() > 4.5f) mill.setAngAcc(0);

            x = (int) (100*Math.random()) + 30;
            y = (int) (100*Math.random()) + 10;
            vx = (int) (15*Math.random()) + 1;
            vy = (int) (30*Math.random()) + 1;

            vx*=5;
            vy*=5;

            Circle circle = new Circle(x, y, (int) (8 * Math.random()) + 1);
            circle.getInertia().setMass(circleMass);
            circle.setAcc(0, 30);
            circle.setVel(vx, vy);
            circle.getMaterial().setRestitution(e);

            physics.addEntity(circle);

            try {
                Thread.sleep(1000 / rate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

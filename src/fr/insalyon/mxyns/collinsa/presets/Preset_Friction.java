package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Material;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.physics.forces.PlanetGravity;
import fr.insalyon.mxyns.collinsa.utils.Utils;

import java.awt.Color;

public class Preset_Friction extends Preset {

    Rect mill;

    @Override
    public void setup(String[] args, Collinsa collinsa) {

        //Création d'élements / entitées à ajouter à la simulation
        Physics physics = collinsa.getPhysics();

        Rect r_sticky = new Rect(40, 300, 220, 30);
        r_sticky.setRot(1.1f);
        r_sticky.setCollisionType(Collision.CollisionType.KINEMATIC);
        r_sticky.setMaterial(Material.STICKY.copy());
        r_sticky.getInertia().setMass(1.5f);
        physics.addEntity(r_sticky);

        Rect r_bouncy = new Rect(250, 300, 200, 30);
        r_bouncy.setMaterial(Material.BOUNCY.copy());
        r_bouncy.getInertia().setMass(100);
        r_bouncy.setRot(.5f);
        r_bouncy.setCollisionType(Collision.CollisionType.KINEMATIC);
        physics.addEntity(r_bouncy);

        Rect smallMill = new Rect(175, 425, 100, 10);
        smallMill.setMaterial(Material.DUMMY.copy());
        smallMill.getInertia().setMass(100);
        smallMill.setRot(.5f);
        smallMill.setAngVel(-2f);
        smallMill.setCollisionType(Collision.CollisionType.KINEMATIC);
        physics.addEntity(smallMill);

        Rect r_cyan = new Rect(250, 500, 500, 30);
        r_cyan.setColor(Color.cyan);
        Utils.applyParameter("--cyanMass", 5f, args, r_cyan.getInertia()::setMass);
        r_cyan.setRot(0.2f);
        r_cyan.setCollisionType(Collision.CollisionType.KINEMATIC);
        physics.addEntity(r_cyan);

        mill = new Rect(780, 500, 275, 15);
        Utils.applyParameter("--millDF", Material.DUMMY.getDynamicFriction(), args, mill.getMaterial()::setDynamicFriction);
        Utils.applyParameter("--millSF", Material.DUMMY.getStaticFriction(), args, mill.getMaterial()::setStaticFriction);
        mill.setAngVel(-1f);
        mill.setAngAcc(-0.1f);
        mill.setCollisionType(Collision.CollisionType.KINEMATIC);
        mill.setActivated(true);
        physics.addEntity(mill);

        Rect r_landing = new Rect(725, 650, 525, 15);
        r_landing.setMaterial(Material.DUMMY.copy());
        r_landing.getMaterial().setStaticFriction(Material.DUMMY.getStaticFriction()/2);
        r_landing.getMaterial().setDynamicFriction(Material.DUMMY.getDynamicFriction()/2);
        r_landing.setRot(.0f);
        r_landing.setCollisionType(Collision.CollisionType.KINEMATIC);
        r_landing.setColor(Color.black);
        physics.addEntity(r_landing);
        physics.globalForces.add(new PlanetGravity(1));
    }

    @Override
    public void loop(String[] args, Collinsa collinsa) {

        Physics physics = collinsa.getPhysics();
        float x,y,vx,vy;

        int rate = Utils.getParameter("--rate", 1, args);

        while (true) {

            if (mill.getAngVel() < -1.5) mill.setAngAcc(0);
            if (physics.getEntities().size() > rate*10)
                mill.setActivated(true);

            x = (int) (100*Math.random()) + 30;
            y = (int) (100*Math.random()) + 10;
            vx = (int) (15*Math.random()) + 1;
            vy = (int) (30*Math.random()) + 1;

            vx*=5f;
            vy*=5;

            Circle circle = new Circle(x, y, (int) (2 * Math.random()) + 2);
            Utils.applyParameter("--m", 1f, args, circle.getInertia()::setMass);
            circle.setVel(vx, vy);
            Utils.applyParameter("--e", Material.DUMMY.getRestitution(), args, circle.getMaterial()::setRestitution);

            physics.addEntity(circle);

            try {
                Thread.sleep(1000 / rate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

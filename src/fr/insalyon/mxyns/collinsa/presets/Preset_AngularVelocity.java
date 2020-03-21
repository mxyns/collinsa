package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Material;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.utils.Utils;

public class Preset_AngularVelocity extends Preset {

    Rect rect; Circle circle;

    @Override
    public void setup(String[] args, Collinsa collinsa) {


        Physics physics = collinsa.getPhysics();

        rect = new Rect(physics.getWidth() / 2, physics.getHeight() / 2, 800, 20);

        Utils.applyParameter("--angle", -.5f, args, circle::setRot);
        rect.setCollisionType(Collision.CollisionType.KINEMATIC);


        circle = new Circle(rect.getPos().x - 130, rect.getPos().y + 50, 10);
        circle.setAcc(0, 20);
        Utils.applyParameter("--acc", 2f, args, circle::setAngAcc);
        Utils.applyParameter("--vel", 3f, args, circle::setAngVel);

        Utils.applyParameter("--df", Material.DUMMY.getDynamicFriction(), args, circle.getMaterial()::setDynamicFriction);
        Utils.applyParameter("--sf", Material.DUMMY.getStaticFriction(), args, circle.getMaterial()::setStaticFriction);

        physics.addEntity(rect);
        physics.addEntity(circle);
    }

    @Override
    public void loop(String[] args, Collinsa collinsa) {

        System.out.println(rect.getInertia());
        System.out.println(circle.getInertia());
    }
}

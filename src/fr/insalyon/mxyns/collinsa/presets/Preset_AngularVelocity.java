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

        float angle = -.5f;
        float dynamicFriction = Material.DUMMY.getDynamicFriction();
        float staticFriction = Material.DUMMY.getStaticFriction();
        float angVel = 3f, angAcc = 2f;

        if (Utils.lookForString("--angle", args) != -1)
            angle = Float.parseFloat(Utils.getArgValue("-angle", args));
        if (Utils.lookForString("--df", args) != -1)
            dynamicFriction = Float.parseFloat(Utils.getArgValue("-df", args));
        if (Utils.lookForString("--sf", args) != -1)
            staticFriction = Float.parseFloat(Utils.getArgValue("-sf", args));
        if (Utils.lookForString("--acc", args) != -1)
            angAcc = Float.parseFloat(Utils.getArgValue("-acc", args));
        if (Utils.lookForString("--vel", args) != -1)
            angVel = Float.parseFloat(Utils.getArgValue("-vel", args));

        Physics physics = collinsa.getPhysics();

        rect = new Rect(physics.getWidth() / 2, physics.getHeight() / 2, 800, 20);
        rect.setRot(angle);
        rect.setCollisionType(Collision.CollisionType.KINEMATIC);


        circle = new Circle(rect.getPos().x - 130, rect.getPos().y + 50, 10);
        circle.setAcc(0, 20);
        circle.setAngAcc(angAcc);
        circle.setAngVel(angVel);

        circle.getMaterial().setDynamicFriction(dynamicFriction);
        circle.getMaterial().setStaticFriction(staticFriction);

        physics.addEntity(rect);
        physics.addEntity(circle);
    }

    @Override
    public void loop(String[] args, Collinsa collinsa) {

        System.out.println(rect.getInertia());
        System.out.println(circle.getInertia());
    }
}

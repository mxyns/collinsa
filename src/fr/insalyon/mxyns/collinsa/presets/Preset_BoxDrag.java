package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Material;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.physics.forces.Motor;
import fr.insalyon.mxyns.collinsa.physics.forces.Spring;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

public class Preset_BoxDrag extends Preset {

    static {
          registerPreset("boxdrag", new Preset_BoxDrag());
    }

    @Override
    public void setup(String[] args, Collinsa collinsa) {

        Physics physics = collinsa.getPhysics();

        float groundDepth = 50;
        float groundHeight = physics.getHeight() - groundDepth / 2;
        Rect ground = new Rect(physics.getWidth() / 2, groundHeight, physics.getWidth(), groundDepth);
        ground.setCollisionType(Collision.CollisionType.KINEMATIC);
        physics.insertEntity(ground);

        float wheelStart = 400;
        float wheelBoxDist = 200;
        float wheelRadius = 20;
        float boxSize = 25;

        Circle wheel = new Circle(wheelStart, groundHeight - wheelRadius, wheelRadius);
        wheel.setMaterial(Material.METAL);
        physics.addForce(new Motor(wheel, 100));
        physics.insertEntity(wheel);

        Rect box = new Rect(new Vec2f(wheelStart - wheelBoxDist, groundHeight - boxSize / 2), new Vec2f(boxSize, boxSize));
        box.setMaterial(Material.ROCK);
        physics.insertEntity(box);

        Spring spring = new Spring(wheel, box, 1000, wheelBoxDist);
        physics.addForce(spring);
    }
}

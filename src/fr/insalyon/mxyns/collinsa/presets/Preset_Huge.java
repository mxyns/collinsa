package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.forces.Gravity;
import fr.insalyon.mxyns.collinsa.utils.Utils;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

@Deprecated
public class Preset_Huge extends Preset {

    @Override
    public void setup(String[] args, Collinsa collinsa) {

        Physics physics = collinsa.getPhysics();
        Utils.applyParameter("--worldSize", new Vec2f(1e9f, 1e9f), args, physics::resize);
        Utils.applyParameter("--scale", 1e6f, args, collinsa.getRenderer()::setRenderScale);
        collinsa.getRenderer().getCameraController().setCameraDisplayBounds(physics.getSize().toDouble());
        collinsa.getRenderer().getCameraController().setCameraFocus(physics.getCenterPos(), true);
        System.out.println(collinsa.getRenderer());

        Circle earth = new Circle(physics.getCenterPos(), 6356752.3f);
        earth.getInertia().setMass(5.972f * 1e24f);
        earth.getInertia().setJ(earth.computeJ());

        Circle moon = new Circle(earth.getPos().copy().add(384400000f, 0), 3474200);
        moon.getInertia().setMass(7.34767309f*1e22f);
        moon.getInertia().setJ(moon.computeJ());
        moon.setVel(0, 1023.055f);

        physics.placeEntity(earth);
        physics.placeEntity(moon);
        physics.addForce(new Gravity(earth, moon));
    }
}

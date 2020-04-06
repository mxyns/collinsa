package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.ConvexPoly;
import fr.insalyon.mxyns.collinsa.physics.entities.Polygon;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

public class Preset_Polygons extends Preset {

    float rate = .3f;
    int loop = 0;
    Polygon poly2;

    @Override
    public void setup(String[] args, Collinsa collinsa) {

        Physics physics = collinsa.getPhysics();

        Polygon poly = new ConvexPoly(new Vec2f(310, 250), 3, 60);
        poly2 = new ConvexPoly(new Vec2f(420, 250), 3, 60);

        physics.addEntity(poly);
        physics.addEntity(poly2);

    }

    @Override
    public void loop(String[] args, Collinsa collinsa) {

        while (true) {

            if (loop <= 8) {
                collinsa.getPhysics().removeEntity(poly2);

                float oldRot = poly2.getRot();

                poly2 = new ConvexPoly(new Vec2f(380, 220), 2 + ++loop, 50);
                poly2.setRot(oldRot);
                poly2.setAngVel(1);
                collinsa.getPhysics().addEntity(poly2);
            } else {

            }

            try {
                Thread.sleep((int) (1000 / rate));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

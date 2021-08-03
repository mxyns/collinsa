package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.physics.forces.PlanetGravity;
import fr.insalyon.mxyns.collinsa.physics.forces.Spring;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

public class Preset_Mesh extends Preset {

    @Override
    public void setup(String[] args, Collinsa collinsa) {

        Physics physics = collinsa.getPhysics();

        float groundDepth = 50;
        Rect ground = new Rect(physics.getWidth() / 2, physics.getHeight() - groundDepth / 2, physics.getWidth(), groundDepth);
        ground.setCollisionType(Collision.CollisionType.KINEMATIC);
        physics.insertEntity(ground);

        physics.addGlobalForce(new PlanetGravity(1));

        float spring = 60;
        float radius = 10;
        float spacing = 100;
        int width = 16;
        int height = 16;

        Vec2f startPos = new Vec2f((physics.getWidth() - width * spacing) / 2 , 400);
        Circle[][] nodes = new Circle[height][width];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                nodes[j][i] = new Circle(Vec2f.zero().add(i, j).mult(spacing).add(startPos), radius);
                physics.insertEntity(nodes[j][i]);
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                for (int k = -1; k <= 1; k++) {
                    for (int l = -1; l <= 1; l++) {
                        if (!(k == 0 && l == 0) && j + k >= 0 && i+l >= 0 && j + k < height && i + l < width) {
                            physics.addForce(new Spring(nodes[j][i], nodes[j + k][i + l], spring, spacing * (k + l == 1 ? 1 : Math.sqrt(2))));
                        }
                    }
                }

            }
        }
    }
}

package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Graphics2D;

/**
 * Rayon pour Ray casting (i.e. rayon lumineux, projectile très rapide), pas sûr de le faire mais peut être intéressant
 */
@Deprecated
public class Ray extends Entity {

    public Ray(Vec2f pos) {

        super(pos);
    }

    @Override
    public double getMaximumSize() {

        return 0;
    }

    @Override
    public float computeJ() {

        return 0;
    }

    @Override
    public float getVolume() {

        return 0;
    }

    @Override
    public void updateAABB() {

    }

    @Override
    public short cardinal() {

        return 0;
    }

    @Override
    public void render(Renderer renderer, Graphics2D g) {

    }
}

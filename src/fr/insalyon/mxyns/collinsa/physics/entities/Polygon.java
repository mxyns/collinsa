package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2;

import java.awt.Graphics2D;

public abstract class Polygon extends Entity{

    public Polygon(Vec2 pos) {

        super(pos);
    }

    public Polygon(double x, double y) {

        super(x, y);
    }

    @Override
    public void render(Renderer renderer, Graphics2D g) {

    }
}

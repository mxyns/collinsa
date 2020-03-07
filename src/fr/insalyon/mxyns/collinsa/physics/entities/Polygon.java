package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

public abstract class Polygon extends Entity{

    public Polygon(Vec2f pos) {

        super(pos);
    }

    public Polygon(double x, double y) {

        super(x, y);
    }
}

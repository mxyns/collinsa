package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2;

import java.awt.Graphics2D;

public abstract class Entity {

    /**
     * Vec2 position de l'entité
     */
    Vec2 pos;

    /**
     * Vec2 vitesse de l'entité
     */
    Vec2 vel;


    /**
     * Vec2 acceleration de l'entité
     */
    Vec2 acc;

    private Entity() {

        vel = Vec2.zero();
        acc = Vec2.zero();
    }
    public Entity(Vec2 pos) {

        this();
        this.pos = pos.copy();
    }
    public Entity(double x, double y) {

        this();
        this.pos = new Vec2(x, y);
    }

    public void update() {

        vel.add(acc);
        pos.add(vel);
    }

    public abstract void render(Renderer renderer, Graphics2D g);

    public Vec2 getPos() {

        return pos;
    }

    public void setPos(Vec2 pos) {

        this.pos = pos;
    }

    public Vec2 getVel() {

        return vel;
    }

    public void setVel(Vec2 vel) {

        this.vel = vel;
    }

    public Vec2 getAcc() {

        return acc;
    }

    public void setAcc(Vec2 acc) {

        this.acc = acc;
    }
}

package fr.insalyon.mxyns.collinsa.physics.collisions;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

public class Collision {

    Entity source, target;

    public Collision(Entity source, Entity target) {

        this.source = source;
        this.target = target;
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Collision && ((source == ((Collision) obj).target && target == ((Collision) obj).source) || (source == ((Collision) obj).source && target == ((Collision) obj).target) );
    }

    @Override
    public int hashCode() {

        return 1;
    }
}

package fr.insalyon.mxyns.collinsa.physics.collisions;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

public interface CollisionListener {

    void aabbCollided(Entity source, Entity target);

    void collisionDectected(Entity source, Entity target, Collision collision);

    void collisionResolved(Entity source, Entity target, Collision collision);

    void collisionIgnored(Entity source, Entity target, Collision collision);
}

package fr.insalyon.mxyns.collinsa.physics.collisions;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

/**
 * Similaire à MouseAdapter, permet faire un CollisionListener qui ne fait rien par défaut. On peut ensuite reécrire seulement les méthodes que l'on veut utiliser.
 */
public class CollisionAdapter implements CollisionListener {

    @Override
    public void aabbCollided(Entity source, Entity target) {}

    @Override
    public void collisionDectected(Entity source, Entity target, Collision collision) {}

    @Override
    public void collisionResolved(Entity source, Entity target, Collision collision) {}

    @Override
    public void collisionIgnored(Entity source, Entity target, Collision collision) {}
}

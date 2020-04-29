package fr.insalyon.mxyns.collinsa.physics.collisions;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

/**
 * Listener pour les collisions. Permet d'effectuer des opérations à chaque étape de la détection / résolution.
 */
public interface CollisionListener {

    /**
     * Evénement envoyé lorque les AABB entrent en collision
     * @param source entité source (celle auquel ce listener appartient)
     * @param target entité cible (l'autre)
     */
    void aabbCollided(Entity source, Entity target);

    /**
     * Evénement envoyé lorque la collision est détectée
     * @param source entité source (celle auquel ce listener appartient)
     * @param target entité cible (l'autre)
     * @param collision objet collision représentant collision
     */
    void collisionDectected(Entity source, Entity target, Collision collision);

    /**
     * Evénement envoyé lorque la collision est résolue
     * @param source entité source (celle auquel ce listener appartient)
     * @param target entité cible (l'autre)
     * @param collision objet collision représentant collision
     */
    void collisionResolved(Entity source, Entity target, Collision collision);

    /**
     * Evénement envoyé si la collision est ignorée
     * @param source entité source (celle auquel ce listener appartient)
     * @param target entité cible (l'autre)
     * @param collision objet collision représentant collision
     */
    void collisionIgnored(Entity source, Entity target, Collision collision);
}

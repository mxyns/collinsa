package fr.insalyon.mxyns.collinsa.physics.collisions;

import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Moteur de collisions, détecte et calcule les conséquences des collisions entre objets
 *
 */
public class Collider {

    /**
     * Simulation associée au Collider
     */
    private Physics physics;

    /**
     * Crée un Collider et lui associe une simulation (Physics)
     * @param physics simulation à associer
     */
    public Collider(Physics physics) {

        this.physics = physics;
    }

    /**
     * Trouve tous les chunks contenant une entité
     * @param e entité pour laquelle trouver dans les chunks
     * @return Set of Chunk ids
     */
    public Set<Integer> getChunksContaining(Entity e) {

        Rectangle.Double aabb = e.getAABB();
        Set<Integer> chunksToAddEntityTo = new TreeSet<>();

        //TopLeft
        chunksToAddEntityTo.add(physics.getPositionHash(aabb.x, aabb.y));

        //TopRight
        chunksToAddEntityTo.add(physics.getPositionHash(aabb.x + aabb.width, aabb.y));

        //BottomRight
        chunksToAddEntityTo.add(physics.getPositionHash(aabb.x + aabb.width, aabb.y + aabb.height));

        //BottomLeft
        chunksToAddEntityTo.add(physics.getPositionHash(aabb.x, aabb.y + aabb.height));

        return chunksToAddEntityTo;
    }

    /**
     * Trouve toutes les entités proches (proches <=> avec lesquelles une collision est envisageable <=> dans un chunk voisin de celui de 'e') de l'entité 'e'
     * @param e entité pour laquelle il faut trouver les entités voisines
     * @return Liste d'entités proches de 'e'
     */
    public LinkedHashSet<Entity> getNearbyEntities(Entity e) {

        //TODO: optimize list type
        // On ne veut pas de doublons, et on parcourera la liste sans accès aléatoire
        // LinkedHashSet est donc un bon candidat
        LinkedHashSet<Entity> nearby = new LinkedHashSet<>();
        Set<Integer> chunksId = getChunksContaining(e);

        for (int chunkId : chunksId)
            if (chunkId >= 0 && chunkId < physics.getTotalChunkCount())
            nearby.addAll(physics.getChunks().get(chunkId).entities);

        nearby.remove(e);

        return nearby;
    }

    /**
     * Renvoie la simulation associée
     * @return physics
     */
    public Physics getPhysics() {

        return physics;
    }

    /**
     * Redéfinit la simulation associée
     * @param physics nouvelle simulation à associer
     */
    public void setPhysics(Physics physics) {

        this.physics = physics;
    }

    /**
     * Détermine s'il y a collision entre 'entity' et 'target'
     * @param entity 1ère entité (source)
     * @param target 2ème entité (cible)
     */
    public void checkForCollision(Entity entity, Entity target) {

        // Broad phase
        entity.setColor(entity.getAABB().intersects(target.getAABB()) ? Color.red : Color.green);
        target.setColor(entity.getAABB().intersects(target.getAABB()) ? Color.red : Color.green);
            // Narrow phase

    }
}

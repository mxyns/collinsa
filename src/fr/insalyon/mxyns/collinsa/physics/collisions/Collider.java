package fr.insalyon.mxyns.collinsa.physics.collisions;

import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.utils.geo.Geometry;

import java.awt.Color;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Moteur de collisions, détecte et calcule les conséquences des collisions entre objets
 */
public class Collider {

    /**
     * Simulation associée au Collider
     */
    private Physics physics;

    /**
     * Liste des dernières collisions détectées
     */
    private final LinkedHashSet<Collision> collisions;

    /**
     * Crée un Collider et lui associe une simulation (Physics)
     *
     * @param physics simulation à associer
     */
    public Collider(Physics physics) {

        this.physics = physics;
        this.collisions = new LinkedHashSet<>();
    }

    /**
     * Trouve tous les chunks contenant une entité
     *
     * @param e entité pour laquelle trouver dans les chunks
     *
     * @return Set of Chunk ids
     */
    public Set<Integer> getChunksContaining(Entity e) {

        AABB aabb = e.getAABB();
        Set<Integer> chunksToAddEntityTo = new TreeSet<>();

        //TopLeft
        chunksToAddEntityTo.add(physics.getPositionHash(aabb.x, aabb.y));

        //TopRight
        chunksToAddEntityTo.add(physics.getPositionHash(aabb.x + aabb.w, aabb.y));

        //BottomRight
        chunksToAddEntityTo.add(physics.getPositionHash(aabb.x + aabb.w, aabb.y + aabb.h));

        //BottomLeft
        chunksToAddEntityTo.add(physics.getPositionHash(aabb.x, aabb.y + aabb.h));

        return chunksToAddEntityTo;
    }

    /**
     * Trouve toutes les entités proches (proches <=> avec lesquelles une collision est envisageable <=> dans un chunk
     * voisin de celui de 'e') de l'entité 'e'
     *
     * @param e entité pour laquelle il faut trouver les entités voisines
     *
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
     *
     * @return physics
     */
    public Physics getPhysics() {

        return physics;
    }

    /**
     * Redéfinit la simulation associée
     *
     * @param physics nouvelle simulation à associer
     */
    public void setPhysics(Physics physics) {

        this.physics = physics;
    }

    /**
     * Détermine s'il y a collision entre 'entity' et 'target'
     *
     * @param entity 1ère entité (source)
     * @param target 2ème entité (cible)
     */
    public void checkForCollision(Entity entity, Entity target) {

        // Broad phase
        if (!entity.getAABB().intersects(target.getAABB())) {
            entity.setColor(Color.green);
            target.setColor(Color.green);

        } else {

            entity.setColor(Color.pink);
            target.setColor(Color.pink);

            // Narrow phase
            if (entity instanceof Circle && target instanceof Circle)
                checkForCircleCircleCollision((Circle) entity, (Circle) target);
            else if (entity instanceof Rect && target instanceof Rect)
                checkForRectRectCollision((Rect) entity, (Rect) target);
            else if (entity instanceof Circle && target instanceof Rect)
                checkForCircleRectCollision((Circle) entity, (Rect) target);
            else if (entity instanceof Rect && target instanceof Circle)
                checkForCircleRectCollision((Circle) target, (Rect) entity);
        }

    }

    public void checkForCircleCircleCollision(Circle entity, Circle target) {

        if (entity.getPos().sqrdDist(target.getPos()) <= Math.pow(entity.r + target.r, 2))
            logCollision(entity, target);
    }

    private void checkForRectRectCollision(Rect entity, Rect target) {

        if (Geometry.rectOnRectSAT(entity, target))
            logCollision(entity, target);
    }

    public void checkForCircleRectCollision(Circle circle, Rect rect) {

        if(Geometry.circleIntersectRectByClamping(circle, rect))
            logCollision(circle, rect);
    }

    public void checkForCircleSegmentCollision(Circle entity, Circle target) {

        if (true)
            logCollision(entity, target);
    }

    public void logCollision(Entity firstEntity, Entity secondEntity) {

        // TODO check if contained before instantiating a new Collision objects
        collisions.add(new Collision(firstEntity, secondEntity));
    }

    public LinkedHashSet<Collision> getRegisteredCollision() {

        return this.collisions;
    }

    public void clearCollisions() {

        this.collisions.clear();
    }
}
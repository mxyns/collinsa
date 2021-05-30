package fr.insalyon.mxyns.collinsa.physics.collisions;

import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.entities.Polygon;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.physics.ticks.Tick;
import fr.insalyon.mxyns.collinsa.utils.geo.Geometry;

import java.awt.Color;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Moteur de collisions, détecte et calcule les conséquences des collisions entre objets
 */
public class Collider {

    /**
     * Simulation associée au Collider
     */
    private Physics physics;

    /**
     * Détermine si le Collider résout les collisions de manière précise, en calculant le moment exact de la collision et en prenant en compte les collisions simultanées
     */
    public boolean preciseResolution = false;

    /**
     * Détermine si le Collider change la couleur des objets en fonction de la détection / résolution de collisions (debug)
     */
    public boolean displayCollisionColor = false;

    /**
     * Liste des dernières collisions détectées
     */
    private final LinkedHashSet<Collision> collisions;

    /**
     * Tableau regroupant les méthodes à utiliser pour checker les collisions entre chaque type d'entité. La position dans la table est donnée par le cardinal de la classe de l'entité
     * Par exemple pour une collision Cercle(cardinal 0) / Polygone(cardinal 2) on utilise collisionChecksJumpTable[0][2]
     */
    private final BiConsumer<Entity, Entity>[][] collisionChecksJumpTable = new BiConsumer[][] {
        { this::checkForCircleCircleCollision, this::checkForCircleRectCollision, this::checkForCirclePolyCollision }, // Circle
        { (r, c) -> checkForCircleRectCollision(c, r), this::checkForRectRectCollision, this::checkForPolyPolyCollision }, // Rectangle
        { (p, c) -> checkForCirclePolyCollision(c, p), this::checkForPolyPolyCollision, this::checkForPolyPolyCollision } // Polygon
    };

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

        // Center
        chunksToAddEntityTo.add(physics.getPositionHash(e.getPos()));

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
    public LinkedHashSet<Entity> getNearbyEntities(Tick tick, Entity e) {

        // On ne veut pas de doublons, et on parcourera la liste sans accès aléatoire
        // LinkedHashSet est donc un bon candidat
        // Il faut trouver une bonne fonction de hashing encore une fois. Sinon c'est pas rapide.
        LinkedHashSet<Entity> nearby = new LinkedHashSet<>();
        Set<Integer> chunksId = getChunksContaining(e);

        for (int chunkId : chunksId)
            if (chunkId >= 0 && chunkId < physics.getTotalChunkCount())
                nearby.addAll(tick.chunks.get(chunkId).entities);

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

        if (entity.getCollisionType() == Collision.CollisionType.IGNORE || target.getCollisionType() == Collision.CollisionType.IGNORE) {

            if (displayCollisionColor) {
                entity.setOutlineColor(Color.gray);
                target.setOutlineColor(Color.gray);
            }

            // Not firing collision ignored yet. Will be done after in Collision.resolve when the if condition will fail bc of CollisionType == IGNORE
        }

        // Broad phase
        if (!entity.getAABB().intersects(target.getAABB())) {

            if (displayCollisionColor) {
                entity.setOutlineColor(Color.green);
                target.setOutlineColor(Color.green);
            }

        } else {

            for (CollisionListener listener : entity.getCollisionListeners())
                listener.aabbCollided(entity, target);

            if (displayCollisionColor) {
                entity.setOutlineColor(Color.pink);
                target.setOutlineColor(Color.pink);
            }

            // Narrow phase
            collisionChecksJumpTable[entity.cardinal()][target.cardinal()].accept(entity, target);
        }
    }

    /**
     * Méthode enregistrant une collision entre deux cercles si elle a bien lieu
     * @param entity premier cercle pour la vérification
     * @param target deuxième cercle pour la vérification
     */
    public void checkForCircleCircleCollision(Object entity, Object target) {

        Circle reference = (Circle) entity;
        Circle incident = (Circle) target;
        if (reference.getPos().sqrdDist(incident.getPos()) <= Math.pow(reference.getR() + incident.getR(), 2))
            logCollision(reference, incident, Physics::generateCircleCircleManifold);
    }

    /**
     * Méthode enregistrant une collision entre deux rectangles si elle a bien lieu
     * @param entity premier rectangle pour la vérification
     * @param target deuxième rectangle pour la vérification
     */
    private void checkForRectRectCollision(Object entity, Object target) {

        Rect reference = (Rect) entity;
        Rect incident = (Rect) target;
        if (Geometry.rectOnRectSAT(reference, incident))
            logCollision(reference, incident, Physics::generatePolygonPolygonManifold);
    }

    /**
     * Méthode enregistrant une collision entre un rectangle et un cercle si elle a bien lieu
     * @param circle premier rectangle pour la vérification
     * @param rect deuxième rectangle pour la vérification
     */
    public void checkForCircleRectCollision(Object circle, Object rect) {

        Circle reference = (Circle) circle;
        Rect incident = (Rect) rect;
        if(Geometry.circleIntersectRectByClamping(reference, incident))
            logCollision(reference, incident, Physics::generateCircleRectangleManifold);
    }

    /**
     * Méthode enregistrant une collision entre deux polygones si elle a bien lieu
     * @param entity premier rectangle pour la vérification
     * @param target deuxième rectangle pour la vérification
     */
    public void checkForPolyPolyCollision(Object entity, Object target) {

        Polygon reference = (Polygon) entity;
        Polygon incident = (Polygon) target;
        if (Geometry.SAT(reference, incident))
            logCollision(reference, incident, Physics::generatePolygonPolygonManifold);
    }

    /**
     * Méthode enregistrant une collision entre un polygone et un cercle. Vérifie plus tard si elle a lieu et annule la collision s'il le faut
     * @param circle premier rectangle pour la vérification
     * @param polygon deuxième rectangle pour la vérification
     */
    private void checkForCirclePolyCollision(Object circle, Object polygon) {

        // No smart checks, they are done while computing the normal and contact point. Collision is dismissed (generateCirclePolygonManifold returns false) if they aren't intersecting
        logCollision((Polygon)polygon, (Circle)circle, Physics::generateCirclePolygonManifold);
    }

    /**
     * Ajoute une collision au registre des collisions détectée lors du tick
     * @param reference première entité impliquée dans la collision
     * @param incident deuxième entité impliquée dans la collision
     * @param resolvingFunction fonction qui sera utilisée pour la résolution de la collision
     */
    public void logCollision(Entity reference, Entity incident, Function<Collision, Boolean> resolvingFunction) {

        collisions.add(new Collision(reference, incident, resolvingFunction));
    }

    // TODO use for raycasting
    @Deprecated
    public void checkForCircleSegmentCollision(Circle entity, Circle target) {

    }

    /**
     * Renvoie le registre des collisions détectées lors du tick
     * @return collisions
     */
    public LinkedHashSet<Collision> getRegisteredCollision() {

        return this.collisions;
    }

    /**
     * Vide le registre des collisions
     */
    public void clearCollisions() {

        this.collisions.clear();
    }

    /**
     * Informe si la détection de collision change la couleur des entités
     * - gris si la collision est ignorée
     * - vert si on a vérifié la collision entre deux objets mais que leurs AABB ne sont pas en intersection
     * - rose si les AABB sont en intersection
     * - rouge si la détection est confirmée (pour le Cercle-Polygone, la collision est tjr confirmée, mais annulée plus tard)
     *
     * @return true si les couleurs doivent être modifiées pour donner une information sur les collisions. false sinon
     */
    public boolean doesDisplayCollisionColor() {

        return displayCollisionColor;
    }

    /**
     * Détermine si la détection de collision change la couleur des entités
     * @see Collider#doesDisplayCollisionColor
     *
     * @param displayCollisionColor true si les couleurs doivent être modifiées pour donner une information sur les collisions. false sinon
     */
    public void setDisplayCollisionColor(boolean displayCollisionColor) {

        this.displayCollisionColor = displayCollisionColor;
    }

    public String toString() {

        return "Collider[JumpTable=Mat[" + collisionChecksJumpTable.length + "x" + collisionChecksJumpTable[0].length + "], displayCollisionColor=" + this.displayCollisionColor + ", preciseCollisionResolution=" + this.preciseResolution + "]";
    }
}
package fr.insalyon.mxyns.collinsa.physics;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.clocks.MillisClock;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collider;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.collisions.CollisionAdapter;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.entities.Polygon;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.physics.forces.Force;
import fr.insalyon.mxyns.collinsa.threads.ProcessingThread;
import fr.insalyon.mxyns.collinsa.utils.geo.Geometry;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Moteur physique s'occupant de la logique physique du jeu.
 */
public class Physics {


    /**
     * Moteur de calcul de collisions
     */
    final private Collider collider = new Collider(this);

    /**
     * Thread de calcul dédié à la mise à jour de la simulation
     */
    private ProcessingThread processingThread;

    /**
     * ArrayList des différents Chunks partitionnant le monde
     * La façon dont sont gérés les chunks permet quand même de garantir l'unicité de chaque chunk ajouté même si on n'a pas un 'Set'
     * L'ArrayList permet d'accéder aléatoirement à un Chunk avec une complexité O(1) ce qui est nécessaire pour une simulation dense ou très vaste (à grand nombre de Chunks).
     * Les Chunks sont organisés grâce au SpatialHashing qui est performant dans les simulations où la répartition des éléments est plutôt homogène.
     * Un Oct-Tree serait moins couteux en mémoire mais plus difficile à implémenter et pas forcément plus performant.
     *
     * ArrayList Complexity:
     * | Add  | Remove | Get  | Contains | Next |
     * | O(1) |  O(n)  | O(1) |   O(n)   | O(1) |
     */
    private final ArrayList<Chunk> chunks = new ArrayList<>();

    /**
     * Permet de stocker la taille des Chunks sans avoir à accéder au Set des chunks à chaque fois
     */
    private final Vec2f chunkSize = Vec2f.zero();

    /**
     * Permet de stocker le nombre de Chunks sans avoir à accéder au Set des chunks à chaque fois
     */
    private final Vec2f chunkCount = Vec2f.zero();

    /**
     * Permet de stocker le nombre total de Chunks sans avoir à accéder au Set des chunks ni à multiplier chunkCount.x par chunkCount.y à chaque fois
     */
    private int totalChunkCount;

    /**
     * Temps fixé en en millisecondes dont la simulation doit avancer à chaque tour si on n'est pas en mode real-time
     */
    private int fixedDeltaTime;

    /**
     * Détermine si la simulation doit être mise à jour en temps réel (peut provoquer des incohérences, la simulation n'est plus déterministe)
     */
    private boolean isRealtime;

    public double totalElapsedTime = 0;

    /**
     * ArrayList des entités présentes dans la simulation.
     * Obligatoire pour pouvoir garder un accès des instances existantes puisqu'elles sont enregistrées temporairement dans les Chunk
     *
     * ArrayList Complexity:
     * | Add  | Remove | Get  | Contains | Next |
     * | O(1) |  O(n)  | O(1) |   O(n)   | O(1) |
     *
     * Suppression d'entité plutôt rare. Add, Get et Next sont systématiquement utilisés
     */
    // FIXME: change this slow bs
    final private CopyOnWriteArrayList<Entity> entities = new CopyOnWriteArrayList<>();

    final public ArrayList<Force> forces = new ArrayList<>(), globalForces = new ArrayList<>();

    /**
     * Largeur et hauteur de la simulation en mètres
     */
    private float width, height;

    /**
     * Crée un moteur physique, avec un nombre défini de chunk avec un refreshRate par défaut de 60, isRealtime = true, fixedDeltaTime = 10
     * @param width largeur de la simulation en mètres
     * @param height hauteur de la simulation en mètres
     * @param horizontalChunkCount nombre de chunks dans à l'horizontale
     * @param verticalChunkCount nombre de chunks dans à la verticale
     */
    public Physics(int width, int height, int horizontalChunkCount, int verticalChunkCount) {

        this(width, height, horizontalChunkCount, verticalChunkCount, 60, true, 10);
    }
    /**
     * Crée un moteur physique, avec un nombre défini de chunk et un refreshRate
     * @param width largeur de la simulation en mètres
     * @param height hauteur de la simulation en mètres
     * @param horizontalChunkCount nombre de chunks dans à l'horizontale
     * @param verticalChunkCount nombre de chunks dans à la verticale
     * @param refreshRate taux de rafraichissement visé par le ProcessingThread
     */
    public Physics(int width, int height, int horizontalChunkCount, int verticalChunkCount, int refreshRate, boolean isRealtime, int fixedDeltaTime) {

        this.width = width;
        this.height = height;
        this.isRealtime = isRealtime;
        this.fixedDeltaTime = fixedDeltaTime;

        processingThread = new ProcessingThread(this, new MillisClock(), refreshRate);
        buildChunks(horizontalChunkCount, verticalChunkCount, width * 1.0f / horizontalChunkCount, height * 1.0f / verticalChunkCount);
    }

    /**
     * Démarre le Thread de calcul et donc le rendu (mise à jour de la simulation)
     */
    public void begin() {

        processingThread.start();
    }

    /**
     * Met en pause le Thread de calcul et donc le rendu (mise à jour de la simulation)
     * @param delay durée de pause
     */
    public void pause(long delay) throws InterruptedException {

        processingThread.sleep(delay);
    }

    /**
     * Stoppe le Thread de calcul et donc le rendu (mise à jour de la simulation)
     */
    public void stop() {

        processingThread.queryStop();
    }

    /**
     * Ajoute une entité au monde.
     * TODO: (Vérifier par la même occasion s'il faut redimensionner les Chunks ou non pas pour l'instant)
     */
    public void addEntity(Entity e) {

        entities.add(e);

        for (int a : collider.getChunksContaining(e))
            if (a >= 0 && a < totalChunkCount)
                chunks.get(a).entities.add(e);
    }

    /**
     * Supprime une entité du monde
     * @param entity entité à supprimer
     */
    public void removeEntity(Entity entity) {

        entity.setActivated(false);

        entities.remove(entity);
        forces.removeIf(force -> force.affects(entity));
        Collinsa.INSTANCE.getMonitoring().entityMonitoring.stopMonitoring(entity);
    }

    /**
     * Replace une entité dans les Chunk auxquels il appartient
     * @param entity entité à replacer
     */
    public void spatialHashing(Entity entity) {

        for (int a : collider.getChunksContaining(entity))
            if (a >= 0 && a < totalChunkCount)
                chunks.get(a).entities.add(entity);
    }

    /**
     * Replace toutes les entités dans les Chunks
     */
    public void spatialHashing() {

        for (Entity e : entities)
            for (int a : collider.getChunksContaining(e))
                if (a >= 0 && a < totalChunkCount)
                    chunks.get(a).entities.add(e);
    }

    /**
     * Partitionne le monde en [n_x * n_y] chunks de taille [w * h]
     * @param n_x nombre de chunks à l'horizontale
     * @param n_y nombre de chunks à la verticale
     * @param w largeur d'un chunk
     * @param h hauteur d'un chunk
     * @see Chunk
     */
    private void buildChunks(int n_x, int n_y, float w, float h) {

        chunks.clear();
        for (int y = 0; y < n_y; ++y)
            for (int x = 0; x < n_x; ++x)
                chunks.add(new Chunk(x * w, y * h, w, h));

        chunkSize.set(w, h);
        chunkCount.set(n_x, n_y);
        totalChunkCount = n_x * n_y;
    }

    /**
     * Vide le contenu de tous les chunks
     */
    public void clearChunks() {

        chunks.forEach(chunk -> chunk.entities.clear());
    }

    /**
     * Crée un hash unique au Chunk permettant de le trier dans le Chunks Set
     * @return unique hash integer
     */
    public int hashChunk(Chunk chunk) {

        return (int)(chunk.bounds.getX() / chunkSize.x) + (int)chunkCount.x * (int)(chunk.bounds.getY() / chunkSize.y);
    }

    /**
     * Crée un hash unique de Chunk permettant de trouver à quel Chunk appartient le point
     * @return unique hash integer
     */
    public int getPositionHash(Vec2f vec) {

        return (int)(vec.x / chunkSize.x) + (int)chunkCount.x * (int)(vec.y / chunkSize.y);
    }

    /**
     * Crée un hash unique de Chunk permettant de trouver à quel Chunk appartient le point
     * @return unique hash integer
     */
    public int getPositionHash(float x, float y) {

        return (int)(x / chunkSize.x) + (int)chunkCount.x * (int)(y / chunkSize.y);
    }
    /**
     * Crée un hash unique de Chunk permettant de trouver à quel Chunk appartient le point
     * @return unique hash integer
     */
    public int getPositionHash(double x, double y) {

        return (int)(x / chunkSize.x) + (int)chunkCount.x * (int)(y / chunkSize.y);
    }

    /**
     * Crée un hash unique de Chunk permettant de trouver à quel Chunk appartient le point
     * @return unique hash integer
     */
    public int getPositionHash(int x, int y) {

        return (int)(x / chunkSize.x) + (int)chunkCount.x * (int)(y / chunkSize.y);
    }

    /**
     * Renvoie l'entité la plus proche d'un point dans un certain rayon
     * @param pos centre de la recherche
     * @param radius rayon de recherche
     * @return entité trouvée s'il y en a une
     */
    public Entity getClosestEntity(Vec2f pos, float radius) {

        final Entity[] selected = new Entity[1];
        if (processingThread.isInterrupted()) {

            selected[0] = getClosestAABBIntersectingEntity(pos, radius);

            if (selected[0] != null)
                return selected[0];

        } else {

            Circle circle = new Circle(pos, radius);
            circle.setCollisionType(Collision.CollisionType.IGNORE);
            circle.addCollisionListener(new CollisionAdapter() {

                @Override
                public void collisionIgnored(Entity source, Entity target, Collision toResolve) {

                    removeEntity(source);
                    selected[0] = target;
                }
            });

            addEntity(circle);

            try {
                Thread.sleep((long) (2 * processingThread.getClock().getLastElapsed() * processingThread.getClock().toSec() * 1000));
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            removeEntity(circle);
        }

        return selected[0];
    }

    /**
     * Renvoie l'entité la plus proche d'une autre (avec intersection d'AABB)
     *
     * @param entity cible
     * @return entité trouvée ou null
     */
    public Entity getClosestAABBIntersectingEntity(Entity entity) {

        float minDistance = Float.POSITIVE_INFINITY, tmp;
        Entity hitEntity = null;
        for (Entity test : collider.getNearbyEntities(entity))
            if (entity.getAABB().intersects(test.getAABB())) {
                if ((tmp=entity.getPos().sqrdDist(test.getPos())) < minDistance) {
                    minDistance = tmp;
                    hitEntity = test;
                }
            }

        return hitEntity;
    }
    public Entity getClosestAABBIntersectingEntity(Vec2f pos, float radius) {

        return getClosestAABBIntersectingEntity(new Circle(pos, radius));
    }

    // TODO : change all float computations by double computations for more precision
    /**
     * Calcule la normale et les points de contacts entre deux cercles avant d'appliquer une bounceImpulse et une frictionImpulse
     * @param toResolve collision entre deux cercles
     */
    public static boolean generateCircleCircleManifold(Collision toResolve) {

        Circle circleA = (Circle) toResolve.getReference();
        Circle circleB = (Circle) toResolve.getIncident();

        // la normale de la collision
        Vec2f normal = circleA.getPos().copy().sub(circleB.getPos());

        toResolve.penetrations = new float[] { circleA.getR() + circleB.getR() - normal.mag() };

        if (normal.normalize() == null) // résout les pbs d'infini
            return false;

        toResolve.normal = normal;
        toResolve.centerToContactReference = new Vec2f[] {Vec2f.zero().add(normal, -circleA.getR())};
        toResolve.centerToContactIncident = new Vec2f[] {Vec2f.zero().add(normal, circleB.getR())};

        return true;
    }

    /**
     * Calcule la normale et les points de contacts entre un cercle et un rectangle.
     * @param toResolve collision entre les deux objets (cercle en reference et rectangle en incident)
     */
    public static boolean generateCircleRectangleManifold(Collision toResolve) {

        Circle circle = (Circle) toResolve.getReference();
        Rect rect = (Rect) toResolve.getIncident();

        // Passage en coordonnées relatives au rectangle
        Vec2f upos = Geometry.rotatePointAboutCenter(circle.getPos(), rect.getPos(), -rect.getRot()).toFloat();
        Vec2f clampedPos = Geometry.clampPointToRect(upos.toDouble(), rect).toFloat();

        Vec2f normal; float penetration;
        if (clampedPos.sqrdDist(upos) == 0.0f) {
            clampedPos = Geometry.clampPointInsideRect(upos.toDouble(), rect).toFloat();
            normal = clampedPos.copy().sub(upos);
            penetration = circle.getR() + clampedPos.dist(upos);
        } else {
            normal = upos.copy().sub(clampedPos);
            penetration = circle.getR() - clampedPos.dist(upos);
        }


        // Retour aux coordonnées absolues, pas besoin de centre de rotation puisque normal est la différence de deux vecteurs "relatifs"
        normal.rotate(rect.getRot());

        if(normal.normalize() == null) // résout les pbs d'infini
            return false;

        toResolve.penetrations = new float[] { penetration };
        toResolve.normal = normal;

        toResolve.centerToContactIncident = new Vec2f[] {Geometry.rotatePointAboutCenter(clampedPos, rect.getPos(), rect.getRot()).toFloat().sub(rect.getPos())};
        toResolve.centerToContactReference = new Vec2f[] {Vec2f.zero().add(normal, -circle.getR())};

        return true;
    }

    /**
     * Calcule la normale et les points de contacts entre deux polygones.
     * @param toResolve collision entre les deux polygones
     */
    public static boolean generatePolygonPolygonManifold(Collision toResolve) {

        Polygon source = (Polygon) toResolve.getReference();
        Polygon target = (Polygon) toResolve.getIncident();

        // Check for a separating axis with A's face planes
        int[] faceA = { 0 };
        float penetrationA = Geometry.findAxisOfLeastPenetration( faceA, source, target);

        // Check for a separating axis with B's face planes
        int[] faceB = { 0 };
        float penetrationB = Geometry.findAxisOfLeastPenetration( faceB, target, source );

        int referenceIndex;
        boolean flip = false; // Always point from b to a

        Polygon reference, incident;

        // Determine which shape contains reference face
        if (penetrationA < penetrationB) {
            reference = source;
            incident = target;
            referenceIndex = faceA[0];
            flip = true;
        } else {
            reference = target;
            incident = source;
            referenceIndex = faceB[0];
        }

        Vec2f[] incidentFace = Vec2f.arrayOf(2);
        Geometry.findIncidentFace(incidentFace, reference, incident, referenceIndex);

        Vec2f v1 = reference.getVertices()[referenceIndex];

        Vec2f referenceFaceTangent = Geometry.getEdge(reference.getVertices(), referenceIndex);
        Vec2f refFaceNormal = new Vec2f(referenceFaceTangent.y, -referenceFaceTangent.x).neg();
        
        referenceIndex = (referenceIndex + 1) % reference.getVertices().length;
        Vec2f v2 = reference.getVertices()[referenceIndex];

        if (refFaceNormal.normalize() == null) {
            System.out.println("[Physics.generatePolygonPolygonManifold] null faceNormal");
            return false;
        }

        float refC = Vec2f.dot(refFaceNormal, v2);
        float negSide = -Vec2f.dot(referenceFaceTangent, v1);
        float posSide = Vec2f.dot(referenceFaceTangent, v2);

        int clipped = 0;
        //FIXME, use doubles ?
        if ((clipped += Geometry.clip(referenceFaceTangent, posSide, incidentFace)) < 2 || (clipped += Geometry.clip(referenceFaceTangent.multOut(-1), negSide, incidentFace)) < 4) {

            System.out.println("[Physics.generatePolygonPolygonManifold] skip, clipped="+clipped);
            return false;
        }

        Vec2f[] contactPoints = new Vec2f[2];
        float[] penetrations = new float[2];

        int cp = 0;
        float separation = Vec2f.dot(refFaceNormal, incidentFace[0]) - refC;
        if (separation <= 0f) {

            penetrations[cp] = -separation;
            contactPoints[cp++] = incidentFace[0];

        } else
            penetrations[cp] = 0;

        separation = Vec2f.dot(refFaceNormal, incidentFace[1]) - refC;
        if (separation <= 0f) {

            penetrations[cp] = -separation;
            contactPoints[cp++] = incidentFace[1];
        }

        // Normal must be from B to A (target -> source)
        if (flip)
            refFaceNormal.neg();

        toResolve.normal = refFaceNormal;
        toResolve.penetrations = penetrations;
        toResolve.centerToContactReference = new Vec2f[cp];
        toResolve.centerToContactIncident = new Vec2f[cp];

        for (int i = 0; i < cp; ++i) {

            toResolve.centerToContactReference[i] = contactPoints[i].copy().sub(reference.getPos());
            toResolve.centerToContactIncident[i] = contactPoints[i].copy().sub(incident.getPos());
        }

        return true;
    }

    /**
     * Calcule la normale et les points de contacts entre un cercle et un polygone.
     * @param toResolve collision entre le cercle et le polygone
     */
    public static boolean generateCirclePolygonManifold(Collision toResolve) {

        Circle circle = (Circle) toResolve.getIncident();
        Polygon polygon = (Polygon) toResolve.getReference();

        Vec2f circlePos = Geometry.rotatePointAboutCenter(circle.getPos(), polygon.getPos(), -polygon.getRot()).toFloat().sub(polygon.getPos());

        Vec2f[] local_vertices = polygon.getLocalVertices();
        Vec2f[] normals = Geometry.getNormals(local_vertices);

        float separation = Float.NEGATIVE_INFINITY;
        int faceNormal = 0;
        for (int i = 0; i < polygon.getVertices().length; ++i) {

            Vec2f normal = normals[i];
            float s = Vec2f.dot(normal, local_vertices[i].copy().sub(circlePos));

            if (s > circle.getR())
                return false;

            if (s > separation) {
                separation = s;
                faceNormal = i;
            }
        }

        Vec2f normal = polygon.getNormals()[faceNormal];

        Vec2f v1 = local_vertices[faceNormal];
        Vec2f v2 = local_vertices[(faceNormal + 1) % polygon.getVertices().length];

        if (separation < 0) { // Center inside polygon

            toResolve.normal = normal;
            toResolve.penetrations = new float[] {Math.abs(separation - circle.getR())};
            Vec2f contact = circle.getPos().copy().add(normal, -Math.abs(separation - circle.getR()) + circle.getR());
            toResolve.centerToContactIncident = new Vec2f[] {normal.multOut(circle.getR())};
            toResolve.centerToContactReference = new Vec2f[] {contact.sub(polygon.getPos())};

            return true;
        }

        float dot1 = Vec2f.dot( circlePos.copy().sub( v1 ), v2.copy().sub( v1 ) );
        float dot2 = Vec2f.dot( circlePos.copy().sub( v2 ), v1.copy().sub( v2 ) );

        if (dot1 <= 0) { // Closer to v1
            if (circlePos.sqrdDist(v1) > circle.getR()*circle.getR())
                return false;

            normal = polygon.getVertices()[faceNormal].copy().sub(circle.getPos()).normalize();
            toResolve.normal = normal;
            toResolve.penetrations = new float[] {Math.abs(separation - circle.getR())};
            Vec2f contact = circle.getPos().copy().add(normal, -Math.abs(separation - circle.getR()) + circle.getR());
            toResolve.centerToContactIncident = new Vec2f[] {normal.multOut(circle.getR())};
            toResolve.centerToContactReference = new Vec2f[] {contact.sub(polygon.getPos())};

            return true;

        } else if (dot2 <= 0) { // Closer to v2

            if (circlePos.sqrdDist(v2) > circle.getR()*circle.getR())
                return false;

            normal = polygon.getVertices()[(faceNormal + 1) % polygon.getVertices().length].copy().sub(circle.getPos()).normalize();
            toResolve.normal = normal;
            toResolve.penetrations = new float[] {Math.abs(separation - circle.getR())};
            Vec2f contact = circle.getPos().copy().add(normal, -Math.abs(separation - circle.getR()) + circle.getR());
            toResolve.centerToContactIncident = new Vec2f[] {normal.multOut(circle.getR())};
            toResolve.centerToContactReference = new Vec2f[] {contact.sub(polygon.getPos())};

            return true;

        } else { // Closer to face

            if (Math.abs(Vec2f.dot(circlePos.copy().sub(v2), normals[faceNormal])) >= circle.getR())
                return false;

        }

        toResolve.normal = normal;
        toResolve.penetrations = new float[] {Math.abs(separation - circle.getR())};
        Vec2f contact = circle.getPos().copy().add(normal, -Math.abs(separation - circle.getR()) + circle.getR());
        toResolve.centerToContactIncident = new Vec2f[] {normal.multOut(circle.getR())};
        toResolve.centerToContactReference = new Vec2f[] {contact.sub(polygon.getPos())};

        return true;
    }

    /**
     * Déplace les deux entités en sens opposé selon la direction d'une normale
     *
     * @param normalBtoA normale allant de B vers A
     * @param penetration distance de penetration entre les deux entités
     * @param isKinematic un deux deux est-il cinématique ? si oui, on ne le déplace pas
     */
    public static void displace(Entity entityA, Entity entityB, Vec2f normalBtoA, float penetration, boolean isKinematic) {

        Vec2f displacementVector = normalBtoA.multOut(penetration);

        if (!isKinematic) {
            displacementVector.div(entityA.getInertia().getMassInv() + entityB.getInertia().getMassInv());

            entityA.getPos().add(displacementVector, entityA.getInertia().getMassInv());
            entityB.getPos().add(displacementVector, -entityB.getInertia().getMassInv());

            return;
        }

        if (!entityA.isKinematic())
            entityA.getPos().add(displacementVector);

        if (!entityB.isKinematic())
            entityB.getPos().sub(displacementVector);
    }

    /**
     * Calcule et applique une bounce impulse : une correction de vitesse pour prendre en compte une collision
     * @param entityA première entité impliquée dans la collision (source en général)
     * @param entityB deuxième entité impliquée dans la collision (target en général)
     * @param rA vecteur partant du centre de rotation de l'entité A vers son point de contact
     * @param rB vecteur partant du centre de rotation de l'entité B vers son point de contact
     * @param normalBtoA normale (unitaire) au point de contact (orientée de B vers A)
     * @return intensité de l'impulse appliquée à A selon normale (pour B il faut prendre l'opposé)
     */
    public static float bounceImpulseAmplitude(Entity entityA, Entity entityB, Vec2f rA, Vec2f rB, Vec2f normalBtoA) {

        float rACrossNSqrd = Vec2f.cross(rA, normalBtoA)*Vec2f.cross(rA, normalBtoA);
        float rBCrossNSqrd = Vec2f.cross(rB, normalBtoA)*Vec2f.cross(rB, normalBtoA);
        float inverseMassSum = entityA.getInertia().getMassInv() + entityB.getInertia().getMassInv() + rACrossNSqrd * entityA.getInertia().getJInv() + rBCrossNSqrd * entityB.getInertia().getJInv();

        Vec2f speedA = entityA.getVel().copy().add(Vec2f.cross(entityA.getAngVel(), rA));
        Vec2f speedB = entityB.getVel().copy().add(Vec2f.cross(entityB.getAngVel(), rB));

        Vec2f relativeSpeed = speedA.sub(speedB);

        if (relativeSpeed.dot(normalBtoA) > 0 || inverseMassSum == 0) return 0;

        // la restitution est la plus petite des deux
        float e = Math.min(entityB.getMaterial().restitution, entityA.getMaterial().restitution);

        // coefficient d'impulsion en fonction des masses et de la restitution
        // impulsion normale = k * vitesseRelative.normale
        return (1 + e) * Math.abs(relativeSpeed.dot(normalBtoA)) / inverseMassSum;
    }

    /**
     * Calcule et applique une friction impulse : une correction de vitesse pour prendre en compte les frottements lors d'une collision
     * @param entityA première entité impliquée dans la collision (source en général)
     * @param entityB deuxième entité impliquée dans la collision (target en général)
     * @param rA vecteur partant du centre de rotation de l'entité A vers son point de contact
     * @param rB vecteur partant du centre de rotation de l'entité B vers son point de contact
     * @param normalBtoA normale (unitaire) au point de contact (orientée de B vers A)
     * @param i_n intensité de la bounce impulse appliquée à cette collision (résultat de bounceImpulse)
     */
    public static Vec2f frictionImpulseVector(Entity entityA, Entity entityB, Vec2f rA, Vec2f rB, Vec2f normalBtoA, float i_n) {

        Vec2f speedA = entityA.getVel().copy().add(Vec2f.cross(entityA.getAngVel(), rA));
        Vec2f speedB = entityB.getVel().copy().add(Vec2f.cross(entityB.getAngVel(), rB));

        Vec2f relativeSpeed = speedB.sub(speedA);

        // Vecteur tangent, on le calcule ici puisque 'normal' est tjr unitaire, on utilise plus relativeSpeed et on a calculé sa projection
        Vec2f tangent = Vec2f.zero().add(relativeSpeed).sub(normalBtoA.multOut(relativeSpeed.dot(normalBtoA)));

        // Si on n'a pas de tangente, on n'a pas de frottements (tangente = 0 si relativeSpeed∙normal = relativeSpeed)
        // Autrement dit si la vitesse relative entre les deux objets est seulement sur la normale
        if (tangent.normalize() == null) // si la tangente est trop petite (car vitesse relative trop petite) pour être normalisée, on arrête tout pour ne pas avoir de vitesse/position infini ou NaN
            return null;

        float rACrossTSqrd = Vec2f.cross(rA, tangent)*Vec2f.cross(rA, tangent);
        float rBCrossTSqrd = Vec2f.cross(rB, tangent)*Vec2f.cross(rB, tangent);

        float inverseMassSum = entityA.getInertia().getMassInv() + entityB.getInertia().getMassInv() + rACrossTSqrd * entityA.getInertia().getJInv() + rBCrossTSqrd * entityB.getInertia().getJInv();

        if (inverseMassSum == 0)
            return null;

        float i_t = -relativeSpeed.dot(tangent) / inverseMassSum;

        // Loi de Coulomb F_frottements <= µF_normale(contact)
        Vec2f frictionImpulse;
        if (Math.abs(i_t) < Math.abs(i_n) * Material.frictionAverage(entityA.getMaterial().staticFriction, entityB.getMaterial().staticFriction))
            frictionImpulse = tangent.mult(-i_t);
        else
            frictionImpulse = tangent.mult(i_n * Material.frictionAverage(entityA.getMaterial().dynamicFriction, entityB.getMaterial().dynamicFriction));

        // Friction impulse sur A (reference)
        return frictionImpulse;
    }

    /**
     * Applique une impulsion sur une entité à un point de contact donnée et d'une intensité donnée.
     * @param entity entité sur laquelle appliqué l'impulse
     * @param centerToContact vecteur partant du centre de rotation de l'entité jusqu'au point où est appliqué l'impulsion
     * @param impulse intensité de l'impulsion
     */
    public static void applyImpulse(Entity entity, Vec2f centerToContact, Vec2f impulse) {

        entity.getVel().add(impulse, entity.getInertia().getMassInv());
        entity.setAngVel(entity.getAngVel() + entity.getInertia().getJInv() * Vec2f.cross(centerToContact, impulse));
    }

    /**
     * Renvoie l'instance du moteur de collisions associé à cette instance du moteur physique
     * @return l'instance du Collider associé au moteur physique
     * @see Collider
     */
    public Collider getCollider() {

        return collider;
    }

    /**
     * Renvoie l'array list contenant toutes les entités de la simulation
     * @return l'array list contenant toutes les entités
     */
    public CopyOnWriteArrayList<Entity> getEntities() {

        return entities;
    }

    /**
     * Renvoie l'array list des chunks du monde
     * @return array list des chunks découpant le monde
     * @see Chunk
     */
    public ArrayList<Chunk> getChunks() {

        return chunks;
    }

    /**
     * Renvoie la largeur du monde en mètres
     * @return largeur du monde en mètres
     */
    public float getWidth() {

        return width;
    }

    /**
     * Redéfinie la largeur du monde en mètres
     * @param width largeur du monde en mètres
     */
    public void setWidth(float width) {

        this.width = width;
    }

    /**
     * Renvoie la hauteur du monde en mètres
     * @return hauteur du monde en mètres
     */
    public float getHeight() {

        return height;
    }

    /**
     * Redéfinie la hauteur du monde en mètres
     * @param height hauteur du monde en mètres
     */
    public void setHeight(float height) {

        this.height = height;
    }

    /**
     * Redimensionne le monde. Mieux vaut le faire pendant que la simulation est en pause pour éviter les ConcurrentModificationException liées à l'ArrayList des Chunks
     * @param newSize nouvelle taille du monde
     */
    public void resize(Vec2f newSize) {

        setWidth(newSize.x);
        setHeight(newSize.y);
        buildChunks((int)chunkCount.x, (int)chunkCount.y, newSize.x / chunkCount.x, newSize.y / chunkCount.y);
    }

    /**
     * Renvoie la taille actuelle des chunks
     * @return Vec2f contenant la largeur d'un chunk en x et la hauteur en y
     */
    public Vec2f getChunkSize() {

        return chunkSize;
    }

    /**
     * Renvoie le nombre actuel de chunks contenus dans un vecteur
     * @return Vec2f contenant le nb de chunks en x et le nb de chunks en y
     */
    public Vec2f getChunkCount() {

        return chunkCount;
    }

    /**
     * Redéfini le nombre de chunks. Nécessite d'utiliser Physics.buildChunks après pour que les changements soient appliqués.
     * @param chunkCount Vec2f contenant le nb de chunks en x et le nb de chunks en y
     */
    public void setChunkCount(Vec2f chunkCount) {

        this.chunkCount.set((int)chunkCount.x, (int)chunkCount.y);
    }

    /**
     * Renvoie le nombre actuel de chunks au total
     * @return totalChunkCount nombre de chunks
     */
    public int getTotalChunkCount() {

        return totalChunkCount;
    }

    /**
     * Renvoie le Thread de calcul associé à la simulation
     * @return thread de rendu
     */
    public ProcessingThread getProcessingThread() {

        return processingThread;
    }

    /**
     * Définit le Thread de calcul associé à la simulation
     * @param processingThread nouveau thread de rendu
     */
    public void setProcessingThread(ProcessingThread processingThread) {

        this.processingThread = processingThread;
    }

    /**
     * Donne l'intervalle de temps dont la simulation avance par tick quand elle est en mode realTime = false;
     * @return fixedDeltaTime
     */
    public int getFixedDeltaTime() {

        return fixedDeltaTime;
    }

    /**
     * Redéfinit le fixedDeltaTime, c'est-à-dire l'intervalle de temps dont la simulation avance par tick quand elle est en mode realTime = false;
     * @param fixedDeltaTime nouvel intervalle de temps par tick
     */
    public void setFixedDeltaTime(int fixedDeltaTime) {

        this.fixedDeltaTime = fixedDeltaTime;
    }

    /**
     * Informe si la simulation est en mode temps-réel
     * @return isRealtime = true si on est en temps-réel
     */
    public boolean isRealtime() {

        return isRealtime;
    }

    /**
     * Détermine si la simulation doit fonctionner en temps réel ou non.
     * @param realtime true pour temps-réel.
     */
    public void setRealtime(boolean realtime) {

        isRealtime = realtime;
    }

    /**
     * Renvoie la position du centre du monde
     * @return vec2f(width/2, height/2)
     */
    public Vec2f getCenterPos() {

        return new Vec2f(width, height).mult(.5f);
    }

    /**
     * Renvoie la taille du monde
     * @return vec2f(width, height)
     */
    public Vec2f getSize() {

        return new Vec2f(width, height);
    }

    public String toString() {

        return "Physics[ Size: [" + width + "m x " + height + "m], " + "Chunks [" + (chunkCount.x * chunkCount.y) + "], Collider: \n    " + collider;
    }
}
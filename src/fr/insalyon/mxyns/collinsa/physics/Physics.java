package fr.insalyon.mxyns.collinsa.physics;

import fr.insalyon.mxyns.collinsa.clocks.MillisClock;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collider;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.entities.Polygon;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.threads.ProcessingThread;
import fr.insalyon.mxyns.collinsa.utils.Utils;
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
    private ArrayList<Chunk> chunks;

    /**
     * Permet de stocker la taille des Chunks sans avoir à accéder au Set des chunks à chaque fois
     */
    private Vec2f chunkSize = Vec2f.zero();

    /**
     * Permet de stocker le nombre de Chunks sans avoir à accéder au Set des chunks à chaque fois
     */
    private Vec2f chunkCount = Vec2f.zero();

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

    /**
     * Largeur et hauteur de la simulation en mètres
     */
    private int width, height;

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
        chunks = buildChunks(horizontalChunkCount, verticalChunkCount, width / horizontalChunkCount, height / verticalChunkCount);
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

        processingThread.interrupt();
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

        entities.remove(entity);
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
     * Partitionne le monde e [n_x * n_y] chunks de taille [w * h]
     * @param n_x nombre de chunks à l'horizontale
     * @param n_y nombre de chunks à la verticale
     * @param w largeur d'un chunk
     * @param h hauteur d'un chunk
     * @return SortedSet de Chunk. Triés par Hash
     * @see Chunk
     */
    private ArrayList<Chunk> buildChunks(int n_x, int n_y, int w, int h) {

        ArrayList<Chunk> chunksSet = new ArrayList<>();
        for (int y = 0; y < n_y; ++y)
            for (int x = 0; x < n_x; ++x)
                chunksSet.add(new Chunk(x * w, y * h, w, h));

        chunkSize.set(w, h);
        chunkCount.set(n_x, n_y);
        totalChunkCount = n_x * n_y;

        return chunksSet;
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

        return (int)(chunk.bounds.x / chunkSize.x) + (int)chunkCount.x * (int)(chunk.bounds.y / chunkSize.y);
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

    // TODO : change all float computations by double computations for more precision

    /**
     * Calcule la normale et les points de contacts entre deux cercles avant d'appliquer une bounceImpulse et une frictionImpulse
     * @param toResolve collision entre deux cercles
     */
    public static void resolveCircleCircleCollision(Collision toResolve) {

        Circle circleA = (Circle) toResolve.getSource();
        Circle circleB = (Circle) toResolve.getTarget();

        // la normale de la collision
        Vec2f normal = circleA.getPos().copy().sub(circleB.getPos());

        float penetrationDepth = circleA.getR() + circleB.getR() - normal.mag();

        if(normal.normalize() == null) // résout les pbs d'infini
            return;

        Vec2f centerToContactA = Vec2f.zero().add(normal, -circleA.getR());
        Vec2f centerToContactB = Vec2f.zero().add(normal, circleB.getR());

        float i_n = bounceImpulse(circleA, circleB, centerToContactA, centerToContactB, normal, penetrationDepth);
        frictionImpulse(circleA, circleB, centerToContactA, centerToContactB, normal, i_n);
    }

    /**
     * Calcule la normale et les points de contacts entre un cercle et un rectangle avant d'appliquer une bounceImpulse et une frictionImpulse
     * @param toResolve collision entre les deux objets (cercle en source et rectangle en target)
     */
    public static void resolveCircleRectangleCollision(Collision toResolve) {

        Circle circle = (Circle) toResolve.getSource();
        Rect rect = (Rect) toResolve.getTarget();

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
            return;

        Vec2f centerToContactRectangle = Geometry.rotatePointAboutCenter(clampedPos, rect.getPos(), rect.getRot()).toFloat().sub(rect.getPos());
        Vec2f centerToContactCircle = circle.getPos().copy().add(normal, -circle.getR()).sub(circle.getPos());

        float i_n = bounceImpulse(circle, rect, centerToContactCircle, centerToContactRectangle, normal, penetration);
        frictionImpulse(circle, rect, centerToContactCircle, centerToContactRectangle, normal, i_n);
    }

    public static void resolvePolygonPolygonCollision(Collision toResolve) {

        Polygon polygon = (Polygon) toResolve.getSource();
        Polygon polygon2 = (Polygon) toResolve.getTarget();

        int[] faceA = { -1 };
        float penA = Geometry.findAxisOfLeastPenetration(faceA, polygon, polygon2);

        int[] faceB = { -1 };
        float penB = Geometry.findAxisOfLeastPenetration(faceB, polygon2, polygon);

        int referenceIndex;

        Polygon reference, incident;
        if (penA < penB) {

            reference = polygon;
            incident = polygon2;
            referenceIndex = faceA[0];
        } else {

            reference = polygon2;
            incident = polygon;
            referenceIndex = faceB[0];
        }

        // FIXME incident face mal détectée quand un seul point pénètre
        Vec2f[] incidentFace = new Vec2f[2];
        Geometry.findIncidentFace(incidentFace, incident, Geometry.getNormals(reference.getVertices())[referenceIndex]);

        Vec2f v1 = reference.getVertices()[referenceIndex];
        referenceIndex = (referenceIndex + (reference.getVertices().length - 1)) % reference.getVertices().length;
        Vec2f v2 = reference.getVertices()[referenceIndex];

        Vec2f referenceFaceTangent = v2.copy().sub(v1).normalize();
        Vec2f refFaceNormal = new Vec2f(referenceFaceTangent.y, -referenceFaceTangent.x);
        Vec2f normal = refFaceNormal.copy();

        float refC = Vec2f.dot(refFaceNormal, v1);
        float negSide = -Vec2f.dot(referenceFaceTangent, v1);
        float posSide = Vec2f.dot(referenceFaceTangent, v2);

        if (Geometry.clip(referenceFaceTangent, posSide, incidentFace) + Geometry.clip(referenceFaceTangent.multOut(-1), negSide, incidentFace) < 4) { /*System.out.println("skip");*/ }

        Vec2f[] contactPoints = new Vec2f[2];
        float[] penetrations = new float[2];

        int cp = 0;
        float separation = Vec2f.dot(refFaceNormal, incidentFace[0]) - refC;
        if (separation >= 0f) {

            penetrations[cp] = -separation;
            contactPoints[cp++] = incidentFace[0];
        } else {
            penetrations[cp] = 0;
        }

        separation = Vec2f.dot(refFaceNormal, incidentFace[1]) - refC;
        if (separation >= 0f) {

            penetrations[cp] = -separation;
            contactPoints[cp++] = incidentFace[1];
        }

        int contactCount = cp;

        if(normal.normalize() == null) // résout les pbs d'infini
            return;

        float mean = Utils.mean(penetrations);
        float half_min = Utils.min(penetrations) * 0.5f;

        //incident.getPos().add(normal, half_min);
        //reference.getPos().add(normal, -half_min);

        for (int i = 0; i < contactCount; ++i) {

            Vec2f centerToContactReference = contactPoints[i].copy().sub(reference.getPos());
            Vec2f centerToContactIncident = contactPoints[i].copy().sub(incident.getPos());

            float i_n = bounceImpulse(reference, incident, centerToContactReference, centerToContactIncident, normal, mean);
            frictionImpulse(reference, incident, centerToContactReference, centerToContactIncident, normal, i_n);
        }
    }

    /**
     * Calcule et applique une bounce impulse : une correction de vitesse pour prendre en compte une collision
     * @param entityA première entité impliquée dans la collision (source en général)
     * @param entityB deuxième entité impliquée dans la collision (target en général)
     * @param rA vecteur partant du centre de rotation de l'entité A vers son point de contact
     * @param rB vecteur partant du centre de rotation de l'entité B vers son point de contact
     * @param normal normale (unitaire) au point de contact (orientée de B vers A)
     * @param penetration profondeur de penetration de l'objet A dans B (ou inversement). positif.
     * @return intensité de l'impulse appliquée à A selon normale (pour B il faut prendre l'opposé)
     */
    // FIXME split bounceImpulse and displacement
    private static float bounceImpulse(Entity entityA, Entity entityB, Vec2f rA, Vec2f rB, Vec2f normal, float penetration) {

        float rACrossNSqrd = Vec2f.cross(rA, normal)*Vec2f.cross(rA, normal);
        float rBCrossNSqrd = Vec2f.cross(rB, normal)*Vec2f.cross(rB, normal);
        float inverseMassSum = entityA.getInertia().getMassInv() + entityB.getInertia().getMassInv() + rACrossNSqrd * entityA.getInertia().getJInv() + rBCrossNSqrd * entityB.getInertia().getJInv();

        Vec2f speedA = entityA.getVel().copy().add(Vec2f.cross(entityA.getAngVel(), rA));
        Vec2f speedB = entityB.getVel().copy().add(Vec2f.cross(entityB.getAngVel(), rB));

        Vec2f relativeSpeed = speedA.sub(speedB);

        if (relativeSpeed.dot(normal) > 0 || inverseMassSum == 0) return 0;

        // la restitution est la plus petite des deux
        float e = Math.min(entityB.getMaterial().restitution, entityA.getMaterial().restitution);

        // coefficient d'impulsion en fonction des masses et de la restitution
        // impulsion normale = k * vitesseRelative.normale
        float i_n = (1+e) * Math.abs(relativeSpeed.dot(normal)) / inverseMassSum;

        // Vecteur de repositionnement
        // TODO: set 0.5f factor to 1 when one of the objects is kinematic to avoid sinking
        Vec2f displacementVector = normal.multOut(penetration * .5f);
        Vec2f impulse = normal.multOut(i_n);

        if (!entityA.isKinematic()) {

            entityA.getPos().add(displacementVector);
            applyImpulse(entityA, rA, impulse);
        }

        if(!entityB.isKinematic()) {

            entityB.getPos().sub(displacementVector);
            applyImpulse(entityB, rB, impulse.neg());
        }

        return i_n;
    }

    /**
     * Calcule et applique une friction impulse : une correction de vitesse pour prendre en compte les frottements lors d'une collision
     * @param entityA première entité impliquée dans la collision (source en général)
     * @param entityB deuxième entité impliquée dans la collision (target en général)
     * @param rA vecteur partant du centre de rotation de l'entité A vers son point de contact
     * @param rB vecteur partant du centre de rotation de l'entité B vers son point de contact
     * @param normal normale (unitaire) au point de contact (orientée de B vers A)
     * @param i_n intensité de la bounce impulse appliquée à cette collision (résultat de bounceImpulse)
     */
    private static void frictionImpulse(Entity entityA, Entity entityB, Vec2f rA, Vec2f rB, Vec2f normal, float i_n) {

        Vec2f speedA = entityA.getVel().copy().add(Vec2f.cross(entityA.getAngVel(), rA));
        Vec2f speedB = entityB.getVel().copy().add(Vec2f.cross(entityB.getAngVel(), rB));

        Vec2f relativeSpeed = speedB.sub(speedA);

        // Vecteur tangent, on le calcule ici puisque 'normal' est tjr unitaire, on utilise plus relativeSpeed et on a calculé sa projection
        Vec2f tangent = Vec2f.zero().add(relativeSpeed).sub(normal.multOut(relativeSpeed.dot(normal)));

        // Si on n'a pas de tangente, on n'a pas de frottements (tangente = 0 si relativeSpeed∙normal = relativeSpeed)
        // Autrement dit si la vitesse relative entre les deux objets est seulement sur la normale
        if (tangent.normalize() == null) // si la tangente est trop petite (car vitesse relative trop petite) pour être normalisée, on arrête tout pour ne pas avoir de vitesse/position infini ou NaN
            return;

        float rACrossTSqrd = Vec2f.cross(rA, tangent)*Vec2f.cross(rA, tangent);
        float rBCrossTSqrd = Vec2f.cross(rB, tangent)*Vec2f.cross(rB, tangent);

        float inverseMassSum = entityA.getInertia().getMassInv() + entityB.getInertia().getMassInv() + rACrossTSqrd * entityA.getInertia().getJInv() + rBCrossTSqrd * entityB.getInertia().getJInv();

        if (inverseMassSum == 0)
            return;

        float i_t = -relativeSpeed.dot(tangent) / inverseMassSum;

        // Loi de Coulomb F_frottements <= µF_normale(contact)
        Vec2f frictionImpulse;
        if (Math.abs(i_t) < Math.abs(i_n) * Material.frictionAverage(entityA.getMaterial().staticFriction, entityB.getMaterial().staticFriction))
            frictionImpulse = tangent.mult(i_t);
        else
            frictionImpulse = tangent.mult(-i_n * Material.frictionAverage(entityA.getMaterial().dynamicFriction, entityB.getMaterial().dynamicFriction));

        if (!entityB.isKinematic())
            applyImpulse(entityB, rB, frictionImpulse);

        if (!entityA.isKinematic())
            applyImpulse(entityA, rA, frictionImpulse.neg());
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
    public int getWidth() {

        return width;
    }

    /**
     * Redéfinie la largeur du monde en mètres
     * @param width largeur du monde en mètres
     */
    private void setWidth(int width) {

        this.width = width;
    }

    /**
     * Renvoie la hauteur du monde en mètres
     * @return hauteur du monde en mètres
     */
    public int getHeight() {

        return height;
    }

    /**
     * Redéfinie la hauteur du monde en mètres
     * @param height hauteur du monde en mètres
     */
    private void setHeight(int height) {

        this.height = height;
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

    public String toString() {

        return "Physics[ Size: [" + width + "m x " + height + "m], " + "Chunks [" + (chunkCount.x * chunkCount.y) + "], Collider: \n    " + collider;
    }
}
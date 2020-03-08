package fr.insalyon.mxyns.collinsa.physics;

import fr.insalyon.mxyns.collinsa.clocks.MillisClock;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collider;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.threads.ProcessingThread;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.util.ArrayList;

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
    final private ArrayList<Entity> entities = new ArrayList<>();

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
        this.isRealtime = false;
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
    public ArrayList<Entity> getEntities() {

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

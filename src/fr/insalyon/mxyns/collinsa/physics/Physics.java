package fr.insalyon.mxyns.collinsa.physics;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2;

import java.awt.Rectangle;
import java.util.*;

/**
 * Moteur physique s'occupant de la logique physique du jeu.
 */
public class Physics {

    /**
     * Moteur de calcul de collisions
     */
    final private Collider collider = new Collider();

    /**
     * Ensemble trié des différents Chunks partitionnant le monde
     * L'implémentation de l'interface Set de SortedSet permet de garantir l'unicité de chaque chunk ajouté
     * Le caractère trié de ce Set permet de garder un ordre logique dans l'organisation des Chunks et donc une manière rapide de trouver le Chunk voulu par hash-ache
     */
    private ArrayList<Chunk> chunks;

    /**
     * Permet de stocker la taille des Chunks sans avoir à accéder au Set des chunks à chaque fois
     */
    private Vec2 chunkSize = Vec2.zero();

    /**
     * Permet de stocker la taille des Chunks sans avoir à accéder au Set des chunks à chaque fois
     */
    private Vec2 chunkCount = Vec2.zero();

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
     * Crée un moteur physique, avec un nombre défini de chunk
     * @param width largeur de la simulation en mètres
     * @param height hauteur de la simulation en mètres
     */
    public Physics(int width, int height, int horizontalChunkCount, int verticalChunkCount) {

        this.width = width;
        this.height = height;

        //chunks = Physics.generateChunks(horizontalChunkCount, verticalChunkCount, width / horizontalChunkCount, height / verticalChunkCount);
        chunks = buildChunks(horizontalChunkCount, verticalChunkCount, width / horizontalChunkCount, height / verticalChunkCount);
    }

    /**
     * Ajoute une entité au monde.
     * (Vérifie par la même occasion s'il faut redimensionner les Chunks ou non pas pour l'instant)
     */
    public void addEntity(Entity e) {

        entities.add(e);


        for (int a : getChunksContaining(e))
            if (a >= 0)
                chunks.get(a).entities.add(e);
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
        chunksToAddEntityTo.add(getPositionHash(aabb.x, aabb.y));

        //TopRight
        chunksToAddEntityTo.add(getPositionHash(aabb.x + aabb.width, aabb.y));

        //BottomRight
        chunksToAddEntityTo.add(getPositionHash(aabb.x + aabb.width, aabb.y + aabb.height));

        //BottomLeft
        chunksToAddEntityTo.add(getPositionHash(aabb.x, aabb.y + aabb.height));

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
            nearby.addAll(chunks.get(chunkId).entities);

        return nearby;
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
    public int getPositionHash(Vec2 vec) {

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
     * Partitionne le monde e [n_x * n_y] chunks de taille [w * h]
     * @param n_x nombre de chunks à l'horizontale
     * @param n_y nombre de chunks à la verticale
     * @param w largeur d'un chunk
     * @param h hauteur d'un chunk
     * @return Tableau2D des chunks: Chunk[y][x]
     * @see Chunk
     *
     * @deprecated not useless anymore
     */
    @Deprecated
    private static Chunk[][] generateChunks(int n_x, int n_y, int w, int h) {

        Chunk[][] chunks = new Chunk[n_y][n_x];

        for (int x = 0; x < n_x; ++x)
            for (int y = 0; y < n_y; ++y)
                chunks[y][x] = new Chunk(x * w, y * h, w, h);

        return chunks;
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
     * @return Vec2 contenant la largeur d'un chunk en x et la hauteur en y
     */
    public Vec2 getChunkSize() {

        return chunkSize;
    }

    /**
     * Renvoie le nombre actuel de chunks contenus dans un vecteur
     * @return Vec2 contenant le nb de chunks en x et le nb de chunks en y
     */
    public Vec2 getChunkCount() {

        return chunkCount;
    }

    public String toString() {

        return "Physics[ Size: [" + width + "m x " + height + "m], " + "Chunks [" + (chunkCount.x * chunkCount.y) + "], Collider: \n    " + collider;
    }
}

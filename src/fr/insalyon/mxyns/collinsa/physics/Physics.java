package fr.insalyon.mxyns.collinsa.physics;

/**
 * Moteur physique s'occupant de la logique physique du jeu.
 */
public class Physics {

    /**
     * Moteur de calcul de collisions
     */
    final private Collider collider = new Collider();

    /**
     * Tableau 2D des différents Chunks partitionnant le monde
     */
    private Chunk[][] chunks;

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

        chunks = Physics.generateChunks(horizontalChunkCount, verticalChunkCount, width / horizontalChunkCount, height / verticalChunkCount);

        System.out.println("Made " + (chunks.length * chunks[0].length) + " chunks");
    }

    /**
     * Partitionne le monde e [n_x * n_y] chunks de taille [w * h]
     * @param n_x nombre de chunks à l'horizontale
     * @param n_y nombre de chunks à la verticale
     * @param w largeur d'un chunk
     * @param h hauteur d'un chunk
     * @return Tableau2D des chunks: Chunk[y][x]
     * @see Chunk
     */
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
     * Renvoie le tableau 2D des chunks du monde
     * @return tableau 2D des chunks déccoupant le monde
     * @see Chunk
     */
    public Chunk[][] getChunks() {

        return chunks;
    }

    /**
     * Renvoie le Chunk de coordonnées (x,y)
     * @param x indice x du Chunk
     * @param y indice y du Chunk
     * @return chunks[y][x]
     */
    public Chunk getChunk(int x, int y) {

        return chunks[y][x];
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

    public String toString() {

        return "Physics[ Size: [" + width + "m x " + height + "m], " + "Chunks [" +chunks.length + "x" + chunks[0].length + "], Collider: \n    " + collider;
    }
}

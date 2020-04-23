package fr.insalyon.mxyns.collinsa.physics;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

import java.awt.geom.Rectangle2D;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Un chunk est une portion spatiale de la zone de calcul (le monde simulé) contenant une partie des entitées.
 * Permet de réduire le nombre de collision checks inutiles
 */
public class Chunk {

    /**
     * Rectangle représentant le chunk (position et taille)
     */
    public Rectangle2D bounds;

    /**
     * Contient les entités présentes dans le chunk
     */
    // TODO : optimize data structure (temporarily is CopyOnWriteArraySet to fix Concurrent Modification problems)
    final public CopyOnWriteArraySet<Entity> entities;

    /**
     * Un chunk
     * @param x position x en mètres
     * @param y position y en mètres
     * @param w largeur en mètres
     * @param h hauteur en mètres
     */
    public Chunk(float x, float y, float w, float h) {

        bounds = new Rectangle2D.Float(x, y, w, h).getBounds2D();
        entities = new CopyOnWriteArraySet<>();
    }

    /**
     * Déplace une entité contenue dans le chunk vers un autre Chunk
     * @param entity entité à déplacer
     * @param chunk chunk de destination
     */
    public void moveEntityTo(Entity entity, Chunk chunk) {

        entities.remove(entity);
        chunk.entities.add(entity);
    }

    public String toString() {

        return "Chunk[pos(T-L)= (" + bounds.getX() + ", " + bounds.getY() + "), size=[" + bounds.getWidth() + ", " + bounds.getHeight() + ") ";
    }
}

package fr.insalyon.mxyns.collinsa.physics;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

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
    final public Set<Entity> entities;

    /**
     * Un chunk
     * @param x position x en mètres
     * @param y position y en mètres
     * @param w largeur en mètres
     * @param h hauteur en mètres
     */
    public Chunk(float x, float y, float w, float h) {

        bounds = new Rectangle2D.Float(x, y, w, h).getBounds2D();
        entities = new HashSet<>();
    }
    /**
     * Un chunk
     * @param x position x en mètres
     * @param y position y en mètres
     * @param w largeur en mètres
     * @param h hauteur en mètres
     */
    public Chunk(double x, double y, double w, double h) {

        bounds = new Rectangle2D.Double(x, y, w, h);
        entities = new HashSet<>();
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

        return "Chunk[pos(T-L)= (" + bounds.getX() + ", " + bounds.getY() + "), size=[" + bounds.getWidth() + ", " + bounds.getHeight() + "), count = " + this.entities.size() + " ]";
    }

    public Chunk copy() {

        Chunk copy = new Chunk(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        entities.forEach(entity -> copy.entities.add(entity.copy()));

        return copy;
    }
}

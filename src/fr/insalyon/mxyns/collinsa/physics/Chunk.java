package fr.insalyon.mxyns.collinsa.physics;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

import java.awt.Rectangle;
import java.util.LinkedHashSet;

/**
 * Un chunk est une portion de la zone de calcul contenant une partie des entitées.
 * Permet de réduire le nombre de collision checks inutiles
 */
public class Chunk {

    /**
     * Rectangle représentant le chunk (position et taille)
     */
    public Rectangle rectangle;

    /**
     * Contient les entités présentes dans le chunk
     */
    public LinkedHashSet<Entity> entities;

    /**
     * Un chunk
     * @param x position x en mètres
     * @param y position y en mètres
     * @param w largeur en mètres
     * @param h hauteur en mètres
     */
    public Chunk(int x, int y, int w, int h) {

        rectangle = new Rectangle(x, y, w, h);
        entities = new LinkedHashSet<>();
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



}

package fr.insalyon.mxyns.collinsa.render;

import java.awt.Graphics2D;

/**
 * Interface implémentée par tout objet pouvant être rendu à l'écran par le Renderer
 */
public interface Renderable {

    /**
     * Méthode abstraite de rendu. Unique à chaque type d'élément affichable
     * @param renderer renderer utilisé pour le rendu
     * @param g graphics associé au panel pour dessin
     */
    void render(Renderer renderer, Graphics2D g);
}

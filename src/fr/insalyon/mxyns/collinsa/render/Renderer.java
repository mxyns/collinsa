package fr.insalyon.mxyns.collinsa.render;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.ui.panels.SandboxPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;


/**
 * Renderer s'occupe de créer un visuel correspondant à l'état de la Sandbox
 */
public class Renderer {

    /**
     * Rectangle renderBounds est un rectangle délimitant la zone de l'espace à rendre (tout objet en dehors de cette zone est toujours mis à jour mais n'est pas affiché)
     */
    private Rectangle renderBounds;

    public Renderer(int width, int height) {

        renderBounds = new Rectangle(width, height);
    }

    /**
     *
     * @param panel Le panel sur lequel afficher le contenu
     * @param g L'object Graphics associé au Panel permettant de dessiner dessus
     */
    public static void renderSandbox(SandboxPanel panel, Graphics g) {

        Rectangle renderBounds = Collinsa.getRenderer().renderBounds;

        Graphics2D g2 = (Graphics2D) g;
    }
}

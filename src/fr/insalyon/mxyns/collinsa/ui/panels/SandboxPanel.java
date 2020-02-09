package fr.insalyon.mxyns.collinsa.ui.panels;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.render.Renderer;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Panel dans lequel est affiché le monde
 */
public class SandboxPanel extends JPanel {

    private Renderer renderer;

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        renderer.renderSandbox(Collinsa.getPhysics(), (Graphics2D)g);

        // Test draw
        Circle circle = new Circle(300, 300, 100);
        renderer.renderCircle(circle, (Graphics2D)g);
        renderer.renderCircle(new Circle(0, 0, 50), (Graphics2D)g);
        renderer.renderCircle(new Circle(Collinsa.getPhysics().getWidth(), 0, 50), (Graphics2D)g);
        renderer.renderCircle(new Circle(0, Collinsa.getPhysics().getHeight(), 50), (Graphics2D)g);
        renderer.renderCircle(new Circle(Collinsa.getPhysics().getWidth(), Collinsa.getPhysics().getHeight(), 50), (Graphics2D)g);
    }

    /**
     * Renvoie le renderer associé au panel
     * @return renderer utilisé pour le rendu du panel
     */
    public Renderer getRenderer() {

        return renderer;
    }

    /**
     * Défini le Renderer associé à ce panel, ne pas utiliser sans redéfinir le panel associé au renderer.
     * @param renderer le nouveau renderer associé
     */
    public void setRenderer(Renderer renderer) {

        this.renderer = renderer;
    }
}

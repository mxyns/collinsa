package fr.insalyon.mxyns.collinsa.ui.panels;

import fr.insalyon.mxyns.collinsa.render.Renderer;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Panel dans lequel est affiché le monde
 */
public class SandboxPanel extends JPanel {

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        Renderer.renderSandbox(this, g);

        int width = getSize().width;
        int height = getSize().height;

        g.setColor(Color.BLACK);
        g.drawRect(5, 5, width - 10, height - 10);
    }
}

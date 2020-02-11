package fr.insalyon.mxyns.collinsa.ui.panels;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.render.CameraController;
import fr.insalyon.mxyns.collinsa.render.Renderer;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;

/**
 * Panel dans lequel est affiché le monde
 */
public class SandboxPanel extends JPanel implements FocusListener {

    /**
     * Renderer associé au panel
     */
    private Renderer renderer;

    public SandboxPanel() {

        //Rend le panel focusable pour pouvoir utiliser les controls clavier
        setFocusable(true);

        //Bordure quand il n'a pas le focus
        setBorder(BorderFactory.createLineBorder(Color.black, 2));

        addFocusListener(this);
    }
    public SandboxPanel(Renderer renderer) {

        this();

        // Ajoute le controleur de la caméra aux keyListeners du panel
        setRenderer(renderer);
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        renderer.renderSandbox(Collinsa.getPhysics(), (Graphics2D)g);
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

        // On enlève tous les controleurs de caméra associés
        for (KeyListener keyListener : this.getKeyListeners())
            if (keyListener instanceof CameraController)
                removeKeyListener(keyListener);

        //On ajoute le nouveau controleur associé à la nouvelle camera
        addKeyListener(renderer.getCameraController());
        addMouseWheelListener(renderer.getCameraController());
        addMouseListener(renderer.getCameraController());
        addMouseMotionListener(renderer.getCameraController());
    }

    /**
     * Lorsque la panel obtient le focus on change la bordure
     */
    @Override
    public void focusGained(FocusEvent e) {

        setBorder(BorderFactory.createLineBorder(Color.blue, 1));
    }

    /**
     * Lorsque le panel perd le focus, on désactive les mouvements de Camera
     */
    @Override
    public void focusLost(FocusEvent e) {

        renderer.getCameraController().deleteActiveKeys();
        setBorder(BorderFactory.createLineBorder(Color.black, 2));
    }
}

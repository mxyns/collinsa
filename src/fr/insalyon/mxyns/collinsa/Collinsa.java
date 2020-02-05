package fr.insalyon.mxyns.collinsa;

import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.ui.frames.MainFrame;

/**
 * Génère une instance Collinsa. Link toutes les classes et fait fonctionner le programme
 */
public class Collinsa {

    public static Collinsa INSTANCE;

    private Physics physics;
    private Renderer renderer;
    private MainFrame mainFrame;

    public Collinsa(int width, int height) {

        renderer = new Renderer(width, height);
        mainFrame = new MainFrame(width, height);
        physics = new Physics();
    }

    public static  void main(String[] args) {

        INSTANCE = new Collinsa(800, 400);
    }

    public static MainFrame getMainFrame() {

        return INSTANCE.mainFrame;
    }

    public static Renderer getRenderer() {

        return INSTANCE.renderer;
    }

    public static Physics getPhysics() {

        return INSTANCE.physics;
    }
}

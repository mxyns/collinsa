package fr.insalyon.mxyns.collinsa;

import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.ui.frames.MainFrame;

import java.awt.Toolkit;

/**
 * Génère une instance Collinsa. Link toutes les classes et fait fonctionner le programme
 */
public class Collinsa {

    /**
     * Stocke l'unique instance du programme pour un accès statique
     */
    public static Collinsa INSTANCE;

    /**
     * Instances du moteur physique, de  rendu, et de la Frame
     */
    private Physics physics;
    private Renderer renderer;
    private MainFrame mainFrame;

    public static double screenRatio = Toolkit.getDefaultToolkit().getScreenSize().getWidth() / Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    /**
     * Instancie Collinsa, un moteur physique et un moteur de rendu. La width et la height sont données en mètres
     *
     * @param width largeur en mètres de la simulation
     * @param height hauteur en mètres de la simulation
     */
    public Collinsa(int width, int height) {

        INSTANCE = this;

        physics = new Physics(width, height, 10, 10);
        mainFrame = new MainFrame(1440, (int)(1440 / screenRatio));
        renderer = new Renderer(mainFrame.sandboxPanel);

        renderer.setRenderScale(1);
        renderer.setCameraZoom(1);

        System.out.println("World: " + physics);
        System.out.println("Renderer: " + renderer);
        System.out.println("    Camera : " + renderer.getCamera());
    }

    public static void main(String[] args) {

        INSTANCE = new Collinsa(1440, (int)(1440 / screenRatio));
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

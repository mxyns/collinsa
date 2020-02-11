package fr.insalyon.mxyns.collinsa;

import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
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
    private static Collinsa INSTANCE;

    /**
     * Instances du moteur physique, de  rendu, et de la Frame
     */
    final private Physics physics;
    final private Renderer renderer;
    final private MainFrame mainFrame;

    private final static double screenRatio = Toolkit.getDefaultToolkit().getScreenSize().getWidth() / Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    /**
     * Instancie Collinsa, un moteur physique et un moteur de rendu. La width et la height sont données en mètres
     *
     * @param width largeur en mètres de la simulation
     * @param height hauteur en mètres de la simulation
     */
    private Collinsa(int width, int height) {

        INSTANCE = this;

        // On crée le Renderer en premier mais il est vide et inutilisable
        renderer = new Renderer();

        // On crée une instance de moteur physique vide.
        physics = new Physics(width, height, 10, 10);

        // On crée une page contenant un panel sur lequel rendre le contenu du moteur physique. A la création du panel, le CameraController lui est associé, puis il recupère le focus
        mainFrame = new MainFrame(1440, (int)(1440 / screenRatio));

        // Maintenant que le panel est prêt à être utilisé (défini et affiché à l'écran), on peut mettre en fonctionnement le renderer. Le panel est par la même occasion notifié du "changement" de renderer.
        setRenderer(renderer);


        renderer.setRenderScale(1);
        renderer.getCameraController().setCameraZoom(1);

        System.out.println("World: " + physics);
        System.out.println("Renderer: " + renderer);
        System.out.println("    Camera : " + renderer.getCameraController());

        Rect rect = new Rect(physics.getChunkSize().x / 2, physics.getChunkSize().y / 2, 60, 100);
        Rect rect1 = new Rect(Collinsa.getPhysics().getWidth() / 2, Collinsa.getPhysics().getHeight()/2, 100,20);
        Circle circle = new Circle(350, 400, 100);
        physics.addEntity(rect);
        physics.addEntity(rect1);
        physics.addEntity(circle);

        System.out.println(physics.getChunksContaining(rect));
        System.out.println(physics.getChunksContaining(rect1));
        System.out.println(physics.getChunksContaining(circle));




        // Pour forcer le dessin du SandboxPanel (par exemple pour avoir un texte qui suit la souris
        // Inutile quand le Thread de rendu sera créé
        /*
        while (true) {
            mainFrame.getSandboxPanel().repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */
    }

    private void setRenderer(Renderer renderer) {

        // On informe le renderer qu'il devra créer son rendu à partir des dimensions du sandboxPanel
        renderer.setDestination(mainFrame.getSandboxPanel());

        // On informe le panel qu'il devra utiliser renderer pour récupérer le rendu dans sa méthode paint
        getMainFrame().getSandboxPanel().setRenderer(renderer);
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

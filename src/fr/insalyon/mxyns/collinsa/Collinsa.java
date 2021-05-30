package fr.insalyon.mxyns.collinsa;

import fr.insalyon.mxyns.collinsa.clocks.MillisClock;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.presets.Preset;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.threads.RenderingThread;
import fr.insalyon.mxyns.collinsa.ui.frames.MainFrame;
import fr.insalyon.mxyns.collinsa.utils.Utils;
import fr.insalyon.mxyns.collinsa.utils.monitoring.Monitoring;

import javax.swing.*;
import java.awt.Toolkit;

import static java.lang.Thread.State.TIMED_WAITING;

// FIXME FORCES

/**
 * Génère une instance Collinsa. Link toutes les classes et fait fonctionner le programme
 */
public class Collinsa {

    /**
     * Stocke l'unique instance du programme pour un accès statique
     */
    public static Collinsa INSTANCE;

    /**
     * Instances du moteur physique, de rendu, et de la Frame
     */
    private final Physics physics;
    private Renderer renderer;
    private final Monitoring monitoring;
    final private MainFrame mainFrame;

    /**
     * Ratio largeur/hauteur de l'écran de l'utilisateur.
     * Utilisé pour générer des Frames avec le même ratio.
     */
    private final static double screenRatio = Toolkit.getDefaultToolkit().getScreenSize().getWidth() / Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    /**
     * Crée une unique instance du programme.
     */
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        // Crée l'INSTANCE unique du programme (Collinsa)
        INSTANCE = new Collinsa(1440, (int) (1440 / screenRatio));

        // Paramètres par défaut
            // Renderer settings
            // On pose une échelle d'affichage de 1 px/m
            INSTANCE.getRenderer().setRenderScale(1);

            // On pose un zoom caméra inital de x1.0
            INSTANCE.getRenderer().getCameraController().setCameraZoom(1f);

            // Physics settings
            INSTANCE.getPhysics().setRealtime(false);

            // On pose le framerate voulu
            INSTANCE.setFramerate(60);

        // si aucun preset n'est lancé, on applique tous les paramètres donnés
        Utils.applyParameters(INSTANCE, args);

        // Si on a activé un preset dans les paramètres de lancement on l'execute et on n'execute pas le programme principal
        if (args.length > 0 && args[0].equals("-s") && args.length > 1)
            Preset.EPreset.run(args[1], args, INSTANCE);


        // Affichage des infos du programme
        System.out.println("World: " + INSTANCE.getPhysics());
        System.out.println("Renderer: " + INSTANCE.getRenderer());

        // Initiate first tick from init tick
        // INSTANCE.physics.getProcessingThread().tick(0);

        //Monitor monitor = new Monitor();

        // Démarre le programme (Simulation & Rendu)
        if (!(INSTANCE.physics.getProcessingThread().isAlive() || INSTANCE.renderer.getRenderingThread().isAlive() || INSTANCE.mainFrame.getSandboxPanel().getRefreshingThread().isAlive()))
            INSTANCE.start();
    }

    /**
     * Instancie Collinsa, un moteur physique et un moteur de rendu. La width et la height sont données en mètres
     *
     * @param width  largeur en mètres de la simulation
     * @param height hauteur en mètres de la simulation
     */
    private Collinsa(int width, int height) {

        // On stocke l'unique instance qui sera créée de Collinsa pour un accès statique plus simple (pas besoin de passer l'instance de classe en classe)
        INSTANCE = this;

        // On crée le Renderer en premier mais il est vide et inutilisable, et n'a pas de thread associé (il ne peut donc pas fonctionner)
        renderer = new Renderer();

        // On crée une instance de moteur physique vide, elle se remplit de Chunks et crée un thread de calcul à son initialisation
        physics = new Physics(width, height, 3, 3);

        // On crée une page contenant un panel sur lequel rendre le contenu du moteur physique. A la création du panel, le CameraController lui est associé, puis il recupère le focus
        mainFrame = new MainFrame(1440, (int) (1440 / screenRatio));

        // On crée une instance pour surveiller le monde
        monitoring = new Monitoring();

        // Maintenant que le panel est prêt à être utilisé (défini et affiché à l'écran), on peut préparer le renderer au rendu. Le panel est par la même occasion notifié du "changement" de renderer.
        setRenderer(renderer);
    }

    /**
     * Démarre le programme (Simulation & Rendu)
     */
    public void start() {

        if (physics.getProcessingThread().isAlive() || renderer.getRenderingThread().isAlive() || mainFrame.getSandboxPanel().getRefreshingThread().isAlive()) {
            System.out.println("[Error] Already running");
            return;
        }

        physics.begin();
        renderer.begin();

        // On patiente histoire d'être sûr qu'une image ait bien été rendue.
        try {
            Thread.sleep(renderer.getRenderingThread().getDelay());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mainFrame.getSandboxPanel().beginRefresh();
    }

    /**
     * Démarre le programme (Simulation & Rendu) après un certain temps
     *
     * @param delay temps avant démarrage
     */
    public void startIn(long delay) {

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        start();
    }

    /**
     * Stoppe le programme (Simulation & Rendu)
     */
    public void stop() {

        System.out.println("[Shutdown] Global shutdown requested. Trying to terminate Threads safely.");

        physics.stop();
        while (!physics.getProcessingThread().isInterrupted()) {}
        System.out.println("[Shutdown] Physics engine stopped.");

        renderer.stop();
        while (!renderer.getRenderingThread().isInterrupted()) {}
        System.out.println("[Shutdown] Rendering engine stopped.");

        mainFrame.getSandboxPanel().stopRefresh();
        while (!mainFrame.getSandboxPanel().getRefreshingThread().isInterrupted()) {}
        System.out.println("[Shutdown] Display refreshing stopped.");
    }

    /**
     * Stoppe le programme (Simulation & Rendu) après un certain temps
     *
     * @param delay temps avant arrêt
     */
    public void stopIn(long delay) {

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stop();
    }

    /**
     * Mets en pause le programme (Simulation & Rendu)
     *
     * @param delay durée de pause
     */
    public void pause(long delay) throws InterruptedException {

        physics.pause(delay);
        while(!physics.getProcessingThread().getState().equals(TIMED_WAITING)) {}
        System.out.println("[Pause] Physics engine paused for ~" + delay +"ms");

        renderer.pause(delay);
        while(!renderer.getRenderingThread().getState().equals(TIMED_WAITING)) {}
        System.out.println("[Pause] Rendering engine paused for ~" + delay +"ms");
    }

    /**
     * (re-)Défini le Renderer utilisé par le programme, doit être appelée au moins une fois après l'initialisation du
     * Renderer, de Physics et de MainFrame
     *
     * @param renderer nouveau renderer utilisé
     */
    private void setRenderer(Renderer renderer) {

        // On informe le renderer qu'il devra créer son rendu à partir des dimensions du sandboxPanel
        renderer.setDestination(mainFrame.getSandboxPanel());

        // On informe le panel qu'il devra utiliser renderer pour récupérer le rendu dans sa méthode paint
        getMainFrame().getSandboxPanel().setRenderer(renderer);

        // Ce sera toujours le cas ici puisqu'on utilise cette méthode uniquement une fois juste après l'instanciation du Renderer
        // Mais dans certains cas il serait possible de changer de Renderer périodiquement par exemple si on veut plusieurs caméra mais avec des échelles de rendu différents (pas faisable seulement en interchangeant les caméras puisque l'échelle est définie dans le Renderer)
        if (renderer.getRenderingThread() == null)
            renderer.setRenderingThread(new RenderingThread(INSTANCE.getPhysics(), new MillisClock(), renderer, 60));

        // Si on a changé de Renderer, on coupe son Thread de rendu car il est inutile et consomme des ressources et du temps de calcul
        if (renderer != getRenderer())
            getRenderer().stop();

        // On redéfinit le Renderer actuel
        this.renderer = renderer;
    }

    /**
     * Redéfini le framerate voulu
     *
     * @param framerate framerate voulu
     */
    public void setFramerate(short framerate) {

        getRenderer().getRenderingThread().setFramerate(framerate);
    }

    /**
     * Redéfini le framerate voulu
     *
     * @param framerate framerate voulu
     */
    public void setFramerate(int framerate) {

        getRenderer().getRenderingThread().setFramerate(framerate);
        getMainFrame().getSandboxPanel().getRefreshingThread().setRefreshRate(framerate);
    }

    /**
     * Redéfini le refreshRate (pour le calcul / ProcessingThread) voulu
     *
     * @param refreshRate refreshRate voulu
     */
    public void setTickrate(short refreshRate) {

        INSTANCE.getPhysics().getProcessingThread().setRefreshRate(refreshRate);
    }

    /**
     * Redéfini le refreshRate (pour le calcul / ProcessingThread) voulu
     *
     * @param refreshRate refreshRate voulu
     */
    public void setTickrate(int refreshRate) {

        INSTANCE.getPhysics().getProcessingThread().setRefreshRate(refreshRate);
    }


    /**
     * Renvoie la Frame principale appartenant à l'unique instance créée
     *
     * @return INSTANCE.mainFrame
     */
    public MainFrame getMainFrame() {

        return INSTANCE.mainFrame;
    }

    /**
     * Renvoie le Moteur de rendu (Renderer) appartenant à l'unique instance créée
     *
     * @return INSTANCE.mainFrame
     */
    public Renderer getRenderer() {

        return INSTANCE.renderer;
    }

    /**
     * Renvoie la Simulation (Physics) appartenant à l'unique instance créée
     *
     * @return INSTANCE.mainFrame
     */
    public Physics getPhysics() {

        return INSTANCE.physics;
    }

    public Monitoring getMonitoring() {

        return monitoring;
    }
}

package fr.insalyon.mxyns.collinsa;

import fr.insalyon.mxyns.collinsa.clocks.MillisClock;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.threads.RenderingThread;
import fr.insalyon.mxyns.collinsa.ui.frames.MainFrame;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Color;
import java.awt.Toolkit;

//TODO:
//  - CollisionListener Interface if user wants to add specific actions on object

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
    private Physics physics;
    private Renderer renderer;
    final private MainFrame mainFrame;

        private final static double screenRatio = Toolkit.getDefaultToolkit().getScreenSize().getWidth() / Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    /**
     * Crée une unique instance du programme.
     */
    public static void main(String[] args) {

        // Crée l'INSTANCE unique du programme (Collinsa)
        INSTANCE = new Collinsa(1440, (int)(1440 / screenRatio));

        // Renderer settings
            // On pose une échelle d'affichage de 1 px/m
            getRenderer().setRenderScale(5);

            // On pose un zoom caméra inital de x1.0
            getRenderer().getCameraController().setCameraZoom(0.18f);
            getRenderer().setRenderChunksBounds(true);
            getRenderer().setRenderEntitiesAABB(true);

        // Physics settings
        getPhysics().setRealtime(false);

        // On pose le framerate voulu
        INSTANCE.setFramerate(60);


        //Création d'élements / entitées à ajouter à la simulation
        Physics physics = getPhysics();
        Rect rect = new Rect(physics.getChunkSize().x / 2, physics.getChunkSize().y / 2, 60, 100);
        Rect rect1 = new Rect(Collinsa.getPhysics().getWidth() / 2, Collinsa.getPhysics().getHeight() / 2, 200,100);
        Rect rect2 = new Rect(Collinsa.getPhysics().getWidth() / 2 - 100, Collinsa.getPhysics().getHeight() / 2 - 130, 200,100);
        //rect1.setAngVel(0.002f);
        rect2.setRot(0.2f);
        //rect2.setVel(new Vec2f(0, 20));

        Circle circle1 = new Circle(rect1.getPos().x - 500, rect1.getPos().y, 20);
        Circle circle2 = new Circle(circle1.getPos().x - 30, circle1.getPos().y - 30, 20);
        circle2.setVel(new Vec2f(0, 10));

        physics.addEntity(circle1);
        physics.addEntity(circle2);

        for (int i = 0; i < 3; ++i){

            Circle circle = new Circle((int)(Math.random() * getPhysics().getWidth()), (int)(Math.random() * getPhysics().getHeight()), 20);

            circle.setColor(new Color((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256)));
            // On teste une accélération vers le bas type gravité = 10g
            circle.setAcc(new Vec2f(0, 98.1f));
            physics.addEntity(circle);
        }
        // On ajoute les entités au moteur physique
        physics.addEntity(rect);
        physics.addEntity(rect1);
        physics.addEntity(rect2);


        // Démarre le programme (Simulation & Rendu)
        INSTANCE.start();
    }

    /**
     * Instancie Collinsa, un moteur physique et un moteur de rendu. La width et la height sont données en mètres
     *
     * @param width largeur en mètres de la simulation
     * @param height hauteur en mètres de la simulation
     */
    private Collinsa(int width, int height) {

        // On stocke l'unique instance qui sera créée de Collinsa pour un accès statique plus simple (pas besoin de passer l'instance de classe en classe)
        INSTANCE = this;

        // On crée le Renderer en premier mais il est vide et inutilisable, et n'a pas de thread associé (il ne peut donc pas fonctionner)
        renderer = new Renderer();

        // On crée une instance de moteur physique vide, elle se remplit de Chunks et crée un thread de calcul à son initialisation
        physics = new Physics(width, height, 10, 10);

        // On crée une page contenant un panel sur lequel rendre le contenu du moteur physique. A la création du panel, le CameraController lui est associé, puis il recupère le focus
        mainFrame = new MainFrame(1440, (int)(1440 / screenRatio));

        // Maintenant que le panel est prêt à être utilisé (défini et affiché à l'écran), on peut mettre en préparer le renderer au rendu. Le panel est par la même occasion notifié du "changement" de renderer.
        setRenderer(renderer);

        // Affichage des infos du programme
        System.out.println("World: " + physics);
        System.out.println("Renderer: " + renderer);
        System.out.println("    Camera : " + renderer.getCameraController());
    }

    /**
     * Démarre le programme (Simulation & Rendu)
     */
    public void start() {

        physics.begin();
        renderer.begin();
    }

    /**
     * Démarre le programme (Simulation & Rendu) après un certain temps
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

        physics.stop();
        renderer.stop();
    }

    /**
     * Stoppe le programme (Simulation & Rendu) après un certain temps
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
     * @param delay durée de pause
     */
    public void pause(long delay) throws InterruptedException {

        physics.pause(delay);
        renderer.pause(delay);
    }

    /**
     * (re-)Défini le Renderer utilisé par le programme, doit être appelée au moins une fois après l'initialisation du Renderer, de Physics et de MainFrame
     * @param renderer nouveau renderer utilisé
     */
    private void setRenderer(Renderer renderer) {
        
        // On informe le renderer qu'il devra créer son rendu à partir des dimensions du sandboxPanel
        renderer.setDestination(mainFrame.getSandboxPanel());

        // On informe le panel qu'il devra utiliser renderer pour récupérer le rendu dans sa méthode paint
        getMainFrame().getSandboxPanel().setRenderer(renderer);

        // Ce sera toujours le cas ici puisque on utilise cette méthode uniquement une fois juste après l'instanciation du Renderer
        // Mais dans certains cas il serait possible de changer de Renderer périodiquement par exemple si on veut plusieurs caméra mais avec des échelles de rendu différents (pas faisable seulement en interchangeant les caméras)
        if (renderer.getRenderingThread() == null)
            renderer.setRenderingThread(new RenderingThread(getPhysics(), new MillisClock(), renderer, 60));

        // Si on a changé de Renderer, on coupe son Thread de rendu car il est inutile est consomme des ressources et du temps de calcul
        if (renderer != getRenderer())
            getRenderer().stop();

        // On informe le Rendering Thread du Renderer à utiliser pour le rendu
        renderer.getRenderingThread().setRenderer(renderer);

        // On redéfinit le Renderer actuel
        this.renderer = renderer;
    }

    /**
     * Redéfini le framerate voulu
     * @param framerate framerate voulu
     */
    public void setFramerate(short framerate) {

        getRenderer().getRenderingThread().setFramerate(framerate);
    }

    /**
     * Redéfini le framerate voulu
     * @param framerate framerate voulu
     */
    public void setFramerate(int framerate) {

        getRenderer().getRenderingThread().setFramerate(framerate);
    }

    /**
     * Redéfini le refreshRate (pour le calcul / ProcessingThread) voulu
     * @param refreshRate refreshRate voulu
     */
    public void setRefreshRate(short refreshRate) {

        getPhysics().getProcessingThread().setRefreshRate(refreshRate);
    }

    /**
     * Redéfini le refreshRate (pour le calcul / ProcessingThread) voulu
     * @param refreshRate refreshRate voulu
     */
    public void setRefreshRate(int refreshRate) {

        getPhysics().getProcessingThread().setRefreshRate(refreshRate);
    }



    /**
     * Renvoie la Frame principale appartenant à l'unique instance créée
     * @return INSTANCE.mainFrame
     */
    public static MainFrame getMainFrame() {

        return INSTANCE.mainFrame;
    }

    /**
     * Renvoie le Moteur de rendu (Renderer) appartenant à l'unique instance créée
     * @return INSTANCE.mainFrame
     */
    public static Renderer getRenderer() {

        return INSTANCE.renderer;
    }

    /**
     * Renvoie la Simulation (Physics) appartenant à l'unique instance créée
     * @return INSTANCE.mainFrame
     */
    public static Physics getPhysics() {

        return INSTANCE.physics;
    }
}

package fr.insalyon.mxyns.collinsa.render;

import fr.insalyon.mxyns.collinsa.utils.geo.Vec2;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Une classe permettant de controller une Camera. En interaction avec le Renderer associé à la camera puisqu'il doit mettre à jour le facteur de rendu du Renderer
 */
public class CameraController implements KeyListener {

    /**
     * Touches du clavier enfoncée à un instant donné.
     */
    private final Set<Integer> pressedKeys = new HashSet<>();

    /**
     * Controlled camera
     */
    final private Camera camera;

    /**
     * Renderer associé à la Camera, permet le calcul du zoom sur la surface de rendu
     */
    final private Renderer renderer;

    public CameraController(Camera camera, Renderer renderer) {

        this.camera = camera;
        this.renderer = renderer;
    }

    /**
     * Renvoie un vecteur unitaire pointant dans la direction donnée par les touches enfoncées du clavier
     * @param keys touches enfoncées
     * @return Vecteur direction unitaire
     */
    public static Vec2 getDirection(Set<Integer> keys) {

        Vec2 direction = Vec2.zero();
        for (Integer key : keys) {
            if (key == KeyEvent.VK_LEFT)
                direction.subX(1);
            if (key == KeyEvent.VK_RIGHT)
                direction.addX(1);
            if (key == KeyEvent.VK_UP)
                direction.subY(1);
            if (key == KeyEvent.VK_DOWN)
                direction.addY(1);
        }

        if (direction.mag() != 0)
            direction.normalize();

        return direction;
    }

    /**
     * The ratio is conserved so we only need height, we use height bc the ratio w/h ratio is stored and mult. is faster than div.
     * @param height camera height in meters
     */
    public void setCameraDisplayBounds(int height) {

        this.camera.setHeight(height);
        renderer.factor = (float)renderer.destination.getHeight() / height;
    }

    /**
     * Définit la taille de la caméra en pixels
     * @param sizeInPixels dimension de la caméra en pixels
     */
    public void setCameraDisplayBoundsInPixels(Dimension sizeInPixels) {

        Dimension sizeInMeters = new Dimension((int)(sizeInPixels.width * renderer.scale), (int)(sizeInPixels.height * renderer.scale));
        setCameraDisplayBounds(sizeInMeters);
    }

    /**
     * Définit la taille de la caméra en mètres
     * @param size dimension de la caméra en mètres
     */
    public void setCameraDisplayBounds(Dimension size) {

        this.camera.setSize(size);
        setCameraDisplayBounds((int)size.getWidth());
    }

    /**
     * Détermine le coin haut-gauche du champs de vue de la caméra
     * @param x en mètres
     * @param y en mètres
     */
    public void setCameraFocus(float x, float y) {

        this.camera.setPos(x, y);
    }

    /**
     * Détermine la position en X du coin haut-gauche du champs de vue de la caméra
     * @param x en mètres
     */
    public void setCameraFocusX(float x) {

        this.camera.setX(x);
    }

    /**
     * Détermine la position en Y du coin haut-gauche du champs de vue de la caméra
     * @param y en mètres
     */
    public void setCameraFocusY(float y) {

        this.camera.setY(y);
    }

    /**
     * Déplace la Camera dans la direction dir d'une distance dist
     * @param dir vecteur directeur unitaire de la direction
     * @param dist distance de déplacement
     */
    public void moveCameraFocus(Vec2 dir, float dist) {

        camera.move(dir.mult(dist));
    }

    /**
     * Renvoie le zoom de la caméra
     * Le zoom c'est l'équivalent de la taille de ma caméra dans le monde. En effet, à échelle constante, si je veux zoomer je dois réduire la taille de ma caméra.
     * La scale rentre en jeu dans le calcul du zoom pour avoir une valeur sans unité
     * @return zoom
     * @see Renderer's scale
     */
    public float getCameraZoom() {

        return (float)(renderer.destination.getWidth() / (renderer.scale * camera.getWidth()));
    }

    /**
     * Applique un nouveau zoom à la caméra et modifie le facteur total.
     * C'est-à-dire que l'on réduit la taille de la caméra pour qu'elle se focus sur une zone du monde.
     * @param zoom Zoom voulu
     * @see Renderer's scale
     * @see Renderer's factor
     */
    public void setCameraZoom(float zoom) {

        this.camera.setHeight(renderer.destination.getHeight() / (renderer.scale * zoom));
        renderer.factor = zoom * renderer.scale;
    }

    /**
     * Fait zoomer la caméra (positif)
     * @param zoomIncrement zoom ajouté (positif)
     */
    public void zoomIn(float zoomIncrement) {

        setCameraZoom(getCameraZoom() + zoomIncrement);
    }

    /**
     * Fait dézoomer la caméra (négatif)
     * @param zoomDecrement zoom retiré (positif)
     */
    public void zoomOut(float zoomDecrement) {

        setCameraZoom(getCameraZoom() - zoomDecrement);
    }


    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Enregistre les pressions de touches utiles (Flèches directionnelles)
     * @param e KeyEvent récupéré par le KeyListener
     */
    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)
            pressedKeys.add(e.getKeyCode());

        moveCameraFocus(getDirection(pressedKeys), 5);

        // TODO: make panel repaint itself with Rendering Thread
        renderer.getDestination().repaint();
    }

    /**
     * Supprime les touches enregistrées lorsqu'elles sont relachées (Flèches directionnelles)
     * @param e KeyEvent récupéré par le KeyListener
     */
    @Override
    public void keyReleased(KeyEvent e) {

        pressedKeys.remove(e.getKeyCode());
    }

    /**
     * Supprime les entrées claviers actives (touches actionnées). Permet d'arrêter le mouvement de la caméra
     */
    public void deleteActiveKeys() {

        pressedKeys.clear();
    }

    public String toString() {

        return "CameraController[activesKeys=" + pressedKeys + ", Camera=" + camera + "]";
    }
}
package fr.insalyon.mxyns.collinsa.render;

import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Une classe permettant de controller une Camera. En interaction avec le Renderer associé à la camera puisqu'il doit mettre à jour le facteur de rendu du Renderer
 * Réagit aux entrées clavier, et aux entrées souris
 */
public class CameraController extends MouseAdapter implements KeyListener {

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

    /**
     * Détermine si un mouvement de caméra par l'utilisateur doit forcer le rendu (augmente donc le nombre de FPS-Rend. temporairement).
     */
    public boolean doesForceRender;

    /**
     * Crée un controleur de caméra à partir d'une Camera et du Renderer qui lui est associé
     *
     * @param camera caméra à controler
     * @param renderer moteur de rendu pour la caméra
     */
    public CameraController(Camera camera, Renderer renderer) {

        this(camera, renderer, false);
    }
    /**
     * Crée un controleur de caméra à partir d'une Camera et du Renderer qui lui est associé
     *
     * @param camera caméra à controler
     * @param renderer moteur de rendu pour la caméra
     * @param doesForceRender indique s'il faut forcer le rendu lors d'un mouvement de caméra
     */
    public CameraController(Camera camera, Renderer renderer, boolean doesForceRender) {

        this.camera = camera;
        this.renderer = renderer;
        this.doesForceRender = doesForceRender;
    }

    /**
     * Renvoie un vecteur unitaire pointant dans la direction donnée par les touches enfoncées du clavier
     * @param keys touches enfoncées
     * @return Vecteur direction unitaire
     */
    public static Vec2f getDirection(Set<Integer> keys) {

        Vec2f direction = Vec2f.zero();
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
     * Renvoie true si le CameraController force le rendu à chaque déplacement / zoom de caméra
     * @return doesForceRender
     */
    public boolean doesForceRender() {

        return doesForceRender;
    }

    /**
     * Redéfinit doesForceRender
     * @param doesForceRender true si le CameraController doit forcer le rendu à chaque mouvement / zoom de caméra.
     */
    public void setDoesForceRender(boolean doesForceRender) {

        this.doesForceRender = doesForceRender;
    }

    /**
     * The ratio is conserved so we only need height, we use height bc the ratio w/h ratio is stored and mult. is faster than div.
     * @param height camera height in meters
     */
    public void setCameraDisplayBounds(int height) {

        this.camera.setHeight(height);
        renderer.factor = (float)renderer.getDestinationSize().getHeight() / height;
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
     * Définit la taille de la caméra en pixels
     * @param width largeur de la caméra en pixels
     * @param height largeur de la caméra en pixels
     */
    public void setCameraDisplayBoundsInPixels(int width, int height) {

        Dimension sizeInMeters = new Dimension((int)(width * renderer.scale), (int)(height * renderer.scale));
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
    public void moveCameraFocus(Vec2f dir, float dist) {

        camera.move(dir.mult(dist));
    }

    /**
     * Translate la Camera d'un vecteur (x, y)
     * @param x distance de déplacement sur x
     * @param y distance de déplacement sur y
     */
    public void moveCameraFocus(int x, int y) {

        camera.move(x, y);
    }

    /**
     * Renvoie le zoom de la caméra
     * Le zoom c'est l'équivalent de la taille de ma caméra dans le monde. En effet, à échelle constante, si je veux zoomer je dois réduire la taille de ma caméra.
     * La scale rentre en jeu dans le calcul du zoom pour avoir une valeur sans unité
     * @return zoom
     * @see Renderer's scale
     */
    public float getCameraZoom() {

        return (float)(renderer.getDestinationSize().getWidth() / (renderer.scale * camera.getWidth()));
    }

    /**
     * Applique un nouveau zoom à la caméra et modifie le facteur total.
     * C'est-à-dire que l'on réduit la taille de la caméra pour qu'elle se focus sur une zone du monde.
     * @param zoom Zoom voulu
     * @see Renderer's scale
     * @see Renderer's factor
     */
    public void setCameraZoom(float zoom) {

        if (zoom <= 0) return;

        this.camera.setHeight(renderer.getDestinationSize().getHeight() / (renderer.scale * zoom));

        //TODO move camera so that center of focus doesn't move

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


    /**
     * Renvoie le vecteur position de la caméra
     * @return Vec2f camera position
     */
    public Vec2f getCameraPosition() {

        return camera.getPos();
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

        moveCameraFocus(getDirection(pressedKeys), 20);

        renderer.forceRender();
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

    // Pas très conventionnel de ne pas mettre l'attribut en tête de classe mais il n'a de rapport qu'avec cette méthode
    private Point dragOrigin;

    @Override
    public void mousePressed(MouseEvent e) {

        dragOrigin = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        moveCameraFocus((int)((dragOrigin.getX() - e.getPoint().getX())  / renderer.factor), (int)((dragOrigin.getY() - e.getPoint().getY()) / renderer.factor));

        dragOrigin = e.getPoint();

        if (doesForceRender)
            renderer.forceRender();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        // Scroll Amount: n° of units / mouseWheelNotch
        // UnitsToScroll: units count scrolled on wheel -> negative if mouse wheel up

        if (e.getWheelRotation() < 0)
            zoomIn(-(float)e.getUnitsToScroll() * 0.05f / e.getScrollAmount());
        else
            zoomOut((float) e.getUnitsToScroll() * 0.05f / e.getScrollAmount());

        if (doesForceRender)
            renderer.forceRender();
    }

    public String toString() {

        return "CameraController[activesKeys=" + pressedKeys + ", Camera=" + camera + ", zoom=x" + getCameraZoom() + "]";
    }
}
package fr.insalyon.mxyns.collinsa.render;

import fr.insalyon.mxyns.collinsa.utils.CyclicList;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.util.Arrays;
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
     * Controlled cameras CyclicList
     * @see CyclicList
     */
    final private CyclicList<Camera> cameras = new CyclicList<>(false);

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

        this.cameras.add(camera);
        this.cameras.next();
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
    public void setCameraDisplayBounds(double height) {

        this.cameras.current().setHeight(height);
        renderer.setRenderFactor(renderer.getDestinationSize().getHeight() / height);
    }

    /**
     * The ratio is conserved so we only need height, we use height bc the ratio w/h ratio is stored and mult. is faster than div.
     * @param width camera width in meters
     * @param height camera height in meters
     */
    public void setCameraDisplayBounds(double width, double height) {

        this.cameras.current().setSize(width, height);
        setCameraDisplayBounds(height);
    }

    /**
     * Définit la taille de la caméra en mètres
     * @param size dimension de la caméra en mètres représentée par un Vec2d
     */
    public void setCameraDisplayBounds(Vec2d size) {

        this.cameras.current().setSize(size);
        setCameraDisplayBounds(size.y);
    }

    /**
     * Définit la taille de la caméra en pixels
     * @param sizeInPixels dimension de la caméra en pixels
     */
    public void setCameraDisplayBoundsInPixels(Dimension sizeInPixels) {

        Vec2d sizeInMeters = new Vec2d(sizeInPixels.width / renderer.getRenderScale(), sizeInPixels.height / renderer.getRenderScale());
        setCameraDisplayBounds(sizeInMeters);
    }

    /**
     * Définit la taille de la caméra en pixels
     * @param width largeur de la caméra en pixels
     * @param height largeur de la caméra en pixels
     */
    public void setCameraDisplayBoundsInPixels(int width, int height) {

        setCameraDisplayBounds(width / renderer.getRenderScale(), height / renderer.getRenderScale());
    }

    /**
     * Positionne le coin haut-gauche (center = false) ou le centre (center = true) du champs de vue de la caméra
     * @param x en mètres
     * @param y en mètres
     * @param center true on veut centrer la caméra sur le point donné
     */
    public void setCameraFocus(float x, float y, boolean center) {

        this.cameras.current().setPos(center ? (float) (x - cameras.current().getWidth() * 0.5f) : x, center ? (float) (y - cameras.current().getHeight() * 0.5f) : y);
    }

    /**
     * Positionne le coin haut-gauche (center = false) ou le centre (center = true) du champs de vue de la caméra
     * @param pos vecteur position (en mètres)
     * @param center true on veut centrer la caméra sur le point donné
     */
    public void setCameraFocus(Vec2f pos, boolean center) {

        setCameraFocus(pos.x, pos.y, center);
    }

    /**
     * Détermine la position en X du coin haut-gauche du champs de vue de la caméra
     * @param x en mètres
     */
    public void setCameraFocusX(float x) {

        this.cameras.current().setX(x);
    }

    /**
     * Détermine la position en Y du coin haut-gauche du champs de vue de la caméra
     * @param y en mètres
     */
    public void setCameraFocusY(float y) {

        this.cameras.current().setY(y);
    }

    /**
     * Déplace la Camera dans la direction dir d'une distance dist
     * @param dir vecteur directeur unitaire de la direction
     * @param dist distance de déplacement
     */
    public void moveCameraFocus(Vec2f dir, float dist) {

        cameras.current().move(dir.setMag(dist));
    }

    /**
     * Translate la Camera d'un vecteur (x, y)
     * @param x distance de déplacement sur x
     * @param y distance de déplacement sur y
     */
    public void moveCameraFocus(int x, int y) {

        cameras.current().move(x, y);
    }
    /**
     * Translate la Camera d'un vecteur (x, y)
     * @param x distance de déplacement sur x
     * @param y distance de déplacement sur y
     */
    public void moveCameraFocus(float x, float y) {

        cameras.current().move(x, y);
    }

    /**
     * Renvoie le zoom de la caméra
     * Le zoom c'est l'équivalent de la taille de ma caméra dans le monde. En effet, à échelle constante, si je veux zoomer je dois réduire la taille de ma caméra.
     * La scale rentre en jeu dans le calcul du zoom pour avoir une valeur sans unité
     * @return zoom
     * @see Renderer's scale
     */
    public float getCameraZoom() {

        return (float)(renderer.getDestinationSize().getWidth() / (renderer.getRenderScale() * cameras.current().getWidth()));
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

        double oldHeight = cameras.current().getHeight();

        this.cameras.current().setHeight(renderer.getDestinationSize().getHeight() / (renderer.getRenderScale() * zoom));

        // Repositionnement de la caméra après zoom pour rester aligner avec le centre
        oldHeight = (oldHeight - cameras.current().getHeight()) * 0.5f;
        this.moveCameraFocus((float)(oldHeight * cameras.current().getRatio()), (float)oldHeight);

        renderer.setRenderFactor(zoom * renderer.getRenderScale());
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

        return cameras.current().getPos();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Enregistre les pressions de touches utiles (Flèches directionnelles)
     * @param e KeyEvent récupéré par le KeyListener
     */
    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
            pressedKeys.add(e.getKeyCode());
            moveCameraFocus(getDirection(pressedKeys), 20);

        } else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP)
            previousCamera();
        else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
            nextCamera();
        else return;

        // On n'arrive ici que si le keyPressed a été utile (la touche appuyée correspondait à une des touches utiles, sinon on passe par le 'else return;')
        if (doesForceRender)
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
    /**
     * Enregistre le point de départ du drag (cliqué-déplacé) de la souris
     */
    @Override
    public void mousePressed(MouseEvent e) {

        dragOrigin = e.getPoint();
    }

    /**
     * Déplace la caméra en fonction de la position de la souris lors d'un drag
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        if (cameras.current().getFollowedEntity() != null)
            return;

        moveCameraFocus((float) ((dragOrigin.getX() - e.getPoint().getX())  / renderer.getRenderFactor()), (float) ((dragOrigin.getY() - e.getPoint().getY()) / renderer.getRenderFactor()));

        dragOrigin = e.getPoint();

        if (doesForceRender)
            renderer.forceRender();
    }

    /**
     * Zoom la caméra quand l'utilisateur fait tourner la molette de la souris
     */
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

    /**
     * Renvoie la CyclicList des caméras
     *
     * @return cameras
     */
    public CyclicList<Camera> getCameraList() {

        return cameras;
    }

    /**
     * Ajoute une caméra à la liste si elle n'existe pas déjà
     *
     * @param camera caméra à ajouter
     * @return true si la caméra a été ajoutée, false sinon.
     */
    public boolean addCamera(Camera camera) {

        return cameras.add(camera);
    }

    /**
     * Passe à la caméra suivante
     *
     * @return la nouvelle camera sélectionnée
     */
    public Camera nextCamera() {

        return setCamera(cameras.next());
    }

    /**
     * Passe à la caméra précédente
     *
     * @return la nouvelle camera sélectionnée
     */
    public Camera previousCamera() {

        return setCamera(cameras.prev());
    }

    /**
     * Définit la caméra sélectionnée
     *
     * @param camera la camera a sélectionner
     * @return la nouvelle camera sélectionnée
     */
    private Camera setCamera(Camera camera) {

        renderer.camera = camera;
        renderer.setRenderFactor(getCameraZoom() * renderer.getRenderScale());

        return renderer.camera;
    }

    public String toString() {

        return "CameraController[activesKeys=" + pressedKeys + ", Cameras=" + Arrays.toString(cameras.toArray()) + ", currentCameraIndex=" + cameras.getIndex() + ", zoom=x" + getCameraZoom() + "]";
    }
}
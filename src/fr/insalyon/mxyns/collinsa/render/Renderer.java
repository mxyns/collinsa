package fr.insalyon.mxyns.collinsa.render;

import fr.insalyon.mxyns.collinsa.physics.Chunk;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.ui.panels.SandboxPanel;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

//TODO: utiliser un BufferStrategy pour supprimer le scintillement et rendre le rendu plus fluide

/**
 * Renderer s'occupe de créer un visuel correspondant à l'état de la Sandbox visible par sa Camera et à l'afficher sur son panel de destination
 */
public class Renderer {

    /**
     * Rectangle renderBounds est un rectangle délimitant la zone de l'espace à rendre (tout objet en dehors de cette zone est toujours mis à jour mais n'est pas affiché)
     * Il est toujours proportionnel à la taille du Panel et donc de la Frame
     */
    private Camera camera;

    /**
     * Scale est l'échelle de rendu en pixels / m, toutes les valeurs dans le programme sont en SI donc les distances en m.
     */
    private float scale = 2f /* pixels / m */;

    /**
     * Factor est le multiplicateur de rendu, prenant en compte l'échelle et le zoom de la camera
     **/
    private float factor = 1.0f;

    /**
     * Détermine si le Renderer dessine les contours des Chunks
     */
    boolean renderChunksBounds = false;

    /**
     * Destination de rendu du Renderer. On la stocke pour sauvegarder la matrice de passage Monde -> Panel et limiter les calculs (quand les matrices seront implémentées)
     */
    private JPanel destination;

    public Renderer() {

         camera = new Camera();
     }
     public Renderer(SandboxPanel panel) {

         camera = new Camera(panel, scale);
         setDestination(panel);
     }

    /**
     * Crée le rendu de la Sandbox et l'affiche sur le panel
     * @param g L'objet Graphics2D associé au Panel permettant de dessiner dessus
     */
    public void renderSandbox(Physics physics, Graphics2D g) {

        //TODO: appliquer de l'antialiasing sur Graphics2D

        for (Chunk[] chunkA : physics.getChunks())
            for (Chunk chunk : chunkA)
                if(shouldRenderChunk(chunk))
                    renderChunk(chunk, g);
    }

    /**
     * Détermine si le chunk est partiellement visible par la caméra et donc s'il doit être rendu
     * @param chunk le chunk vérifié
     * @return true si le chunk est visible
     * @see Camera
     */
    public boolean shouldRenderChunk(Chunk chunk) {

        return camera.sees(chunk);
    }
    /**
     * Render le chunk sur le panel de destination
     * @param chunk Le Chunk à rendre
     * @param g L'objet Graphics2D associé au Panel permettant de dessiner dessus
     */
    public void renderChunk(Chunk chunk, Graphics2D g) {

        for (Entity entity : chunk.entities)
            entity.render(this, g);

        if (renderChunksBounds)
            g.drawRect((int)((chunk.rectangle.x - camera.getPos().x)* factor), (int)((chunk.rectangle.y - camera.getPos().y) * factor), (int)(chunk.rectangle.getWidth() * factor), (int)(chunk.rectangle.getHeight() * factor));
    }

    /**
     * Rendu d'un cercle via le Graphics2D.
     * @param circle cercle à rendre
     * @param g graphics2d utilisé
     */
    public void renderCircle(Circle circle, Graphics2D g) {

        Vec2 center = circle.getPos();
        g.draw(new Ellipse2D.Double(factor * (center.x - circle.r - camera.getPos().x), factor * (center.y - circle.r - camera.getPos().y), 2 * factor * circle.r, 2 * factor *circle.r));
    }

    /**
     * Rendu d'un Rect via le Graphics2D.
     * @param rect Rect à rendre
     * @param g graphics2d utilisé
     */
    public void renderRect(Rect rect, Graphics2D g) {

    }

    // WARNING: Only for display, do not modify Camera without passing by Renderer's methods
    public Camera getCamera() {

        return camera;
    }

    /**
     * Définit la destination de rendu du Renderer
     * @param destination JPanel sur lequel sera rendue l'image.
     */
    public void setDestination(SandboxPanel destination) {

        this.destination = destination;
        setCameraDisplayBoundsInPixels(destination.getSize());
        destination.setRenderer(this);
    }

    /**
     * The ratio is conserved so we only need height, we use height bc the ratio w/h ratio is stored and mult. is faster than div.
     * @param height camera height in meters
     */
    public void setCameraDisplayBounds(int height) {

        this.camera.setHeight(height);
        this.factor = (float)this.destination.getHeight() / height;
    }

    /**
     * Définit la taille de la caméra en pixels
     * @param sizeInPixels dimension de la caméra en pixels
     */
    public void setCameraDisplayBoundsInPixels(Dimension sizeInPixels) {

        Dimension sizeInMeters = new Dimension((int)(sizeInPixels.width * scale), (int)(sizeInPixels.height * scale));
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
     * Renvoie le zoom de la caméra
     * Le zoom c'est l'équivalent de la taille de ma caméra dans le monde. En effet, à échelle constante, si je veux zoomer je dois réduire la taille de ma caméra.
     * La scale rentre en jeu dans le calcul du zoom pour avoir une valeur sans unité
     * @return zoom
     * @see #scale
     */
    public float getCameraZoom() {

        return (float)(destination.getWidth() / (scale * camera.getWidth()));
    }

    /**
     * Applique un nouveau zoom à la caméra et modifie le facteur total.
     * C'est-à-dire que l'on réduit la taille de la caméra pour qu'elle se focus sur une zone du monde.
     * @param zoom Zoom voulu
     * @see #scale
     * @see #factor
     */
    public void setCameraZoom(float zoom) {

        this.camera.setHeight(this.destination.getHeight() / (scale * zoom));
        this.factor = zoom * scale;
    }

    /**
     * Renvoie l'échelle de rendu en px par mètres
     * @return scale
     * @see #scale
     */
    public float getRenderScale() {

        return scale;
    }

    /**
     *  Définit une nouvelle échelle de rendu en px/m et applique cette modification au facteur total.
     * @param scale la nouvelle échelle de rendu en px/m
     * @see #scale
     * @see #factor
     */
    public void setRenderScale(float scale) {

        this.factor = this.factor * scale / this.scale;
        this.scale = scale;
    }
}

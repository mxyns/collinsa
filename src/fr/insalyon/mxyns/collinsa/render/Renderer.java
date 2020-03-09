package fr.insalyon.mxyns.collinsa.render;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Chunk;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.AABB;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.threads.RenderingThread;
import fr.insalyon.mxyns.collinsa.ui.panels.SandboxPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * Renderer s'occupe de créer un visuel correspondant à l'état de la Sandbox visible par sa Camera et à l'afficher sur son panel de destination
 */
public class Renderer {

    private GraphicsBuffer graphicsBuffer;

    /**
     * Thread dédié au rendu des images
     * N'est pas final car on associe le Physics après l'instanciation du Renderer
     */
    private RenderingThread renderingThread;

    /**
     * Rectangle renderBounds est un rectangle délimitant la zone de l'espace à rendre (tout objet en dehors de cette zone est toujours mis à jour mais n'est pas affiché)
     * Il est toujours proportionnel à la taille du Panel et donc de la Frame
     */
    final private Camera camera;

    /**
     * Le controleur qui permet de controller la Camera (déplacement, zoom/redimensionnement, etc.)
     */
    final private CameraController cameraController;

    /**
     * Scale est l'échelle de rendu en pixels / m, toutes les valeurs dans le programme sont en SI donc les distances en m.
     */
    float scale = 1.0f /* pixels / m */;

    /**
     * Factor est le multiplicateur de rendu, prenant en compte l'échelle et le zoom de la camera
     **/
    double factor = 1.0f;

    /**
     * Détermine si le Renderer dessine les contours des Chunks
     */
    private boolean renderChunksBounds = false;

    /**
     * Détermine si le Renderer dessine les AABB
     */
    private boolean renderEntitiesAABB = false;

    /**
     * Détermine si le Renderer dessine les bords du monde
     */
    private boolean renderWorldBounds = true;

    /**
     * Couleur des bordures de Chunks (si dessinées)
     */
    private Color chunkBoundsColor = Color.black;

    /**
     * Couleur des bordures de Chunks (si dessinées)
     */
    private Color AABBBoundsColor = Color.yellow;

    /**
     * Crée un Renderer vide inutilisable. Besoin de définir une destination de rendu.
     */
    public Renderer() {

        camera = new Camera();
        cameraController = new CameraController(camera, this);
    }

    /**
     * Crée un Renderer avec sa destination. Pour l'utiliser il faut appeler setRenderer() dans le programme
     * @param panel destination de rendu
     */
    public Renderer(SandboxPanel panel) {

        camera = new Camera(panel, scale);
        cameraController = new CameraController(camera, this);
        setDestination(panel);
    }

    /**
     * Démarre le Thread de rendu et donc le rendu (mise à jour de l'affichage)
     */
    public void begin() {

        renderingThread.start();
    }

    /**
     * Met en pause le Thread de rendu et donc le rendu (mise à jour de l'affichage)
     * @param delay durée de pause
     */
    public void pause(long delay) throws InterruptedException {

        renderingThread.sleep(delay);
    }

    /**
     * Stoppe le Thread de rendu et donc le rendu (mise à jour de l'affichage)
     */
    public void stop() {

        renderingThread.interrupt();
    }

    /**
     * Force le rendu (utilisé majoritairement pour donner un sentiment de fluidité lors des déplacements de caméra)
     */
    public void forceRender() {

        renderingThread.forceRender();
    }

    /**
     * Crée le rendu de la Sandbox en utilisant un Graphics2D spécifique
     * @param physics simulation à rendre
     * @param g L'objet Graphics2D permettant de dessiner sur le composant
     */
    public void renderSandbox(Physics physics, Graphics2D g) {

        //TODO: appliquer de l'antialiasing sur Graphics2D

        for (Chunk chunk : physics.getChunks())
            if(shouldRenderChunk(chunk))
                renderChunk(chunk, g);

        g.setColor(Color.black);

        if (renderWorldBounds) {
            g.drawRect((int) (camera.getPos().x * -factor), (int) ((camera.getPos().y) * -factor), (int) (physics.getWidth() * factor), (int) (physics.getHeight() * factor));
        }

        g.drawString("FPS-Proc.:"+(Collinsa.getPhysics().getProcessingThread().getClock().lastElapsed != 0 ? 1000 / Collinsa.getPhysics().getProcessingThread().getClock().lastElapsed : 0), 5, 17);
        g.drawString("FPS-Rend.:"+(getRenderingThread().getClock().lastElapsed != 0 ? 1000 / getRenderingThread().getClock().lastElapsed : 0), 5, 29);
        g.drawString("FPS-Disp.:"+(Collinsa.getMainFrame().getSandboxPanel().getRefreshingThread().getClock().lastElapsed != 0 ? 1000 / Collinsa.getMainFrame().getSandboxPanel().getRefreshingThread().getClock().lastElapsed : 0), 5, 41);
    }

    /**
     * Crée le rendu de la Sandbox directement dans le buffer
     */
    public void render(Physics physics) {

        graphicsBuffer.resetBackBuffer();
        renderSandbox(physics, graphicsBuffer.getGraphics2D());
        graphicsBuffer.flip();
    }

    /**
     * Détermine si le chunk est partiellement visible par la caméra et donc s'il doit être rendu
     * @param chunk le chunk vérifié
     * @return true si le chunk est visible
     * @see Camera
     */
    private boolean shouldRenderChunk(Chunk chunk) {

        return camera.sees(chunk);
    }
    /**
     * Render le chunk sur le panel de destination
     * @param chunk Le Chunk à rendre
     * @param g L'objet Graphics2D associé au Panel permettant de dessiner dessus
     */
    private void renderChunk(Chunk chunk, Graphics2D g) {

        AffineTransform transform = g.getTransform();

        for (Entity entity : chunk.entities) {

            entity.render(this, g);
            g.setTransform(transform);

            if (renderEntitiesAABB) {
                AABB aabb = entity.getAABB();
                g.setColor(AABBBoundsColor);
                g.drawRect((int) ((aabb.x - camera.getPos().x) * factor), (int) ((aabb.y - camera.getPos().y) * factor), (int) (aabb.getWidth() * factor), (int) (aabb.getHeight() * factor));
            }
        }

        if (renderChunksBounds) {
            g.setColor(chunkBoundsColor);
            g.drawRect((int) ((chunk.bounds.x - camera.getPos().x) * factor), (int) ((chunk.bounds.y - camera.getPos().y) * factor), (int) (chunk.bounds.getWidth() * factor), (int) (chunk.bounds.getHeight() * factor));
            g.drawString(String.valueOf(Collinsa.getPhysics().getPositionHash(chunk.bounds.x, chunk.bounds.y)), (int) ((chunk.bounds.x - camera.getPos().x) * factor), (int) ((chunk.bounds.y - camera.getPos().y + Collinsa.getPhysics().getChunkSize().y) * factor));
        }
    }

    /**
     * Rendu d'un cercle via le Graphics2D.
     * @param circle cercle à rendre
     * @param g graphics2d utilisé
     */
    public void renderCircle(Circle circle, Graphics2D g) {

        g.setColor(circle.getColor());
        g.draw(new Ellipse2D.Double(factor * (circle.getPos().x - circle.r - camera.getPos().x), factor * (circle.getPos().y - circle.r - camera.getPos().y), 2 * factor * circle.r, 2 * factor *circle.r));
    }

    /**
     * Rendu d'un Rect via le Graphics2D.
     * @param rect Rect à rendre
     * @param g graphics2d utilisé
     */
    public void renderRect(Rect rect, Graphics2D g) {

        g.setColor(rect.getColor());
        g.rotate(rect.getRot(), factor * (rect.getPos().x - camera.getPos().x), factor * (rect.getPos().y - camera.getPos().y));
        g.draw(new Rectangle2D.Double(factor * (rect.getPos().x - rect.size.x * 0.5f - camera.getPos().x), factor * (rect.getPos().y - rect.size.y * 0.5f - camera.getPos().y), factor * rect.size.x, factor * rect.size.y));

        g.rotate(-rect.getRot(), factor * (rect.getPos().x - camera.getPos().x), factor * (rect.getPos().y - camera.getPos().y));
        for (int i = 0; i < 4; ++i)
            g.drawString("corner #"+i, (float)factor*(rect.getCorners()[i].x - camera.getPos().x), (float)factor*(rect.getCorners()[i].y - camera.getPos().y));
    }

    /**
     * Renvoie le panel de destination du renderer
     * @return panel de rendu
     */
    public Dimension getDestinationSize() {

        return graphicsBuffer.getImageSize();
    }

    /**
     * Définit la taille de la destination de rendu du Renderer
     * @param destination JPanel sur lequel sera rendue l'image.
     */
    public void setDestination(SandboxPanel destination) {

        this.graphicsBuffer = new GraphicsBuffer(destination.getSize());
        cameraController.setCameraDisplayBoundsInPixels(destination.getSize());

        // A faire soit même (pour éviter de le faire deux fois, et pour éviter un pb de récursion puisqu'on pourrait très bien faire renderer.setDestination(this) dans le setRenderer du panel)
        //destination.setRenderer(this);
    }
    /**
     * Définit la taille de la destination de rendu du Renderer
     * @param size Taille de la destination de rendu
     */
    public void setDestination(Dimension size) {

        this.graphicsBuffer = new GraphicsBuffer(size);
        cameraController.setCameraDisplayBoundsInPixels(size);

        // A faire soit même (pour éviter de le faire deux fois, et pour éviter un pb de récursion puisqu'on pourrait très bien faire renderer.setDestination(this) dans le setRenderer du panel)
        //destination.setRenderer(this);
    }
    /**
     * Définit la taille de la destination de rendu du Renderer
     * @param width largeur de la destination de rendu
     * @param height hauteur de la destination de rendu
     */
    public void setDestination(int width, int height) {

        this.graphicsBuffer = new GraphicsBuffer(width, height);
        cameraController.setCameraDisplayBoundsInPixels(width, height);

        // A faire soit même (pour éviter de le faire deux fois, et pour éviter un pb de récursion puisqu'on pourrait très bien faire renderer.setDestination(this) dans le setRenderer du panel)
        //destination.setRenderer(this);
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

    /**
     * Renvoie le controleur de caméra
     * @return controleur de caméra
     */
    public CameraController getCameraController() {

        return cameraController;
    }

    /**
     * Renvoie le Thread de rendu associé au Renderer
     * @return thread de rendu
     */
    public RenderingThread getRenderingThread() {

        return renderingThread;
    }

    /**
     * Définit le Thread de rendu associé au Renderer*
     */
    public void setRenderingThread(RenderingThread renderingThread) {

        this.renderingThread = renderingThread;
        renderingThread.setRenderer(this);
    }

    /**
     * Informe si les AABB des entités vont être dessinées ou non
     * @return renderEntitiesAABB = true si elles vont être dessinées
     */
    public boolean doesRenderEntitiesAABB() {

        return renderEntitiesAABB;
    }

    /**
     * Détermine si les AABB des entités vont être dessinées ou non
     * @param renderEntitiesAABB = true pour les dessiner
     */
    public void setRenderEntitiesAABB(boolean renderEntitiesAABB) {

        this.renderEntitiesAABB = renderEntitiesAABB;
    }

    /**
     * Informe si les bordures des chunks vont être dessinées ou non
     * @return renderChunkBounds = true si elles vont être dessinées
     */
    public boolean doesRenderChunksBounds() {

        return renderChunksBounds;
    }

    /**
     * Détermine si les bordures des chunks vont être dessinées ou non
     * @param renderChunksBounds = true pour les dessiner
     */
    public void setRenderChunksBounds(boolean renderChunksBounds) {

        this.renderChunksBounds = renderChunksBounds;
    }

    /**
     * Informe si les bordures du monde vont être dessinées ou non
     * @return renderWorldBounds = true si elles vont être dessinées
     */
    public boolean doesRenderWorldBounds() {

        return renderWorldBounds;
    }

    /**
     * Détermine si les bordures du monde vont être dessinées ou non
     * @param renderWorldBounds = true pour les dessiner
     */
    public void setRenderWorldBounds(boolean renderWorldBounds) {

        this.renderWorldBounds = renderWorldBounds;
    }

    /**
     * Renvoie la couleur définie pour les bordures de Chunk
     * @return chunkBoundsColor la couleur de bordures de Chunk
     */
    public Color getChunkBoundsColor() {

        return chunkBoundsColor;
    }


    /**
     * Redéfinit la couleur définie pour les bordures de Chunk
     * @param chunkBoundsColor la nouvelle couleur de bordures de Chunk
     */
    public void setChunkBoundsColor(Color chunkBoundsColor) {

        this.chunkBoundsColor = chunkBoundsColor;
    }

    /**
     * Renvoie la couleur définie pour les AABBs des entités
     * @return AABBBoundsColor la couleur des AABBs
     */
    public Color getAABBBoundsColor() {

        return AABBBoundsColor;
    }

    /**
     * Redéfinit la couleur définie pour les AABBs des entités
     * @param AABBBoundsColor la nouvelle couleur des AABBs
     */
    public void setAABBBoundsColor(Color AABBBoundsColor) {

        this.AABBBoundsColor = AABBBoundsColor;
    }

    public GraphicsBuffer getGraphicsBuffer() {

        return this.graphicsBuffer;
    }

    public String toString() {

        return "Renderer[renderSize=(" + this.getDestinationSize().getWidth() + ", " + this.getDestinationSize().getHeight() + "), renderFactor=" + this.factor + ",\n   CameraController=" + getCameraController()

            + "]";
    }
}

package fr.insalyon.mxyns.collinsa.render;

import fr.insalyon.mxyns.collinsa.physics.Chunk;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.AABB;
import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.entities.Polygon;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;
import fr.insalyon.mxyns.collinsa.physics.forces.Force;
import fr.insalyon.mxyns.collinsa.threads.RenderingThread;
import fr.insalyon.mxyns.collinsa.ui.panels.SandboxPanel;
import fr.insalyon.mxyns.collinsa.utils.Utils;
import fr.insalyon.mxyns.collinsa.utils.geo.Geometry;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.*;
import java.awt.geom.*;
import java.util.LinkedHashSet;

import static fr.insalyon.mxyns.collinsa.Collinsa.INSTANCE;

/**
 * Renderer s'occupe de créer un visuel correspondant à l'état de la Sandbox visible par sa Camera qui est accessible depuis son GraphicsBuffer
 */
public class Renderer {

    /**
     * Buffer de deux images contenant les images générées par le Renderer et à afficher
     */
    private GraphicsBuffer graphicsBuffer;

    /**
     * Thread dédié au rendu des images
     * N'est pas final car on associe le Physics après l'instanciation du Renderer
     */
    private RenderingThread renderingThread;

    /**
     * Camera est la caméra utilisée actuellement par le renderer. Elle permet de délimiter la zone de rendu et de ne pas rendre les objets étant en dehors de cette zone.
     * Puisque le CameraController du Renderer peut avoir plusieurs caméras, Renderer.camera contiendra seulement la caméra actuelle, et cet attribut sera mis à jour à chaque changement de caméras.
     */
    Camera camera;

    /**
     * Le controleur qui permet de controller la Camera (déplacement, zoom/redimensionnement, etc.)
     */
    final private CameraController cameraController;

    /**
     * Scale est l'échelle de rendu en pixels / m, toutes les valeurs dans le programme sont en SI donc les distances en m.
     */
    private float scale = 1.0f /* pixels / metre */, forceScale = 1.0f /* pixels / newton */;

    /**
     * Factor est le multiplicateur de rendu, prenant en compte l'échelle et le zoom de la camera
     **/
    private double factor = 1.0f, forceFactor = 1.0;

    /**
     * Détermine si le Renderer dessine les contours des Chunks, les AABB, les bords du monde et le repère utilisé.
     */
    private boolean renderChunksBounds = false, renderEntitiesAABB = false, renderWorldBounds = true, renderCoordinateSystem = false, renderForces = false, wireframeDisplay = false;

    /**
     * Couleur des bordures de Chunks (si dessinées), des bounding boxes des entités et des bords du monde
     */
    private Color chunkBoundsColor = Color.black, AABBBoundsColor = Color.yellow, worldBoundsColor = Color.black;

    /**
     * Les extras à rendre. Des éléments n'appartenant pas au moteur physique à afficher en plus du rendu classique.
     */
    final private LinkedHashSet<Renderable> extras = new LinkedHashSet<>();

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

        renderingThread.queryStop();
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

        // Antialiasing pour avoir rendu bien lisse
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (camera == null) {
            System.out.println("Render failed : no camera selected");
            return;
        }

        if (camera.getFollowedEntity() != null) {
            cameraController.setCameraFocus(camera.getFollowedEntity().getPos(), true);
        }

        // On fait le rendu de tous les Chunk visibles
        for (Chunk chunk : physics.getChunks())
            if(shouldRenderChunk(chunk))
                renderChunk(chunk, g);


        // On affiche les limites du monde si voulu (renderWorldBounds = true)
        if (renderWorldBounds) {
            g.setColor(worldBoundsColor);
            g.drawRect((int)(-camera.getPos().x * factor),(int)(-camera.getPos().y * factor), (int) (factor * physics.getWidth()), (int) (factor * physics.getHeight()));
        }

        // On affiche le système de coordonnées du monde si voulu (renderCoordinateSystem = true)
        if (renderCoordinateSystem) {

            int axesSize = (int) (0.1 * Math.min(physics.getWidth(), physics.getHeight()));

            g.setColor(Color.red);
            g.draw(new Line2D.Double(-camera.getPos().x * factor, -camera.getPos().y * factor, (axesSize - camera.getPos().x) * factor, -camera.getPos().y * factor));
            g.setColor(Color.green);
            g.draw(new Line2D.Double(factor * -camera.getPos().x, factor * -camera.getPos().y, factor * -camera.getPos().x, factor * (axesSize-camera.getPos().y)));
        }

        // On affiche les forces si voulu (renderForces = true)
        if (renderForces) {

            // On met à jour le facteur de rendu des forces.
            forceFactor = factor / scale * forceScale;

            for (Force force : physics.forces)
                force.render(this, g);
        }

        // On affiche les extras
        for (Renderable extra : extras)
            extra.render(this, g);


        // On dessine l'entité sélectionée par l'outil de sélection
        INSTANCE.getMainFrame().selectionTool.drawSelectedEntityOutline(this, g);


        // HUD : On affiche les différents compteurs de FPS
        Rectangle2D textBox = new Rectangle2D.Float(0, 0, 90, 45);

        g.setColor(graphicsBuffer.getBackgroundColor());
        g.fill(textBox);

        g.setColor(worldBoundsColor);
        g.drawString("FPS-Proc.:"+(INSTANCE.getPhysics().getProcessingThread().getClock().lastElapsed != 0 ? 1000 / INSTANCE.getPhysics().getProcessingThread().getClock().lastElapsed : 0), 5, 17);
        g.drawString("FPS-Rend.:"+(getRenderingThread().getClock().lastElapsed != 0 ? 1000 / getRenderingThread().getClock().lastElapsed : 0), 5, 29);
        g.drawString("FPS-Disp.:"+(INSTANCE.getMainFrame().getSandboxPanel().getRefreshingThread().getClock().lastElapsed != 0 ? 1000 / INSTANCE.getMainFrame().getSandboxPanel().getRefreshingThread().getClock().lastElapsed : 0), 5, 41);

        g.draw(textBox);

        Dimension imageSize = getDestinationSize();

        float moreWidth = 7 * (int) Math.log10(getCameraController().getCameraList().size() + 1);
              moreWidth += getCameraController().getCameraList().getIndex() == 0 ? 0 : 7 * (int) Math.log10(getCameraController().getCameraList().getIndex());


        textBox.setRect(0, imageSize.height - 15, 90 + moreWidth, 15);
        g.setColor(graphicsBuffer.getBackgroundColor());
        g.fill(textBox);

        g.setColor(worldBoundsColor);
        g.drawString("Camera #" + (getCameraController().getCameraList().getIndex() + 1) + " / " + getCameraController().getCameraList().size(), 5, imageSize.height - 3);
        g.draw(textBox);

        textBox.setRect(textBox.getX() + textBox.getWidth(), textBox.getY(), 76, 15);
        g.setColor(graphicsBuffer.getBackgroundColor());
        g.fill(textBox);

        g.setColor(worldBoundsColor);
        g.drawString("Zoom x" + Math.round(100 * cameraController.getCameraZoom()) / 100.0d, (int) (textBox.getX() + 10), imageSize.height - 3);
        g.draw(textBox);
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
            g.drawRect((int) ((chunk.bounds.getX() - camera.getPos().x) * factor), (int) ((chunk.bounds.getY() - camera.getPos().y) * factor), (int) (chunk.bounds.getWidth() * factor), (int) (chunk.bounds.getHeight() * factor));
            g.drawString(String.valueOf(INSTANCE.getPhysics().getPositionHash(chunk.bounds.getX(), chunk.bounds.getY())), (int) ((chunk.bounds.getX() - camera.getPos().x) * factor), (int) ((chunk.bounds.getY() - camera.getPos().y + INSTANCE.getPhysics().getChunkSize().y) * factor));
        }
    }

    /**
     * Rendu d'un cercle via le Graphics2D.
     * @param circle cercle à rendre
     * @param g graphics2d utilisé
     */
    public void renderCircle(Circle circle, Graphics2D g) {

        Shape shape = new Ellipse2D.Double(factor * (circle.getPos().x - circle.getR() - camera.getPos().x), factor * (circle.getPos().y - circle.getR() - camera.getPos().y), 2 * factor * circle.getR(), 2 * factor * circle.getR());
        g.rotate(circle.getRot(), factor * (circle.getPos().x - camera.getPos().x), factor * (circle.getPos().y - camera.getPos().y));

        if (!wireframeDisplay && circle.getFillColor() != null) {
            g.setColor(circle.getFillColor());
            g.fill(shape);
        }

        if (circle.getOutlineColor() != null) {
            g.setColor(circle.getOutlineColor());
            g.draw(shape);
        }

        g.draw(new Line2D.Double(factor * (circle.getPos().x - camera.getPos().x), factor * (circle.getPos().y - camera.getPos().y), factor * (circle.getPos().x - camera.getPos().x), factor * (circle.getPos().y - circle.getR() - camera.getPos().y)));

        g.rotate(-circle.getRot(), factor * (circle.getPos().x - camera.getPos().x), factor * (circle.getPos().y - camera.getPos().y));
    }

    /**
     * Rendu d'un Rect via le Graphics2D.
     * @param rect Rect à rendre
     * @param g graphics2d utilisé
     */
    public void renderRect(Rect rect, Graphics2D g) {

        Shape shape = new Rectangle2D.Double(factor * (rect.getPos().x - rect.getSize().x * 0.5f - camera.getPos().x), factor * (rect.getPos().y - rect.getSize().y * 0.5f - camera.getPos().y), factor * rect.getSize().x, factor * rect.getSize().y);

        g.rotate(rect.getRot(), factor * (rect.getPos().x - camera.getPos().x), factor * (rect.getPos().y - camera.getPos().y));

        if (!wireframeDisplay && rect.getFillColor() != null) {
            g.setColor(rect.getFillColor());
            g.fill(shape);
        }

        if (rect.getOutlineColor() != null) {
            g.setColor(rect.getOutlineColor());
            g.draw(shape);
        }

        g.rotate(-rect.getRot(), factor * (rect.getPos().x - camera.getPos().x), factor * (rect.getPos().y - camera.getPos().y));

        /*for (int i = 0; i < 4; ++i)
            g.drawString("corner #"+i, (float)factor*(rect.getCorners()[i].x - camera.getPos().x), (float)factor*(rect.getCorners()[i].y - camera.getPos().y));*/
    }

    /**
     * Rendu d'un Polygon via le Graphics2D
     * @param polygon Polygon à rendre
     * @param g graphics2d utilisé
     */
    public void renderPolygon(Polygon polygon, Graphics2D g) {

        Path2D outline = new Path2D.Float();
        outline.moveTo(factor * (polygon.getPos().x - camera.getPos().x),factor * (polygon.getPos().y - camera.getPos().y));

        Vec2f[] vertices = polygon.getVertices();
        for (int i = 0; i < vertices.length; ++i)
            outline.lineTo(factor * (vertices[i].x - camera.getPos().x),factor * (vertices[i].y - camera.getPos().y));

        outline.lineTo(factor * (vertices[0].x - camera.getPos().x) ,factor * (vertices[0].y - camera.getPos().y));
        outline.closePath();

        if (!wireframeDisplay && polygon.getFillColor() != null) {
            g.setColor(polygon.getFillColor());
            g.fill(outline);
        }

        if (polygon.getOutlineColor() != null) {
            g.setColor(polygon.getOutlineColor());
            g.draw(outline);
        }

        if (renderEntitiesAABB) {
            g.setColor(Color.green);
            Vec2f barycenter = Geometry.getBarycenter(polygon.getVertices());
            g.draw(new Ellipse2D.Double((barycenter.x - camera.getPos().x) * factor - 2.5f, (barycenter.y - camera.getPos().y) * factor - 2.5f, 5f, 5f));

            g.setColor(Color.red);
            g.draw(new Ellipse2D.Double((polygon.getPos().x - camera.getPos().x) * factor - 2.5f, (polygon.getPos().y - camera.getPos().y) * factor - 2.5f, 5f, 5f));
        }
    }

    /**
     * Rendu d'un vecteur via le Graphics2D
     * @param pos point de départ du vecteur
     * @param vector vecteur à dessiner
     * @param scale facteur par lequel multiplier le vecteur
     * @param color couleur du vecteur
     * @param g graphics2d utilisé
     */
    public void renderVector(Vec2f pos, Vec2d vector, double scale, Color color, Graphics2D g) {

        g.setColor(color);
        g.draw(new Line2D.Double((pos.x - camera.getPos().x) * factor, (pos.y - camera.getPos().y) * factor, (pos.x - camera.getPos().x) * factor + vector.x * scale, (pos.y - camera.getPos().y) * factor + vector.y * scale));
    }

    /**
     * Rendu d'un vecteur avec un texte via le Graphics2D
     * @param text texte à ajouter au vecteur
     * @param pos point de départ du vecteur
     * @param vector vecteur à dessiner
     * @param scale facteur par lequel multiplier le vecteur
     * @param color couleur du vecteur
     * @param g graphics2d utilisé
     */
    public void renderVector(String text, Vec2f pos, Vec2d vector, double scale, Color color, Graphics2D g) {

        g.setColor(color);
        g.draw(new Line2D.Double((pos.x - camera.getPos().x) * factor, (pos.y - camera.getPos().y) * factor, (pos.x - camera.getPos().x) * factor + vector.x * scale, (pos.y - camera.getPos().y) * factor + vector.y * scale));

        if (text != null && text.length() > 0) {

            Vec2f textPos = pos.copy().sub(camera.getPos()).mult(factor).add(vector, scale * .5);
            double angle = vector.angleWith(new Vec2f(0,1));
            g.rotate(angle, textPos.x, textPos.y);
            g.drawString(text, textPos.x, textPos.y);
            g.rotate(-angle, textPos.x, textPos.y);
        }
    }

    /**
     * Rendu d'un ressort
     * @param firstEnd première extrémité du ressort
     * @param secondEnd seconde extrémité du ressort
     * @param tension tension dans le ressort (norme de la force qu'il applique aux entités auxquelles il est relié)
     * @param SPRING_CONSTANT constante de raideur du ressort
     * @param REST_LENGTH longueur au repos du ressort
     * @param g graphics2d utilisé
     */
    public void renderSpring(Vec2f firstEnd, Vec2f secondEnd, double tension, double SPRING_CONSTANT, double REST_LENGTH, Graphics2D g) {

        // On copie le graphics pour ne pas modifier le Stroke de l'ancien graphics
        g = (Graphics2D) g.create();

        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND,
                                    BasicStroke.JOIN_MITER, 10, new float[] { 1f + (float) Math.abs(REST_LENGTH / SPRING_CONSTANT - tension / 500f) }, 0.0f));

        g.setColor(Utils.lerpColor(Color.magenta, new Color(40, 0, 56), .3 * (1 + tension / (SPRING_CONSTANT * REST_LENGTH)) + .25));
        g.draw(new Line2D.Double(  (firstEnd.x - camera.getPos().x) * factor, (firstEnd.y - camera.getPos().y) * factor, (secondEnd.x - camera.getPos().x) * factor, (secondEnd.y - camera.getPos().y) * factor));
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

        setDestination(destination.getSize().width, destination.getSize().height);
    }
    /**
     * Définit la taille de la destination de rendu du Renderer
     * @param size Taille de la destination de rendu
     */
    public void setDestination(Dimension size) {

        setDestination(size.width, size.height);
    }
    /**
     * Définit la taille de la destination de rendu du Renderer
     * @param width largeur de la destination de rendu
     * @param height hauteur de la destination de rendu
     */
    public void setDestination(int width, int height) {

        this.graphicsBuffer = new GraphicsBuffer(width, height);
        cameraController.setCameraDisplayBoundsInPixels(width, height);
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

        setRenderFactor(this.factor * scale / this.scale);
        this.scale = scale;
    }

    /**
     * Renvoie le facteur de rendu du Renderer
     * @return factor
     */
    public double getRenderFactor() {

        return this.factor;
    }

    /**
     * Redéfinit le facteur de rendu du Renderer
     * @param newFactor nouveau facteur de rendu
     * A ne pas modifier manuellement !! (D'où l'accès package)
     */
    void setRenderFactor(double newFactor) {

        this.factor = newFactor;
        this.forceFactor = factor / scale * forceScale;
    }

    /**
     * Redéfinit le facteur utilisé pour le rendu des forces
     * @param newFactor nouveau facteur de rendu des forces
     */
    void setForceFactor(double newFactor) {

        this.forceFactor = newFactor;
    }

    /**
     * Renvoie le facteur utilisé pour le rendu des forces
     * @return forceFactor
     */
    public double getForceFactor() {

        return forceFactor;
    }

    /**
     * Renvoie l'échelle utilisée pour le rendu des forces
     * @return forceScale
     */
    public float getForceScale() {

        return forceScale;
    }

    /**
     * Redéfinit l'échelle utilisée pour le rendu des forces
     * @param forceScale nouvelle échelle en px/N
     */
    public void setForceScale(float forceScale) {

        setForceFactor(this.forceFactor * forceScale / this.forceScale);
        this.forceScale = forceScale;
    }

    /**
     * Renvoie le controleur de caméra
     * @return controleur de caméra
     */
    public CameraController getCameraController() {

        return cameraController;
    }

    /**
     * Renvoie la caméra utilisée actuellent par le Renderer
     * @return camera <=> cameraController.cameras.current()
     */
    public Camera getCamera() {

        return camera;
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
     * Renvoie true si le Renderer dessine le système de coordonnées
     * @return renderCoordinateSystem
     */
    public boolean doesRenderCoordinateSystem() {

        return renderCoordinateSystem;
    }

    /**
     * Redéfinit renderCoordinateSystem, qui détermine si le Renderer doit dessiner le système de coordonnées
     * @param renderCoordinateSystem true si le Renderer doit dessiner le repère
     */
    public void setRenderCoordinateSystem(boolean renderCoordinateSystem) {

        this.renderCoordinateSystem = renderCoordinateSystem;
    }

    /**
     * Renvoie true si le Renderer dessine les vecteurs de forces à l'écran
     * @return renderForces
     */
    public boolean doesRenderForces() {

        return renderForces;
    }

    /**
     * Redéfinit renderForces, qui détermine si le Renderer doit dessiner les vecteurs de forces
     * @param renderForces true si le Renderer doit dessiner les vecteurs forces
     */
    public void setRenderForces(boolean renderForces) {

        this.renderForces = renderForces;
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

    /**
     * Renvoie le GraphicsBuffer du Renderer
     * @return graphicsBuffer
     */
    public GraphicsBuffer getGraphicsBuffer() {

        return this.graphicsBuffer;
    }

    /**
     * Renvoie la couleur des bordures de monde
     * @return worldBoundsColor
     */
    public Color getWorldBoundsColor() {

        return worldBoundsColor;
    }

    /**
     * Redéfinit la couleur des bordures de monde
     * @param worldBoundsColor nouvelle couleur des bords du monde
     */
    public void setWorldBoundsColor(Color worldBoundsColor) {

        this.worldBoundsColor = worldBoundsColor;
    }

    /**
     * Renvoie true si le mode d'affichage est wireframe, c'est-à-dire qu'on ne dessine que les bords des entités et pas leur fond
     * @return wireframeDisplay
     */
    public boolean isDisplayModeWireframe() {

        return wireframeDisplay;
    }

    /**
     * Redéfinit wireframeDisplay qui détermine si le Renderer doit dessiner le remplissage / couleur de fond des entités
     * @param wireframeDisplay true pour dessiner uniquement les bords
     */
    public void setWireframeDisplay(boolean wireframeDisplay) {

        this.wireframeDisplay = wireframeDisplay;
    }

    /**
     * Ajoute un élément Renderable aux extras
     * @see #extras
     * @param renderable élément à ajouter
     */
    public void addExtra(Renderable renderable) {

        extras.add(renderable);
    }

    /**
     * Supprime un élément Renderable des extras
     * @see #extras
     * @param renderable élément à supprimer
     */
    public void removeExtra(Renderable renderable) {

        extras.remove(renderable);
    }

    public String toString() {

        return "Renderer[renderSize=(" + this.getDestinationSize().getWidth() + ", " + this.getDestinationSize().getHeight() + "), renderFactor=" + this.factor + ",\n   CameraController=" + getCameraController()

            + "]";
    }
}

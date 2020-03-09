package fr.insalyon.mxyns.collinsa.ui.panels;

import fr.insalyon.mxyns.collinsa.render.CameraController;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.threads.RefreshingThread;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;

/**
 * Panel dans lequel est affiché le monde.
 * C'est un panel ordinaire à quelques choses près :
 *    - Il écoute les mouvements/clics/molette de souris, la prise/perte de focus et transmet les infos pour déplacer la caméra du renderer qui lui est associé
 *    - Il se redessine automatiquement grâce au refreshingThread
 */
public class SandboxPanel extends JPanel implements FocusListener {

    /**
     * Renderer associé au panel, utilisé pour recupérer l'image
     */
    private Renderer renderer;

    /**
     * RefreshingThread associé au panel, il sert à forcer l'affichage à se mettre à jour automatiquement
     * (c'est-à-dire qu'il force le repaint du panel périodiquement)
     */
    private RefreshingThread refreshingThread;

    public SandboxPanel() {

        // On crée un Thread qui permettra de repaint le panel automatiquement (il n'est pas actif par défaut)
        refreshingThread = new RefreshingThread(this);

        // Rend le panel focusable pour pouvoir utiliser les controls clavier
        setFocusable(true);

        // Bordure quand il n'a pas le focus
        setBorder(BorderFactory.createLineBorder(Color.black, 2));

        addFocusListener(this);
    }
    public SandboxPanel(Renderer renderer) {

        this();

        // Ajoute le controleur de la caméra aux keyListeners du panel
        setRenderer(renderer);
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        // On dessine la dernière image générée accessible dans le buffer
        g.drawImage(renderer.getGraphicsBuffer().getImage(),0 , 0, null);
        this.getBorder().paintBorder(this, g, 0, 0, getWidth(), getHeight());
    }

    /**
     * Renvoie le renderer associé au panel
     * @return renderer utilisé pour le rendu du panel
     */
    public Renderer getRenderer() {

        return renderer;
    }

    /**
     * Défini le Renderer associé à ce panel, ne pas utiliser sans redéfinir le panel associé au renderer.
     * @param renderer le nouveau renderer associé
     */
    public void setRenderer(Renderer renderer) {

        this.renderer = renderer;

        // On enlève tous les controleurs de caméra associés
        for (KeyListener keyListener : this.getKeyListeners())
            if (keyListener instanceof CameraController)
                removeKeyListener(keyListener);

        //On ajoute le nouveau controleur associé à la nouvelle camera
        addKeyListener(renderer.getCameraController());
        addMouseWheelListener(renderer.getCameraController());
        addMouseListener(renderer.getCameraController());
        addMouseMotionListener(renderer.getCameraController());
    }

    /**
     * Renvoie le RefreshingThread associé au panel
     * @return refreshingThread
     */
    public RefreshingThread getRefreshingThread() {

        return refreshingThread;
    }

    /**
     * Redéfinit le RefreshingThread associé au panel
     * @param refreshingThread nouveau thread de rafraichissement
     */
    private void setRefreshingThread(RefreshingThread refreshingThread) {

        this.refreshingThread = refreshingThread;
    }

    /**
     * Lorsque la panel obtient le focus on change la bordure
     */
    @Override
    public void focusGained(FocusEvent e) {

        setBorder(BorderFactory.createLineBorder(Color.blue, 1));
    }

    /**
     * Lorsque le panel perd le focus, on désactive les mouvements de Camera
     */
    @Override
    public void focusLost(FocusEvent e) {

        renderer.getCameraController().deleteActiveKeys();
        setBorder(BorderFactory.createLineBorder(Color.black, 2));
    }

    /**
     * Lance le Thread de rafraichissement du Panel
     */
    public void beginRefresh() {

        refreshingThread.start();
    }

    /**
     * Stoppe le Thread de rafraichissement du Panel
     */
    public void stopRefresh() {

        refreshingThread.interrupt();
    }
}

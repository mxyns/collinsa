package fr.insalyon.mxyns.collinsa.ui.panels;

import fr.insalyon.mxyns.collinsa.render.CameraController;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.threads.RefreshingThread;
import fr.insalyon.mxyns.collinsa.ui.frames.MainFrame;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;

/**
 * Panel dans lequel est affiché le monde. C'est un panel ordinaire à quelques choses près : - Il écoute les
 * mouvements/clics/molette de souris, la prise/perte de focus et transmet les infos pour déplacer la caméra du renderer
 * qui lui est associé - Il se redessine automatiquement grâce au refreshingThread
 */
public class SandboxPanel extends JPanel implements FocusListener {

    private final MainFrame mainFrame;

    /**
     * Renderer associé au panel, utilisé pour recupérer l'image
     */
    private Renderer renderer;

    /**
     * RefreshingThread associé au panel, il sert à forcer l'affichage à se mettre à jour automatiquement (c'est-à-dire
     * qu'il force le repaint du panel périodiquement)
     */
    private RefreshingThread refreshingThread;

    public SandboxPanel(MainFrame mainFrame) {

        this.mainFrame = mainFrame;

        // On crée un Thread qui permettra de repaint le panel automatiquement (il n'est pas actif par défaut)
        refreshingThread = new RefreshingThread(this);

        // Rend le panel focusable pour pouvoir utiliser les controls clavier
        setFocusable(true);

        // Bordure quand il n'a pas le focus
        setBorder(BorderFactory.createLineBorder(Color.black, 2));

        addFocusListener(this);


        // On transmet tous les événements importants aux outils
        MouseAdapter toolEventTransmitter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                mainFrame.getSandboxPanel().requestFocus();
                mainFrame.getSelectedTool().onClick(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {

                mainFrame.getSelectedTool().onDrag(e);
                mainFrame.getSandboxPanel().requestFocus();
            }

            @Override
            public void mousePressed(MouseEvent e) {

                mainFrame.getSandboxPanel().requestFocus();
                mainFrame.getSelectedTool().onMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                mainFrame.getSandboxPanel().requestFocus();
                mainFrame.getSelectedTool().onMouseReleased(e);
            }
        };
        addMouseListener(toolEventTransmitter);
        addMouseMotionListener(toolEventTransmitter);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                mainFrame.getSelectedTool().onKeyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {

                mainFrame.getSelectedTool().onKeyReleased(e);
            }
        });
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        // On dessine la dernière image générée accessible dans le buffer
        if (renderer != null)
            g.drawImage(renderer.getGraphicsBuffer().getImage(), 0, 0, null);

        // On redessine les bordures pour savoir si le panel est focusé ou pas
        this.getBorder().paintBorder(this, g, 0, 0, getWidth(), getHeight());
    }

    /**
     * Renvoie le renderer associé au panel
     *
     * @return renderer utilisé pour le rendu du panel
     */
    public Renderer getRenderer() {

        return renderer;
    }

    /**
     * Défini le Renderer associé à ce panel, ne pas utiliser sans redéfinir le panel associé au renderer.
     *
     * @param renderer le nouveau renderer associé
     */
    public void setRenderer(Renderer renderer) {

        this.renderer = renderer;
    }

    public void addCameraController(CameraController cameraController) {

        addKeyListener(cameraController);
        addMouseWheelListener(cameraController);
        addMouseListener(cameraController);
        addMouseMotionListener(cameraController);
    }

    public void removeCameraController(CameraController cameraController) {

        removeKeyListener(cameraController);
        removeMouseWheelListener(cameraController);
        removeMouseListener(cameraController);
        removeMouseMotionListener(cameraController);
    }

    /**
     * Renvoie le RefreshingThread associé au panel
     *
     * @return refreshingThread
     */
    public RefreshingThread getRefreshingThread() {

        return refreshingThread;
    }

    /**
     * Redéfinit le RefreshingThread associé au panel
     *
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

        refreshingThread.queryStop();
    }
}

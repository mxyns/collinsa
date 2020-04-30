package fr.insalyon.mxyns.collinsa.ui.tools;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Outil permettant le contrôle des caméras
 *
 * Les contrôles ne sont actifs que si le SandboxPanel est focusé (bordure bleue). Si ce n'est pas le cas il faut faire clic gauche sur le panel.
 *
 * Contrôles :
 *    + Clavier :
 *        - flèches directionnelles : déplacer la caméra
 *        - PAGEUP / PAGEDOWN : changer de caméra
 *    + Souris :
 *        - clic gauche : suivre l'entité visée
 *        - clic droit : arrêt du suivi
 *        - cliqué-glissé : translate la caméra
 *        - molette : zoom
 */
public class MoveCameraTool extends Tool {


    /**
     * Constructeur qui précise le nom, le tooltip et le chemin de l'icone de l'outil
     */
    public MoveCameraTool() {

        super("Move Camera", "Controls the cameras", "/icons/move_camera.png");
    }
    
    /**
     * Trouve l'entité la plus proche du clic et la fait suivre
     */
    @Override
    public void onClick(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON1) {

            Vec2f posInWorld = new Vec2f(e.getX(), e.getY()).div((float) Collinsa.INSTANCE.getRenderer().getRenderFactor()).add(Collinsa.INSTANCE.getRenderer().getCamera().getPos());
            Entity selected = Collinsa.INSTANCE.getPhysics().getClosestEntity(posInWorld, .1f);
            if (selected != null)
                Collinsa.INSTANCE.getRenderer().getCamera().follow(selected);

        } else
            Collinsa.INSTANCE.getRenderer().getCamera().follow(null);
    }

    /**
     * Au moment où l'outil est sélectionné, on ajoute au SandboxPanel le CameraController qui est un Listener
     * @see fr.insalyon.mxyns.collinsa.render.CameraController
     * @see fr.insalyon.mxyns.collinsa.render.CameraController#mouseDragged(MouseEvent) 
     * @see fr.insalyon.mxyns.collinsa.render.CameraController#mousePressed(MouseEvent) 
     * @see fr.insalyon.mxyns.collinsa.render.CameraController#mouseReleased(MouseEvent) 
     * @see fr.insalyon.mxyns.collinsa.render.CameraController#mouseWheelMoved(MouseWheelEvent) (MouseEvent) 
     * @see fr.insalyon.mxyns.collinsa.render.CameraController#keyPressed(KeyEvent) 
     * @see fr.insalyon.mxyns.collinsa.render.CameraController#keyReleased(KeyEvent)
     */
    @Override
    public void onSelected() {

        Collinsa.INSTANCE.getMainFrame().getSandboxPanel().addCameraController(Collinsa.INSTANCE.getRenderer().getCameraController());
    }

    /**
     * Supprime le CameraController du panel pour désactiver les controles caméra.
     */
    @Override
    public void onDeselected() {

        Collinsa.INSTANCE.getMainFrame().getSandboxPanel().removeCameraController(Collinsa.INSTANCE.getRenderer().getCameraController());
    }
}

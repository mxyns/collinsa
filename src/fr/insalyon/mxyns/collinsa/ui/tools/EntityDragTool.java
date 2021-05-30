package fr.insalyon.mxyns.collinsa.ui.tools;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.UUID;

/**
 * Outil permettant de déplacer une entité dans la simulation en la déplaçant avec la souris
 * Clic-gauche désactive l'entité pendant le déplacement pour ignorer les collision tandis que clic droit laisse l'entité activée.
 * Avec un clic droit l'entité pousse donc les autres quand on la déplace
 */
public class EntityDragTool extends Tool {

    /**
     * Comme pour la caméra, point (sur l'écran <=> en pixels) d'origine de chaque déplacement
     */
    private Point dragOrigin;

    /**
     * Ancien état (activé / désactivé) de l'entité déplacée
     */
    private boolean previousState;

    /**
     * Entité en train d'être déplacée
     */
    private UUID grabbedEntity;

    /**
     * Constructeur qui précise le nom, le tooltip et le chemin de l'icone de l'outil
     */
    public EntityDragTool() {

        super("Déplacement d'entité", "<html>Déplace l'entité cliquée<br>Clic gauche désactive l'entité avant déplacement<br>Clic droit laisse l'entité active (elle continue de pousser les autres)</html>", "/icons/drag.png");
    }

    /**
     * Enregistre la position du premier clic à partir du MouseEvent donné
     */
    @Override
    public void onMousePressed(MouseEvent e) {

        Vec2f posInWorld = new Vec2f(e.getX(), e.getY()).div((float) Collinsa.INSTANCE.getRenderer().getRenderFactor()).add(Collinsa.INSTANCE.getRenderer().getCamera().getPos());
        Entity selected = Collinsa.INSTANCE.getPhysics().getClosestEntity(posInWorld, .1f);

        if (selected != null) {
            dragOrigin = e.getPoint();
            previousState = selected.isActivated();
            grabbedEntity = selected.uuid;

            if (e.getButton() == MouseEvent.BUTTON1)
                selected.setActivated(false);
        } else {
            dragOrigin = null;
            grabbedEntity = null;
        }
    }

    /**
     * Déplace l'entité et replace l'origine pour le prochain déplacement
     */
    @Override
    public void onDrag(MouseEvent e) {

        if (dragOrigin == null || grabbedEntity == null)
            return;

        double renderFactor = Collinsa.INSTANCE.getRenderer().getRenderFactor();

        Entity toEdit = Collinsa.INSTANCE.getPhysics().getTickMachine().getPrev().entities.get(grabbedEntity);
        toEdit.getPos().sub((float) ((dragOrigin.getX() - e.getPoint().getX())  / renderFactor), (float) ((dragOrigin.getY() - e.getPoint().getY()) / renderFactor));

        //toEdit.update(0);

        dragOrigin = e.getPoint();
    }

    /**
     * Réactive (s'il le faut) l'entité qui était attrapée par l'outil quand on relache la souris et remet les variables dans leur état initial
     */
    @Override
    public void onMouseReleased(MouseEvent e) {

        if (grabbedEntity != null)
            Collinsa.INSTANCE.getPhysics().getEntities().get(grabbedEntity).setActivated(previousState);

        grabbedEntity = null;
        dragOrigin = null;
    }
}

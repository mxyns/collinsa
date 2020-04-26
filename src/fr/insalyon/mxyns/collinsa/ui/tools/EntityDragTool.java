package fr.insalyon.mxyns.collinsa.ui.tools;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Point;
import java.awt.event.MouseEvent;

public class EntityDragTool extends Tool {

    private Point dragOrigin;
    private boolean previousState;
    private Entity grabbedEntity;

    public EntityDragTool() {

        super("Entity drag", "Translates the currently selected entity", "/drag.png");
    }

    @Override
    public void onMousePressed(MouseEvent e) {

        Vec2f posInWorld = new Vec2f(e.getX(), e.getY()).div((float) Collinsa.INSTANCE.getRenderer().getRenderFactor()).add(Collinsa.INSTANCE.getRenderer().getCamera().getPos());
        Entity selected = Collinsa.INSTANCE.getPhysics().getClosestEntity(posInWorld, .1f);

        if (selected != null) {
            dragOrigin = e.getPoint();
            previousState = selected.isActivated();
            grabbedEntity = selected;

            if (e.getButton() == MouseEvent.BUTTON1)
                selected.setActivated(false);
        } else {
            dragOrigin = null;
            grabbedEntity = null;
        }
    }

    @Override
    public void onDrag(MouseEvent e) {

        if (dragOrigin == null || grabbedEntity == null)
            return;

        double renderFactor = Collinsa.INSTANCE.getRenderer().getRenderFactor();
        grabbedEntity.getPos().sub((float) ((dragOrigin.getX() - e.getPoint().getX())  / renderFactor), (float) ((dragOrigin.getY() - e.getPoint().getY()) / renderFactor));

        dragOrigin = e.getPoint();
    }

    @Override
    public void onMouseReleased(MouseEvent e) {

        if (grabbedEntity != null)
            grabbedEntity.setActivated(previousState);

        grabbedEntity = null;
        dragOrigin = null;
    }
}

package fr.insalyon.mxyns.collinsa.ui.tools;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.event.MouseEvent;

public class MoveCameraTool extends Tool {

    public MoveCameraTool() {

        super("Move Camera", "Controls the cameras", "/move_camera.png");
    }

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

    @Override
    public void onSelected() {

        Collinsa.INSTANCE.getMainFrame().getSandboxPanel().addCameraController(Collinsa.INSTANCE.getRenderer().getCameraController());
    }

    @Override
    public void onDeselected() {

        Collinsa.INSTANCE.getMainFrame().getSandboxPanel().removeCameraController(Collinsa.INSTANCE.getRenderer().getCameraController());
    }
}

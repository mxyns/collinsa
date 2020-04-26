package fr.insalyon.mxyns.collinsa.ui.tools;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.event.MouseEvent;

public class FreezeEntityTool extends Tool{

    public FreezeEntityTool() {

        super("Freeze Entity", "Click on an entity to (de)activate it", "/timer.png");
    }

    @Override
    public void onClick(MouseEvent e) {

        Physics physics = Collinsa.INSTANCE.getPhysics();

        Vec2f posInWorld = new Vec2f(e.getX(), e.getY()).div((float) Collinsa.INSTANCE.getRenderer().getRenderFactor()).add(Collinsa.INSTANCE.getRenderer().getCamera().getPos());
        Entity selected = physics.getClosestEntity(posInWorld, .1f);

        if (selected != null)
            selected.setActivated(!selected.isActivated());

    }
}

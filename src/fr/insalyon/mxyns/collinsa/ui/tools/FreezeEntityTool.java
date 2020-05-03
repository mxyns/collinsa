package fr.insalyon.mxyns.collinsa.ui.tools;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.event.MouseEvent;

/**
 * Outil qui permet de désactiver une entité cliquée
 */
public class FreezeEntityTool extends Tool{


    /**
     * Constructeur qui précise le nom, le tooltip et le chemin de l'icone de l'outil
     */
    public FreezeEntityTool() {

        super("Freeze une entité", "Clic sur une entité pour l'activer/désactiver", "/icons/timer.png");
    }

    /**
     * Trouve l'entité la plus proche du point cliqué et la désactive
     */
    @Override
    public void onClick(MouseEvent e) {

        Physics physics = Collinsa.INSTANCE.getPhysics();

        Vec2f posInWorld = new Vec2f(e.getX(), e.getY()).div((float) Collinsa.INSTANCE.getRenderer().getRenderFactor()).add(Collinsa.INSTANCE.getRenderer().getCamera().getPos());
        Entity selected = physics.getClosestEntity(posInWorld, .1f);

        if (selected != null)
            selected.setActivated(!selected.isActivated());

    }
}

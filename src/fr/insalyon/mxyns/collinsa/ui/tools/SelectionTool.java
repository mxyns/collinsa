package fr.insalyon.mxyns.collinsa.ui.tools;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.Utils;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Outil permettant de sélectionner une entité (pour la modifier / supprimer ou juste mieux la voir)
 */
public class SelectionTool extends Tool {

    /**
     * Entité actuellement sélectionnée
     */
    Entity selected = null;

    /**
     * Constructeur qui précise le nom, le tooltip et le chemin de l'icone de l'outil
     */
    public SelectionTool() {

        super("Selection", "Select an entity", "/icons/select.png");
    }

    /**+
     * Trouve l'entité la plus proche du clic et la garde dans 'selected'
     * Clic gauche pour sélectionner
     * Clic droit pour désélectionner
     */
    @Override
    public void onClick(MouseEvent e) {

        if (e.getButton() != MouseEvent.BUTTON1) {
            setSelectedEntity(null);
            return;
        }

        Vec2f posInWorld = new Vec2f(e.getX(), e.getY()).div((float) Collinsa.INSTANCE.getRenderer().getRenderFactor()).add(Collinsa.INSTANCE.getRenderer().getCamera().getPos());
        Physics physics = Collinsa.INSTANCE.getPhysics();

        selected = physics.getClosestEntity(posInWorld, .1f);
    }

    /**
     * Redéfinit la valeur de 'selected', l'entité sélectionnée par l'outil
     * @param entity nouvelle entité sélectionnée
     */
    public void setSelectedEntity(Entity entity) {

        this.selected = entity;
    }

    /**
     * Dessine une bordure en pointillés autour de l'entité sélectionnée
     * @see fr.insalyon.mxyns.collinsa.ui.panels.SandboxPanel#paint(Graphics)
     * @param renderer renderer utilisé pour récupérer l'image à modifier
     */
    public void drawSelectedEntityOutline(Renderer renderer) {

        if (getSelectedEntity() == null)
            return;

        Graphics2D g2 = (Graphics2D) renderer.getGraphicsBuffer().getImage().getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Entity selected = getSelectedEntity();

        Color[] oldColors = {selected.getOutlineColor(), selected.getFillColor()};
        Color newColor = Utils.getHighContrastColor(selected.getFillColor() != null ? selected.getFillColor() : selected.getOutlineColor());
        selected.setOutlineColor(newColor);
        selected.setFillColor(null);

        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                                     BasicStroke.JOIN_MITER, 10, new float[] { (float) (selected.getMaximumSize() * .1) }, 0.0f));

        selected.render(renderer, g2);

        selected.setOutlineColor(oldColors[0]);
        selected.setFillColor(oldColors[1]);
    }

    /**
     * Renvoie l'entité actuellement sélectionnée
     * @return selectedEntity
     */
    public Entity getSelectedEntity() {

        return selected;
    }
}
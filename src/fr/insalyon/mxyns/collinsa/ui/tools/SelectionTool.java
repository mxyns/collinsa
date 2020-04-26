package fr.insalyon.mxyns.collinsa.ui.tools;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.Utils;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

public class SelectionTool extends Tool {

    Entity selected = null;
    Color oldOutlineColor;

    public SelectionTool() {

        super("Selection", "Select an entity", "/select.png");
    }

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

    public void setSelectedEntity(Entity entity) {

        if (this.selected != null && oldOutlineColor != null)
            this.selected.setOutlineColor(oldOutlineColor);

        this.selected = entity;

        /*oldOutlineColor = entity.getOutlineColor();
        Color backgroundColor = Collinsa.INSTANCE.getRenderer().getGraphicsBuffer().getBackgroundColor();
        Color fill = entity.getFillColor() != null ? entity.getFillColor() : (entity.getOutlineColor() != null ? entity.getOutlineColor() : backgroundColor);
        Color outline = entity.getOutlineColor() != null ? entity.getOutlineColor() : fill;

        Color contrastColor = Utils.getHighContrastColor(backgroundColor, fill, outline);
        this.selected.setOutlineColor(contrastColor);*/
    }

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

    public Entity getSelectedEntity() {

        return selected;
    }
}
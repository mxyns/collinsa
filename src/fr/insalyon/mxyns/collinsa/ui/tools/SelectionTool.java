package fr.insalyon.mxyns.collinsa.ui.tools;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.Utils;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Outil permettant de sélectionner une entité (pour la modifier / supprimer ou juste mieux la voir)
 */
public class SelectionTool extends Tool {

    /**
     * Entité actuellement sélectionnée
     */
    private Entity selected = null;

    /**
     * Constructeur qui précise le nom, le tooltip et le chemin de l'icone de l'outil
     */
    public SelectionTool() {

        super("Selection", "<html>Sélectionne une entité pour suppression / modification<br>"
                            + "Souris : <br>"
                            + "   - Clic gauche pour sélectionner<br>"
                            + "   - Clic droit pour désélectionner<br>"
                            + "<br>Clavier : <br>"
                            + "   - ENTER pour modifier<br>"
                            + "   - ESC pour désélectionner<br>"
                            + "   - SUPPR / DEL pour supprimer"
                            + "</html>", "/icons/select.png");
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
     * Contrôles au clavier
     */
    @Override
    public void onKeyPressed(KeyEvent e) {

        if (selected != null)
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                setSelectedEntity(null);
            else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                Collinsa.INSTANCE.getMainFrame().supprimer();
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER)
                Collinsa.INSTANCE.getMainFrame().ouvrirPageModification(selected);
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
    public void drawSelectedEntityOutline(Renderer renderer, Graphics2D g) {

        if (selected == null)
            return;

        // On fait une copie du Graphics pour ne pas affecter le reste du rendu
        g = (Graphics2D) g.create();

        Entity selected = getSelectedEntity();

        Color[] oldColors = { selected.getOutlineColor(), selected.getFillColor()};
        Color newColor = Utils.getHighContrastColor(selected.getFillColor() != null ? selected.getFillColor() : selected.getOutlineColor());
        selected.setOutlineColor(newColor);
        selected.setFillColor(null);

        g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                                    BasicStroke.JOIN_MITER, 10, new float[] { (float) (selected.getMaximumSize() * .1) }, 0.0f));

        selected.render(renderer, g);

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
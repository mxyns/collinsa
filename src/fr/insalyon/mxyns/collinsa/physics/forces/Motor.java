package fr.insalyon.mxyns.collinsa.physics.forces;

import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Graphics2D;

public class Motor extends Force {

    /**
     * Couple moteur
     */
    public double torque;

    /**+
     * Crée un moteur qui applique un couple moteur 'torque' autour du CdM
     * @param torque moment du couple moteur
     */
    public Motor(Entity target, double torque) {

        this.target = target;
        this.torque = torque;
    }

    /**
     * S'applique seulement à target donc on simplifie la méthode
     * @return true si appliquée
     */
    @Override
    public boolean apply() {

        if (target.getCollisionType() == Collision.CollisionType.CLASSIC)
            applyMoment(target, computeMoment(null, null));

        return true;
    }

    /**
     * @return F = 0 car la résultante d'un moteur couple est nulle
     */
    @Override
    protected Vec2d computeValue() {

        return Vec2d.zero();
    }

    /**
     * Aucune idée pour l'affichage d'un moteur pour l'instant (un M au centre peut être ?)
     */
    @Override
    public void render(Renderer renderer, Graphics2D g) {

        Vec2f textPos = target.getPos().copy().sub(renderer.getCamera().getPos()).mult(renderer.getRenderFactor());
        String text = "M[" + torque + "]";
        g.drawString(text, textPos.x - g.getFontMetrics().stringWidth(text) * .5f, textPos.y);
    }

    /**
     * Même si la résultante est nulle, le couple moteur applique un moment (ici toujours au CdM pour l'instant)
     * @return torque
     */
    @Override
    protected double computeMoment(Vec2d GM, Vec2d value) {

        return torque;
    }
}

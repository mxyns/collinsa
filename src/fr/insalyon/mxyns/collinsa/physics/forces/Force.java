package fr.insalyon.mxyns.collinsa.physics.forces;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.ticks.Tick;
import fr.insalyon.mxyns.collinsa.render.Renderable;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.UUID;

import static fr.insalyon.mxyns.collinsa.physics.collisions.Collision.CollisionType.CLASSIC;

/**
 * Classe représentant une Force physique.
 */
public abstract class Force implements Renderable {

    /**
     * Entités source et cible de la force.
     * La seule différence est que si on met la force dans les globalForces de Physics, la force sera appliquée entre 'source' et toutes les autres entités de la simulation.
     */
    protected Entity target = null, source = null;

    /**
     * Vecteurs partant du centre de l'entité source et target jusqu'au point d'application de la force, dans le repère local
     */
    public Vec2d toTargetApplicationPointLocal = Vec2d.zero(), toSourceApplicationPointLocal = Vec2d.zero();

    /**
     * Vecteurs partant du centre de l'entité source et target jusqu'au point d'application de la force dans un repère 'intermédiaire' (rotation prise en compte mais pas la position de l'entité)
     */
    protected Vec2d toTargetApplicationPoint = Vec2d.zero(), toSourceApplicationPoint = Vec2d.zero();

    /**
     * Dernière valeur de la force calculée. Permet de ne pas avoir à recalculer la valeur pour rendu
     */
    public Vec2d lastValue;

    /**
     * Couleur par défaut pour le rendu des forces. Modifiable pour chaque force par la suite
     */
    protected Color renderColor = Color.GREEN;

    /**
     * Méthode abstraite. Calcule la valeur de la force donc différente pour chaque force
     *
     * @return Vec2d vecteur force (direction et norme)
     * @param readTick tick utilisé pour les calculs
     */
    protected abstract Vec2d computeValue(Tick readTick);

    public abstract Force copy();

    public Force copy(Entity readSource, Entity readTarget) {

        Force copy = this.copy();
        copy.target = readTarget;
        copy.source = readSource;

        return copy;
    }

    /**
     * Calcule le moment créé par la force lors de son application en un point M
     *
     *
     * @param readTick
     * @param GM vecteur G(centre de l'entité) ->  M (point d'application)
     * @param value vecteur force appliqué
     *
     * @return valeur du moment autour de l'axe (Gz)
     */
    protected double computeMoment(Tick readTick, Vec2d GM, Vec2d value) {

        return Vec2d.cross(GM, value);
    }

    /**
     * Applique une force aux entités 'source' et 'target'
     *
     * @return true si la force a été appliquée
     * @param readTick tick utilisé pour les calculs
     */
    public boolean apply(Tick readTick) {

        if (target == null || (target.isKinematic() && source.isKinematic()) && lastValue == null) return false;

        lastValue = computeValue(readTick);
        toSourceApplicationPoint = toSourceApplicationPointLocal.rotateOut(source.getRot());
        toTargetApplicationPoint = toTargetApplicationPointLocal.rotateOut(target.getRot());

        if(Double.isNaN(lastValue.x) || Double.isNaN(lastValue.y))
            return false;

        if (target.getCollisionType() == CLASSIC) {

            applyForce(target, lastValue);
            applyMoment(target, computeMoment(readTick, toTargetApplicationPoint, lastValue));
        }

        if (source != null && source.getCollisionType() == CLASSIC) {

            applyForce(source, lastValue.neg());
            applyMoment(source, computeMoment(readTick, toSourceApplicationPoint, lastValue));
            lastValue.neg();
        }

        return true;
    }

    /**
     * Applique une force à une entité
     *
     * @param entity entité sur laquelle appliquer la force
     * @param force force à appliquer
     */
    public static void applyForce(Entity entity, Vec2d force) {

        entity.getAcc().add(force, entity.getInertia().getMassInv());
    }

    /**
     * Applique un moment (modification d'accélération angulaire à une entité)
     * @param entity entité sur laquelle appliquer le moment
     * @param moment valeur du moment à appliquer
     */
    public static void applyMoment(Entity entity, double moment) {

        entity.setAngAcc((float) (entity.getAngAcc() + moment * entity.getInertia().getJInv()));
    }

    /**
     * Redéfinit la cible de la force
     *
     * @param entity nouvelle cible
     */
    public void setTarget(Entity entity) {

        target = entity;
    }

    /**
     * Rendu d'une force : une ligne avec un texte
     * @param renderer renderer utilisé pour le rendu
     * @param g graphics associé au panel pour dessin
     */
    @Override
    public void render(Renderer renderer, Graphics2D g) {

        if (lastValue == null)
            return;

        renderer.renderVector(target.getPos().copy().add(toTargetApplicationPoint), lastValue, renderer.getForceFactor(), renderColor, g);
        renderer.renderVector(source.getPos().copy().add(toSourceApplicationPoint), lastValue, -renderer.getForceFactor(), renderColor, g);

        String text = getClass().getSimpleName();
        if (text.length() > 0) {

            Vec2f targetEnd = target.getPos().copy().add(toTargetApplicationPoint);
            Vec2f sourceEnd = source.getPos().copy().add(toSourceApplicationPoint);

            // FIXME : giga moche qd toTargetApplicationPoint != (0,0) donc c raté
            Vec2f textPos = targetEnd.sub(renderer.getCamera().getPos()).add(sourceEnd.sub(targetEnd), .5).mult(renderer.getRenderFactor());

            double angle = lastValue.angleWith(new Vec2f(0,1));

            g.rotate(angle, textPos.x, textPos.y);
            g.drawString(text, textPos.x, textPos.y);
            g.rotate(-angle, textPos.x, textPos.y);
        }
    }

    /**
     * Détermine si la force concerne l'entité donnée
     *
     * @param entity entité donnée
     * @return true si cette entité est soit la source soit la target
     */
    public boolean affects(Entity entity) {

        return entity == source || entity == target;
    }
    public boolean affects(UUID uuid) {

        return (source != null && uuid == source.uuid) || (target != null && uuid == target.uuid);
    }

    public Entity getTarget() {

        return target;
    }

    public Entity getSource() {

        return source;
    }
}
package fr.insalyon.mxyns.collinsa.ui.tools;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.forces.*;
import fr.insalyon.mxyns.collinsa.utils.Utils;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import javax.swing.JOptionPane;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.function.BiConsumer;

/**
 * Outil permettant de choisir et d'appliquer une force à une entité, entre deux entités, ou d'ajouter une force globale.
 *
 * Clic gauche :
 *    - sélectionne une entité et propose de lui ajouter une force. si cette force a besoin de deux entités, le prochain clic permettra de la sélectionner
 *
 * Clic droit :
 *    - si le premier clic est un clic droit
 *       - si le clic est fait dans le vide et que la force choisie concerne deux entités, on propose de créer une force globale avec l'entité visée comme source
 *       - si le clic est fait dans le vide et que la force choisie concerne une seule entité, on propose de créer une force globale à partir de la force choisie
 *       - si le clic est fait sur une entité et que la force choisie n'en concerne qu'une, on applique la force normalement
 *
 *    - sinon il se comporte comme un clic normal
 */
public class ForcesTool extends Tool {

    private Entity first = null;
    private Entity second = null;
    private final ArrayList<String> forces = new ArrayList<>();
    private final ArrayList<BiConsumer<Entity, Entity>> forcesFunctions = new ArrayList<>();
    private int choix = Integer.MIN_VALUE;

    /**
     * Constructeur qui précise le nom, le tooltip et le chemin de l'icone de l'outil
     * Initialise aussi les ArrayList forces et forcesFunctions
     */
    public ForcesTool() {

        super("Forces", "Add a force to the selected item", null);
        forces.add("Gravité");
        forcesFunctions.add(this::makeGravity);
        forces.add("Gravité Planète");
        forcesFunctions.add(this::makePlanetGravity);
        forces.add("Ressort");
        forcesFunctions.add(this::makeSpring);
        forces.add("Moteur");
        forcesFunctions.add(this::makeMotor);
    }

    @Override
    public void onClick(MouseEvent e) {

        Vec2f posInWorld = new Vec2f(e.getX(), e.getY()).div((float) Collinsa.INSTANCE.getRenderer().getRenderFactor()).add(Collinsa.INSTANCE.getRenderer().getCamera().getPos());
        Entity selected = Collinsa.INSTANCE.getPhysics().getClosestEntity(posInWorld, .1f);

        if (choix == -1) { // Si on a annulé le dialog on remet tout à zero
            reset();
            return;
        }

        if (first == null) { // premier clic

            choix = JOptionPane.showOptionDialog(Collinsa.INSTANCE.getMainFrame(), "Sélectionnez la force à appliquer", "Forces", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, forces.toArray(), forces.get(0));
            first = selected;

            // Si c'est une des forces qui ne s'applique qu'à un seul objet ou si l'utilisateur a fait un clic droit (=> force globale)
            if (e.getButton() != MouseEvent.BUTTON1 || choix == 1 || choix == 3)
                createForce(choix);

        } else if (second == null) { // deuxieme clic

            second = selected;
            createForce(choix);
        }
    }

    /**
     * Crée une force à partir de l'indice choisi
     * @param choix indice de la force
     */
    public void createForce(int choix) {

        if (first == second && first != null) {
            reset();
            return;
        }

        forcesFunctions.get(choix).accept(first, second);
        reset();
    }

    /**
     * Crée une force Moteur et l'applique à 'entity'
     * @param entity entité à laquelle appliquer la force. si null, on applique une force globale
     * @param dummy à ignorer, est ici juste pour que makeMotor soit un BiConsumer
     */
    private void makeMotor(Entity entity, Entity dummy) {

        String ret = JOptionPane.showInputDialog(Collinsa.INSTANCE.getMainFrame(), "Indiquez le couple moteur à appliquer", "Moteur", JOptionPane.OK_CANCEL_OPTION);
        Double torque;
        try {

            torque = Double.parseDouble(ret);
        } catch (Exception e) {
            torque = null;
        }

        if (torque == null)
            return;

        if (entity == null)
            addGlobalForce(new Motor(entity, torque));
        else
            addForce(new Motor(entity, torque));
    }

    /**
     * Crée une force de Gravité et l'applique entre 'entity' et 'target'
     * @param source entité source à laquelle appliquer la force
     * @param target deuxième entité à laquelle appliquer la force. si null, on applique une force globale
     */
    public void makeGravity(Entity source, Entity target) {

        if (source == null)
            return;

        if (target == null)
            addGlobalForce(new Gravity(source, null));
        else
            addForce(new Gravity(source, target));
    }

    /**
     * Crée une force PlanetGravity et l'applique à 'entity'
     * @param entity entité à laquelle appliquer la force. si null, on applique une force globale
     * @param dummy à ignorer, est ici juste pour que makeMotor soit un BiConsumer
     */
    public void makePlanetGravity(Entity entity, Entity dummy /* à ignorer, est ici juste pour que makePlanetGravity soit un BiConsumer */) {

        String ret = JOptionPane.showInputDialog(Collinsa.INSTANCE.getMainFrame(), "Indiquez l'intensité de la gravité choisie", "Gravité", JOptionPane.OK_CANCEL_OPTION);
        Double gFactor;
        try {

            gFactor = Double.parseDouble(ret);
        } catch (Exception e) {
            gFactor = null;
        }

        if (gFactor == null)
            return;

        gFactor = Utils.constrain(gFactor, -20, 20);

        if (entity == null)
            addGlobalForce(new PlanetGravity(null, gFactor));
        else
            addForce(new PlanetGravity(entity, gFactor));
    }

    /**
     * Crée un ressort et l'applique entre 'entity' et 'target'
     * @param source entité source à laquelle appliquer la force
     * @param target deuxième entité à laquelle appliquer la force. si null, on applique une force globale
     */
    public void makeSpring(Entity source, Entity target) {

        if (source == null)
            return;

        String ret2 = JOptionPane.showInputDialog(Collinsa.INSTANCE.getMainFrame(), "Indiquez la constante de raideur", "Ressort", JOptionPane.OK_CANCEL_OPTION);
        String ret3 = JOptionPane.showInputDialog(Collinsa.INSTANCE.getMainFrame(), "Indiquez la longueur à vide", "Ressort", JOptionPane.OK_CANCEL_OPTION);

        Double springCst, restLength;

        try {

            springCst = Double.parseDouble(ret2);
            restLength = Double.parseDouble(ret3);
        } catch (Exception e) {

            springCst = null;
            restLength = null;
        }

        if (springCst == null || restLength == null)
            return;

        if (target == null)
            addGlobalForce(new Spring(source, null, springCst, restLength));
        else
            addForce(new Spring(source, target, springCst, restLength));
    }

    /**
     * Ajoute une force à la simulation
     * @param force force à ajouter
     */
    public void addForce(Force force) {

        Collinsa.INSTANCE.getPhysics().forces.add(force);
    }

    /**
     * Ajoute une force globale (qui s'applique à toutes les entités) à la simulation
     * @param force force globale à ajouter
     */
    public void addGlobalForce(Force force) {

        int reponse = JOptionPane.showConfirmDialog(Collinsa.INSTANCE.getMainFrame(), "Êtes-vous sûr de vouloir créer une force globale ?", "Création de force globale", JOptionPane.YES_NO_OPTION);

        if (reponse == JOptionPane.YES_OPTION)
            Collinsa.INSTANCE.getPhysics().globalForces.add(force);
    }

    /**
     * On remet à zero toutes les variables (retour à l'état initial)
     */
    public void reset() {

        this.choix = Integer.MIN_VALUE;
        this.first = null;
        this.second = null;
    }
}

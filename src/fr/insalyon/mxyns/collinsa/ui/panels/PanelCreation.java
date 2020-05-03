package fr.insalyon.mxyns.collinsa.ui.panels;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import java.awt.Color;

/**
 * Classe abstraite qui permet la réalisation d'un Panel qui sera modifié en fonction du type d'entité choisi
 */
public abstract class PanelCreation extends JPanel {

    /**
     * Listener utilisé pour informer la Frame que des modifications ont été faites sur les composants du PanelCreation
     */
    final protected ChangeListener listener;

    /**
     * Couleur du panel
     */
    Color my = new Color(93,155,155);

    public PanelCreation(ChangeListener listeningFrame) {

        setSize(300, 200);
        setLocation(200,50);
        setBackground(my);

        this.listener = listeningFrame;
    }

    /**
     * Méthode appelée lors de la création d'une entité. Différente pour chaque panel car chaque panel crée un type d'entité différent
     * @return entité créée
     */
    public abstract Entity creerEntite();

    /**
     * Charge les paramètres d'une entité et les insère les bonnes valeurs dans les composants correspondant
     * @param entity entité à lire
     */
    public abstract void loadEntity(Entity entity);

    /**
     * Applique les valeurs contenues dans les composantsà une entité
     * @param entity entité à modifier
     */
    public abstract void editEntity(Entity entity);
}
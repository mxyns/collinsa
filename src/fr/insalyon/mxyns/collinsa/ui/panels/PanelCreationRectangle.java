package fr.insalyon.mxyns.collinsa.ui.panels;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;

/**
 * panel qui permet de créer un rectangle
 */
public class PanelCreationRectangle extends PanelCreation {

    JSpinner largeur, hauteur;

    /**
     * permet de modifier la hauteur et la largeur
     */
    public PanelCreationRectangle(ChangeListener frame) {

        super(frame);
        setBorder(BorderFactory.createTitledBorder("Rectangle"));

        hauteur = new JSpinner();
        SpinnerNumberModel hauteurModel = new SpinnerNumberModel(10, .1, Float.MAX_VALUE, .1);
        hauteur.setModel(hauteurModel);
        hauteur.setBorder(BorderFactory.createTitledBorder("Hauteur"));
        hauteur.setSize(150, 40);
        hauteur.setPreferredSize(new Dimension(100, 40));
        hauteur.addChangeListener(listener);
        add(hauteur);

        largeur = new JSpinner();
        SpinnerNumberModel largeurModel = new SpinnerNumberModel(10, .1, Float.MAX_VALUE, .1);
        largeur.setModel(largeurModel);
        largeur.setBorder(BorderFactory.createTitledBorder("Largeur"));
        largeur.setSize(150, 40);
        largeur.setPreferredSize(new Dimension(100, 40));
        largeur.setBounds(100,200,20,20);
        largeur.addChangeListener(listener);
        add(largeur);
    }

    /**
     * crée un rectangle
     * @return un nouveau rectangle
     */
    public Entity creerEntite() {

        double l= (double) largeur.getValue();
        double h= (double) hauteur.getValue();

        return new Rect(0, 0 , (float) l, (float) h);
    }

    @Override
    public void loadEntity(Entity entity) {

        if (entity instanceof Rect) {

            largeur.setValue((double) ((Rect)entity).getSize().x);
            hauteur.setValue((double) ((Rect)entity).getSize().y);
        }
    }

    @Override
    public void editEntity(Entity entity) {

        if (entity instanceof Rect)
            ((Rect) entity).getSize().set( (float)(double) largeur.getValue(), (float)(double) hauteur.getValue());
    }
}

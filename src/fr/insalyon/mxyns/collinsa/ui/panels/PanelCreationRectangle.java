package fr.insalyon.mxyns.collinsa.ui.panels;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;

import javax.swing.*;

public class PanelCreationRectangle extends PanelCreation{

    JSpinner largeur, hauteur;

    public PanelCreationRectangle() {

        super();
        hauteur = new JSpinner();
        SpinnerNumberModel hauteurModel = new SpinnerNumberModel(10, 1, 100, .1);
        hauteur.setModel(hauteurModel);
        hauteur.setBorder(BorderFactory.createTitledBorder("Hauteur"));
        hauteur.setBounds(100,200,20,20);
        add(hauteur);

        largeur = new JSpinner();
        SpinnerNumberModel largeurModel = new SpinnerNumberModel(10, 1, 100, .1);
        largeur.setModel(largeurModel);
        largeur.setBorder(BorderFactory.createTitledBorder("Largeur"));
        largeur.setBounds(100,200,20,20);
        add(largeur);


        setBorder(BorderFactory.createTitledBorder("Rectangle"));
    }

    public Entity creerEntite() {

        double l= (double) largeur.getValue();
        double h= (double) hauteur.getValue();

        return new Rect(0, 0 , (float) l, (float) h);


    }
}

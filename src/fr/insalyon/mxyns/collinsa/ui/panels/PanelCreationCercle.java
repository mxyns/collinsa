package fr.insalyon.mxyns.collinsa.ui.panels;

import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

import javax.swing.*;

public class PanelCreationCercle extends PanelCreation {

    // permet de choisir le rayon du cercle
    JSpinner rayon = new JSpinner();

    public PanelCreationCercle() {

        super();
        SpinnerNumberModel rayonModel = new SpinnerNumberModel(10, .1, 100, .1);
        rayon.setModel(rayonModel);
        rayon.setBounds(100,200,150,20);
        rayon.setBorder(BorderFactory.createTitledBorder("Rayon"));
        add(rayon);

        setBorder(BorderFactory.createTitledBorder("Cercle"));
    }

    //On créer un cercle
    public Entity creerEntite() {

        double radius = (double) rayon.getValue();

        return new Circle(0, 0, (float) radius);
    }
}

package fr.insalyon.mxyns.collinsa.ui.panels;

import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

import javax.swing.*;

/**
 * panel qui permet la création d'un cercle
 */
public class PanelCreationCercle extends PanelCreation {

    // permet de choisir le rayon du cercle
    JSpinner rayon = new JSpinner();

    /**
     * crée le panel où l'on peut modifier le rayon
     */
    public PanelCreationCercle() {

        super();
        SpinnerNumberModel rayonModel = new SpinnerNumberModel(10, .1, 500, .1);
        rayon.setModel(rayonModel);
        rayon.setBounds(100,200,150,20);
        rayon.setBorder(BorderFactory.createTitledBorder("Rayon"));
        add(rayon);

        setBorder(BorderFactory.createTitledBorder("Cercle"));
    }

    /**
     * On crée un cercle
     * @return un nouveau cercle dans le monde
     */
    public Entity creerEntite() {

        double radius = (double) rayon.getValue();

        return new Circle(0, 0, (float) radius);
    }
}

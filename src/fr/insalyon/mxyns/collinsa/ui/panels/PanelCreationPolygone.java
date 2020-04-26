package fr.insalyon.mxyns.collinsa.ui.panels;

import fr.insalyon.mxyns.collinsa.physics.entities.ConvexPoly;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class PanelCreationPolygone extends PanelCreation {

    JSlider cotes;
    JSpinner rayon;

    public PanelCreationPolygone() {

        super();
        cotes = new JSlider(JSlider.HORIZONTAL, 3, 40, 10);
        cotes.setBorder(BorderFactory.createTitledBorder("Nombre côtés : " + cotes.getValue()));
        cotes.setBounds(0, 350, 400, 70);
        cotes.addChangeListener(e -> cotes.setBorder(BorderFactory.createTitledBorder("Nombre côtés  : " + cotes.getValue())));
        cotes.setMajorTickSpacing(cotes.getMaximum() - cotes.getMinimum());
        cotes.setPaintTicks(true);
        cotes.setPaintLabels(true);
        add(cotes);

        rayon = new JSpinner();
        SpinnerNumberModel rayonModel = new SpinnerNumberModel(10, 1, 100, .1);
        rayon.setModel(rayonModel);
        rayon.setBorder(BorderFactory.createTitledBorder("Rayon"));
        rayon.setBounds(100,200,20,20);
        add(rayon);

        setBorder(BorderFactory.createTitledBorder("Polygone"));
    }


    // on crée un polygone
    public Entity creerEntite() {

        int c = cotes.getValue();
        double r = (double) rayon.getValue();

        return new ConvexPoly(Vec2f.zero(), c , (float) r );
    }
}

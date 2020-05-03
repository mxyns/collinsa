package fr.insalyon.mxyns.collinsa.ui.panels;

import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

import javax.swing.BorderFactory;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;

/**
 * panel qui permet la création d'un cercle
 */
public class PanelCreationCercle extends PanelCreation {

    // permet de choisir le rayon du cercle
    JSpinner rayon = new JSpinner();

    /**
     * crée le panel où l'on peut modifier le rayon
     */
    public PanelCreationCercle(ChangeListener frame) {

        super(frame);
        SpinnerNumberModel rayonModel = new SpinnerNumberModel(10, .1, Float.MAX_VALUE, .1);
        rayon.setModel(rayonModel);
        rayon.setSize(150, 40);
        rayon.setPreferredSize(new Dimension(100, 40));
        rayon.setBorder(BorderFactory.createTitledBorder("Rayon"));
        rayon.addChangeListener(listener);
        add(rayon);

        setBorder(BorderFactory.createTitledBorder("Cercle"));
    }

    /**
     * On crée un cercle
     * @return un nouveau cercle dans le monde
     */
    @Override
    public Entity creerEntite() {

        return new Circle(0, 0, (float)(double) rayon.getValue());
    }

    @Override
    public void editEntity(Entity entity) {

        if (entity instanceof Circle)
            ((Circle)entity).setR( (float)(double) rayon.getValue());
    }

    @Override
    public void loadEntity(Entity entity) {

        if (entity instanceof Circle)
            rayon.setValue( (double) ((Circle) entity).getR());

    }
}

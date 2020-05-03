package fr.insalyon.mxyns.collinsa.ui.panels;

import fr.insalyon.mxyns.collinsa.physics.entities.ConvexPoly;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.entities.Polygon;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;

/**
 * panel qui permet la création d'un polygone
 */
public class PanelCreationPolygone extends PanelCreation {

    JSlider cotes;
    JSpinner rayon;

    /**
     * permet de modifier le nombre de côtés et le rayon
     */
    public PanelCreationPolygone(ChangeListener frame) {

        super(frame);

        rayon = new JSpinner();
        SpinnerNumberModel rayonModel = new SpinnerNumberModel(10, .1, Float.MAX_VALUE, .1);
        rayon.setModel(rayonModel);
        rayon.setBorder(BorderFactory.createTitledBorder("Rayon"));
        rayon.setSize(150, 40);
        rayon.setPreferredSize(new Dimension(100, 40));
        rayon.addChangeListener(listener);
        add(rayon);

        cotes = new JSlider(JSlider.HORIZONTAL, 3, 40, 10);
        cotes.setBorder(BorderFactory.createTitledBorder("Nombre côtés : " + cotes.getValue()));
        cotes.addChangeListener(e -> cotes.setBorder(BorderFactory.createTitledBorder("Nombre côtés  : " + cotes.getValue())));
        cotes.setMajorTickSpacing(cotes.getMaximum() - cotes.getMinimum());
        cotes.setPaintTicks(true);
        cotes.setPaintLabels(true);
        cotes.addChangeListener(listener);
        add(cotes);

        setBorder(BorderFactory.createTitledBorder("Polygone"));
    }


    /**
     * crée un polygone
     * @return un nouveau polygone
     */
    @Override
    public Entity creerEntite() {

        int c = cotes.getValue();

        return new ConvexPoly(Vec2f.zero(), c , (float)(double) rayon.getValue() );
    }

    @Override
    public void editEntity(Entity entity) {

        if (entity instanceof ConvexPoly) {

            int n = cotes.getValue();
            float r = (float)(double) rayon.getValue();

            // On laisse le constructeur de ConvexPoly nous générer un polygone correctement
            ConvexPoly temp = new ConvexPoly(Vec2f.zero(), n , r);

            // Puis on vole les local_vertices générés et on les donne à l'entité à modifier
            ((ConvexPoly)entity).setLocalVertices(temp.getLocalVertices());
        }
    }

    @Override
    public void loadEntity(Entity entity) {

        if (entity instanceof Polygon) {

            cotes.setValue(((Polygon) entity).getVertices().length);
            rayon.setValue((double) ((Polygon) entity).getLocalVertices()[0].mag());
        }
    }
}

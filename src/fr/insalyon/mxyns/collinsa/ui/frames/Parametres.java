package fr.insalyon.mxyns.collinsa.ui.frames;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.forces.Force;
import fr.insalyon.mxyns.collinsa.physics.forces.PlanetGravity;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Frame qui contient les paramètres que l'on peut modifier
 */
public class Parametres extends JFrame {

    PlanetGravity gravite;

    // Ouverture d'une nouvelle fenêtre lorsqu'on clique sur le bouton paramètres qui permet de désactiver la gravité,
    // d'afficher les bounding boxes et les chunks'bounds, de changer la couleur de l'arrière plan ou encore de changer l'échelle des temps...

    /**
     * Création d'un Panel paramètre
     */
    public Parametres () {

        super("Paramètres");
        setSize(800, 555);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        Renderer renderer = Collinsa.INSTANCE.getRenderer();
        Physics physics = Collinsa.INSTANCE.getPhysics();

        // hauteur, largeur du monde
        SpinnerNumberModel largeurModel = new SpinnerNumberModel(physics.getWidth(), .1, Float.MAX_VALUE, .1);
        JSpinner largeur = new JSpinner(largeurModel);
        largeur.setModel(largeurModel);
        largeur.setBounds(500,15,200,50);
        largeur.setBorder(BorderFactory.createTitledBorder("Largeur du monde"));
        add(largeur);

        SpinnerNumberModel hauteurModel = new SpinnerNumberModel(physics.getHeight(), .1, Float.MAX_VALUE, .1);
        JSpinner hauteur = new JSpinner(hauteurModel);
        hauteur.setBounds(500,85,200,50);
        hauteur.setModel(hauteurModel);
        hauteur.setBorder(BorderFactory.createTitledBorder("Hauteur du monde"));
        add(hauteur);

        // nombre de chunks en horizontal et vertical
        SpinnerNumberModel modelNbChunksH = new SpinnerNumberModel((int)physics.getChunkCount().x, 1, Integer.MAX_VALUE, 1);
        JSpinner nbChunksH = new JSpinner(modelNbChunksH);
        nbChunksH.setModel(modelNbChunksH);
        nbChunksH.setBounds(500,155,200,50);
        nbChunksH.setBorder(BorderFactory.createTitledBorder("Nombre de chunks (Horizontal)"));
        add(nbChunksH);

        SpinnerNumberModel modelNbChunksV = new SpinnerNumberModel((int)physics.getChunkCount().y, 1, Integer.MAX_VALUE, 1);
        JSpinner nbChunksV = new JSpinner(modelNbChunksV);
        nbChunksV.setModel(modelNbChunksV);
        nbChunksV.setBounds(500,225,200,50);
        nbChunksV.setBorder(BorderFactory.createTitledBorder("Nombre de chunks (Vertical)"));
        add(nbChunksV);

        //on rajoute un bouton pour activer les paramètres ci-dessus
        JButton appliquer = new JButton("Appliquer");
        appliquer.setBounds(550,300,100,30);
        appliquer.addActionListener(e -> {

            // Si on ne choisit pas OUI dans le Dialog.
            if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, "<html>Modifier la taille du monde ou le nombre de chunks pendant la simulation est une opération risquée qui peut mener à un crash du logiciel. <br> Ces paramètres sont les seuls à ne pas être appliqués en temps-réel. <br> Êtes-vous sûr de vouloir continuer ?</html>", "Opération dangereuse", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE))
                return;

            try {
                Collinsa.INSTANCE.pause(1000);

                physics.setChunkCount(new Vec2f((float) (int) nbChunksH.getValue(), (float) (int) nbChunksV.getValue()));
                physics.resize(new Vec2d((double) largeur.getValue(), (double) hauteur.getValue()).toFloat());

            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });
        add(appliquer);

        //on peut afficher différents axes
        JCheckBox axes = new JCheckBox("Afficher les axes", renderer.doesRenderCoordinateSystem());
        axes.setBounds(0, 45, 200, 15);
        axes.addActionListener(e -> renderer.setRenderCoordinateSystem(axes.isSelected()));
        add(axes);

        JCheckBox chunks = new JCheckBox("Afficher les chunks", renderer.doesRenderChunksBounds());
        chunks.setBounds(0, 65, 200, 15);
        chunks.addActionListener(e -> renderer.setRenderChunksBounds(chunks.isSelected()));
        add(chunks);

        JCheckBox world = new JCheckBox("Afficher les limites du monde", renderer.doesRenderWorldBounds());
        world.setBounds(0, 85, 200, 15);
        world.addActionListener(e -> renderer.setRenderWorldBounds(world.isSelected()));
        add(world);

        //On peut modifier l'intensité de la gravité
        JSlider graviteSlider = new JSlider(JSlider.HORIZONTAL, -2000, 2000, gravite == null ? 100 : (int) (100 * gravite.gFactor));
        graviteSlider.setBorder(BorderFactory.createTitledBorder("Intensité de la gravité : " + graviteSlider.getValue() / 100f + "g"));
        graviteSlider.setBounds(200, 2, 200, 40);
        graviteSlider.addChangeListener(e -> {

            graviteSlider.setBorder(BorderFactory.createTitledBorder("Intensité de la gravité : " + graviteSlider.getValue() / 100f + "g"));
            if (gravite != null)
                gravite.gFactor = graviteSlider.getValue() / 100f;
        });
        add(graviteSlider);

        gravite = null;
        // On récupère le dernier PlanetGravity global et on considère que c'est le seul présent

        //FIXME : refaire toute l'interface avec le GUI Designer
        ArrayList<Force> globalForces = physics.getTickMachine().current().globalForces;
        for (Force force : globalForces)
            if (force instanceof PlanetGravity)
                gravite = (PlanetGravity) force;

        JCheckBox gravite = new JCheckBox("Gravité", this.gravite != null);
        gravite.setBounds(0, 5, 200, 15);
        gravite.addActionListener(e -> {
            if (this.gravite != null && !gravite.isSelected()) {
                globalForces.remove(this.gravite);
                this.gravite = null;
            } else {
                globalForces.add(this.gravite = new PlanetGravity(1));
                graviteSlider.setValue(100);
            }

            graviteSlider.setEnabled(gravite.isSelected());
        });
        add(gravite);

        JCheckBox boxes = new JCheckBox("Afficher les boîtes de collision", renderer.doesRenderEntitiesAABB());
        boxes.setBounds(0, 25, 200, 15);
        boxes.addActionListener(e -> renderer.setRenderEntitiesAABB(boxes.isSelected()));
        add(boxes);

        JCheckBox realtime = new JCheckBox("Temps réel", physics.isRealtime());
        realtime.setBounds(0, 105, 200, 15);
        realtime.addActionListener(e -> physics.setRealtime(realtime.isSelected()));
        add(realtime);

        JCheckBox forces = new JCheckBox("Afficher les forces", renderer.doesRenderForces());
        forces.setBounds(0, 125, 200, 15);
        forces.addActionListener(e -> renderer.setRenderForces(forces.isSelected()));
        add(forces);

        JSlider forcesSlider = new JSlider(JSlider.HORIZONTAL, 1, 50000, (int) (renderer.getForceScale() * 5000));
        forcesSlider.setBorder(BorderFactory.createTitledBorder("Echelle des forces (px/N) : " + forcesSlider.getValue() / 5000f ));
        forcesSlider.setBounds(200, 108, 200, 40);
        forcesSlider.addChangeListener(e -> {
            renderer.setForceScale(forcesSlider.getValue() / 5000f);

            forcesSlider.setBorder(BorderFactory.createTitledBorder("Echelle des forces (px/N) : " + forcesSlider.getValue() / 5000f ));
        });
        add(forcesSlider);

        JCheckBox wireFrame = new JCheckBox("Activer le mode wireframe", renderer.isDisplayModeWireframe());
        wireFrame.setBounds(0, 145, 200, 15);
        wireFrame.addActionListener(e ->renderer.setWireframeDisplay(wireFrame.isSelected()));
        add(wireFrame);


        JSlider scale = new JSlider(JSlider.HORIZONTAL, 1, 200, (int) (renderer.getRenderScale() * 100));
        scale.setBorder(BorderFactory.createTitledBorder("Echelle : " + scale.getValue() / 100f));
        scale.setBounds(0, 165, 400, 60);
        scale.addChangeListener(e -> {
            renderer.setRenderScale(scale.getValue() / 100f);
            scale.setBorder(BorderFactory.createTitledBorder("Echelle : " + scale.getValue() / 100f));
        });
        scale.setMajorTickSpacing(scale.getMaximum() - scale.getMinimum());
        scale.setPaintTicks(false);
        scale.setPaintLabels(true);
        add(scale);

        JSlider dt = new JSlider(JSlider.HORIZONTAL, 1, 200,  physics.getFixedDeltaTime());
        dt.setBorder(BorderFactory.createTitledBorder("Pas de temps (temps réel = false) : "+ dt.getValue()));
        dt.setBounds(0, 235, 400, 60);
        dt.addChangeListener(e -> {
            dt.setBorder(BorderFactory.createTitledBorder("Pas de temps (temps réel = false) : " + dt.getValue()));
            physics.setFixedDeltaTime(dt.getValue());
        });
        dt.setMajorTickSpacing(dt.getMaximum() - dt.getMinimum());
        dt.setPaintTicks(false);
        dt.setPaintLabels(true);
        add(dt);

        //on peut choisir la fréquence à laquelle on met à jour la simulation
        JSlider fpsp = new JSlider(JSlider.HORIZONTAL, 1, 240, physics.getProcessingThread().getRefreshRate());
        fpsp.setBorder(BorderFactory.createTitledBorder("Nombre de fois qu'on met à jour la simulation par seconde : "+ fpsp.getValue()));
        fpsp.setBounds(0, 310, 400, 60);
        fpsp.addChangeListener(e -> {
            fpsp.setBorder(BorderFactory.createTitledBorder("Nombre de fois qu'on met à jour la simulation par seconde : "+ fpsp.getValue()));
            Collinsa.INSTANCE.setTickrate(fpsp.getValue());
        });
        fpsp.setMajorTickSpacing(fpsp.getMaximum() - fpsp.getMinimum());
        fpsp.setPaintTicks(false);
        fpsp.setPaintLabels(true);
        add(fpsp);

        JSlider fpsr = new JSlider(JSlider.HORIZONTAL, 1, 240, renderer.getRenderingThread().getFramerate());
        fpsr.setBorder(BorderFactory.createTitledBorder("Nombre d'images créées par seconde : "+ fpsr.getValue()));
        fpsr.setBounds(0, 380, 400, 60);
        fpsr.addChangeListener(e -> {
            fpsr.setBorder(BorderFactory.createTitledBorder("Nombre d'images créées par seconde : "+ fpsr.getValue()));
            Collinsa.INSTANCE.setFramerate(fpsr.getValue());
        });
        fpsr.setMajorTickSpacing(fpsr.getMaximum() - fpsr.getMinimum());
        fpsr.setPaintTicks(false);
        fpsr.setPaintLabels(true);
        add(fpsr);

        JSlider fpsd = new JSlider(JSlider.HORIZONTAL, 1, 240, Collinsa.INSTANCE.getMainFrame().getSandboxPanel().getRefreshingThread().getRefreshRate());
        fpsd.setBorder(BorderFactory.createTitledBorder("Nombre d'images affichées par seconde : "+ fpsd.getValue()));
        fpsd.setBounds(0, 450, 400, 60);
        fpsd.addChangeListener(e -> {
            fpsd.setBorder(BorderFactory.createTitledBorder("Nombre d'images affichées par seconde : "+ fpsd.getValue()));
            Collinsa.INSTANCE.getMainFrame().getSandboxPanel().getRefreshingThread().setRefreshRate(fpsd.getValue());
        });
        fpsd.setMajorTickSpacing(fpsd.getMaximum() - fpsd.getMinimum());
        fpsd.setPaintTicks(false);
        fpsd.setPaintLabels(true);
        add(fpsd);

        setVisible(true);

        // On le fait après le setVisible puisqu'avant les Insets ne sont pas encore calculés et valent tous 0
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setBounds(450, 10, 10, getHeight() - getInsets().top - getInsets().bottom - 20);
        add(sep);

    }
}
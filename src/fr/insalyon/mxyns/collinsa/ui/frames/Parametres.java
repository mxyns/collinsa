package fr.insalyon.mxyns.collinsa.ui.frames;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2d;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import javax.swing.*;

public class Parametres extends JFrame {
    // Ouverture d'une nouvelle fenêtre lorsqu'on clique sur le bouton paramètres qui permet de désactiver la gravité,
    // d'afficher les bounding boxes et les chunks'bounds, de changer la couleur de l'arrière plan ou encore de changer l'échelle des temps...

    // TODO: ajouter le mode de rendu wireframe (checkbox -> Collinsa.INSTANCE.getRenderer().setWireframeDisplay( true / false ))
    public Parametres (int width, int height) {

        super("Paramètres");
        setSize(width, height);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // hauteur, largeur du monde
        // nombre de chunks en horizontal et vertical

        SpinnerNumberModel largeurModel = new SpinnerNumberModel(Collinsa.INSTANCE.getPhysics().getWidth(), .1, 1440, .1);
        JSpinner largeur = new JSpinner(largeurModel);
        largeur.setModel(largeurModel);
        largeur.setBounds(500,15,200,50);
        largeur.setBorder(BorderFactory.createTitledBorder("Largeur du monde"));
        add(largeur);

        SpinnerNumberModel hauteurModel = new SpinnerNumberModel(Collinsa.INSTANCE.getPhysics().getHeight(), .1, 1440, .1);
        JSpinner hauteur = new JSpinner(hauteurModel);
        hauteur.setBounds(500,85,200,50);
        hauteur.setModel(hauteurModel);
        hauteur.setBorder(BorderFactory.createTitledBorder("Hauteur du monde"));
        add(hauteur);

        SpinnerNumberModel modelNbChunksH = new SpinnerNumberModel((int)Collinsa.INSTANCE.getPhysics().getChunkCount().x, 1, 50, 1);
        JSpinner nbChunksH = new JSpinner(modelNbChunksH);
        nbChunksH.setModel(modelNbChunksH);
        nbChunksH.setBounds(500,155,200,50);
        nbChunksH.setBorder(BorderFactory.createTitledBorder("Nombre de chunks (Horizontal)"));
        add(nbChunksH);

        SpinnerNumberModel modelNbChunksV = new SpinnerNumberModel((int)Collinsa.INSTANCE.getPhysics().getChunkCount().y, 1, 50, 1);
        JSpinner nbChunksV = new JSpinner(modelNbChunksV);
        nbChunksV.setModel(modelNbChunksV);
        nbChunksV.setBounds(500,225,200,50);
        nbChunksV.setBorder(BorderFactory.createTitledBorder("Nombre de chunks (Vertical)"));
        add(nbChunksV);

        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setBounds(450, 15, 10, 500);
        add(sep);

        //on rajoute un bouton pour activer les paramètres ci-dessus
        JButton appliquer = new JButton("Appliquer");
        appliquer.setBounds(600,300,100,100);
        appliquer.addActionListener(e -> {

            try {
                Collinsa.INSTANCE.pause(300);

                Collinsa.INSTANCE.getPhysics().setChunkCount(new Vec2f((float) (int) nbChunksH.getValue(), (float) (int) nbChunksV.getValue()));
                Collinsa.INSTANCE.getPhysics().resize(new Vec2d((double) largeur.getValue(), (double) hauteur.getValue()).toFloat());

            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });
        add(appliquer);

        JCheckBox axes = new JCheckBox("Afficher les axes", Collinsa.INSTANCE.getRenderer().doesRenderCoordinateSystem());
        axes.setBounds(0, 45, 200, 15);
        axes.addActionListener(e -> Collinsa.INSTANCE.getRenderer().setRenderCoordinateSystem(axes.isSelected()));
        add(axes);

        JCheckBox chunks = new JCheckBox("Afficher les chunks", Collinsa.INSTANCE.getRenderer().doesRenderChunksBounds());
        chunks.setBounds(0, 65, 200, 15);
        chunks.addActionListener(e -> Collinsa.INSTANCE.getRenderer().setRenderChunksBounds(chunks.isSelected()));
        add(chunks);

        JCheckBox world = new JCheckBox("Afficher les limites du monde", Collinsa.INSTANCE.getRenderer().doesRenderWorldBounds());
        world.setBounds(0, 85, 200, 15);
        world.addActionListener(e -> Collinsa.INSTANCE.getRenderer().setRenderWorldBounds(world.isSelected()));
        add(world);

        JSlider graviteSlider = new JSlider(JSlider.HORIZONTAL, 1, 2000, 100);
        graviteSlider.setBorder(BorderFactory.createTitledBorder("Intensité de la gravité : " + graviteSlider.getValue() / 100f + "g"));
        graviteSlider.setBounds(200, 2, 190, 40);
        graviteSlider.addChangeListener(e -> {

            graviteSlider.setBorder(BorderFactory.createTitledBorder("Intensité de la gravité : " + graviteSlider.getValue() / 100f + "g"));
        });
        add(graviteSlider);

        JCheckBox gravite = new JCheckBox("Gravité", true);
        gravite.setBounds(0, 5, 200, 15);
        gravite.addActionListener(e -> graviteSlider.setEnabled(gravite.isSelected()));
        add(gravite);

        JCheckBox boxes = new JCheckBox("Afficher les boîtes de collision", Collinsa.INSTANCE.getRenderer().doesRenderEntitiesAABB());
        boxes.setBounds(0, 25, 200, 15);
        boxes.addActionListener(e -> Collinsa.INSTANCE.getRenderer().setRenderEntitiesAABB(boxes.isSelected()));
        add(boxes);

        JCheckBox realtime = new JCheckBox("Temps réel", Collinsa.INSTANCE.getPhysics().isRealtime());
        realtime.setBounds(0, 105, 200, 15);
        realtime.addActionListener(e -> Collinsa.INSTANCE.getPhysics().setRealtime(realtime.isSelected()));
        add(realtime);


        JSlider scale = new JSlider(JSlider.HORIZONTAL, 1, 200, (int) (Collinsa.INSTANCE.getRenderer().getRenderScale() * 100));
        scale.setBorder(BorderFactory.createTitledBorder("Echelle : " + scale.getValue() / 100f));
        scale.setBounds(0, 125, 400, 70);
        scale.addChangeListener(e -> {
            Collinsa.INSTANCE.getRenderer().setRenderScale(scale.getValue() / 100f);
            scale.setBorder(BorderFactory.createTitledBorder("Echelle : " + scale.getValue() / 100f));
        });
        scale.setMajorTickSpacing(scale.getMaximum() - scale.getMinimum());
        scale.setPaintTicks(false);
        scale.setPaintLabels(true);
        add(scale);

        JSlider fpsp = new JSlider(JSlider.HORIZONTAL, 1, 240, Collinsa.INSTANCE.getPhysics().getProcessingThread().getRefreshRate());
        fpsp.setBorder(BorderFactory.createTitledBorder("Nombre de fois qu'on met à jour la simulation par seconde : "+ fpsp.getValue()));
        fpsp.setBounds(0, 275, 400, 70);
        fpsp.addChangeListener(e -> {
            fpsp.setBorder(BorderFactory.createTitledBorder("Nombre de fois qu'on met à jour la simulation par seconde : "+ fpsp.getValue()));
            Collinsa.INSTANCE.getPhysics().getProcessingThread().setRefreshRate(fpsp.getValue());
        });
        fpsp.setMajorTickSpacing(fpsp.getMaximum() - fpsp.getMinimum());
        fpsp.setPaintTicks(false);
        fpsp.setPaintLabels(true);
        add(fpsp);

        JSlider dt = new JSlider(JSlider.HORIZONTAL, 1, 200,  Collinsa.INSTANCE.getPhysics().getFixedDeltaTime());
        dt.setBorder(BorderFactory.createTitledBorder("Pas de temps (temps réel = false) : "+ dt.getValue()));
        dt.setBounds(0, 200, 400, 70);
        dt.addChangeListener(e -> {
            dt.setBorder(BorderFactory.createTitledBorder("Pas de temps (temps réel = false) : " + dt.getValue()));
            Collinsa.INSTANCE.getPhysics().setFixedDeltaTime(dt.getValue());
        });
        dt.setMajorTickSpacing(dt.getMaximum() - dt.getMinimum());
        dt.setPaintTicks(false);
        dt.setPaintLabels(true);
        add(dt);

        JSlider fpsr = new JSlider(JSlider.HORIZONTAL, 1, 240, Collinsa.INSTANCE.getRenderer().getRenderingThread().getFramerate());
        fpsr.setBorder(BorderFactory.createTitledBorder("Nombre d'images créées par seconde : "+ fpsr.getValue()));
        fpsr.setBounds(0, 350, 400, 70);
        fpsr.addChangeListener(e -> {
            fpsr.setBorder(BorderFactory.createTitledBorder("Nombre d'images créées par seconde : "+ fpsr.getValue()));
            Collinsa.INSTANCE.getRenderer().getRenderingThread().setFramerate(fpsr.getValue());
            Collinsa.INSTANCE.getMainFrame().getSandboxPanel().getRefreshingThread().setRefreshRate(fpsr.getValue());
        });
        fpsr.setMajorTickSpacing(fpsr.getMaximum() - fpsr.getMinimum());
        fpsr.setPaintTicks(false);
        fpsr.setPaintLabels(true);
        add(fpsr);

        setVisible(true);
    }
}
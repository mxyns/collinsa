package fr.insalyon.mxyns.collinsa.ui.frames;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Material;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision.CollisionType;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.ui.panels.PanelCreation;
import fr.insalyon.mxyns.collinsa.ui.panels.PanelCreationCercle;
import fr.insalyon.mxyns.collinsa.ui.panels.PanelCreationPolygone;
import fr.insalyon.mxyns.collinsa.ui.panels.PanelCreationRectangle;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;

/**
 * Fenêtre qui permet de créer de nouveaux objets
 */
public class Creation extends JFrame implements ActionListener, ChangeListener {

    /**
     * Les différents éléments composant la frame Paramètres
     *    - dir <=> direction
     *    - mag <=> norme vecteur
     *    - vel <=> vitesse / vélocité
     *    - acc <=> accélération
     *    - ang <=> angulaire
     */
    private JComboBox forme;
    private JComboBox materiau;
    private JComboBox collision;
    private PanelCreation panelCreation;
    private JSpinner x, y;
    private JSpinner dirVel, magVel, dirAcc, magAcc;
    private JSlider rot, angVel, angAcc;
    private JCheckBox checkBoxMasse, checkBoxInertie;
    private JSpinner choixMasse, choixInertie;

    private Entity aModifier = null;
    private Entity preview = null;

    // On crée un tableau comportant les différents types d'entité qu'on peut créer avec la Frame
    Object[] formesDispo = new Object[] { "Cercle", "Rectangle", "Polygone" };
    JButton ok = new JButton("OK");
    JPanel panel = buildContentPanel();

    /**
     * Constructeur qui crée la fenêtre création
     * @param width largeur de la fenêtre
     * @param height hauteur de la fenêtre
     */
    public Creation(int width, int height) {

        super("Création");
        setSize(width, height);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // on fait en sorte que la fenêtre puisse se fermer sans tout fermer
        setContentPane(panel);
        setVisible(true);
        setResizable(false);

        updatePreview(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {

                clearPreview();
            }
        });
    }

    /**
     * Nouveau constructeur qui permet de récupérer les paramètres de l'objet lorsque l'on veut le modifier
     * @param width la largeur de la fenêtre
     * @param height la hauteur de la fenêtre
     * @param aModifier l'entité à modifier
     */
    public Creation(int width, int height, Entity aModifier) {

        this(width, height);

        this.aModifier = aModifier;

        x.setValue((double)aModifier.getPos().x);
        x.setEnabled(false);
        x.setToolTipText("Veuillez utiliser l'outil déplacement pour changer la position de l'entité");

        y.setValue((double)aModifier.getPos().y);
        y.setEnabled(false);
        y.setToolTipText("Veuillez utiliser l'outil déplacement pour changer la position de l'entité");

        dirVel.setValue((Math.toDegrees(aModifier.getVel().angleWith(new Vec2f(1, 0))) + 360) % 360);
        magVel.setValue((double)aModifier.getVel().mag());
        dirAcc.setValue((Math.toDegrees(aModifier.getAcc().angleWith(new Vec2f(1, 0))) + 360) % 360);
        magAcc.setValue((double)aModifier.getAcc().mag());
        angVel.setValue((int)Math.toDegrees(aModifier.getAngVel()));
        angAcc.setValue((int)Math.toDegrees(aModifier.getAngAcc()));
        rot.setValue((int)Math.toDegrees( aModifier.getRot()));

        forme.setEnabled(false);
        forme.setToolTipText("<html>Impossible de changer la forme de l'objet.<br> Veuillez supprimer celui-ci et en recréer un nouveau.</html>");
        forme.setSelectedIndex(aModifier.cardinal());

        // Si le matériau n'est pas un matériau par défaut (connu de Material) et qu'il a été modifié, on ajoute temporairement une option pour celui-ci afin de ne pas le modifier
        String materialName = Material.getMaterialName(aModifier.getMaterial());
        if (((DefaultComboBoxModel) materiau.getModel()).getIndexOf(materialName) == -1)
            materiau.addItem(materialName);

        materiau.setSelectedItem(Material.getMaterialName(aModifier.getMaterial()));

        collision.setSelectedItem(aModifier.getCollisionType().name());
        panelCreation.loadEntity(aModifier);

        setTitle("Modification d'un " + forme.getSelectedItem().toString().toLowerCase());
    }

    /**
     * Génère un JPanel composé des éléments nécessaires à la création d'un objet
     * @return panel
     */
    private JPanel buildContentPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(null);

        forme = new JComboBox(formesDispo);
        materiau = new JComboBox();
        collision = new JComboBox();

        // JSpinner qui renseigne sur la position de l'objet
        SpinnerNumberModel xModel = new SpinnerNumberModel(Collinsa.INSTANCE.getPhysics().getWidth() / 2, 0f, Collinsa.INSTANCE.getPhysics().getWidth(), .1f);
        x = new JSpinner(xModel);
        x.setModel(xModel);
        x.setBounds(50 ,180,100,40);
        x.setBorder(BorderFactory.createTitledBorder("Abscisse (x)"));
        x.addChangeListener(this);
        panel.add(x);
        SpinnerNumberModel yModel = new SpinnerNumberModel(Collinsa.INSTANCE.getPhysics().getHeight() / 2, 0f, Collinsa.INSTANCE.getPhysics().getHeight(), .1f);
        y = new JSpinner(xModel);
        y.setModel(yModel);
        y.setBounds(50,230,100,40);
        y.setBorder(BorderFactory.createTitledBorder("Ordonnée (y)"));
        y.addChangeListener(this);
        panel.add(y);


        // Liste des matériaux pour remplir la JCombobox
        for (Field field : Material.class.getFields()) {
            try {

                if (field.get(null) instanceof Material)
                    this.materiau.addItem(field.getName());

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Tableau des collisions pour remplir la JComboBox
        for (int i=0; i<CollisionType.values().length; i++)
            collision.addItem(CollisionType.values()[i].name());


        // on met un actionlistener pour avoir un aperçu de ce qu'on construit
        forme.setBounds(50,30,100,45);
        forme.setBorder(BorderFactory.createTitledBorder("Type d'objet : "));
        forme.addActionListener( this);

        materiau.setBounds(50,130,130,45);
        materiau.setBorder(BorderFactory.createTitledBorder("Matériau : "));
        materiau.addActionListener(this);

        collision.setBounds(50, 80, 125,45);
        collision.setBorder(BorderFactory.createTitledBorder("Type de collision : "));
        collision.addActionListener(this);

        panel.add(forme);
        panel.add(materiau);
        panel.add(collision);

        //on rajoute un bouton pour lancer la création
        ok = new JButton("OK");
        ok.setBounds(660,400,100,40);
        ok.addActionListener(this);
        panel.add(ok);

        SpinnerNumberModel choixMasseModel = new SpinnerNumberModel(0f, 0f, Float.MAX_VALUE, .1f);
        choixMasse = new JSpinner(choixMasseModel);
        choixMasse.setBorder(BorderFactory.createTitledBorder("Masse de l'entité : " + choixMasse.getValue()));
        choixMasse.addChangeListener(this);
        choixMasse.setBounds(3, 410, 130, 40);
        choixMasse.setEnabled(false);
        panel.add(choixMasse);

        checkBoxMasse = new JCheckBox("Modifier la masse", false);
        checkBoxMasse.setBounds(3, 390, 200, 15);
        checkBoxMasse.addActionListener(e -> choixMasse.setEnabled(checkBoxMasse.isSelected()));
        checkBoxMasse.addChangeListener(this);
        panel.add(checkBoxMasse);

        SpinnerNumberModel choixInertieModel = new SpinnerNumberModel(0f, 0f, Float.MAX_VALUE, .1f);
        choixInertie = new JSpinner(choixInertieModel);
        choixInertie.setBounds(220 ,410,120,40);
        choixInertie.setBorder(BorderFactory.createTitledBorder("Moment d'inertie : " + choixInertie.getValue()));
        choixInertie.addChangeListener(this);
        choixInertie.setEnabled(false);
        panel.add(choixInertie);

        checkBoxInertie = new JCheckBox("Modifier le moment d'inertie", false);
        checkBoxInertie.setBounds(220, 390, 200, 15);
        checkBoxInertie.addActionListener(e -> choixInertie.setEnabled(checkBoxInertie.isSelected()));
        checkBoxInertie.addChangeListener(this);
        panel.add(checkBoxInertie);

        // On crée les JSpinner permettant de changer la vitesse et l'accélération, ils ont tous les deux besoins d'une direction et d'une vitesse
        SpinnerNumberModel dirvModel = new SpinnerNumberModel(0f, 0f, 360f, .1f);
        dirVel = new JSpinner(dirvModel);
        dirVel.setModel(dirvModel);
        dirVel.setBounds(520 , 50, 120, 40);
        dirVel.setBorder(BorderFactory.createTitledBorder("Direction vitesse"));
        dirVel.addChangeListener(this);
        panel.add(dirVel);

        SpinnerNumberModel norvModel = new SpinnerNumberModel(0f, 0f, 1000f, .1f);
        magVel = new JSpinner(norvModel);
        magVel.setModel(norvModel);
        magVel.setBounds(520 , 100, 120, 40);
        magVel.setBorder(BorderFactory.createTitledBorder("Norme vitesse"));
        magVel.addChangeListener(this);
        panel.add(magVel);

        SpinnerNumberModel diraModel = new SpinnerNumberModel(0f, 0f, 1000f, .1f);
        dirAcc = new JSpinner(diraModel);
        dirAcc.setModel(diraModel);
        dirAcc.setBounds(520 , 150, 150, 40);
        dirAcc.setBorder(BorderFactory.createTitledBorder("Direction accélération"));
        dirAcc.addChangeListener(this);
        panel.add(dirAcc);

        SpinnerNumberModel noraModel = new SpinnerNumberModel(0f, 0f, 1000f, .1f);
        magAcc = new JSpinner(noraModel);
        magAcc.setModel(noraModel);
        magAcc.setBounds(520 , 200, 150, 40);
        magAcc.setBorder(BorderFactory.createTitledBorder("Norme accélération"));
        magAcc.addChangeListener(this);
        panel.add(magAcc);


        // On créer les JSLider pour changer la rotation, la vitesse angulaire et l'accéleration angulaire
        rot = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
        rot.setBorder(BorderFactory.createTitledBorder("Rotation : " + rot.getValue()));
        rot.setBounds(3, 300, 250, 70);
        rot.addChangeListener(e -> rot.setBorder(BorderFactory.createTitledBorder("Rotation : " + rot.getValue())));
        rot.setMajorTickSpacing(rot.getMaximum() - rot.getMinimum());
        rot.setPaintTicks(true);
        rot.setPaintLabels(true);
        rot.addChangeListener(this);
        panel.add(rot);

        angVel = new JSlider(JSlider.HORIZONTAL, 0, 720, 0);
        angVel.setBorder(BorderFactory.createTitledBorder("Vitesse angulaire : " + angVel.getValue()));
        angVel.setBounds(263, 300, 250, 70);
        angVel.addChangeListener(e -> angVel.setBorder(BorderFactory.createTitledBorder("Vitesse angulaire : " + angVel.getValue())));
        angVel.setMajorTickSpacing(angVel.getMaximum() - angVel.getMinimum());
        angVel.setPaintTicks(true);
        angVel.setPaintLabels(true);
        angVel.addChangeListener(this);
        panel.add(angVel);

        angAcc = new JSlider(JSlider.HORIZONTAL, 0, 720, 0);
        angAcc.setBorder(BorderFactory.createTitledBorder("Accélération angulaire : " + angAcc.getValue()));
        angAcc.setBounds(523, 300, 250, 70);
        angAcc.addChangeListener(e -> angAcc.setBorder(BorderFactory.createTitledBorder("Accélération angulaire : " + angAcc.getValue())));
        angAcc.setMajorTickSpacing(angAcc.getMaximum() - angAcc.getMinimum());
        angAcc.setPaintTicks(true);
        angAcc.setPaintLabels(true);
        angAcc.addChangeListener(this);
        panel.add(angAcc);

        panelCreation = new PanelCreationCercle(this);
        panel.add(panelCreation);

        return panel;
    }

    /**
     * Méthode qui construit l'objet que l'utilisateur séléctionne et lui applique tous les paramètres voulus
     *
     * @return resultat une entité choisi
     */
    public Entity creerEntite() {

        Entity resultat = panelCreation.creerEntite();
        editEntity(resultat, true);

        return resultat;
    }

    /**
     * Applique les valeurs choisies à une entité
     * @param entity entité à modifier
     */
    public void editEntity(Entity entity, boolean updatePos) {

        if (entity == null) return;

        // On donne à l'entité les paramètres qu'on a choisi
        if (updatePos)
            entity.setPos((float)(double)x.getValue(), (float)(double)y.getValue());
        entity.setVel(Vec2f.fromAngle((float)Math.toRadians((float)(double) dirVel.getValue())).mult((float)(double) magVel.getValue()));
        entity.setAcc(Vec2f.fromAngle((float)Math.toRadians((float)(double) dirAcc.getValue())).mult((float)(double) magAcc.getValue()));
        entity.setRot((float) Math.toRadians(rot.getValue()));
        entity.setAngVel((float) Math.toRadians(angVel.getValue()));
        entity.setAngAcc((float) Math.toRadians(angAcc.getValue()));
        entity.setCollisionType(CollisionType.valueOf(collision.getSelectedItem().toString()));

        // Si le matériau sélectionné est connu de Material, on l'applique.
        Material material = Material.getMaterialFromName(materiau.getSelectedItem().toString());
        if (material != null)
            entity.setMaterial(material);

        if (checkBoxMasse.isSelected())
            entity.getInertia().setMass((float)(double) choixMasse.getValue());

        if (checkBoxInertie.isSelected())
            entity.getInertia().setJ((float)(double) choixInertie.getValue());
    }

    /**
     * Met à jour l'aperçu
     */
    public void updatePreview(boolean updatePos) {

        if (preview == null) {
            preview = creerEntite();
            Collinsa.INSTANCE.getRenderer().addExtra(preview);
        }

        panelCreation.editEntity(preview);
        editEntity(preview, updatePos);
        Collinsa.INSTANCE.getMainFrame().selectionTool.setSelectedEntity(preview);
    }

    /**
     * Remet à zero et supprime l'aperçu
     */
    public void clearPreview() {

        Collinsa.INSTANCE.getRenderer().removeExtra(preview);

        if (Collinsa.INSTANCE.getMainFrame().selectionTool.getSelectedEntity() == preview)
            Collinsa.INSTANCE.getMainFrame().selectionTool.setSelectedEntity(null);

        preview = null;
    }

    /**
     * Répond aux clics sur les boutons, et dans les JComboBox puisqu'elles n'ont pas de ChangeListener.
     * si on clique sur ok, on lance création de l'objet ou on le modifie
     * si on change de type d'objet, on ouvre un panel spécifique au type d'objet
     * si on change de matériau, on met à jour la masse et le moment d'inertie puisque la densité a peu être changé
     */
    @Override
    public void actionPerformed(ActionEvent event) {

        if (event.getSource() == ok) {

            if (aModifier == null) { // si on a utilisé Creation pour une création
                Entity nouvobjet = creerEntite();

                // Après l'avoir créé, on l'envoie dans le monde
                Collinsa.INSTANCE.getPhysics().addEntity(nouvobjet);

            } else { // si on a utilisé Creation pour une modification

                // modifier les paramètres de l'entité, mais pas sa position
                panelCreation.editEntity(aModifier);
                editEntity(aModifier, false);



                // Avant de fermer la page on vire l'aperçu
                clearPreview();

                // On ferme la Frame et on la détruit
                setVisible(false);
                dispose();
            }

        } else if (event.getSource() == forme) {

            panel.remove(panelCreation);

            if (forme.getSelectedItem().equals(formesDispo[0]))
                panelCreation = new PanelCreationCercle(this);

            else if (forme.getSelectedItem().equals(formesDispo[1]))
                panelCreation = new PanelCreationRectangle(this);

            else if (forme.getSelectedItem().equals(formesDispo[2]))
                panelCreation = new PanelCreationPolygone(this);

            // On vire l'objet stocké dans la preview puisqu'il n'est plus du bon type. updatePreview() est appelé en fin de méthode et se chargera de le regénérer correctement
            clearPreview();

            // On ajoute le panel utile à la nouvelle forme et on fait regénérer l'affichage de l'interface à Java
            panel.add(panelCreation);
            panel.updateUI();

        } else if (event.getSource() == materiau) {

            // Changement de matériau change la densité, mais un JComboxBox n'a pas de ChangeListener
            if (!checkBoxMasse.isSelected())
                choixMasse.setValue( (double) creerEntite().getInertia().getMass());

            if (!checkBoxInertie.isSelected())
                choixInertie.setValue( (double) creerEntite().getInertia().getJ());
        }

        // On met à jour l'aperçu
        updatePreview(false);
    }

    /**
     * Met à jour l'aperçu et les valeurs de masses et de moment d'inertie quand une des valeurs changent
     */
    @Override
    public void stateChanged(ChangeEvent e) {

        if (e.getSource() == ok) return;

        updatePreview(aModifier == null);

        if (!checkBoxMasse.isSelected())
            choixMasse.setValue( (double) creerEntite().getInertia().getMass());

        if (!checkBoxInertie.isSelected())
            choixInertie.setValue( (double) creerEntite().getInertia().getJ());
    }
}
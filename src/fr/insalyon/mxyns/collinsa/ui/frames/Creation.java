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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;


public class Creation extends JFrame implements ActionListener {
    // création des deux boutons déroulants pour choisir la forme et le matériau
    private JComboBox forme;
    private JComboBox materiau;
    private JComboBox collision;
    private PanelCreation panelCreation;
    private JSpinner x, y;
    private JSpinner dirv, norv, dira, nora;
    private JSlider rot,angv, anga;

    // On crée certains objets parce qu'on veut y accéder facilement
    Object[] obj = new Object[] { "Cercle", "Rectangle", "Polygone" };
    JButton ok = new JButton("OK");
    JPanel panel = buildContentPanel();

    public Creation(String nom, int width, int height) {

        super("Création");
        setSize(width, height);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // on fait en sorte que la fen^tre puisse se fermer sans tout fermer
        setContentPane(panel);
        setVisible(true);
    }

    private JPanel buildContentPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(null);

        forme = new JComboBox(obj);
        materiau = new JComboBox();
        collision = new JComboBox();

        // JSpinner qui renseigne sur la position de l'objet
        SpinnerNumberModel xModel = new SpinnerNumberModel(Collinsa.INSTANCE.getPhysics().getWidth() / 2, 0, 1000, .1);
        x = new JSpinner(xModel);
        x.setModel(xModel);
        x.setBounds(650 ,100,100,40);
        x.setBorder(BorderFactory.createTitledBorder("Abscisse"));
        panel.add(x);
        SpinnerNumberModel yModel = new SpinnerNumberModel(Collinsa.INSTANCE.getPhysics().getHeight() / 2, 0, 700, .1);
        y = new JSpinner(xModel);
        y.setModel(yModel);
        y.setBounds(650,200,100,40);
        y.setBorder(BorderFactory.createTitledBorder("Ordonnée"));
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

        // Tableau des collisions our remplir la JComboBox
        for (int i=0; i<CollisionType.values().length; i++)
            collision.addItem(CollisionType.values()[i].name());


        // on met un actionlistener pour avoir un aperçu de ce qu'on construit
        forme.setBounds(50,50,100,45);
        forme.setBorder(BorderFactory.createTitledBorder("Type d'objet : "));
        forme.addActionListener( this);

        materiau.setBounds(50,150,100,45);
        materiau.setBorder(BorderFactory.createTitledBorder("Matériau : "));
        materiau.addActionListener(this);

        collision.setBounds(50, 250, 125,45);
        collision.setBorder(BorderFactory.createTitledBorder("Type de collision : "));
        collision.addActionListener(this);


        panel.add(forme);
        panel.add(materiau);
        panel.add(collision);

        //on rajoute un bouton pour lancer la création
        ok = new JButton("OK");
        ok.setBounds(350,600,100,40);
        ok.addActionListener(this);
        panel.add(ok);

        // On crée les JSpinner permettant de changer la vitesse et l'accélération, ils ont tous les deux besoins d'une direction et d'une vitesse
        SpinnerNumberModel dirvModel = new SpinnerNumberModel(0, 0, 360, .1);
        dirv = new JSpinner(dirvModel);
        dirv.setModel(dirvModel);
        dirv.setBounds(450 ,350,120,40);
        dirv.setBorder(BorderFactory.createTitledBorder("Direction vitesse"));
        panel.add(dirv);

        SpinnerNumberModel norvModel = new SpinnerNumberModel(0, 0, 1000, .1);
        norv = new JSpinner(norvModel);
        norv.setModel(norvModel);
        norv.setBounds(450 ,400,120,40);
        norv.setBorder(BorderFactory.createTitledBorder("Norme vitesse"));
        panel.add(norv);

        SpinnerNumberModel diraModel = new SpinnerNumberModel(0, 0, 1000, .1);
        dira = new JSpinner(diraModel);
        dira.setModel(diraModel);
        dira.setBounds(600 ,350,150,40);
        dira.setBorder(BorderFactory.createTitledBorder("Direction accélération"));
        panel.add(dira);

        SpinnerNumberModel noraModel = new SpinnerNumberModel(0, 0, 1000, .1);
        nora = new JSpinner(noraModel);
        nora.setModel(noraModel);
        nora.setBounds(600 ,400,150,40);
        nora.setBorder(BorderFactory.createTitledBorder("Norme accélération"));
        panel.add(nora);


        // On créer les JSLider pour changer la rotation, la vitesse angulaire et l'accéleration angulaire
        rot = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
        rot.setBorder(BorderFactory.createTitledBorder("Rotation : " + rot.getValue()));
        rot.setBounds(0, 350, 400, 70);
        rot.addChangeListener(e -> rot.setBorder(BorderFactory.createTitledBorder("Rotation : " + rot.getValue())));
        rot.setMajorTickSpacing(rot.getMaximum() - rot.getMinimum());
        rot.setPaintTicks(true);
        rot.setPaintLabels(true);
        panel.add(rot);

        angv = new JSlider(JSlider.HORIZONTAL, 0, 720, 0);
        angv.setBorder(BorderFactory.createTitledBorder("Vitesse angulaire : " + angv.getValue()));
        angv.setBounds(0, 420, 400, 70);
        angv.addChangeListener(e -> angv.setBorder(BorderFactory.createTitledBorder("Vitesse angulaire : " + angv.getValue())));
        angv.setMajorTickSpacing(angv.getMaximum() - angv.getMinimum());
        angv.setPaintTicks(true);
        angv.setPaintLabels(true);
        panel.add(angv);

        anga = new JSlider(JSlider.HORIZONTAL, 0, 720, 0);
        anga.setBorder(BorderFactory.createTitledBorder("Accélération angulaire : " + anga.getValue()));
        anga.setBounds(0, 490, 400, 70);
        anga.addChangeListener(e -> anga.setBorder(BorderFactory.createTitledBorder("Accélération angulaire : " + anga.getValue())));
        anga.setMajorTickSpacing(anga.getMaximum() - anga.getMinimum());
        anga.setPaintTicks(true);
        anga.setPaintLabels(true);
        panel.add(anga);

        panelCreation = new PanelCreationCercle();
        panel.add(panelCreation);

        return panel;
    }

    // méthode qui construit l'objet que l'utilisateur séléctionne
    public Entity creerEntite() {

        Entity resultat = panelCreation.creerEntite();
        // On donne à l'entité les paramètres qu'on a choisi
        resultat.setPos((float)(double)x.getValue(), (float)(double)y.getValue());
        resultat.setVel(Vec2f.fromAngle((float)Math.toRadians((double)dirv.getValue())).mult((float)(double)norv.getValue()));
        resultat.setAcc(Vec2f.fromAngle((float)Math.toRadians((double)dira.getValue())).mult((float)(double)nora.getValue()));
        resultat.setRot((float) Math.toRadians(rot.getValue()));
        resultat.setAngVel((float) Math.toRadians(angv.getValue()));
        resultat.setAngAcc((float) Math.toRadians(anga.getValue()));
        resultat.setCollisionType(CollisionType.valueOf(collision.getSelectedItem().toString()));

        if (resultat != null) {
            try {
                resultat.setMaterial((Material) Material.class.getField(materiau.getSelectedItem().toString()).get(null));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        return resultat;
    }

    // si on clique sur ok, on lance création de l'objet, sinon, on affiche un aperçu de l'objet
    public void actionPerformed(ActionEvent event) {

        if (event.getSource() == ok) {

            Entity nouvobjet = creerEntite();

            // Après l'avoir créé, on l'envoie dans le monde
            Collinsa.INSTANCE.getPhysics().addEntity(nouvobjet);

        } else if (event.getSource() == forme) {

            panel.remove(panelCreation);

            if (forme.getSelectedItem().equals(obj[0]))
                panelCreation = new PanelCreationCercle();

            else if (forme.getSelectedItem().equals(obj[1]))
                panelCreation = new PanelCreationRectangle();

            else if (forme.getSelectedItem().equals(obj[2]))
                panelCreation = new PanelCreationPolygone();

            panel.add(panelCreation);
            panel.updateUI();


        } else {
            // on ajoute un aperçu de l'objet dans le pane
            // panel.add(creerEntite()); // TODO comment afficher dans panel ???
        }
    }
}
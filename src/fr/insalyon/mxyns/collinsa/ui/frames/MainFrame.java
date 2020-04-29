package fr.insalyon.mxyns.collinsa.ui.frames;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.ui.panels.SandboxPanel;
import fr.insalyon.mxyns.collinsa.ui.tools.*;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.Component;

/**
 * Frame principale, titre et Sandbox
 */
public class MainFrame extends JFrame {

    /**
     * Panel permettant le rendu de la simulation via le Renderer
     */
    final private SandboxPanel sandboxPanel;

    public SelectionTool selectionTool;
    private JToolBar toolbar;

    Color my = new Color(93,155,155);

    public MainFrame (int width, int height) {

        super ("Simulateur Physique");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        sandboxPanel = new SandboxPanel(this);
        sandboxPanel.setBounds((int) (width * .005f), (int) (height * .049f), (int) (width * .85f), (int) (height * .908f));
        add(sandboxPanel);

        genererInterface(width, height);

        // On s'assure que le panel a bien le focus initialement
        sandboxPanel.requestFocus();

        setVisible(true);
    }

    private void genererInterface(int width, int height) {

        /* Création des différents boutons du menu
         * Créer, modifier, supprimer et les paramètres que l'on peut rajouter
         * On décrit les tailles et positions des boutons en fonction de la taille de la fenètre (en %)
         */
        JButton creer = new JButton("Créer");
        add(creer);
        creer.setBounds((int)(0.87*width),(int)(0.06*height),(int)(0.1*width),(int)(0.15*height));
        creer.setBackground(my);
        creer.setForeground(Color.black);
        creer.addActionListener(e -> ouvrirPageCreation());


        JButton supprimer = new JButton("Supprimer");
        add(supprimer);
        supprimer.setBounds((int)(0.87*width),(int)(0.06*height+0.17*height),(int)(0.1*width),(int)(0.15*height));
        supprimer.setBackground(my);
        supprimer.setForeground(Color.black);
        supprimer.addActionListener(e -> Supprimer());

        JButton modifier = new JButton("Modifier");
        add(modifier);
        modifier.setBounds((int)(0.87*width),(int)(0.06*height+2*0.17*height),(int)(0.1*width),(int)(0.15*height));
        modifier.setBackground(my);
        modifier.setForeground(Color.black);
        modifier.addActionListener(e -> ouvrirPageModification());

        JButton parametres = new JButton("Paramètres");
        add(parametres);
        parametres.setBounds((int)(0.87*width),(int)(0.06*height+3*0.17*height),(int)(0.1*width),(int)(0.15*height));
        parametres.setBackground(my);
        parametres.setForeground(Color.black);
        parametres.addActionListener(e -> ouvrirParametres());

        toolbar = new JToolBar(); ButtonGroup toolGroup = new ButtonGroup();
        Tool[] tools = new Tool[] { selectionTool = new SelectionTool(), new EntityDragTool(), new FreezeEntityTool(), new MoveCameraTool(), new ForcesTool() };
        for (Tool tool : tools) {
            toolGroup.add(tool);
            toolbar.add(tool);
        }
        selectionTool.setSelected(true);

        toolbar.setBounds(0, 0, getWidth(), 32);
        add(toolbar);
    }

    private void Supprimer() {

        if (selectionTool.getSelectedEntity() != null)
            Collinsa.INSTANCE.getPhysics().removeEntity(selectionTool.getSelectedEntity());
    }

    private void ouvrirPageModification() {

        // TODO on supprime l'objet et on rouvre la page création
        if (selectionTool.getSelectedEntity() != null)
            new Creation(1200, 800);
    }

    public void ouvrirPageCreation() {

        new Creation (800, 800);
    }

    public void ouvrirParametres() {

        new Parametres();
    }


    /**
     * Renvoie l'instance du panel de rendu de la simulation
     * @return panel de rendu de la simulation
     */
    public SandboxPanel getSandboxPanel() {

        return sandboxPanel;
    }

    /**
     * Renvoie l'outil sélectionné dans la Toolbar
     * @return outil sélectionné
     */
    public Tool getSelectedTool() {

        for (Component component : toolbar.getComponents())
            if (component instanceof Tool)
                if (((Tool) component).isSelected())
                    return (Tool) component;

        return null;
    }
}
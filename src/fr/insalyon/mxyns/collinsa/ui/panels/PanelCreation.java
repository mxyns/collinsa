package fr.insalyon.mxyns.collinsa.ui.panels;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

import javax.swing.JPanel;
import java.awt.Color;

public abstract class PanelCreation extends JPanel {
    Color my = new Color(93,155,155);

    public PanelCreation() {

        setSize(300, 200);
        setLocation(250,50);
        setBackground(my);

    }

    public abstract Entity creerEntite();
}
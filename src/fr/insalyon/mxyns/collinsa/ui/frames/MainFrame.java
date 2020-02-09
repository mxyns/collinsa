package fr.insalyon.mxyns.collinsa.ui.frames;


import fr.insalyon.mxyns.collinsa.ui.panels.SandboxPanel;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

/**
 * Frame principale, titre et Sandbox
 */
public class MainFrame extends JFrame {

    /**
     * Panel permettant le rendu de la simulation via le Renderer
     */
    public SandboxPanel sandboxPanel;

    public MainFrame(int width, int height) {
        super();

        setTitle("CollINSA");

        JLabel title = new JLabel("CollINSA");
        title.setHorizontalAlignment(JLabel.CENTER);
        Font font = new Font("Trebuchet MS", Font.BOLD, 24);
        title.setFont(font);

        sandboxPanel = new SandboxPanel();
        sandboxPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));

        add(sandboxPanel, BorderLayout.CENTER);
        add(title, BorderLayout.NORTH);

        setResizable(false);

        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Renvoie l'instance du panel de rendu de la simulation
     * @return panel de rendu de la simulation
     */
    public SandboxPanel getSandboxPanel() {

        return sandboxPanel;
    }
}

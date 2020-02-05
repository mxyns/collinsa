package fr.insalyon.mxyns.collinsa.ui.frames;


import fr.insalyon.mxyns.collinsa.ui.panels.SandboxPanel;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Font;

/**
 * Frame principale, titre et Sandbox
 */
public class MainFrame extends JFrame {

    public MainFrame(int width, int height) {
        super();

        setTitle("CollINSA");
        setLocationRelativeTo(null);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel title = new JLabel("CollINSA");
        title.setHorizontalAlignment(JLabel.CENTER);
        Font font = new Font("Trebuchet MS", Font.BOLD, 24);
        title.setFont(font);

        SandboxPanel sandboxPanel = new SandboxPanel();

        add(sandboxPanel, BorderLayout.CENTER);
        add(title, BorderLayout.NORTH);
        setVisible(true);
    }
}

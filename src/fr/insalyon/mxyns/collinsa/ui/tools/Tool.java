package fr.insalyon.mxyns.collinsa.ui.tools;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Classe générique représentant un outil utilisé dans la MainFrame
 */
public abstract class Tool extends JToggleButton {

    /**
     * Nom de l'outil
     */
    public String name;

    /**
     * Constructeur générique d'un outil
     * @param name nom de l'outil
     * @param tooltip tooltip à afficher
     * @param iconPath chemin d'accès de son icone. si null, on prend la première lettre du nom comme icone
     */
    public Tool(String name, String tooltip, String iconPath) {

        this.name = name;
        setToolTipText(tooltip);

        if (iconPath != null)
            try {
                InputStream iconUrl = Tool.class.getResourceAsStream(iconPath);
                if (iconUrl == null) {

                    setText(String.valueOf(name.charAt(0)));
                    System.out.println("Couldn't load " + iconPath);
                } else
                    setIcon(new ImageIcon(ImageIO.read(iconUrl).getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        else
            setText(String.valueOf(name.charAt(0)));

        addItemListener(e -> {

            if (e.getID() == ItemEvent.ITEM_STATE_CHANGED)
                if (e.getStateChange() == ItemEvent.SELECTED)
                    onSelected();
                else if (e.getStateChange() == ItemEvent.DESELECTED)
                    onDeselected();
        });
    }

    // Ces méthodes ne sont pas abstraites elles ne sont pas toutes utilisée par chaque outil. Il est plus pratique que l'outil Override seulement les méthodes qui lui sont utiles.

    /**
     * Appelée quand l'outil est sélectionné dans la barre d'outils
     */
    public void onSelected() {}

    /**
     * Appelée quand l'outil est désélectionné dans la barre d'outils
     */
    public void onDeselected() {}

    /**
     * Appelée quand un bouton de la souris est enfoncé et que l'outil est sélectionné
     */
    public void onMousePressed(MouseEvent e) {}

    /**
     * Appelée quand un bouton de la souris est relaché et que l'outil est sélectionné
     */
    public void onMouseReleased(MouseEvent e) {}

    /**
     * Appelée lors d'un clic souris et que l'outil est sélectionné
     */
    public void onClick(MouseEvent e) {}

    /**
     * Appelée lors d'un drag (cliqué-glissé) et que l'outil est sélectionné
     */
    public void onDrag(MouseEvent e) {}
}
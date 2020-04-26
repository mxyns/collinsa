package fr.insalyon.mxyns.collinsa.ui.tools;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class Tool extends JToggleButton {

    public String name;

    public Tool(String name, String tooltip, String iconPath) {

        this.name = name;

        setToolTipText(tooltip);

        try {

            setIcon(new ImageIcon(ImageIO.read(Tool.class.getResource(iconPath)).getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        addItemListener(e -> {

            if (e.getID() == ItemEvent.ITEM_STATE_CHANGED)
                if (e.getStateChange() == ItemEvent.SELECTED)
                    onSelected();
                else if (e.getStateChange() == ItemEvent.DESELECTED)
                    onDeselected();
        });
    }

    // Ces méthodes ne sont pas abstraites elles ne sont pas toutes utilisée par chaque outil. Il est plus pratique que l'outil Override seulement les méthodes qui lui sont utiles.
    public void onSelected() {}
    public void onDeselected() {}
    public void onMousePressed(MouseEvent e) {}
    public void onMouseReleased(MouseEvent e) {}
    public void onClick(MouseEvent e) {}
    public void onDrag(MouseEvent e) {}
}
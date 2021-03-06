package fr.insalyon.mxyns.collinsa.render;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Représente une paire d'image.
 *
 * +------------+     +-------------+
 * |            |     |             |
 * | BackBuffer |     | FrontBuffer |
 * |  Dessinée  |     |  Affichée   |
 * |            |     |             |
 * +------------+     +-------------+
 *
 * On dessine sur le BackBuffer et on affiche le FrontBuffer
 * Quand on a finit de dessiner sur le BackBuffer on échange les deux adresses mémoires, Front devient Back et Back devient Front
 * La dernière image dessinée entièrement passe de BackBuffer à FrontBuffer, et on vide le BackBuffer (l'image affichée juste avant) pour redessiner une nouvelle image
 * Il a pour but de réduire le scintillement et de créer des images que l'on peut éventuellement récupérer pour faire un enregistrement.
 * Aussi il permet de ne plus lier le Panel au Renderer. Idéalement il faudrait stocker ces images dans la VRAM mais ce n'est pas le cas ici.
 *
 */
public class GraphicsBuffer {

    /**
     * Couleur de fond pour les images
     */
    private Color backgroundColor = Color.white;

    /**
     * Taille des images à dessiner
     */
    private Dimension imageSize;

    /**
     * Type d'image (encodage des couleurs : RGB)
     */
    private int imageType = BufferedImage.TYPE_INT_RGB;

    /**
     * Tableau contenant les deux images
     * 0 = backbuffer (write), 1 = frontbuffer (read)
     */
    private final Image[] buffer = new Image[2];

    /**
     * Crée un GraphicsBuffer avec des images de taille (width, height)
     * @param width largeur des images à générer
     * @param height hauteur des images à générer
     */
    public GraphicsBuffer(int width, int height) {

        for (int i = 0; i < buffer.length; ++i) {

            buffer[i] = new BufferedImage(width, height, imageType);
            buffer[i].getGraphics().setColor(backgroundColor);
        }

        this.imageSize = new Dimension(width, height);
    }

    /**
     * Crée un GraphicsBuffer avec des images de dimension 'size'
     * @param size dimension des images
     */
    public GraphicsBuffer(Dimension size) {

        this((int)size.getWidth(), (int)size.getHeight());
    }

    /**
     * Redimensionne les images générées et à générer
     * @param width nouvelle largeur d'image
     * @param height nouvelle hauteur d'image
     */
    public void resize(int width, int height) {

        for (int i = 0; i < buffer.length; ++i) {

            BufferedImage scaledImage = new BufferedImage(width, height, imageType);
            scaledImage.getGraphics().setColor(backgroundColor);
            scaledImage.getGraphics().drawImage(buffer[i].getScaledInstance(width, height, Image.SCALE_DEFAULT), 0, 0, null);

            buffer[i].flush();
            buffer[i] = scaledImage;
        }

        this.imageSize = new Dimension(width, height);
    }

    /**
     * Echange le front et le back buffer
     */
    public void flip() {

        Image temp = buffer[0];
        buffer[0] = buffer[1];
        buffer[1] = temp;
    }

    /**
     * Vide l'image du back buffer
     */
    public void resetBackBuffer() {

        Graphics graphics = getGraphics2D();
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, imageSize.width, imageSize.height);
    }

    /**
     * Renvoie le Graphics2D du BackBuffer
     * @return Graphics2D du BackBuffer
     */
    public Graphics2D getGraphics2D() {

        return (Graphics2D) getGraphics();
    }

    /**
     * Renvoie le Graphics du BackBuffer
     * @return Graphics du BackBuffer
     */
    public Graphics getGraphics() {

        return getBackBuffer().getGraphics();
    }

    /**
     * Renvoie l'image à afficher (celle du FrontBuffer)
     * @return frontbuffer (buffer[1])
     */
    public Image getImage() {

        return buffer[1];
    }

    /**
     * Renvoie l'image sur laquelle dessiner (celle du BackBuffer)
     * @return backbuffer (buffer[0])
     */
    public Image getBackBuffer() {

        return buffer[0];
    }

    /**
     * Renvoie la taille des images générées
     * @return imageSize
     */
    public Dimension getImageSize() {

        return imageSize;
    }

    /**
     * Renvoie la couleur de fond à appliquer sur les images rendues
     *
     * @return backgroundColor
     */
    public Color getBackgroundColor() {

        return backgroundColor;
    }

    /**
     * Redéfinit la couleur de fond des images rendues
     *
     * @param backgroundColor nouvelle couleur de fond
     */
    public void setBackgroundColor(Color backgroundColor) {

        this.backgroundColor = backgroundColor;
    }
}

package fr.insalyon.mxyns.collinsa.render;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphicsBuffer {

    private Color backgroundColor = Color.white;
    private Dimension imageSize;
    private int imageType = BufferedImage.TYPE_INT_RGB;

    // 0 = backbuffer (write), 1 = frontbuffer (read)
    private final Image[] buffer = new Image[2];

    public GraphicsBuffer(int width, int height) {

        buffer[0] = new BufferedImage(width, height, imageType);
            buffer[0].getGraphics().setColor(backgroundColor);

        buffer[1] = new BufferedImage(width, height, imageType);
            buffer[1].getGraphics().setColor(backgroundColor);

        this.imageSize = new Dimension(width, height);
    }
    public GraphicsBuffer(Dimension size) {

        this((int)size.getWidth(), (int)size.getHeight());
    }

    public void resize(int width, int height) {

        BufferedImage newFrontBuffer = new BufferedImage(width, height, imageType);
        newFrontBuffer.getGraphics().setColor(backgroundColor);
        newFrontBuffer.getGraphics().drawImage(buffer[1].getScaledInstance(width, height, Image.SCALE_DEFAULT), 0, 0, null);


        BufferedImage newBackBuffer = new BufferedImage(width, height, imageType);
        newBackBuffer.getGraphics().setColor(backgroundColor);
        newBackBuffer.getGraphics().drawImage(buffer[0].getScaledInstance(width, height, Image.SCALE_DEFAULT), 0, 0, null);

        this.imageSize = new Dimension(width, height);
    }

    public void flip() {

        Image temp = buffer[0];
        buffer[0] = buffer[1];
        buffer[1] = temp;
    }

    public void resetBackBuffer() {

        getGraphics().setColor(backgroundColor);
        getGraphics().fillRect(0, 0, imageSize.width, imageSize.height);
    }

    public Graphics2D getGraphics2D() {

        return (Graphics2D) getGraphics();
    }

    public Graphics getGraphics() {

        return getBackBuffer().getGraphics();
    }

    public Image getImage() {

        return buffer[1];
    }

    public Image getBackBuffer() {

        return buffer[0];
    }

    public Dimension getImageSize() {

        return imageSize;
    }
}

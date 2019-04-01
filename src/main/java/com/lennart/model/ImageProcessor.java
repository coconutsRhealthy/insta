package com.lennart.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageProcessor {

    public static BufferedImage getBufferedImageScreenShot(int x, int y, int width, int height) {
        Point point = new Point(x, y);
        Dimension dimension = new Dimension(width, height);
        Rectangle rectangle = new Rectangle(point, dimension);

        BufferedImage screenCapture = null;

        try {
            screenCapture = new Robot().createScreenCapture(rectangle);
        } catch (AWTException e) {
            System.out.println("Exception occured in createScreenShot: " + e.getMessage());
        }
        return screenCapture;
    }

    public static void saveBufferedImage(BufferedImage bufferedImage, String path) throws IOException {
        ImageIO.write(bufferedImage, "png", new File(path));
    }
}

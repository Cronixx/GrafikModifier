package crnxx;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

/**
 * Utility Class for storing loaded Pictures along with their path
 * */
public class ImagePathBundle {
    private BufferedImage img;
    private Path path;

    public BufferedImage getImg() {
        return img;
    }

    public Path getPath() {
        return path;
    }

    public ImagePathBundle(BufferedImage input, Path path) {
        this.img = input;
        this.path = path;
    }
}

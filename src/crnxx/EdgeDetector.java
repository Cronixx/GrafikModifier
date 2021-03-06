package crnxx;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

/**
 *  TODO: Use Callable!
 * */
public class EdgeDetector implements Runnable {
    private final int id;
    private int[][] sobel1 = {
            {-1,0,1},
            {-2,0,2},
            {-1,0,1}
    };

    private int[][] sobel2 = {
            {1,2,1},
            {0,0,0},

            {-1,-2,-1}
    };

    private int[][] scharr1 = {
            {3,0,-3},
            {10,0,-10},
            {3,0,-3}
    };

    private int[][] scharr2 = {
            {3,10,3},
            {0,0,0},
            {-3,-10,-3}
    };
    private volatile Path path;
    private volatile BufferedImage img;
    private volatile BufferedImage output;

    public EdgeDetector(ImagePathBundle ipb, int id) {
        this.img = ipb.getImg();
        this.path = ipb.getPath();
        this.id = id;
        System.out.println("Created EdgeDetector #"+this.id);
    }

    /**
     * Edge detection calculation.
     *
     * First luminance is calculated.
     * After that, the sobel operators are applied to every pixel.
     * The distance between the two resulting values is our grayscale color,
     * to which we set the color of our pixel.
     *
     * Afterwards, the result is stored in output
     * */
    public void run() {
        System.out.println("Thread #"+this.id+" starts working.");
        double[][] lum = luminance(img);

        for (int x = 1; x < img.getWidth() - 1; x++) {
            for (int y = 1; y < img.getHeight() - 1; y++) {
                int grayx = 0;
                int grayy = 0;
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        grayx += lum[x + i][y + j] * sobel1[1 + i][1 + j];
                        grayy += lum[x + i][y + j] * sobel2[1 + i][1 + j];
//                        grayx += lum[x + i][y + j] * scharr1[1 + i][1 + j];
//                        grayy += lum[x + i][y + j] * scharr2[1 + i][1 + j];
                    }
                }
                int distanceSobel1Sobel2 = (int) Math.sqrt(grayx * grayx + grayy * grayy);
                int gray = normalizeColor(distanceSobel1Sobel2);
                img.setRGB(x, y, new Color(gray, gray, gray).getRGB());
            }
        }
        this.output = img;
        System.out.println("Thread #"+this.id+" complete");
    }

    /** Calculate luminance of every pixel from input picture */
    private static double[][] luminance(BufferedImage img) {
        double[][] output = new double[img.getWidth()][img.getHeight()];
        for(int x = 0; x < img.getWidth(); x++) {
            for(int y = 0; y < img.getHeight(); y++) {
                Color pixel = new Color(img.getRGB(x,y));
                output[x][y] = 0.299*pixel.getRed() + 0.587*pixel.getGreen() + 0.114 * pixel.getBlue();
            }
        }
        return output;
    }

    /** Normalizes the Color (bigger than 255 is set to 255, smaller than 0 set to zero, rest stays)*/
    private static int normalizeColor(int input) {
        return input > 255 ?  255 : input < 0 ? 0 : input;
    }

    public Path getPath() {
        return path;
    }

    public ImagePathBundle getOutput() {
        return new ImagePathBundle(this.output, this.path);
    }
}

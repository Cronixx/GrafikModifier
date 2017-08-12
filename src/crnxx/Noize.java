package crnxx;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.Callable;

public class Noize implements Callable<BufferedImage> {


    @Override
    public BufferedImage call() throws Exception {
        int width = 1024;
        int height = width;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Random r = new Random();
        for(int i = 0; i < width; i++) {
            for(int j = 0; j< height; j++) {
                int red = normalizeColor(r.nextInt(255));
                int green = normalizeColor(r.nextInt(255));
                int blue = normalizeColor(r.nextInt(255));
                Color randColor = new Color(red, green, blue);
                Color black = new Color(0,0,0);
                Color white = new Color(255,255,255);

                if(i % 27 == 0 || j % 13 == 0) {
                    img.setRGB(j,i,randColor.getRGB());
                } else {
                    img.setRGB(j,i,black.getRGB());
                }
//                img.setRGB(j,i,randColor.getRGB());
            }
        }
        System.out.println("Finished random img");
        return img;
    }

    private int normalizeColor(int input) {
        return input > 255 ?  255 : input < 0 ? 0 : input;
    }
}

package crnxx;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

/**
 *  Edge Detection
 * */
public class Main {
    static int[][] sobel1 = {
            {-1,0,1},
            {-2,0,2},
            {-1,0,1}
    };
    static int[][] sobel2 = {
            {1,2,1},
            {0,0,0},
            {-1,-2,-1}
    };

    public static void main(String[] args) {
        String filepath = "C:\\test\\img.jpg";
        ArrayList<Path> pathList = getTargetPaths();
        ArrayList<BufferedImage> imgList = readImage(pathList);
        int count = 0;

        if(imgList.size() < 0) {
            System.out.println("Konnte kein Bild lesen");
            return;
        }
        for(int imageCount = 0; imageCount < imgList.size(); imageCount++) {
            BufferedImage img = imgList.get(imageCount);
            double[][] lum = luminanz(img);

            for (int x = 1; x < img.getWidth() - 1; x++) {
                for (int y = 1; y < img.getHeight() - 1; y++) {
                    int grayx = 0;
                    int grayy = 0;
                    for (int i = -1; i < 2; i++) {
                        for (int j = -1; j < 2; j++) {
                            grayx += lum[x + i][y + j] * sobel1[1 + i][1 + j];
                            grayy += lum[x + i][y + j] * sobel2[1 + i][1 + j];
                        }
                    }
                    int distanceSobel1Sobel2 = (int) Math.sqrt(grayx * grayx + grayy * grayy);
                    int gray = normalisiereFarbe(distanceSobel1Sobel2);
                    img.setRGB(x, y, new Color(gray, gray, gray).getRGB());
                }
            }
            writeImage(img, "" + pathList.get(imageCount).getParent() + "\\output\\mod_" + pathList.get(imageCount).getFileName());
        }
    }

    public static ArrayList<Path> getTargetPaths() {
        Path dir = Paths.get("C:\\test\\");
        ArrayList<Path> pathList = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file : stream) {
                String filename = "" + file.getFileName();
                if(filename.substring(filename.length()-4).equals(".jpg")) {
                    System.out.println("Found "+ file.getParent() + "\\" + filename);
                    pathList.add(file);
                }
            }
        } catch (IOException | DirectoryIteratorException x) {
            System.err.println(x);
        }

        return pathList;
    }

    public static ArrayList<BufferedImage> readImage(ArrayList<Path> pathList) {
        ArrayList<BufferedImage> imgList = new ArrayList<>();
        try {
            for(Path file : pathList) {
                imgList.add(ImageIO.read(Files.newInputStream(file)));
            }
            return imgList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeImage(BufferedImage img, String fullPath) {
        try {
            ImageIO.write(img, "jpg", Files.newOutputStream(Paths.get(fullPath)) );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double[][] luminanz(BufferedImage img) {
        double[][] output = new double[img.getWidth()][img.getHeight()];
        for(int x = 0; x < img.getWidth(); x++) {
            for(int y = 0; y < img.getHeight(); y++) {
                Color pixel = new Color(img.getRGB(x,y));
                output[x][y] = 0.299*pixel.getRed() + 0.587*pixel.getGreen() + 0.114 * pixel.getBlue();
            }
        }
        return output;
    }

    public static int normalisiereFarbe(int input) {
        return input > 255 ?  255 : input < 0 ? 0 : input;
    }
}

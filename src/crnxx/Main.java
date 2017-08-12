package crnxx;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

/**
 *  Edge Detection
 *  https://www.wikiwand.com/de/Canny-Algorithmus ??????
 *
 *  TODO: java.util.HashMap<java.lang.Thread, java.lang.Callable<BufferedImage>>
 * */
public class Main {


    public static void main(String[] args) {
        ArrayList<Path> pathList;
        if(args.length < 1) {
            pathList = getTargetPaths("C:\\test\\");
        } else {
            pathList = getTargetPaths(args[0]);
        }
        ArrayList<BufferedImage> imgList = readImage(pathList);
        ArrayList<EdgeDetector> detectorList = new ArrayList<>();
        ArrayList<Thread> threadlist = new ArrayList<>();

        if(imgList.size() <= 0) {
            System.out.println("couldnt find .jpg or .jpeg files in dir");
            return;
        }

        for(int imageCount = 0; imageCount < imgList.size(); imageCount++) {
            detectorList.add(new EdgeDetector(new ImagePathBundle(imgList.get(imageCount), pathList.get(imageCount)), imageCount));
            threadlist.add(new Thread(detectorList.get(detectorList.size()-1)));
            System.out.println("start thread #"+(threadlist.size()-1));
            threadlist.get(threadlist.size()-1).start();
        }
        for(Thread t : threadlist) {
            try {
                System.out.println("wait for join");
                t.join();

                System.out.println("join complete");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(EdgeDetector d : detectorList) {
            writeImage(d.getOutput().getImg(), "" + d.getOutput().getPath().getParent() + "\\output\\mod_" + d.getOutput().getPath().getFileName());
        }
    }

    /**
     * Reads a dir for .jpg files and saves their paths to pathList.
     * */
    private static ArrayList<Path> getTargetPaths(String path) {
        Path dir = Paths.get(path);
        ArrayList<Path> pathList = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file : stream) {
                String filename = file.getFileName().toString();
                if(filename.length() > 4 && filename.charAt(filename.length()-4) == '.') {
                    if (filename.substring(filename.length() - 4).equals(".jpg") || filename.substring(filename.length() - 5).equals(".jpeg") || filename.substring(filename.length() - 4).equals(".png")) {
                        System.out.println("Found " + file.getParent() + "\\" + filename);
                        pathList.add(file);
                    }
                }
            }
        } catch (IOException | DirectoryIteratorException x) {
            System.err.println(x);
        }
        return pathList;
    }


    /**
     * Reads all .jpg files specified in pathList into memory
     * */
    private static ArrayList<BufferedImage> readImage(ArrayList<Path> pathList) {
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


    /**
     * Save image as .jpg to ./output/mod_<filename>
     * */
    private static void writeImage(BufferedImage img, String fullPath) {
        try {
            ImageIO.write(img, fullPath.charAt(fullPath.length()-4) == '.' ? fullPath.substring(fullPath.length()-3) : fullPath.substring(fullPath.length()-4), Files.newOutputStream(Paths.get(fullPath)) );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}

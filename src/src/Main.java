/**
 * Created by Clovis on 24/03/2016.
 */
import aimage.*;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class Main {

    public static ArrayList<OCRImage> images = new ArrayList<>();

    private static File[] listFiles(String dir) {
        File[] files = null;
        File dirobj = new File(dir);
        if(dirobj.exists() == false)
            IJ.showMessage("Le dossier n'existe pas.");
        else
            files = dirobj.listFiles();
        return files;
    }

    private static void createListeImage(String path, ArrayList<OCRImage> listeImg) {
        File[] files = listFiles(path);

        if (files.length != 0) {
            for (int i = 0; i < files.length; i++) {
                ImagePlus tempImg = new ImagePlus((files[i].getAbsolutePath()));
                new ImageConverter(tempImg).convertToGray8();
                listeImg.add(new OCRImage(tempImg,
                        files[i].getName().substring(0, 1).charAt(0),
                        files[i].getAbsolutePath()));
            }
        }
    }


    private static int[][] createConfusionMatrix() {

        int[][] matrix = new int[10][10];

        for (OCRImage ocrImage : Main.images) {
            if (ocrImage.getLabel() != '+' && ocrImage.getLabel() != '-' && ocrImage.getDecision() != '?') {
                int label = Integer.parseInt(String.valueOf(ocrImage.getLabel()));
                int decision = Integer.parseInt(String.valueOf(ocrImage.getDecision()));
                matrix[label][decision] = matrix[label][decision] + 1 ;
            }

        }

        return matrix;
    }

    private static void logOCR(String pathOut) {

        int[][] matrix = Main.createConfusionMatrix();

        try {
            FileWriter fileWriter = new FileWriter(pathOut, false);
            BufferedWriter buffer = new BufferedWriter(fileWriter);
            buffer.write("Test OCR effectue le " + new Date() + "\n  ");
            for (int i = 0; i < 10; i++) {
                buffer.write("    " + i);
            }
            buffer.write("\n");
            buffer.write("----------------------------------------------------");
            double total = 0;
            double value = 0;
            for (int i = 0; i < 10; i++) {
                buffer.write("\n" + i + "|");
                for (int j = 0; j < 10; j++) {
                    buffer.write("    " + matrix[i][j]);
                    if (i == j) {
                        value += matrix[i][j];
                    }
                    total += matrix[i][j];
                }
            }
            buffer.write("\n----------------------------------------------------\n");
            double t = 0;
            if (total != 0) {
                t = value / total;
            }
            buffer.write("Le taux de reconnaissance est de " + Math.floor(t * 100) + "%");
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void analyse() {
        ArrayList<ArrayList<Double>> others = new ArrayList<>();
        for(OCRImage img : Main.images)
            others.add(img.getVect());

        for (int i = 0; i < Main.images.size(); i++) {
            int res = CalculMath.PPV(Main.images.get(i).getVect(), others, i);
            if (Main.images.get(res).getLabel() != '+' && Main.images.get(res).getLabel() != '-') {
                Main.images.get(i).setDecision(Main.images.get(res).getLabel());
            }
        }
    }

    private static void setFeatureNdgVect() {
        for (OCRImage o : Main.images) {
            o.setFeatureNdg();
        }
    }

    private static void setFeatureProfilHV() {
        for (OCRImage o : Main.images) {
            o.setFeatureProfilHV();
        }
    }

    private static void setFeatureRapportIso() {
        for (OCRImage o : Main.images) {
            o.setFeatureRapportIso();
        }
    }

    private static void setFeatureZoning() {
        for (OCRImage o : Main.images) {
            o.zoning();
        }
    }

    public static void main(String[] args)
    {
        createListeImage("./../Ressources", Main.images);
        setFeatureNdgVect();
        setFeatureProfilHV();
        setFeatureRapportIso();
        setFeatureZoning();
        analyse();
        logOCR("./../log.txt");
    }

}


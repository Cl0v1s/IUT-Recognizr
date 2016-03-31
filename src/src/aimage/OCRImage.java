package aimage;

import ij.*;
import ij.process.ImageProcessor;

import java.util.ArrayList;


public class OCRImage {

    private ImagePlus img;
    private char label;
    private String path;
    private char decision;
    private ArrayList<Double> vect;

    public OCRImage(ImagePlus img, char label, String path) {
        this.img = img;
        this.label = label;
        this.path = path;
        decision = '?';
        vect = new ArrayList<>();

        this.resize(20, 20);

        /*int val=(int)(Math.random()*10.0);
        String i = Integer.toString(val);
        char a = i.charAt(0);
        this.decision=(char)a;*/
    }


    private double AverageNdg() {
        ImageProcessor imageProcessor = this.img.getProcessor();
        byte[] pixels = (byte[]) imageProcessor.getPixels();
        int total = 0;
        for (int i = 0; i < imageProcessor.getWidth(); i++) {
            for (int j = 0; j < imageProcessor.getHeight(); j++) {
                total += pixels[i * imageProcessor.getHeight() + j] & 0xff;
            }
        }
        return total / pixels.length;
    }

    public void setFeatureNdg() {
        vect.add(this.AverageNdg());
    }

    public void setFeatureProfilHV() {
        setFeatureProfilH();
        setFeatureProfilV();
    }


    public void setFeatureRapportIso() {
        rapportIso();
    }

    private void setFeatureProfilV() {
        ImageProcessor ip = this.img.getProcessor();
        byte[] pixels = (byte[]) ip.getPixels();
        double total = 0;
        for (int i = 0; i < ip.getWidth(); i++) {
            for (int j = 0; j < ip.getHeight(); j++) {
                total += pixels[i * ip.getHeight() + j] & 0xff;
            }
            this.vect.add(total / ip.getHeight());
            total = 0;
        }
    }

    private void setFeatureProfilH() {
        ImageProcessor ip = this.img.getProcessor();
        byte[] pixels = (byte[]) ip.getPixels();
        double total = 0;
        for (int i = 0; i < ip.getHeight(); i++) {
            for (int j = 0; j < ip.getWidth(); j++) {
                total += pixels[i * ip.getWidth() + j] & 0xff;
            }
            this.vect.add(total / ip.getWidth());
            total = 0;
        }
    }

    public void rapportIso() {
        double perimeter = 0;
        double surface = 0;

        ImageProcessor ip = this.img.getProcessor();
        byte[] pixels = (byte[]) ip.getPixels();
        for (int i = 0; i < ip.getHeight(); i++) {
            for (int j = 0; j < ip.getWidth(); j++) {
                int pix = pixels[i * ip.getWidth() + j] & 0xff;
                if (pix > 127) {
                    pixels[i * ip.getWidth() + j] = (byte) 0;
                } else {
                    pixels[i * ip.getWidth() + j] = (byte) 255;
                }
            }
        }
        for (int i = 1; i < ip.getWidth() - 1; i++) {
            for (int j = 1; j < ip.getHeight() - 1; j++) {
                int top = pixels[i + ip.getWidth() * j - ip.getWidth()] & 0xff;
                int right = pixels[i + ip.getWidth() * j + 1] & 0xff;
                int bottom = pixels[i + ip.getWidth() * j + ip.getWidth()] & 0xff;
                int left = pixels[i + ip.getWidth() * j - 1] & 0xff;
                int middle = pixels[i + ip.getWidth() * j] & 0xff;
                if (middle == 0) {
                    if (top == 255 || bottom == 255 || right == 255 || left == 255) {
                        perimeter++;
                    }
                    surface++;
                }
            }
        }
        this.vect.add(perimeter / (4 * Math.PI * surface));
    }

    private void resize(int larg, int haut) {
        ImageProcessor ip2 = img.getProcessor();
        ip2.setInterpolate(true);
        ip2 = ip2.resize(larg, haut);
        this.img.setProcessor(null, ip2);
    }

    //Zoning trop complexe, revu ci-dessous
    /*public void zoning()
    {
        ImageProcessor ip = this.img.getProcessor();
        byte[] pixels = (byte[]) ip.getPixels();
        //Détermination de la taille des blocs
        int size = ip.getWidth()/3;
        for(int i = 0; i != 3; i++) //Beaucoup trop de complexité, à revoir
        {
            for(int u = 0; u != 3 ;u++)
            {
                double total = 0;
                for(int x = 0; x != size; x++)
                {
                    for(int y = 0; y != size; y++)
                    {
                        total += pixels[i*size+x*size+y] & 0xff;
                    }
                }
                IJ.showMessage("Moyenne Bloc "+Integer.toString(i)+","+Integer.toString(u)+":"+Double.toString(total / (size * size)));
                vect.add(total / (size * size));
            }
        }

    }*/

    public void zoning() {
        ImageProcessor ip = img.getProcessor();
        byte[] pixels = (byte[]) ip.getPixels();
        ArrayList<ArrayList<Byte>> data = new ArrayList<>(16);
        for (int i = 0; i < 16; i++) {
            data.add(new ArrayList<>());
        }
        for (int i = 0; i < ip.getHeight(); i++) {
            for (int j = 0; j < ip.getWidth(); j++) {
                int x = j / (ip.getWidth() / 4);
                int y = i / (ip.getHeight() / 4);
                int id = x + y * 4;
                if (id < 16) {
                    data.get(id).add(pixels[i * ip.getWidth() + j]);
                }
            }
        }
        for (int i = 0; i < data.size(); i++) {
            double total = 0;
            for (int j = 0; j < data.get(i).size(); j++) {
                total += data.get(i).get(j) & 0xff;
            }
            vect.add(total / data.get(i).size());
        }
    }


    public ImagePlus getImg() {
        return img;
    }

    public void setImg(ImagePlus img) {
        this.img = img;
    }

    public void setVect(int i, double val) {
        vect.set(i, val);
    }

    public Double getVect(int i) {
        return vect.get(i);
    }

    public ArrayList<Double> getVect() {
        return this.vect;
    }

    public char getLabel() {
        return label;
    }

    public char getDecision() {
        return decision;
    }

    public void setDecision(char decision) {
        this.decision = decision;
    }
}






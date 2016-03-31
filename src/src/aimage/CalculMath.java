package aimage;

import ij.IJ;

import java.util.ArrayList;

/**
 * Created by Clovis on 26/03/2016.
 */
public class CalculMath {

    public static double distEucli(ArrayList<Double> vect1, ArrayList<Double> vect2) throws Exception {
        if (vect1.size() == vect2.size()) {
            double res = 0;
            for (int i = 0; i < vect1.size(); i++) {
                res += Math.abs(vect1.get(i) - vect2.get(i));
            }
            return res;
        }
        throw new Exception("Les deux vecteurs doivent être de même taille.");
    }

    public static int PPV(ArrayList<Double> vect, ArrayList<ArrayList<Double>> tabVect, int except) {

        int index = -1;
        double min = -1;

        for (int i = 0; i < tabVect.size(); i++) {
            if(i != except)
            {
                try {
                    double d = CalculMath.distEucli(tabVect.get(i), vect);
                    if (d < min || min == -1) {
                        min = d;
                        index = i;
                    }
                }
                catch (Exception e)
                {
                    IJ.showMessage("Comparaison impossible: " + e.getMessage());
                }
            }
        }

        return index;
    }
}

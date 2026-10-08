// Author: Liam Shum
import java.util.ArrayList;

/*
This program is used to classify an email and returns a string saying if it is spam or notSpam.
It takes in Email e and the ArrayList that contains both spam and notSpam models
*/
public class Classify {
    public static int classifyLabelFromFeatures(ArrayList<Integer> emailFeatures, ArrayList<ArrayList<Double>> models) {
        double notspamdistance = DistanceCalculator.findDistance(emailFeatures, models.get(0));
        double spamdistance = DistanceCalculator.findDistance(emailFeatures, models.get(1));

        if (spamdistance < notspamdistance) {
            return 1;
        }

        return 0;
    }

    public static int classifyLabel(Email e, ArrayList<ArrayList<Double>> models) {
        ArrayList<Integer> emailFeatures = FeatureExtractor.extractFeatures(e.getText());
        ArrayList<Double> emailFeaturesDouble = new ArrayList<>();
        for (int feature : emailFeatures) {
            emailFeaturesDouble.add((double) feature);
        }
        e.setFeatures(emailFeaturesDouble);
        return classifyLabelFromFeatures(emailFeatures, models);
    }

    public static String classify(Email e, ArrayList<ArrayList<Double>> models) {
        int predictedLabel = classifyLabel(e, models);
        if (predictedLabel == 1) {
            return "This email is classified as spam.";
        } else {
            return "This email is classified as not spam.";
        }
    }
}

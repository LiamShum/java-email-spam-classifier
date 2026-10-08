// Author: Liam Shum
import java.util.ArrayList;

/**
 * This program will take in the ArrayList<Double> features of a specific email as well as the ArrayList<Double> of
 * one of the models. It will then calculate the distance between it and return it as a double which determines how similar the email's 
 * features are to the specific model. This program uses the Euclidean distance equation.
 */
public class DistanceCalculator {
    // emailFeatures is the features of the email, Model is the specific model inputted (spam or notspam model)
    public static double findDistance(ArrayList<Integer> emailFeatures, ArrayList<Double> Model) {
        double sum = 0.0;

        if (emailFeatures.size() != Model.size()) {
            throw new IllegalArgumentException("Feature lists must be the same size");
        }

        double[] weights = FeatureExtractor.getActiveFeatureWeights();

        for (int i = 0; i < emailFeatures.size(); i++) {
            double diff = (emailFeatures.get(i) - Model.get(i)) / (Model.get(i) + 1);
            sum += weights[i] * diff * diff;
        }

        return Math.sqrt(sum);
    } 
}

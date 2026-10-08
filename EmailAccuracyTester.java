// Author: Liam Shum
import java.util.ArrayList;

/**
 * This program tests each email in the CSV file by comparing the classifier's
 * prediction to the email's real label, then reports the overall accuracy.
 */
public class EmailAccuracyTester {
    public static void main(String[] args) throws Exception {
        testDatasetAccuracy("spam_or_not_spam.csv");
    }

    public static void testDatasetAccuracy(String filename) throws Exception {
        ArrayList<ArrayList<Email>> csvEmails = DatasetReader.readFile(filename);
        ArrayList<ArrayList<Email>> trainingEmails = ModelBuilder.getTrainingSplit(csvEmails);
        ArrayList<ArrayList<Email>> testingEmails = ModelBuilder.getTestingSplit(csvEmails);
        ArrayList<ArrayList<Double>> models = ModelBuilder.buildModel(trainingEmails);

        ArrayList<Email> allEmails = new ArrayList<>();
        allEmails.addAll(testingEmails.get(0));
        allEmails.addAll(testingEmails.get(1));

        int correctCount = 0;
        int spamCorrect = 0;
        int notSpamCorrect = 0;
        int spamTotal = testingEmails.get(1).size();
        int notSpamTotal = testingEmails.get(0).size();

        for (Email email : allEmails) {
            String prediction = Classify.classify(email, models);
            int actualLabel = email.getLabel();
            int predictedLabel = prediction.contains("spam.") && !prediction.contains("not spam") ? 1 : 0;

            if (predictedLabel == actualLabel) {
                correctCount++;
                if (actualLabel == 1) {
                    spamCorrect++;
                } else {
                    notSpamCorrect++;
                }
            }
        }

        double accuracy = allEmails.isEmpty() ? 0.0 : (correctCount * 100.0) / allEmails.size();

        System.out.println("Training emails: " + (trainingEmails.get(0).size() + trainingEmails.get(1).size()));
        System.out.println("Testing emails: " + allEmails.size());
        System.out.println("Active features: " + FeatureExtractor.getActiveFeatureNames());
        System.out.println("Correct predictions: " + correctCount);
        System.out.println("Not spam accuracy: " + String.format("%.2f", notSpamTotal == 0 ? 0.0 : (notSpamCorrect * 100.0) / notSpamTotal) + "%");
        System.out.println("Spam accuracy: " + String.format("%.2f", spamTotal == 0 ? 0.0 : (spamCorrect * 100.0) / spamTotal) + "%");
        System.out.println("Accuracy: " + String.format("%.2f", accuracy) + "%");
    }
}

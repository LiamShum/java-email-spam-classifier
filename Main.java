// Author: Liam Shum
import java.util.ArrayList;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) throws Exception {
        ArrayList<ArrayList<Email>> csvEmails = DatasetReader.readFile("spam_or_not_spam.csv");
        ArrayList<ArrayList<Email>> trainingEmails = ModelBuilder.getTrainingSplit(csvEmails);
        ArrayList<ArrayList<Double>> bothModels = ModelBuilder.buildModel(trainingEmails);
        System.out.println("Models have been created.");
        System.out.println("notSpam model average features: " + bothModels.get(0));
        System.out.println("Spam model average features: " + bothModels.get(1));
        System.out.println("Active learned features: " + FeatureExtractor.getActiveFeatureNames());

        ArrayList<Email> allEmails = new ArrayList<>();
        allEmails.addAll(trainingEmails.get(0));
        allEmails.addAll(trainingEmails.get(1));

        CSVWriter.writeEmailFeatures("email_features.csv", allEmails);
        CSVWriter.writeModelSummary("model_summary.csv", bothModels);

        Scanner sc = new Scanner(System.in);
        System.out.println("Input the text of the email you would like to classify.");
        String emailText = sc.nextLine();
        Email inputEmail = new Email(emailText,0);

        System.out.println("Calculating...");
        System.out.println();
        System.out.println(Classify.classify(inputEmail, bothModels));

        System.out.println();
        ArrayList<Double> f = inputEmail.getFeatures();
        System.out.println("Extracted features:");
        ArrayList<String> featureNames = FeatureExtractor.getActiveFeatureNames();
        for (int i = 0; i < f.size(); i++) {
            System.out.println(featureNames.get(i) + ": " + f.get(i));
        }
    }
}

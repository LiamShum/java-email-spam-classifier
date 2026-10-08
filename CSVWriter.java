// Author: Liam Shum
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CSVWriter {

    public static void writeEmailFeatures(String filename, ArrayList<Email> emails) {
        try {
            FileWriter writer = new FileWriter(filename);
            ArrayList<String> featureNames = FeatureExtractor.getActiveFeatureNames();

            for (int i = 0; i < featureNames.size(); i++) {
                writer.write(featureNames.get(i) + ",");
            }
            writer.write("label\n");

            for (Email e : emails) {
                ArrayList<Double> f = e.getFeatures();

                for (int i = 0; i < f.size(); i++) {
                    writer.write(f.get(i) + ",");
                }

                writer.write(e.getLabel() + "\n");
            }

            writer.close();
            System.out.println("Email features saved to " + filename);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeModelSummary(String filename, ArrayList<ArrayList<Double>> models) {
        try {
            FileWriter writer = new FileWriter(filename);
            ArrayList<String> featureNames = FeatureExtractor.getActiveFeatureNames();

            writer.write("model,");
            for (int i = 0; i < featureNames.size(); i++) {
                writer.write(featureNames.get(i));
                if (i < featureNames.size() - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");

            writer.write("notSpam,");
            for (int i = 0; i < models.get(0).size(); i++) {
                writer.write(models.get(0).get(i).toString());
                if (i < models.get(0).size() - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");

            writer.write("spam,");
            for (int i = 0; i < models.get(1).size(); i++) {
                writer.write(models.get(1).get(i).toString());
                if (i < models.get(1).size() - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");

            writer.close();
            System.out.println("Model summary saved to " + filename);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Author: Liam Shum
// imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
/**\
 * This program takes in the ArrayList of two ArrayLists that contains both the spam and notSpam arraylists
 * It will then calculate the average of all the features of both the spam and notSpam arraylists and 
 * put the averages in two ArrayLists and return it as an ArrayList that contains both models
 */
public class ModelBuilder {
    public static ArrayList<ArrayList<Double>> buildModel(ArrayList<ArrayList<Email>> result) {
        learnDatasetFeaturesFast(result);

        ArrayList<Email> notSpam = result.get(0);
        ArrayList<Email> Spam = result.get(1);

        for(Email email : notSpam) {
            ArrayList<Integer> features = FeatureExtractor.extractFeatures(email.getText());
            ArrayList<Double> DoubleFeatures = new ArrayList<>();
            for(int feature : features) {
                DoubleFeatures.add((double)feature);
            }
            email.setFeatures(DoubleFeatures);
        }

        for(Email email : Spam) {
            ArrayList<Integer> features = FeatureExtractor.extractFeatures(email.getText());
            if (features == null || features.size() != FeatureExtractor.getActiveFeatureNames().size()) {
                features = new ArrayList<>();
                for (int i = 0; i < FeatureExtractor.getActiveFeatureNames().size(); i++) {
                    features.add(0);
                }
            }
            ArrayList<Double> DoubleFeatures = new ArrayList<>();
            for(int feature : features) {
                DoubleFeatures.add((double)feature);
            }
            email.setFeatures(DoubleFeatures);
        }

        ArrayList<Double> learnedWeights = learnFeatureWeights(notSpam, Spam);
        FeatureExtractor.setActiveFeatureWeights(learnedWeights);

        ArrayList<Double> notSpamAverageFeatures = getAverage(notSpam);
        ArrayList<Double> SpamAverageFeatures = getAverage(Spam);
        ArrayList<ArrayList<Double>> avgFeatures = new ArrayList<>();
        avgFeatures.add(notSpamAverageFeatures);
        avgFeatures.add(SpamAverageFeatures);
        return avgFeatures;
    }

    public static ArrayList<ArrayList<Double>> buildModelWithSearch(ArrayList<ArrayList<Email>> result) {
        learnDatasetFeatures(result);

        ArrayList<Email> notSpam = result.get(0);
        ArrayList<Email> Spam = result.get(1);

        for(Email email : notSpam) {
            ArrayList<Integer> features = FeatureExtractor.extractFeatures(email.getText());
            ArrayList<Double> DoubleFeatures = new ArrayList<>();
            for(int feature : features) {
                DoubleFeatures.add((double)feature);
            }
            email.setFeatures(DoubleFeatures);
        }

        for(Email email : Spam) {
            ArrayList<Integer> features = FeatureExtractor.extractFeatures(email.getText());
            if (features == null || features.size() != FeatureExtractor.getActiveFeatureNames().size()) {
                features = new ArrayList<>();
                for (int i = 0; i < FeatureExtractor.getActiveFeatureNames().size(); i++) {
                    features.add(0);
                }
            }
            ArrayList<Double> DoubleFeatures = new ArrayList<>();
            for(int feature : features) {
                DoubleFeatures.add((double)feature);
            }
            email.setFeatures(DoubleFeatures);
        }

        ArrayList<Double> learnedWeights = learnFeatureWeights(notSpam, Spam);
        FeatureExtractor.setActiveFeatureWeights(learnedWeights);

        ArrayList<Double> notSpamAverageFeatures = getAverage(notSpam);
        ArrayList<Double> SpamAverageFeatures = getAverage(Spam);
        ArrayList<ArrayList<Double>> avgFeatures = new ArrayList<>();
        avgFeatures.add(notSpamAverageFeatures);
        avgFeatures.add(SpamAverageFeatures);
        return avgFeatures;
    }

    public static ArrayList<Double> getAverage(ArrayList<Email> emails) {
        int count = 0;
        ArrayList<Double> emailAverageFeatures = new ArrayList<>();

        if (emails.isEmpty()) {
            return emailAverageFeatures;
        }

        int featureCount = emails.get(0).getFeatures().size();
        double[] sums = new double[featureCount];

        for(Email email : emails) {
            for (int i = 0; i < featureCount; i++) {
                sums[i] += email.getFeatures().get(i);
            }
            count++;
        }

        for (int i = 0; i < featureCount; i++) {
            emailAverageFeatures.add(count == 0 ? 0 : sums[i] / count);
        }

        return emailAverageFeatures;
    }

    public static ArrayList<ArrayList<Email>> getTrainingSplit(ArrayList<ArrayList<Email>> result) {
        ArrayList<ArrayList<Email>> split = new ArrayList<>();
        split.add(new ArrayList<>());
        split.add(new ArrayList<>());

        for (int label = 0; label < 2; label++) {
            ArrayList<Email> emails = result.get(label);
            int midpoint = emails.size() / 2;
            for (int i = 0; i < midpoint; i++) {
                split.get(label).add(emails.get(i));
            }
        }

        return split;
    }

    public static ArrayList<ArrayList<Email>> getTestingSplit(ArrayList<ArrayList<Email>> result) {
        ArrayList<ArrayList<Email>> split = new ArrayList<>();
        split.add(new ArrayList<>());
        split.add(new ArrayList<>());

        for (int label = 0; label < 2; label++) {
            ArrayList<Email> emails = result.get(label);
            int midpoint = emails.size() / 2;
            for (int i = midpoint; i < emails.size(); i++) {
                split.get(label).add(emails.get(i));
            }
        }

        return split;
    }

    private static void learnDatasetFeatures(ArrayList<ArrayList<Email>> result) {
        ArrayList<ArrayList<Email>> subTraining = splitHalf(result, true);
        ArrayList<ArrayList<Email>> validation = splitHalf(result, false);

        HashMap<String, Integer> spamWordCounts = new HashMap<>();
        HashMap<String, Integer> notSpamWordCounts = new HashMap<>();
        HashMap<String, Integer> spamPhraseCounts = new HashMap<>();
        HashMap<String, Integer> notSpamPhraseCounts = new HashMap<>();

        collectCounts(subTraining.get(1), spamWordCounts, spamPhraseCounts);
        collectCounts(subTraining.get(0), notSpamWordCounts, notSpamPhraseCounts);

        int[] spamWordOptions = {10, 15, 20, 25};
        int[] notSpamWordOptions = {0, 5, 10, 15};
        int[] spamPhraseOptions = {5, 10, 15, 20};
        int[] notSpamPhraseOptions = {0, 5, 10, 15};

        double bestAccuracy = -1.0;
        int bestSpamWordCount = 15;
        int bestNotSpamWordCount = 5;
        int bestSpamPhraseCount = 10;
        int bestNotSpamPhraseCount = 5;

        for (int spamWordCount : spamWordOptions) {
            for (int notSpamWordCount : notSpamWordOptions) {
                for (int spamPhraseCount : spamPhraseOptions) {
                    for (int notSpamPhraseCount : notSpamPhraseOptions) {
                        ArrayList<String> topSpamWords = selectTopTokens(spamWordCounts, notSpamWordCounts, spamWordCount, 4);
                        ArrayList<String> topNotSpamWords = selectTopTokens(notSpamWordCounts, spamWordCounts, notSpamWordCount, 4);
                        ArrayList<String> topSpamPhrases = selectTopTokens(spamPhraseCounts, notSpamPhraseCounts, spamPhraseCount, 3);
                        ArrayList<String> topNotSpamPhrases = selectTopTokens(notSpamPhraseCounts, spamPhraseCounts, notSpamPhraseCount, 3);

                        FeatureExtractor.configureLearnedFeatures(topSpamWords, topNotSpamWords, topSpamPhrases, topNotSpamPhrases);
                        setEmailFeatures(subTraining.get(0));
                        setEmailFeatures(subTraining.get(1));
                        ArrayList<Double> learnedWeights = learnFeatureWeights(subTraining.get(0), subTraining.get(1));
                        FeatureExtractor.setActiveFeatureWeights(learnedWeights);

                        double score = evaluateScore(subTraining, validation);
                        if (score > bestAccuracy) {
                            bestAccuracy = score;
                            bestSpamWordCount = spamWordCount;
                            bestNotSpamWordCount = notSpamWordCount;
                            bestSpamPhraseCount = spamPhraseCount;
                            bestNotSpamPhraseCount = notSpamPhraseCount;
                        }
                    }
                }
            }
        }

        HashMap<String, Integer> fullSpamWordCounts = new HashMap<>();
        HashMap<String, Integer> fullNotSpamWordCounts = new HashMap<>();
        HashMap<String, Integer> fullSpamPhraseCounts = new HashMap<>();
        HashMap<String, Integer> fullNotSpamPhraseCounts = new HashMap<>();

        collectCounts(result.get(1), fullSpamWordCounts, fullSpamPhraseCounts);
        collectCounts(result.get(0), fullNotSpamWordCounts, fullNotSpamPhraseCounts);

        ArrayList<String> bestSpamWords = selectTopTokens(fullSpamWordCounts, fullNotSpamWordCounts, bestSpamWordCount, 4);
        ArrayList<String> bestNotSpamWords = selectTopTokens(fullNotSpamWordCounts, fullSpamWordCounts, bestNotSpamWordCount, 4);
        ArrayList<String> bestSpamPhrases = selectTopTokens(fullSpamPhraseCounts, fullNotSpamPhraseCounts, bestSpamPhraseCount, 3);
        ArrayList<String> bestNotSpamPhrases = selectTopTokens(fullNotSpamPhraseCounts, fullSpamPhraseCounts, bestNotSpamPhraseCount, 3);

        FeatureExtractor.configureLearnedFeatures(bestSpamWords, bestNotSpamWords, bestSpamPhrases, bestNotSpamPhrases);
    }

    private static void learnDatasetFeaturesFast(ArrayList<ArrayList<Email>> result) {
        HashMap<String, Integer> spamWordCounts = new HashMap<>();
        HashMap<String, Integer> notSpamWordCounts = new HashMap<>();
        HashMap<String, Integer> spamPhraseCounts = new HashMap<>();
        HashMap<String, Integer> notSpamPhraseCounts = new HashMap<>();

        collectCounts(result.get(1), spamWordCounts, spamPhraseCounts);
        collectCounts(result.get(0), notSpamWordCounts, notSpamPhraseCounts);

        ArrayList<String> bestSpamWords = selectTopTokens(spamWordCounts, notSpamWordCounts, 20, 4);
        ArrayList<String> bestNotSpamWords = selectTopTokens(notSpamWordCounts, spamWordCounts, 10, 4);
        ArrayList<String> bestSpamPhrases = selectTopTokens(spamPhraseCounts, notSpamPhraseCounts, 15, 3);
        ArrayList<String> bestNotSpamPhrases = selectTopTokens(notSpamPhraseCounts, spamPhraseCounts, 5, 3);

        FeatureExtractor.configureLearnedFeatures(bestSpamWords, bestNotSpamWords, bestSpamPhrases, bestNotSpamPhrases);
    }

    private static ArrayList<ArrayList<Email>> splitHalf(ArrayList<ArrayList<Email>> result, boolean firstHalf) {
        ArrayList<ArrayList<Email>> split = new ArrayList<>();
        split.add(new ArrayList<>());
        split.add(new ArrayList<>());

        for (int label = 0; label < 2; label++) {
            ArrayList<Email> emails = result.get(label);
            int midpoint = emails.size() / 2;
            int start = firstHalf ? 0 : midpoint;
            int end = firstHalf ? midpoint : emails.size();
            for (int i = start; i < end; i++) {
                split.get(label).add(emails.get(i));
            }
        }

        return split;
    }

    private static void setEmailFeatures(ArrayList<Email> emails) {
        for (Email email : emails) {
            ArrayList<Integer> features = FeatureExtractor.extractFeatures(email.getText());
            ArrayList<Double> doubleFeatures = new ArrayList<>();
            for (int feature : features) {
                doubleFeatures.add((double) feature);
            }
            email.setFeatures(doubleFeatures);
        }
    }

    private static double evaluateScore(ArrayList<ArrayList<Email>> training, ArrayList<ArrayList<Email>> validation) {
        ArrayList<Double> notSpamAverageFeatures = getAverage(training.get(0));
        ArrayList<Double> spamAverageFeatures = getAverage(training.get(1));
        ArrayList<ArrayList<Double>> models = new ArrayList<>();
        models.add(notSpamAverageFeatures);
        models.add(spamAverageFeatures);

        int correct = 0;
        int total = 0;
        int spamCorrect = 0;
        int spamTotal = 0;

        for (int label = 0; label < 2; label++) {
            for (Email email : validation.get(label)) {
                ArrayList<Integer> features = featuresFromEmail(email);
                int predictedLabel = Classify.classifyLabelFromFeatures(features, models);
                if (predictedLabel == email.getLabel()) {
                    correct++;
                    if (email.getLabel() == 1) {
                        spamCorrect++;
                    }
                }
                if (email.getLabel() == 1) {
                    spamTotal++;
                }
                total++;
            }
        }

        if (total == 0) {
            return 0.0;
        }

        double overallAccuracy = (correct * 100.0) / total;
        double spamAccuracy = spamTotal == 0 ? 0.0 : (spamCorrect * 100.0) / spamTotal;
        return (overallAccuracy * 0.6) + (spamAccuracy * 0.4);
    }

    private static ArrayList<Integer> featuresFromEmail(Email email) {
        if (email.getFeatures().size() != FeatureExtractor.getActiveFeatureNames().size()) {
            ArrayList<Integer> extractedFeatures = FeatureExtractor.extractFeatures(email.getText());
            ArrayList<Double> doubleFeatures = new ArrayList<>();
            for (int feature : extractedFeatures) {
                doubleFeatures.add((double) feature);
            }
            email.setFeatures(doubleFeatures);
            return extractedFeatures;
        }

        ArrayList<Integer> features = new ArrayList<>();
        for (double feature : email.getFeatures()) {
            features.add((int) Math.round(feature));
        }
        return features;
    }

    private static ArrayList<Double> learnFeatureWeights(ArrayList<Email> notSpam, ArrayList<Email> spam) {
        ArrayList<Double> weights = new ArrayList<>();

        if (notSpam.isEmpty() || spam.isEmpty()) {
            for (int i = 0; i < FeatureExtractor.getActiveFeatureNames().size(); i++) {
                weights.add(1.0);
            }
            return weights;
        }

        ArrayList<Double> notSpamMeans = getAverage(notSpam);
        ArrayList<Double> spamMeans = getAverage(spam);
        ArrayList<Double> notSpamVariance = getVariance(notSpam, notSpamMeans);
        ArrayList<Double> spamVariance = getVariance(spam, spamMeans);

        for (int i = 0; i < notSpamMeans.size(); i++) {
            double meanDifference = Math.abs(spamMeans.get(i) - notSpamMeans.get(i));
            double pooledVariance = notSpamVariance.get(i) + spamVariance.get(i) + 0.25;
            double learnedWeight = (meanDifference + 0.1) / pooledVariance;

            if (learnedWeight < 0.05) {
                learnedWeight = 0.05;
            }

            weights.add(learnedWeight * 10.0);
        }

        return weights;
    }

    private static ArrayList<Double> getVariance(ArrayList<Email> emails, ArrayList<Double> means) {
        ArrayList<Double> variances = new ArrayList<>();

        if (emails.isEmpty()) {
            for (int i = 0; i < means.size(); i++) {
                variances.add(0.0);
            }
            return variances;
        }

        double[] sums = new double[means.size()];

        for (Email email : emails) {
            for (int i = 0; i < means.size(); i++) {
                double diff = email.getFeatures().get(i) - means.get(i);
                sums[i] += diff * diff;
            }
        }

        for (int i = 0; i < sums.length; i++) {
            variances.add(sums[i] / emails.size());
        }

        return variances;
    }


    private static void collectCounts(ArrayList<Email> emails, HashMap<String, Integer> wordCounts, HashMap<String, Integer> phraseCounts) {
        for (Email email : emails) {
            ArrayList<String> tokens = FeatureExtractor.tokenizeWords(email.getText());
            ArrayList<String> phrases = FeatureExtractor.buildBigrams(tokens);
            HashSet<String> uniqueTokens = new HashSet<>(tokens);
            HashSet<String> uniquePhrases = new HashSet<>(phrases);

            for (String token : uniqueTokens) {
                wordCounts.put(token, wordCounts.getOrDefault(token, 0) + 1);
            }

            for (String phrase : uniquePhrases) {
                phraseCounts.put(phrase, phraseCounts.getOrDefault(phrase, 0) + 1);
            }
        }
    }

    private static ArrayList<String> selectTopTokens(HashMap<String, Integer> primaryCounts,
                                                     HashMap<String, Integer> otherCounts,
                                                     int limit,
                                                     int minPrimaryCount) {
        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : primaryCounts.entrySet()) {
            int primary = entry.getValue();
            int other = otherCounts.getOrDefault(entry.getKey(), 0);
            int score = primary - other;

            if (primary >= minPrimaryCount && score > 0) {
                entries.add(Map.entry(entry.getKey(), score));
            }
        }

        Collections.sort(entries, Comparator.comparingInt(Map.Entry<String, Integer>::getValue).reversed());

        ArrayList<String> selected = new ArrayList<>();
        int max = Math.min(limit, entries.size());
        for (int i = 0; i < max; i++) {
            selected.add(entries.get(i).getKey());
        }

        return selected;
    }
}

// Author: Liam Shum
import java.util.ArrayList;
import java.util.HashMap;


public class FeatureExtractor {
    // initialization
    private static ArrayList<String> learnedSpamWords = new ArrayList<>();
    private static ArrayList<String> learnedNotSpamWords = new ArrayList<>();
    private static ArrayList<String> learnedSpamPhrases = new ArrayList<>();
    private static ArrayList<String> learnedNotSpamPhrases = new ArrayList<>();
    private static ArrayList<String> activeFeatureNames = new ArrayList<>();
    private static ArrayList<Double> activeFeatureWeights = new ArrayList<>();

    public static void configureLearnedFeatures(ArrayList<String> spamWords,
                                                ArrayList<String> notSpamWords,
                                                ArrayList<String> spamPhrases,
                                                ArrayList<String> notSpamPhrases) {
        learnedSpamWords = new ArrayList<>(spamWords);
        learnedNotSpamWords = new ArrayList<>(notSpamWords);
        learnedSpamPhrases = new ArrayList<>(spamPhrases);
        learnedNotSpamPhrases = new ArrayList<>(notSpamPhrases);

        activeFeatureNames = new ArrayList<>();
        activeFeatureWeights = new ArrayList<>();

        activeFeatureNames.add("link_count");
        activeFeatureWeights.add(5.0);
        activeFeatureNames.add("money_count");
        activeFeatureWeights.add(4.0);
        activeFeatureNames.add("uppercase_count");
        activeFeatureWeights.add(0.15);
        activeFeatureNames.add("number_count");
        activeFeatureWeights.add(0.5);

        for (String word : learnedSpamWords) {
            activeFeatureNames.add("spam_word_" + word);
            activeFeatureWeights.add(6.0);
        }

        for (String word : learnedNotSpamWords) {
            activeFeatureNames.add("not_spam_word_" + word);
            activeFeatureWeights.add(6.0);
        }

        for (String phrase : learnedSpamPhrases) {
            activeFeatureNames.add("spam_phrase_" + phrase.replace(" ", "_"));
            activeFeatureWeights.add(10.0);
        }

        for (String phrase : learnedNotSpamPhrases) {
            activeFeatureNames.add("not_spam_phrase_" + phrase.replace(" ", "_"));
            activeFeatureWeights.add(10.0);
        }
    }

    public static void setActiveFeatureWeights(ArrayList<Double> learnedWeights) {
        activeFeatureWeights = new ArrayList<>(learnedWeights);
    }

    public static ArrayList<String> getActiveFeatureNames() {
        return new ArrayList<>(activeFeatureNames);
    }

    public static double[] getActiveFeatureWeights() {
        double[] weights = new double[activeFeatureWeights.size()];
        for (int i = 0; i < activeFeatureWeights.size(); i++) {
            weights[i] = activeFeatureWeights.get(i);
        }
        return weights;
    }

    public static ArrayList<String> tokenizeWords(String text) {
        ArrayList<String> tokens = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            return tokens;
        }

        String[] words = text.split("\\s+");
        for (String word : words) {
            String clean = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            if (!clean.isEmpty()) {
                tokens.add(clean);
            }
        }

        return tokens;
    }

    public static ArrayList<String> buildBigrams(ArrayList<String> tokens) {
        ArrayList<String> phrases = new ArrayList<>();

        for (int i = 0; i < tokens.size() - 1; i++) {
            phrases.add(tokens.get(i) + " " + tokens.get(i + 1));
        }

        return phrases;
    }

    public static ArrayList<Integer> extractFeatures(String text) {
        ArrayList<Integer> extractedFeatures = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            for (int i = 0; i < activeFeatureNames.size(); i++) {
                extractedFeatures.add(0);
            }
            return extractedFeatures;
        }

        int uppercase_count = 0;
        int link_count = 0;
        int number_count = 0;
        int money_count = 0;

        String[] words = text.split("\\s+");
        ArrayList<String> tokens = tokenizeWords(text);
        ArrayList<String> phrases = buildBigrams(tokens);
        HashMap<String, Integer> wordCounts = buildCountMap(tokens);
        HashMap<String, Integer> phraseCounts = buildCountMap(phrases);

        for (String word : words) {
            String clean = word.replaceAll("[^a-zA-Z0-9]", "");
            String lower = clean.toLowerCase();

            if (!lower.startsWith("number") && !lower.startsWith("url")) {
                for (int n = 0; n < word.length(); n++) {
                    if (Character.isUpperCase(word.charAt(n))) {
                        uppercase_count++;
                    }
                }
            }

            if (word.equalsIgnoreCase("URL")) {
                link_count++;
            }

            if (word.equalsIgnoreCase("NUMBER")) {
                number_count++;
            }

            if (lower.equals("money") ||
                lower.equals("cash") ||
                lower.equals("price") ||
                lower.equals("loan") ||
                lower.equals("credit") ||
                lower.equals("refinance") ||
                lower.equals("rate") ||
                lower.equals("insurance") ||
                lower.equals("quote")) {
                money_count++;
            }
        }

        extractedFeatures.add(link_count);
        extractedFeatures.add(money_count);
        extractedFeatures.add(uppercase_count);
        extractedFeatures.add(number_count);

        for (String word : learnedSpamWords) {
            extractedFeatures.add(wordCounts.getOrDefault(word, 0));
        }

        for (String word : learnedNotSpamWords) {
            extractedFeatures.add(wordCounts.getOrDefault(word, 0));
        }

        for (String phrase : learnedSpamPhrases) {
            extractedFeatures.add(phraseCounts.getOrDefault(phrase, 0));
        }

        for (String phrase : learnedNotSpamPhrases) {
            extractedFeatures.add(phraseCounts.getOrDefault(phrase, 0));
        }

        return extractedFeatures;
    }

    private static HashMap<String, Integer> buildCountMap(ArrayList<String> items) {
        HashMap<String, Integer> counts = new HashMap<>();

        for (String item : items) {
            counts.put(item, counts.getOrDefault(item, 0) + 1);
        }

        return counts; // finds count of all words
    }
}

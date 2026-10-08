// Author: Liam Shum
// import
import java.util.ArrayList;
/**
 * This object acts as the Email object that
 * will be able to store each email with its text,
 * its label (0 or 1), and its certain features
 * label 0 = not spam
 * label 1 = spam
 */
public class Email {
    // attributes
    String text; // text of the email
    int label; // labeled as spam or not spam
    ArrayList<Double> features; // Arraylist of the features of each email

    // Constructor
    public Email(String text, int label) {
        this.text = text;
        this.label = label;
        this.features = new ArrayList();
    } // Email()

    // getters
    public int getLabel() {
        return label;
    }
    public String getText() {
        return text;
    }
    public ArrayList<Double> getFeatures() {
        return features;
    }
    
    // setters
    public void setFeatures(ArrayList<Double> features) {
        this.features = features;
    }
} // Email

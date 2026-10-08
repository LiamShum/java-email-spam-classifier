// Author: Liam Shum
// imports
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * This program will read through each email using a scanner and separating
 * each email in two parts, by its text and its label. It will then store the
 * email into the ArrayList emails.
 * Then ArrayList emails will be sorted into two ArrayLists by its label
 * lastly the two ArrayLists will be added to an ArrayList result
 */
public class DatasetReader {
    // Constructor that takes in a file and returns an ArrayList of two ArrayLists containing the spam or notSpa,
    public static ArrayList<ArrayList<Email>> readFile(String filename) throws Exception {

        // initialize arraylist that holds all emails
        ArrayList<Email> emails = new ArrayList<Email>();

        // Create scanner that will open and read the file
        Scanner sc = new Scanner(new File(filename));

        // This skips the header of the csv file 
        if(sc.hasNextLine()) {
            sc.nextLine();
        }
        
        // loops through each email, splitting them into two parts, and adding it to the ArrayList
        while(sc.hasNextLine()) {
            String line = sc.nextLine();

            // Creates integer of the index of where the last comma is in the csv file so it separates the email correctly from its label
            int lastComma = line.lastIndexOf(",");

            // Creates a string of the text of the email starting from index 0 to the last comma that separates it from the label
            String text = line.substring(0, lastComma);

            // Creates a string of the number that is the label of the email (0 or 1)
            String labelStr = line.substring(lastComma + 1);
            
            // turns the String labelStr into an integer and stores it into int label
            int label = Integer.parseInt(labelStr);

            // adds the email to the ArrayList emails
            emails.add(new Email(text, label));
        }

        // This will go through each email in the ArrayList, check if it is labled 0 or 1, then add the ones labeled 0 to notSpam and labeled 1 to Spam
        // then adds the two arrays to ArrayList results, index 0 is notSpam ArrayList, index 1 is Spam ArrayList.
        ArrayList<Email> Spam = new ArrayList<>();
        ArrayList<Email> notSpam = new ArrayList<>();
        for(Email email : emails) {
            if(email.getLabel() == 0) { // checks if email is 0, if it is, add to notSpam arraylist, otherwise, add to spam arraylist
                notSpam.add(email);
            }
            else {
                Spam.add(email);
            }
        }
        ArrayList<ArrayList<Email>> result = new ArrayList<>();
        result.add(notSpam);
        result.add(Spam);

        sc.close();

        // return ArrayList that contains the Spam and notSpam ArrayLists
        return result;


    } // readFile()
} // DatasetReader

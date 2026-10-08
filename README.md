# Java Email Spam Classifier

A command-line Java application that classifies email text as **spam** or **not spam**. It learns useful words, two-word phrases, feature weights, and average feature profiles from labeled examples, then predicts new labels using a custom weighted-distance algorithm.

The project uses the Java standard library, including `ArrayList`, `HashMap`, and `HashSet`, without an external machine-learning framework.

## How It Works

### 1. Load and split the dataset

The program reads email text and labels from a CSV file:

- `0` = not spam
- `1` = spam

It separates the emails by class, uses approximately the first half of each class for training, and reserves the remaining half for testing. The current split follows dataset order; it does not shuffle the emails.

### 2. Extract numerical features

Each email is converted into a list of counts. Four fixed features measure `URL` placeholder tokens, money-related words, uppercase characters, and `NUMBER` placeholder tokens.

The training process also selects up to 20 spam-associated words, 10 non-spam-associated words, 15 spam-associated two-word phrases, and 5 non-spam-associated phrases. Together with the fixed features, this produces up to **54 features** per email.

During feature selection, `HashSet` removes repetitions within each email so a token is counted once per email. Candidates are ranked by the difference in how many training emails contain them in each class, subject to minimum occurrence requirements. During feature extraction, `HashMap` counts occurrences of the selected words and phrases within an individual email.

### 3. Build the models

The program calculates an average feature profile for each class. It also learns feature weights using differences between class averages and the variation within each class.

### 4. Classify new text

A new email is processed using the same features. The classifier compares it with both average profiles using a weighted distance calculation that scales each difference by the model's feature value plus one.

The class with the smaller distance becomes the prediction. Equal distances return **not spam**.

## Project Structure

Keep the Java source files together in the project folder.

| File | Responsibility |
| --- | --- |
| `Main.java` | Trains the classifier, exports reports, and accepts one email through the terminal. |
| `Email.java` | Stores an email's text, label, and extracted features. |
| `DatasetReader.java` | Reads the CSV and groups emails by label. |
| `FeatureExtractor.java` | Cleans text, builds two-word phrases, and extracts feature counts. |
| `ModelBuilder.java` | Splits the dataset, selects features, learns weights, and calculates class averages. |
| `DistanceCalculator.java` | Calculates the custom weighted distance to a class profile. |
| `Classify.java` | Returns a predicted label or a readable classification message. |
| `EmailAccuracyTester.java` | Evaluates predictions on the held-out testing split. |
| `CSVWriter.java` | Exports training features and model averages. |

## Setup

You need a Java Development Kit (JDK) with both `java` and `javac` available in your terminal. No external libraries or build tools are required.

Place a labeled dataset named `spam_or_not_spam.csv` in the project folder. The reader expects a header row followed by one email per line, with its numeric label after the final comma.

Example format:

```csv
text,label
Please review the meeting agenda,0
Claim cash now at URL,1
```

This example illustrates the format only; use a larger dataset containing both classes for training and testing. Verify the dataset's original source and redistribution permissions before uploading the CSV to a public repository.

## Run the Classifier

Open a terminal in the project folder and compile the source files:

```bash
javac *.java
```

Start the interactive program:

```bash
java Main
```

When prompted, enter an email as a single line. For example:

```text
You may qualify for a cash loan. Visit URL for details.
```

The prediction depends on the training data. The program prints either `This email is classified as spam.` or `This email is classified as not spam.`, followed by the extracted feature names and values.

Run the program from the project folder so it can locate the dataset.

## Evaluate Accuracy

After compiling, run:

```bash
java EmailAccuracyTester
```

The tester independently trains a model and evaluates the held-out emails. It reports training and testing counts, active features, correct predictions, overall accuracy, and the percentage correctly identified within each class.

The outputs labeled `Spam accuracy` and `Not spam accuracy` represent **class-specific recall**. Results depend on the dataset and its ordering; no fixed accuracy percentage is claimed here.

## Generated Reports

Running `Main` creates or overwrites two CSV files in the working directory:

| Output | Contents |
| --- | --- |
| `email_features.csv` | Extracted feature values and known labels for the training emails. |
| `model_summary.csv` | Average feature values for the not-spam and spam models. |

## Current Limitations

The link and number counters recognize the placeholder tokens `URL` and `NUMBER`, not arbitrary web addresses or digit sequences. Raw email text needs additional preprocessing for those counters to work as intended.

The CSV reader expects single-line records and does not implement full quoted or multiline CSV parsing. The ordered train/test split may also produce different results depending on how the dataset is arranged.

The model is rebuilt on each run. The exported reports do not provide a complete saved-model loading workflow, and the program does not connect to a live inbox.

`ModelBuilder` also includes an optional `buildModelWithSearch()` method. Neither `Main` nor `EmailAccuracyTester` calls it; this README describes the default `buildModel()` workflow.

## Author

Liam Shum

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class runner {
    public static List<docLabel> readDocumentsFromFolder(String folderPath) {
        List<docLabel> documents = new ArrayList<>();
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();

        // Hardcoded mapping between filenames and labels
        Map<String, String> filenameToLabel = new HashMap<>();
        filenameToLabel.put("processed01.txt", "C1");
        filenameToLabel.put("processed02.txt", "C1");
        filenameToLabel.put("processed03.txt", "C1");
        filenameToLabel.put("processed04.txt", "C1");
        filenameToLabel.put("processed05.txt", "C4");
        filenameToLabel.put("processed06.txt", "C4");
        filenameToLabel.put("processed07.txt", "C7");
        filenameToLabel.put("processed08.txt", "C7");
        filenameToLabel.put("processed09.txt", "C1");
        filenameToLabel.put("processed10.txt", "C4");

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    try {
                        // Read the content of the file
                        String content = new String(Files.readAllBytes(file.toPath()));
                        // Look up the filename in the mapping to find the label
                        String label = filenameToLabel.get(file.getName());
                        if (label != null) {
                            documents.add(new docLabel(content, label));
                        } else {
                            // Handle the case where the filename is not in the mapping
                        }
                    } catch (IOException e) {
                        // Handle exception
                        e.printStackTrace();
                    }
                }
            }
        }
        return documents;
    }

    private static String getCategory(String str) {
        String res = "";
        if (str.contains("C1")) {
            res = "Airline Safety";
        }
        else if (str.contains("C4")) {
            res = "Hoof and Mouth Disease";
        }
        else if (str.contains("C7")) {
            res = "Mortgage Rates";
        }
        return res;
    }

    public static List<List<docLabel>> partitionIntoFolds(List<docLabel> documents, int numFolds) {
        List<List<docLabel>> folds = new ArrayList<>();
        int foldSize = documents.size() / numFolds;
    
        for (int i = 0; i < numFolds; i++) {
            int startIndex = i * foldSize;
            int endIndex = (i + 1) * foldSize;
    
            // Handle the last fold if the number of documents is not evenly divisible by numFolds
            if (i == numFolds - 1 && documents.size() % numFolds != 0) {
                endIndex = documents.size();
            }
    
            List<docLabel> fold = new ArrayList<>(documents.subList(startIndex, endIndex));
            folds.add(fold);
        }
    
        return folds;
    }
    

    public static double crossValidation(List<docLabel> documents, int k, String distanceMetric, boolean logAddOne) {
        List<List<docLabel>> folds = partitionIntoFolds(documents, 10);
    
        int totalTests = 0;
        int score = 0; // Initialize score
    
        for (int i = 0; i < folds.size(); i++) {
            List<docLabel> trainingData = new ArrayList<>();
            for (int j = 0; j < folds.size(); j++) {
                if (i != j) trainingData.addAll(folds.get(j));
            }
            List<docLabel> testData = folds.get(i);
    
            kNN kNN = new kNN(trainingData); // Using the provided logAddOne
    
            // Test on each document in the test fold
            for (docLabel testDoc : testData) {
                String test_label = kNN.classifyDocument(testDoc.doc, k, distanceMetric);
                // Check if the classified label matches the actual label
                if (test_label.equals(testDoc.label)) {
                    score += 1; // Increment score if the labels match
                }
                totalTests += 1; // Increment the total number of tests
            }
        }
    
        return (double) score / totalTests * 100; // Calculate and return accuracy
    }
    
    public static void main(String[] args) {
        List<docLabel> documents = readDocumentsFromFolder("../data/processed");
        // Get performance % for each k
        
        // for (int i = 1; i < 10; i++) {
        //     double accuracy = crossValidation(documents, i, distanceMetric, logAddOne);
        //     System.out.println("k: " + i);
        //     System.out.println("Accuracy: " + accuracy + "%");
        // }
        
        // customizable
        String distanceMetric = "ncd"; // "cos" for cosine, "euc" for euclidean, "ncd" for normalized compression distance
        int k = 6; // must be 0 < k < 10

        // Classifies test document
        String test_document = """
        Airlines airline I am a safe hoof and mouth.
            """;
        kNN kNN = new kNN(documents);
        var test_label = kNN.classifyDocument(test_document, k, distanceMetric);
        System.out.println("label: " + test_label + " (" + getCategory(test_label) + ")");
    }
}
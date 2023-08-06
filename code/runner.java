import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class runner {
    private static double[][] confusionMatrix = new double[3][3];

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
        var res = "";
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

    private static int getClassIndex(String label) {
        switch (label) {
            case "C1":
                return 0;
            case "C4":
                return 1;
            case "C7":
                return 2;
            default:
                throw new IllegalArgumentException("Unexpected label: " + label);
        }
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

    public static double crossValidation(List<docLabel> documents, int k, String distanceMetric) {
        List<List<docLabel>> folds = partitionIntoFolds(documents, 10);
        int totalTests = 0;
        int score = 0; 
    
        for (int i = 0; i < folds.size(); i++) {
            List<docLabel> trainingData = new ArrayList<>();
            for (int j = 0; j < folds.size(); j++) {
                if (i != j) trainingData.addAll(folds.get(j));
            }
            List<docLabel> testData = folds.get(i);
    
            kNN kNN = new kNN(trainingData);
    
            // Test on each document in the test fold
            for (docLabel testDoc : testData) {
                String test_label = kNN.classifyDocument(testDoc.doc, k, distanceMetric);
                // Check if the classified label matches the actual label
                int actualClass = getClassIndex(testDoc.label);
                int predictedClass = getClassIndex(test_label);
                confusionMatrix[actualClass][predictedClass] += 1.0;
                if (test_label.equals(testDoc.label)) {
                    score += 1; // Increment score if the labels match
                }
                totalTests += 1; // Increment the total number of tests
            }
        }
    
        return (double) score / totalTests * 100; // Calculate and return accuracy
    }
    
    
    // public static String preprocessData(String filePath, String stopWordPath){
    //     preprocessing.stopWordDoc(stopWordPath);
    //     List<String> fileStrings = preprocessing.reader(filePath);
    //     String outputString = String.join(" ", fileStrings);
    //     return outputString;
    // }

    private static String getStringFromFilePath(String filepath) {
        try {
            return new String(Files.readAllBytes(new File(filepath).toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static void printFuzzy(Map<String, Double> fuzzyResult) {
        Map<String, Double> sortedFuzzyRes = fuzzyResult.entrySet()
        .stream()
        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
        .collect(Collectors.toMap(
            Map.Entry::getKey, 
            Map.Entry::getValue, 
            (oldValue, newValue) -> oldValue, 
            LinkedHashMap::new));

        for (Map.Entry<String, Double> entry : sortedFuzzyRes.entrySet()) {
            var c = entry.getKey();
            var category = getCategory(entry.getKey());
            var percent = entry.getValue();
            System.out.printf(c + " (" + category + "): " + "%.2f%%" + "\n", percent);
        }
    }
   
    private static void printConfusionMatrix(double[][] matrix, String[] classNames) {
        if (matrix.length != classNames.length || matrix[0].length != classNames.length) {
            System.out.println("Error: Matrix dimensions do not match class names.");
            return;
        }
        System.out.print("\t");
        for (String className : classNames) {
            System.out.print(className + "\t");
        }
        System.out.println();
    
        for (int i = 0; i < matrix.length; i++) {
            System.out.print(classNames[i] + "\t");
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.printf("%.2f\t", matrix[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void printPerformance(List<docLabel> database, String distanceMetric) {
        for (int i = 1; i < 10; i++) {
            double accuracy = crossValidation(database, i, distanceMetric);
            System.out.println("k: " + i);
            System.out.println("Accuracy: " + accuracy + "%");
        }
    }

    private static void printConfusionMetrics(double[][] matrix) {
        double truePos = 0;
        double falsePos = 0;
        double falseNeg = 0;
        double trueNeg = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (i == j) truePos += matrix[i][j];
                else {
                    falsePos += matrix[i][j];
                    falseNeg += matrix[j][i];
                }
            }
            for (int k = 0; k < matrix.length; k++) {
                if (k != i) {
                    for (int l = 0; l < matrix.length; l++) {
                        if (l != i) trueNeg += matrix[k][l];
                    } 
                }
            }
        }
        double accuracy = 100 * (truePos + trueNeg) / (truePos + falsePos + falseNeg + trueNeg);
        double precision = 100 * truePos / (truePos + falsePos);
        double recall = 100 * truePos / (truePos + falseNeg);
        double F1 = (precision + recall == 0) ? 0 : 2 * precision * recall / (precision + recall);

        System.out.printf("Accuracy: %.2f%%\n", accuracy);
        System.out.printf("Precision: %.2f%%\n", precision);
        System.out.printf("Recall: %.2f%%\n", recall);
        System.out.printf("F1 Score: %.2f\n", F1);
    }

    private static void testValidInput(int k, String distanceMetric, String filepath) {
        if (k < 1 || k > 9) {
            System.out.println("Invalid k value");
            System.exit(1);
        }
        
        var test_data = getStringFromFilePath(filepath);
        if (test_data == null) {
            System.out.println("File not found");
            System.exit(1);
        }

        if (!(distanceMetric.equals("man") || 
        distanceMetric.equals("cos") || 
        distanceMetric.equals("euc") || 
        distanceMetric.equals("ncd"))) {
            System.out.println("Invalid distance metric. expected man, cos, euc, or ncd.");
            System.exit(1);
        }
    }
    public static void main(String[] args) {
        List<docLabel> database = readDocumentsFromFolder("../data/processed");
        kNN kNN = new kNN(database);

        String distanceMetric = args[0];
        int k = Integer.parseInt(args[1]);
        String filepath = args[2];

        // Get performance % for each k given a distanceMetric - doesn't classify your document
        System.out.println("Printing accuracy per k value...");
        printPerformance(database, distanceMetric);

        // Confusion Matrix is generated from cross-validation, not classifying your document
        String[] classNames = {"C1","C4","C7"};
        System.out.println("Confusion Matrix using " + distanceMetric);
        printConfusionMatrix(confusionMatrix, classNames);
        printConfusionMetrics(confusionMatrix);

        // Classify your document
        System.out.println("\nClassifying your document...");
        testValidInput(k, distanceMetric, filepath);
        var test_data = getStringFromFilePath(filepath);
        var test_result= kNN.fuzzyClassifyDocument(test_data, k, distanceMetric);
        printFuzzy(test_result);
    }
}
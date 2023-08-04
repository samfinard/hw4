import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class tester {
    private static String[] readFolder(String folderPath) {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        List<String> fileContentsList = new ArrayList<>();
        
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                try {
                    String content = Files.readString(file.toPath());
                    fileContentsList.add(content);
                } catch (Exception e) {
                    System.out.println("Error reading file: " + file.getName());
                }   
            }
        }
    return fileContentsList.toArray(new String[fileContentsList.size()]);
    }
    public static void main(String[] args){
        String C1path = "/Users/samfinard/Desktop/hw3_working/data/f1";
        String C4path = "/Users/samfinard/Desktop/hw3_working/data/f2";
        String C7path = "/Users/samfinard/Desktop/hw3_working/data/f3";

        // preparing docs
        String[] folder1 = readFolder(C1path);
        String[] folder2 = readFolder(C4path);
        String[] folder3 = readFolder(C7path);

        var documents1 = termDocument.convertToWords(folder1);
        var documents2 = termDocument.convertToWords(folder2);
        var documents3 = termDocument.convertToWords(folder3);

        List<String[]> docs = new ArrayList<>();
        docs.addAll(documents1);
        docs.addAll(documents2);
        docs.addAll(documents3);

        // get concepts and TFIDF vector
        var concepts = termDocument.getConcepts(docs);
        var TFIDFVector = termDocument.getTFIDFVector(docs, concepts);
        
        // stores TFIDF Matrix into .txt for visualization
        var TFIDFMatrix = termDocument.getTFIDFMatrix(docs, concepts);
        termDocument.storeMatrix(TFIDFMatrix, "TFIDFMatrix.txt");
        
        // 3 clusters
        var clusters = new clusterSet(docs, 3, true);
        
        // number of iterations
        clusters.run(1000);
        
        // top 2 concepts per cluster
        var topC = clusters.topConcepts(2, TFIDFVector);
      
        // prints top concepts per cluster
        for (int i = 0; i < topC.length; i++) {
            System.out.println("Cluster " + i + ":");
            for (int j = 0; j < topC[i].length; j++) {
                System.out.println(topC[i][j]);
            }
            System.out.println();
        }
        System.out.println();

        // one correct concept per document
        List<String> correctConcepts = Arrays.asList(
            "airline", "airline", "airline", "airline", "airline", "airline", "airline", "airline",
            "disease", "disease", "disease", "disease", "disease", "disease", "disease", "disease",
            "bank", "bank", "bank", "bank", "bank", "bank", "bank", "bank" 
        ); 

        // creates confusion matrix, correct concepts, and extra (incorrect) concepts
        var confusionMatrix = clusters.makeConfusionMatrix(docs, correctConcepts);
        var actualConcepts = clusterSet.uniqueConcepts(correctConcepts);
        var extraConcepts = clusters.extraConcepts(actualConcepts);

        // prints confusion matrix
        int i = 0;
        int size = actualConcepts.length + extraConcepts.length;
        for (i = 0; i < actualConcepts.length; i++) {
            System.out.print(actualConcepts[i] + " ");
            for (int j = 0; j < size; j++) {
                System.out.print(confusionMatrix[i][j] + " ");
            }
            System.out.println();
        }
        for (; i < size; i++) {
            System.out.print(extraConcepts[i - actualConcepts.length] + " ");
            for (int j = 0; j < size; j++) {
                System.out.print(confusionMatrix[i][j] + " ");
            }
            System.out.println();
        }

        // prints precision, recall, and f1
        var precision = clusterSet.precision(confusionMatrix);
        var recall = clusterSet.recall(confusionMatrix);
        var f1 = clusterSet.f1(precision, recall);

        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("F1: " + f1);
    }
}
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class termDocument {
    public static int[][] makeMatrix(List<String[]> folder, HashSet<String> concepts){
        int d = folder.size();
        int c = concepts.size();
        int[][] termMatrix = new int[d][c];
        
        String[] conceptsArray = concepts.toArray(new String[c]);
        int i = 0;
        for (var words : folder) {
            for (int j = 0; j < c; j++) {
                termMatrix[i][j] = getFrequency(words, conceptsArray[j]);
            }
            i++;
        }
        return termMatrix;
    }

    private static int getFrequency(String[] words, String concept){
        int frequency = 0;
        for (String word : words) {
            if (word.equals(concept)) {
                frequency++;
            }
        }
        return frequency;
    }

    public static HashSet<String> getConcepts(List<String[]> folder){
        HashSet<String> concepts = new HashSet<String>();
        for (var document : folder) {
            for (String word : document) {
                concepts.add(word);
            }
        }
        return concepts;
    }
    
    public static double[][] TFIDFMatrixFromTerm(int[][] termMatrix){
        int d = termMatrix.length;
        int c = termMatrix[0].length;
        double[][] transformedArray = new double[d][c];
        for (int i = 0; i < d; i++) {
            int[] document = termMatrix[i];
            double[] transformedDocument = transformedArray[i];
            int count = count(document);
            for (int t = 0; t < c; t++) {
                transformedDocument[t] = getIDF(termMatrix, t) * document[t] / count;
            }
        }
        return transformedArray;
    }

    public static void storeMatrix(double[][] matrix, String filename) {
        try {
            var writer = new PrintWriter(filename);
            for (var row : matrix) {
                for (var cell : row) {
                    writer.print(cell + " ");
                }
                writer.println();
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static int count(int[] document){ 
        int d = document.length;
        int count = 0;
        for (int i = 0; i < d; i++) {
            if (document[i] > 0) {
                count++;
            }
        }
        return count;
    }

    private static double getIDF(int[][] termMatrix, int t){
        int d = termMatrix.length;
        int count = 0;
        for (int i = 0; i < d; i++) {
            if (termMatrix[i][t] > 0) {
                count++;
            }
        }
        return Math.log((double) d / count);
    }

    public static double[] getIDFVector(List<String[]> documents, HashSet<String> concepts){
        var matrix = makeMatrix(documents, concepts);
        var res = new double[concepts.size()];
        for (int i = 0; i < concepts.size(); i++) {
            res[i] = getIDF(matrix, i);
        }
        return res;
    }

    public static double[] getTFIDFVector(List<String[]> documents, HashSet<String> concepts){
        var TFIDF_matrix = getTFIDFMatrix(documents, concepts);
        var res = new double[concepts.size()];
        for (int i = 0; i < concepts.size(); i++) {
            for (int j = 0; j < documents.size(); j++) {
                res[i] += TFIDF_matrix[j][i];
            }
        }
        return res;
    }
    public static double[][] getTFIDFMatrix(List<String[]> documents, HashSet<String> concepts){
        var matrix = makeMatrix(documents, concepts);
        var TFIDF_matrix = TFIDFMatrixFromTerm(matrix);
        return TFIDF_matrix;
    }

    public static List<Map.Entry<String, Double>> folderSortedTFIDF (List<String[]> folder){
        HashSet<String> concepts = getConcepts(folder);
        var conc = concepts.toArray(new String[concepts.size()]);
        int[][] TFIDFmatrix = makeMatrix(folder, concepts);
        HashMap<String, Double> sortedTFIDF = new HashMap<String, Double>();

        for (int j = 0; j < TFIDFmatrix[0].length; j++) {
            Double sum = 0.0;
            for (int i = 0; i < TFIDFmatrix.length; i++) {
                sum += TFIDFmatrix[i][j];
            }
            sortedTFIDF.put(conc[j], sum);
        }
        List<Map.Entry<String, Double>> list = new ArrayList<>(sortedTFIDF.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        return list;
    }

    public static List<String[]> convertToWords(String[] folder) {
        List<String[]> documents = new ArrayList<String[]>();
        for (String document : folder) {
            documents.add(document.split(" "));
        }
        return documents;
    }

    private static void printMatrix(double[][] matrix){
        for (int i = 0; i < matrix.length; i++) {
            var row = matrix[i];
            for (int j = 0; j < row.length; j++) {
                var output = String.format("%.4f", row[j]);
                System.out.print(output + " ");
            }
            System.out.println();
        }
    }
    
    private static void printSortedTFIDF(List<Map.Entry<String, Double>> sortedTFIDF){
        for (var entry : sortedTFIDF) {
            var value = String.format("%.4f", entry.getValue());
            System.out.println(entry.getKey() + " " + value);
        }
    }
    public static void main(String[] args){
        String[] folder = {"a a a a b b", "a a b b b b b c c c", "c c c c d d d d d d d", "a a a a d d d", "c c c c c c c c c d"};

        var folderFiles = convertToWords(folder);
        HashSet<String> concepts = getConcepts(folderFiles);
        
        var matrix = getTFIDFMatrix(folderFiles, concepts);       
        printMatrix(matrix);
        
        System.out.println();

        // String[] folder1 = {"penalise home  owner at expense big client to whom they offer seemingly unlimited credit  today  a top official at a lead mortgage investment institute  fonspa  renato cassaro  accuse uk bank abbey national", "cna  pm ask for low mortgage rate for quake  affect victim taipei  dec 19  cna   premier yu shyi  kun instruct ministry finance thursday to coordinate domestic bank within a week to adjust downwards mortgage rate for victim disastrous earthquake three year ago  with 4 percent as goal  premier issue instruction as 921 earthquake postdisaster recovery commission meet for 20th time since killer temblor hit taiwan sept 21  1999  with central taiwan bear brunt casualty damage  ministry finance invite commission"};
        String[] folder1 = {"a a a a b b", "a a b b b b b c c c", "c c c c d d d d d d d", "a a a a d d d", "c c c c c c c c c d"};
        String[] folder2 = {"a a a b b b b b b b c", "b b b c c d d", "a b b b b b b b c c c c d d", "d d d d d d d e e e e e e f"};
        String[] folder3 = {"c c c c c c c d", "d d d d d d e e e", "e e f f f f f f f f"};

        var sortedTFIDF1 = folderSortedTFIDF(convertToWords(folder1));
        var sortedTFIDF2 = folderSortedTFIDF(convertToWords(folder2));
        var sortedTFIDF3 = folderSortedTFIDF(convertToWords(folder3));
        // need to create topics.txt for each folder
        printSortedTFIDF(sortedTFIDF1);
        System.out.println();
        printSortedTFIDF(sortedTFIDF2);
        System.out.println();
        printSortedTFIDF(sortedTFIDF3);
    }
}
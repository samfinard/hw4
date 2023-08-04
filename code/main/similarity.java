import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class similarity {
    public static double[] getVector(String[] document, HashSet<String> concepts){
        double[] vector = new double[concepts.size()];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = 0;
        }
        var conc = concepts.toArray(new String[concepts.size()]);
        for (int i = 0; i < vector.length; i++) {
            vector[i] = getFrequency(document, conc[i]);
        }
        return vector;
    }

    private static double getFrequency(String[] words, String concept){
        int frequency = 0;
        for (String word : words) {
            if (word.equals(concept)) {
                frequency++;
            }
        }
        return frequency;
    }
    
    public static double euclideanDist(String[] doc1, String[] doc2){
        var docs = new ArrayList<String[]>();
        docs.add(doc1);
        docs.add(doc2);
        var concepts = getConcepts(docs);
        var vector1 = getVector(doc1, concepts);
        var vector2 = getVector(doc2, concepts);
        return euclideanDist(vector1, vector2);
    }

    public static double euclideanDist(double[] vector1, double[] vector2){
        double distance = 0;
        for (int i = 0; i < vector1.length; i++) {
            distance += Math.pow(vector1[i] - vector2[i], 2);
        }
        return Math.sqrt(distance);
    }
    
    public static double cosineSim(String[] doc1, String[] doc2){
        var docs = new ArrayList<String[]>();
        docs.add(doc1);
        docs.add(doc2);
        var concepts = getConcepts(docs);
        var vector1 = getVector(doc1, concepts);
        var vector2 = getVector(doc2, concepts);
        return cosineSim(vector1, vector2);
    }

    public static double cosineSim(double[] vector1, double[] vector2){
        double distance = 0;
        var a = 0.0;
        var b = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            distance += vector1[i] * vector2[i];
            a += Math.pow(vector1[i], 2);
            b += Math.pow(vector2[i], 2);
        }
        return 1 - (distance / (Math.sqrt(a) * Math.sqrt(b)));
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
}
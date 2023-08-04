import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class clusterSet {
    private cluster[] clusters;
    private boolean euclidean;

    public clusterSet(List<String[]> documents, int k, boolean euclidean) {
        this.clusters = new cluster[k];
        this.euclidean = euclidean;
        var concepts = similarity.getConcepts(documents);
        for (int i = 0; i < k; i++) { // initialization random: first k clusters
            this.clusters[i] = new cluster(similarity.getVector(documents.get(i), concepts), concepts);
            clusters[i].addDocument(documents.get(i));
        }
        // for each document, get distance between it and all clusters. Put document in closest cluster
        for (int i = k; i < documents.size(); i++) {
            var document = documents.get(i);
            var distances = new double[k];
            for (int j = 0; j < k; j++) {
                distances[j] = getDistance(document, clusters[j], euclidean);
            }
            var min = distances[0];
            var index = 0;
            for (int j = 1; j < k; j++) {
                if (distances[j] < min) {
                    min = distances[j];
                    index = j;
                }
            }
            clusters[index].addDocument(document);
        }
    }

    private ArrayList<String[]> allDocuments() {
        var allDocuments = new ArrayList<String[]>();
        for (cluster c : clusters) {
            allDocuments.addAll(c.getDocuments());
        }
        return allDocuments;
    }

    private void clearAllDocs() {
        for (cluster c : clusters) {
            c.clearDocs();
        }
    }

    private void reCenterAll() {
        for (cluster c : clusters) {
            c.reCenter();
        }
    }
    private void reShuffle() {
        var allDocs = allDocuments();
        clearAllDocs();
        for (String[] document : allDocs) {
            var distances = new double[clusters.length];
            for (int i = 0; i < clusters.length; i++) {
                distances[i] = getDistance(document, clusters[i], euclidean);
            }
            var min = distances[0];
            var index = 0;
            for (int i = 1; i < clusters.length; i++) {
                if (distances[i] < min) {
                    min = distances[i];
                    index = i;
                }
            }
            clusters[index].addDocument(document);
        }
        reCenterAll();
    }
    
    private static double getDistance(String[] document, cluster cluster, boolean euclidean){
        var vector1 = similarity.getVector(document, cluster.getConcepts());
        if (euclidean)
            return similarity.euclideanDist(vector1, cluster.getCentroid());
        else
            return similarity.cosineSim(vector1, cluster.getCentroid());
    }

    public void run(int iterations) {
        var prev_centroids = new double[clusters.length][];
        for (int i = 0; i < clusters.length; i++) {
            prev_centroids[i] = clusters[i].getCentroid().clone();
        }
        reCenterAll();
        for (int i = 0; i < iterations; i++) { // exit condition #1: number of iterations
            reShuffle();
            var curr_centroids = new double[clusters.length][];
            for (int j = 0; j < clusters.length; j++) {
                curr_centroids[j] = clusters[j].getCentroid().clone();
            }
            var distance = 0;
            for (int k = 0; k < clusters.length; k++) { // exist condition #2: centroids don't change (enough)
                 distance += similarity.euclideanDist(prev_centroids[k], curr_centroids[k]);
            }
            if (distance < 0.00001) {
                    // System.out.println("Converged after " + i + " iterations");
                    return;
            }
            prev_centroids = curr_centroids;
        }
    }

    public String[][] topConcepts(int n, double[] TFIDF) {
        String[][] topConcepts = new String[clusters.length][];
        for (int i = 0; i < clusters.length; i++) {
            topConcepts[i] = clusters[i].topConcepts(n, TFIDF);
        }
        return topConcepts;
    }

    private int getIndexOf(String[] arr, String s) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(s))
                return i;
        }
        return -1;
    }
    
    public int[][] makeConfusionMatrix(List<String[]> documents, List<String> correctConcepts) {
        var uniqueConcepts = uniqueConcepts(correctConcepts);
        var extraConcepts = extraConcepts(uniqueConcepts);
        var size = uniqueConcepts.length + extraConcepts.length;
        int[][] res = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                res[i][j] = 0;
            }
        }
        for (cluster c : clusters) {
            for (String[] document : c.getDocuments()) {
                var actual = correctConcepts.get(documents.indexOf(document));
                var predicted = c.mainConcept;
                var predicted_idx = getIndexOf(uniqueConcepts, predicted);
                if (predicted_idx == -1)
                    predicted_idx = getIndexOf(extraConcepts, predicted) + uniqueConcepts.length;
                var actual_idx = getIndexOf(uniqueConcepts, actual);
                res[predicted_idx][actual_idx]++;
            }
        }
        return res;
    }

    public static String[] uniqueConcepts(List<String> correctConcepts){
        HashSet<String> unique = new HashSet<>();
        for (String s : correctConcepts) {
            unique.add(s);
        }
        return unique.toArray(new String[unique.size()]);
    }

    public String[] extraConcepts(String[] uniqueConcepts){
        List<String> extra = new ArrayList<>();
        for (cluster c : clusters) {
            if (getIndexOf(uniqueConcepts, c.mainConcept) == -1) {
                extra.add(c.mainConcept);
            }
        }
        return extra.toArray(new String[extra.size()]);
    }


    public static double precision(int[][] confusionMatrix) {
        int size = confusionMatrix.length;
        double correct = 0;
        double sum = 0;
        for (int i = 0; i < size; i++) {
            correct += confusionMatrix[i][i];
            for (int j = 0; j < size; j++) {
                sum += confusionMatrix[j][i];
            }
        }
        return correct / sum;
    }
    
    public static double recall(int[][] confusionMatrix) {
        int size = confusionMatrix.length;
        double correct = 0;
        double sum = 0;
        for (int i = 0; i < size; i++) {
            correct += confusionMatrix[i][i];
            for (int j = 0; j < size; j++) {
                sum += confusionMatrix[i][j];
            }
        }
        return correct / sum;
    }

    public static double f1(double precision, double recall) {
        return (2 * precision * recall / (precision + recall));
    }
    public static double getAverage(double[] arr) {
        double sum = 0;
        for (double d : arr) {
            sum += d;
        }
        return sum / arr.length;
    }
    public static void main(String[] args) {
         String[] testData = {
            "a a a a b",
            "a a a a c c", 
            "b b b b c c",  
            "b b b b b c c c c"
        };

        var words = termDocument.convertToWords(testData);

        List<String[]> docs = new ArrayList<>();
        docs.addAll(words);

        var concepts = termDocument.getConcepts(docs);
        var TFIDFVector = termDocument.getTFIDFVector(docs, concepts);

        var clusters = new clusterSet(docs, 2, false); // 3 clusters
        clusters.run(1000);
        
        var topC = clusters.topConcepts(2, TFIDFVector); // top 2 concepts per cluster
        // prints top concepts per cluster
        for (int i = 0; i < topC.length; i++) {
            System.out.println("Cluster " + i + ":");
            for (int j = 0; j < topC[i].length; j++) {
                System.out.println(topC[i][j]);
            }
        }
        System.out.println();
    }
}

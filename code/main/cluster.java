import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class cluster {
    private List<String[]> documents;
    private double[] centroid;
    private HashSet<String> concepts;
    public String mainConcept;

    public cluster(double[] centroid, HashSet<String> concepts) {
        this.centroid = centroid;
        this.concepts = concepts;
        this.documents = new ArrayList<>();
    }

    public HashSet<String> getConcepts() {
        return concepts;
    }

    public double[] getCentroid() {
        return centroid;
    }

    public List<String[]> getDocuments() {
        return documents;
    }

    public void addDocument(String[] document) {
        documents.add(document);
    }

    public void clearDocs() {
        this.documents = new ArrayList<>();
    }

    public boolean contains(String[] document) {
        return documents.contains(document);
    }
    
    public void reCenter() {
        for (int i = 0; i < centroid.length; i++) {
            centroid[i] = 0;
        }
        for (String[] document : documents) {
            var vector = similarity.getVector(document, concepts);
            for (int i = 0; i < vector.length; i++) {
                centroid[i] += vector[i];
            }
        }
        for (int i = 0; i < centroid.length; i++) {
            centroid[i] /= documents.size();
        }
    }

    public String[] topConcepts(int n, double[] TFIDF) {
        HashMap<String, Double> sortedTFIDF = new HashMap<String, Double>();
        var conc = concepts.toArray();
        for (int i = 0; i < TFIDF.length; i++) {
            sortedTFIDF.put(conc[i].toString(), TFIDF[i] * centroid[i]);
        }
        List<Map.Entry<String, Double>> list = new ArrayList<>(sortedTFIDF.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        // return top n elements of the sorted list
        var res = new String[n];
        for (int i = 0; i < n; i++) {
            res[i] = list.get(i).getKey();
        }
        mainConcept = res[0];
        return res;
    }
}
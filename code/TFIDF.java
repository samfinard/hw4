import java.util.*;

public class TFIDF {

    private List<docLabel> documents;
    private List<String> conceptWords;
    private Map<String, Map<String, Integer>> tf;
    private Map<String, Double> idf;
    private Map<String, Map<String, Double>> tfidfMatrix;

    public TFIDF(List<docLabel> documents, boolean logAddOne) {
        this.documents = documents;
        this.conceptWords = createConceptWords(documents);
        this.tf = new HashMap<>();
        this.idf = new HashMap<>();
        this.tfidfMatrix = new HashMap<>();
        calculateTFandIDF(logAddOne);
    }


    private void calculateTFandIDF(boolean logAddOne) {
        int docCount = documents.size();
        Map<String, Integer> docFreq = new HashMap<>();

        // Calculate term frequency and document frequency
        for (int i = 0; i < docCount; i++) {
            String[] words = documents.get(i).doc.split(" ");
            Map<String, Integer> wordFreq = new HashMap<>();
            for (String word : words) {
                if (conceptWords.contains(word)) {
                    wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
                    docFreq.put(word, docFreq.getOrDefault(word, 0) + 1);
                }
            }
            tf.put("Document" + (i + 1), wordFreq);
        }
        
        // Calculate inverse document frequency
        if (logAddOne) {
            for (String word : docFreq.keySet()) {
                idf.put(word, Math.log1p((double) docCount / docFreq.get(word))); // == math.log(1.0 + x)
            }
        }
        else {
            for (String word : docFreq.keySet()) {
                idf.put(word, Math.log((double) docCount / docFreq.get(word))); // == math.log(1.0)
            }
        }
    }

    public double calculateTFIDF(String document, String word) {
        Map<String, Integer> wordFreq = tf.get(document);
        Double wordIDF = idf.get(word);
        if (wordFreq == null || wordIDF == null) {
            return 0;
        }
        return wordFreq.getOrDefault(word, 0) * wordIDF;
    }

    public void createTFIDFMatrix() {
        for (int i = 0; i < documents.size(); i++) {
            Map<String, Double> docTFIDF = new HashMap<>();
            for (String word : conceptWords) {
                double tfidfScore = calculateTFIDF("Document" + (i + 1), word);
                docTFIDF.put(word, tfidfScore);
            }
            tfidfMatrix.put("Document" + (i + 1), docTFIDF);
        }
    }

    public void printTFIDFMatrix() {
        for (String doc : tfidfMatrix.keySet()) {
            System.out.println("Document: " + doc);
            Map<String, Double> docTFIDF = tfidfMatrix.get(doc);
            for (String word : docTFIDF.keySet()) {
                System.out.println(word + ", " + docTFIDF.get(word));
            }
            System.out.println("-----------------------");
        }
    }

    private List<String> createConceptWords(List<docLabel> docs) {
        Set<String> uniqueWords = new HashSet<>();
        for (var doc : docs) {
            uniqueWords.addAll(Arrays.asList(doc.doc.split(" ")));
        }
        return new ArrayList<>(uniqueWords);
    }
    public List<Double> getVector (String doc) {
        List<Double> vector = new ArrayList<>();
        for (String word : conceptWords) {
            vector.add(calculateTFIDF(doc, word));
        }
        return vector;
    }
    public static void main(String[] args){
        List<docLabel> documents = new ArrayList<>();
        documents.add(new docLabel("a b c", "a"));
        documents.add(new docLabel("a b c", "b"));
        

        List<String> conceptWords = new ArrayList<>();
        conceptWords.add("a");
        conceptWords.add("b");
        TFIDF tfidf = new TFIDF(documents, true);
        tfidf.createTFIDFMatrix();
        tfidf.printTFIDFMatrix();
    }
}
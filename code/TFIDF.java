import java.util.*;

public class TFIDF {

    private List<String> documents;
    private List<String> conceptWords;
    private Map<String, Map<String, Integer>> tf;
    private Map<String, Double> idf;
    private Map<String, Map<String, Double>> tfidfMatrix;
    private boolean logAddOne;

    public TFIDF(List<String> documents, boolean logAddOne) {
        this.documents = documents;
        this.conceptWords = createConceptWords(documents);
        this.logAddOne = logAddOne;
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
            String[] words = documents.get(i).split(" ");
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

    private List<String> createConceptWords(List<String> documents) {
        Set<String> uniqueWords = new HashSet<>();
        for (String doc : documents) {
            uniqueWords.addAll(Arrays.asList(doc.split(" ")));
        }
        return new ArrayList<>(uniqueWords);
    }
    public static void main(String[] args){
        List<String> documents = new ArrayList<>();
        documents.add("a c c");
        documents.add("a b b b b");
        documents.add("a a d d d d");

        List<String> conceptWords = new ArrayList<>();
        conceptWords.add("a");
        conceptWords.add("b");
        TFIDF tfidf = new TFIDF(documents, true);
        tfidf.createTFIDFMatrix();
        tfidf.printTFIDFMatrix();
    }
}
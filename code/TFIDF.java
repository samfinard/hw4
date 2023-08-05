import java.util.*;

public class TFIDF {
    private List<docLabel> documents;
    private List<String> conceptWords;
    private double[][] tfidfMatrix;
    private Map<String, Integer> docFreq;

    public TFIDF(List<docLabel> documents, boolean logAddOne) {
        this.documents = documents;
        this.conceptWords = createConceptWords(documents);
        this.tfidfMatrix = makeMatrix(logAddOne);
    }


    private double[][] makeMatrix(boolean logAddOne) {
        var res = new double[documents.size()][conceptWords.size()];        
        // Calculate document frequency for each concept word
        docFreq = new HashMap<>();
        for (String word : conceptWords) {
            for (docLabel doc : documents) {
                if (doc.doc.contains(word)) {
                    docFreq.put(word, docFreq.getOrDefault(word, 0) + 1);
                }
            }
        }
        // Calculate TF-IDF for each document and concept word
        for (int i = 0; i < documents.size(); i++) {
            String document = documents.get(i).doc;
            res[i] = getVector(document, logAddOne); 
        }
        return res;
    }

    public double[] getVector(String document, boolean logAddOne) {
        double[] res = new double[conceptWords.size()];
        int docCount = documents.size();        
        for (int j = 0; j < conceptWords.size(); j++) {
            String word = conceptWords.get(j);
            double tf = 0;
            // Count the occurrences of the word in the document
            int index = document.indexOf(word);
            while (index != -1) {
                tf++;
                index = document.indexOf(word, index + word.length());
            }
            var df = docFreq.getOrDefault(word, 0);
            double idf;
            // Calculate IDF
            if (logAddOne) {
                idf = Math.log(docCount / ((double) (df) + 1));
            }
            else {
                idf = Math.log(docCount / (double) (df));
            }
            res[j] = tf * idf;
        }
        return res;
    }
    private List<String> createConceptWords(List<docLabel> docs) {
        Set<String> uniqueWords = new HashSet<>();
        for (var doc : docs) {
            uniqueWords.addAll(Arrays.asList(doc.doc.split(" ")));
        }
        return new ArrayList<>(uniqueWords);
    }
    

    
    public static void main(String[] args){
        List<docLabel> documents = new ArrayList<>();
        documents.add(new docLabel("a b c", "a"));
        documents.add(new docLabel("a b c", "b"));
        

        List<String> conceptWords = new ArrayList<>();
        conceptWords.add("a");
        conceptWords.add("b");
        TFIDF tfidf = new TFIDF(documents, true);
    }
}
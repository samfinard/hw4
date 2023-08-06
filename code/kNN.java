import java.util.*;

public class kNN {
    private TFIDF tfidfMatrix;
    private List<docLabel> docLabels;
    private int zero_dist = 0;
    
    public kNN(List<docLabel> docs) {
        this.docLabels = docs;
        this.tfidfMatrix = new TFIDF(docs);
    }

    private List<docLabel> getTopKDist(String unknownDoc, int k, String distanceMetric) {
        List<docLabel> docDist = new ArrayList<>();
        // calculate distance from unknownDoc to all others
        for (docLabel doc : docLabels) {
            double distance = Integer.MAX_VALUE;
            if (distanceMetric.equalsIgnoreCase("ncd")) {
                distance = similarity.NCD(unknownDoc, doc.doc);
            } else {
                var unknown = new docLabel(unknownDoc, " ");
                var unknownDocVector = tfidfMatrix.getVector(unknown);
                var otherDocVector = tfidfMatrix.getVector(doc);

                if (distanceMetric.equalsIgnoreCase("cos")) { // cosine distance
                    distance = similarity.cosineSim(unknownDocVector, otherDocVector);
                } else if (distanceMetric.equalsIgnoreCase("euc")) { // Euclidean distance
                    distance = similarity.euclideanDist(unknownDocVector, otherDocVector);
                } else if (distanceMetric.equalsIgnoreCase("man")) { // Manhattan distance
                    distance = similarity.manhattanDist(unknownDocVector, otherDocVector);
                }
                else {
                    System.out.println("Invalid distance metric + " + distanceMetric + "");
                    System.exit(1);
                }
            }
            docDist.add(new docLabel(doc, distance));
        }
        // sort the distances and keep the top k
        Collections.sort(docDist, (a, b) -> Double.compare(a.distance, b.distance));
        var topKDocs = docDist.subList(0, k);
        return topKDocs;
    }
    private String getMostFrequentClass(List<docLabel> kNearestDocs) {
        // count the labels 
        Map<String, Integer> labelFrequency = new HashMap<>();
        for (docLabel doc : kNearestDocs) {
            labelFrequency.put(doc.label, labelFrequency.getOrDefault(doc.label, 0) + 1);
        }

        // return the one with the highest frequency
        Map.Entry<String, Integer> mostFreq = null;
        for (Map.Entry<String, Integer> entry : labelFrequency.entrySet()) {
            if (mostFreq == null || entry.getValue() > mostFreq.getValue()) {
                mostFreq = entry;
            }
        }
        return mostFreq.getKey();
    }
    public Map<String, Double> fuzzyClassifyDocument(String unknownDoc, int k, String distanceMetric) {
        var topKDocs = getTopKDist(unknownDoc, k, distanceMetric);
        Map<String, Double> labelFrequency = new HashMap<>();
        double sum = 0;
        for (docLabel doc : topKDocs) {
            if (doc.distance == 0) zero_dist++;
            double frequency = 1.0 / doc.distance;
            labelFrequency.put(doc.label, labelFrequency.getOrDefault(doc.label, 0.0) + frequency);
            sum += frequency;
        }
        // Normalize to percentages
        for (Map.Entry<String, Double> entry : labelFrequency.entrySet()) {
            labelFrequency.put(entry.getKey(), (entry.getValue() / sum) * 100);
        }
        return labelFrequency;
    }
    public String classifyDocument(String unknownDoc, int k, String distanceMetric) {
      var topKDocs = getTopKDist(unknownDoc, k, distanceMetric);
      var mostFreqLabel = getMostFrequentClass(topKDocs);
      return mostFreqLabel;
    }
}
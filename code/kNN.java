import java.util.*;

public class kNN {
    private TFIDF tfidfMatrix;
    private List<docLabel> docLabels;

    public kNN(List<docLabel> docs) {
        this.docLabels = docs;
    }
    public String classifyDocument(String unknownDoc, int k, String distanceMetric) { // e c    n
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

                if (distanceMetric.equalsIgnoreCase("cos")) {
                    distance = similarity.cosineSim(unknownDocVector, otherDocVector);
                } else if (distanceMetric.equalsIgnoreCase("euc")) { // Euclidean distance
                    distance = similarity.euclideanDist(unknownDocVector, otherDocVector);
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

        // count the labels and return the most frequent one
        Map<String, Integer> labelFrequency = new HashMap<>();
        for (docLabel doc : topKDocs) {
            labelFrequency.put(doc.label, labelFrequency.getOrDefault(doc.label, 0) + 1);
        }

        // Find the label with the highest frequency
        Map.Entry<String, Integer> mostFreq = null;
        for (Map.Entry<String, Integer> entry : labelFrequency.entrySet()) {
            if (mostFreq == null || entry.getValue() > mostFreq.getValue()) {
                mostFreq = entry;
            }
        }
        return mostFreq.getKey();
    }

}

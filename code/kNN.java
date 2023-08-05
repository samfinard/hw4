import java.util.*;

public class kNN {
    private final Map<String, Map<String, Double>> tfidfMatrix;
    private final Map<String, String> documents;
    
    public kNN(Map<String, Map<String, Double>> tfidfMatrix, Map<String, String> documents) {
        this.tfidfMatrix = tfidfMatrix;
        this.documents = documents;
    }

    public String classifyDocument(String documentContent, int k, String distanceMetric) {
        List<Map.Entry<String, Double>> distances = new ArrayList<>();
        Map<String, Integer> labelCounts = new HashMap<>();
        // Calculate the distance from the given document to all others
        for (String otherDocument : documents.keySet()) {
            double distance;
            if (distanceMetric.equalsIgnoreCase("ncd")) {
                distance = similarity.NCD(documentContent, documents.get(otherDocument));
            } else {
                Map<String, Double> documentVector = tfidfMatrix.get(documentContent);
                Map<String, Double> otherDocumentVector = tfidfMatrix.get(otherDocument);

                double[] docVectorArray = documentVector.values().stream().mapToDouble(Double::doubleValue).toArray();
                double[] otherDocVectorArray = otherDocumentVector.values().stream().mapToDouble(Double::doubleValue).toArray();

                if (distanceMetric.equalsIgnoreCase("cosine")) {
                    distance = similarity.cosineSim(docVectorArray, otherDocVectorArray);
                } else { // Default to Euclidean distance
                    distance = similarity.euclideanDist(docVectorArray, otherDocVectorArray);
                }
            }

            distances.add(new AbstractMap.SimpleEntry<>(otherDocument, distance));
        }

        // Sort the distances and select the top k
        distances.sort(Map.Entry.comparingByValue());
        List<Map.Entry<String, Double>> nearestNeighbors = distances.subList(0, k);

        // Classify the document based on the class of the majority of the k closest neighbors
        for (Map.Entry<String, Double> entry : nearestNeighbors) {
            String label = labels.get(entry.getKey());
            labelCounts.put(label, labelCounts.getOrDefault(label, 0) + 1);
        }
        return Collections.max(labelCounts.entrySet(), Map.Entry.comparingByValue()).getKey();

    }

}

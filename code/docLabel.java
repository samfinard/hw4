import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class docLabel {
    private String doc;
    public String label;
    public double distance;
    public String[] words;


    public docLabel(String doc, String label) {
        this.doc = doc;
        this.label = label;
        this.words = getWords(doc);
    }
    public docLabel(docLabel doc, double distance) {
        this.doc = doc.doc;
        this.label = doc.label;
        this.words = doc.words;
        this.distance = distance;
    }


    public boolean contains(String word) {
        for (String w : words) {
            if (w.equals(word)) {
                return true;
            }
        }
        return false;
    }

    public double wordCount(String word) {
        double count = 0;
        for (String w : words) {
            if (w.equals(word)) {
                count++;
            }
        }
        return count;
    }

    private String[] getWords(String doc) {
        var text = doc.toLowerCase();
        String[] words = text.split("[\\s,]+");
        words = Arrays.stream(words).map(word -> word.replaceAll("[^a-z]", "")).toArray(String[]::new);
        Set<String> stopWords = new HashSet<>(Arrays.asList("a", "an", "the", "is", "in", "and", "of", "to", "this", "that"));
        List<String> res = new ArrayList<>();
        for (String word : words) {
            if (!stopWords.contains(word)) {
                res.add(word);
            }
        }
        return res.toArray(String[]::new);
    }

}

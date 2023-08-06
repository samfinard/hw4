import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class docLabel {
    public String doc;
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
        Set<String> stopWords = new HashSet<>(Arrays.asList(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "as", "at", 
            "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", 
            "could", "did", "do", "does", "doing", "down", "during", "each", "few", "for", "from", 
            "further", "had", "has", "have", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", 
            "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", 
            "into", "is", "it", "it's", "its", "itself", "let's", "me", "more", "most", "my", "myself", "nor", "of", 
            "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "she", 
            "she'd", "she'll", "she's", "should", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", 
            "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", 
            "those", "through", "to", "too", "under", "until", "up", "very", "was", "we", "we'd", "we'll", "we're", "we've", 
            "were", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", 
            "with", "would", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves"
            ));
        String[] words = text.split("[\\s,]+");
        List<String> res = new ArrayList<>();
        for (String word : words) {
            if (!stopWords.contains(word)) {
                res.add(word);
            }
        }
       // convert List<String> to String[]
        words = res.toArray(new String[0]);
        words = Arrays.stream(words).map(word -> word.replaceAll("[^a-z]", "")).toArray(String[]::new); 
        return words;
    }

}

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class runner {
    public static List<docLabel> readDocumentsFromFolder(String folderPath) {
        List<docLabel> documents = new ArrayList<>();
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();

        // Hardcoded mapping between filenames and labels
        Map<String, String> filenameToLabel = new HashMap<>();
        filenameToLabel.put("processed01.txt", "C1");
        filenameToLabel.put("processed02.txt", "C1");
        filenameToLabel.put("processed03.txt", "C1");
        filenameToLabel.put("processed04.txt", "C1");
        filenameToLabel.put("processed05.txt", "C4");
        filenameToLabel.put("processed06.txt", "C4");
        filenameToLabel.put("processed07.txt", "C7");
        filenameToLabel.put("processed08.txt", "C7");
        filenameToLabel.put("processed09.txt", "C1");
        filenameToLabel.put("processed10.txt", "C4");

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    try {
                        // Read the content of the file
                        String content = new String(Files.readAllBytes(file.toPath()));
                        // Look up the filename in the mapping to find the label
                        String label = filenameToLabel.get(file.getName());
                        if (label != null) {
                            documents.add(new docLabel(content, label));
                        } else {
                            // Handle the case where the filename is not in the mapping
                        }
                    } catch (IOException e) {
                        // Handle exception
                        e.printStackTrace();
                    }
                }
            }
        }
        return documents;
    }

    private static String getCategory(String str) {
        String res = "";
        if (str.contains("C1")) {
            res = "Airline Safety";
        }
        else if (str.contains("C4")) {
            res = "Hoof and Mouth Disease";
        }
        else if (str.contains("C7")) {
            res = "Mortgage Rates";
        }
        return res;
    }
    public static void main(String[] args) {
        List<docLabel> documents = readDocumentsFromFolder("../data/processed");
        boolean logAddOne = true;
        String distanceMetric = "ncd"; // "cos" for cosine, "ncd" for normalized compression distance, anything else for euclid
        int k = 5; // must be < 10
        kNN kNN = new kNN(documents, logAddOne);
        
        String test_document = """
        Airlines airline I am a safe hoof and mouth.
            """;
        
        var test_label = kNN.classifyDocument(test_document, k, distanceMetric);
        System.out.println("label: " + test_label + " (" + getCategory(test_label) + ")");
    }
}

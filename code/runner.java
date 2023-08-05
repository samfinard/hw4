import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashMap;
import java.util.Map;

public class runner {
    public List<String> folderToList(String folderPath) {
        List<String> folder_contents = new ArrayList<String>();
        try (Stream<Path> walk = Files.walk(Paths.get(folderPath))) {
            List<Path> textFiles = walk
            .filter(Files::isRegularFile)
            .filter(x -> x.toString().endsWith(".txt"))
            .collect(Collectors.toList());
            for (Path textFile : textFiles) {
                String text = new String(Files.readAllBytes(textFile));
                folder_contents.add(text);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return folder_contents;
    }

    public static List<docLabel> testReadDocsFromFolder(String folderPath) {
        List<docLabel> documents = new ArrayList<>();
        Map<String, String> filenameToLabel = new HashMap<>();
        filenameToLabel.put("unknown01.txt", "C1");
        filenameToLabel.put("unknown02.txt", "C1");
        filenameToLabel.put("unknown03.txt", "C1");
        filenameToLabel.put("unknown04.txt", "C1");
        filenameToLabel.put("unknown05.txt", "C4");
        filenameToLabel.put("unknown06.txt", "C4");
        filenameToLabel.put("unknown07.txt", "C7");
        filenameToLabel.put("unknown08.txt", "C7");
        filenameToLabel.put("unknown09.txt", "C1");
        filenameToLabel.put("unknown10.txt", "C4");
        for (Map.Entry<String, String> entry : filenameToLabel.entrySet()) {
            String filename = entry.getKey();
            String label = entry.getValue();
            try {
                String content = new String(Files.readAllBytes(Paths.get(folderPath, filename)));
                documents.add(new docLabel(content, label));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return documents;
    }
    public static List<docLabel> readDocumentsFromFolder(String folderPath) {
        List<docLabel> documents = new ArrayList<>();
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();

        // Hardcoded mapping between filenames and labels
        Map<String, String> filenameToLabel = new HashMap<>();
        filenameToLabel.put("unknown01.txt", "C1");
        filenameToLabel.put("unknown02.txt", "C1");
        filenameToLabel.put("unknown03.txt", "C1");
        filenameToLabel.put("unknown04.txt", "C1");
        filenameToLabel.put("unknown05.txt", "C4");
        filenameToLabel.put("unknown06.txt", "C4");
        filenameToLabel.put("unknown07.txt", "C7");
        filenameToLabel.put("unknown08.txt", "C7");
        filenameToLabel.put("unknown09.txt", "C1");
        filenameToLabel.put("unknown10.txt", "C4");
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
        List<docLabel> documents = testReadDocsFromFolder("../data/input");

        boolean logAddOne = true;
        String distanceMetric = ""; // "cos" for cosine, "ncd" for normalized compression distance, anything else for euclid
        int k = 1; // must be < 10
        kNN kNN = new kNN(documents, logAddOne);
        
        // String test_document = "Hoof and Mouth Disease is a deadly disease. I have a hoof. Then I have a mouth. Then I have a hoof and mouth.";
        String test_document = """
            Airline Safety. I am supposed to be safe on airlines. I am an airline who flies and I need to be safe.
            """;
        var test_label = kNN.classifyDocument(test_document, k, distanceMetric);
        System.out.println("label: " + test_label + " (" + getCategory(test_label) + ")");
    }
}

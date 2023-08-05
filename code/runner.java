import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static void main(String[] args) {
        runner m = new runner();
        List<String> folder_contents = m.folderToList("../data/processed");
        System.out.println(folder_contents.get(0));
    }
}

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class similarity {
    public static double cosineSim(double[] vector1, double[] vector2){
        double distance = 0;
        var a = 0.0;
        var b = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            distance += vector1[i] * vector2[i];
            a += Math.pow(vector1[i], 2);
            b += Math.pow(vector2[i], 2);
        }
        return 1 - (distance / (Math.sqrt(a) * Math.sqrt(b)));
    }
    public static double euclideanDist(double[] vector1, double[] vector2){
        double distance = 0;
        for (int i = 0; i < vector1.length; i++) {
            distance += Math.pow(vector1[i] - vector2[i], 2);
        }
        return Math.sqrt(distance);
    }
    private static int getCompressedSize(String str) {
        int size = 0;
        if (str == null || str.length() == 0) {
            return size;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(str.getBytes(StandardCharsets.UTF_8));
            gzip.close();
            size = baos.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }
    public static int NCD(String str1, String str2) { // Normalized Compression Distance
        int size1 = getCompressedSize(str1);
        int size2 = getCompressedSize(str2);
        int size12 = getCompressedSize(str1 + str2);
        return (size12 - Math.min(size1, size2)) / Math.max(size1, size2);
    }
}
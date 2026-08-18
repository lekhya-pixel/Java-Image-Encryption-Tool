import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

    // Read all bytes from a file into byte[]
    public static byte[] readFileToBytes(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    // Write byte[] to a file
    public static void writeBytesToFile(String path, byte[] data) throws IOException {
        Files.write(Paths.get(path), data);
    }
}

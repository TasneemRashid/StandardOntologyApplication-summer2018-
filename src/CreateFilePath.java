/**
 *
 * @author tasneem
 */
import java.io.File;
public class CreateFilePath {
    /**
     * This file creates the file path depending on the OS of the system.
     * @param fileName name of the file
     * @return complete path of the file
     */
    public String createFilePath(String fileName)
    {
        String directory = System.getProperty("user.dir");
        String path = directory + File.separator + fileName;
        return path;
    }
}

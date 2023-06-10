import java.io.File;
import java.util.Vector;

public class FolderFilesGetter {
    public static Vector<String> getFilePaths(String directory) {
        File folder = new File(directory);
        Vector<String> filePaths = new Vector<String>();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            for (File file : files) {
                if (file.isFile()) {
                    filePaths.add(file.getAbsolutePath());
                }
            }
        }

        return filePaths;
    }
}

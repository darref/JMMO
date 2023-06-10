import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileReaderWriter {
    public static void writeToFile(String filename, String content)
    {
        File file = new File(filename);
        File parentDir = file.getParentFile();

        // Vérifier si les dossiers parents existent, sinon les créer
        if (!parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                System.out.println("Impossible de créer les dossiers parents.");
                return;
            }
        }

        // Créer le fichier
        try {
            boolean created = file.createNewFile();
            if (created) {
                System.out.println("Le fichier a été créé avec succès.");
            } else {
                System.out.println("Le fichier existe déjà , nous allons l'écraser.");
            }

            // Écrire la chaîne de caractères dans le fichier
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.close();

            System.out.println("La chaîne de caractères a été écrite dans le fichier avec succès.");
        } catch (IOException e) {
            System.out.println("Une erreur s'est produite lors de la création ou de l'écriture du fichier : " + e.getMessage());
        }
    }

    public static String readFromFile(String filename) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public static boolean fileExists(String filename) {
        File file = new File(filename);
        return file.exists();
    }

    public static List<String> getFilePaths(String directoryPath) throws IOException {
        List<String> filePaths = new ArrayList<>();
        Path directory = Paths.get(directoryPath);

        if (Files.isDirectory(directory)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
                for (Path path : directoryStream) {
                    if (Files.isRegularFile(path)) {
                        filePaths.add(path.toString());
                    }
                }
            }
        }

        return filePaths;
    }
}

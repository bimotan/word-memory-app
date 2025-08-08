import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileUtils {
    public static List<String> listTextFiles() {
        List<String> textFiles = new ArrayList<>();
        File dir = new File(System.getProperty("user.dir"));
        
        File[] files = dir.listFiles((d, name) -> 
            name.toLowerCase().endsWith(".txt"));
        
        if (files != null) {
            for (File file : files) {
                textFiles.add(file.getName());
            }
        }
        return textFiles;
    }
    
    public static boolean hasTextFiles() {
        return !listTextFiles().isEmpty();
    }
    
    public static String createTextFile(String baseName) throws IOException {
        if (!baseName.toLowerCase().endsWith(".txt")) {
            baseName += ".txt";
        }
        
        int counter = 1;
        String fileName = baseName;
        while (Files.exists(Paths.get(fileName))) {
            String namePart = baseName.substring(0, baseName.lastIndexOf('.'));
            String extPart = baseName.substring(baseName.lastIndexOf('.'));
            fileName = namePart + "(" + counter + ")" + extPart;
            counter++;
        }
        
        Files.createFile(Paths.get(fileName));
        return fileName;
    }
}
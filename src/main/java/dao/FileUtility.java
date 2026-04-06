package dao;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class FileUtility {

    public static void safeWrite(String filePath, String header, List<String> lines) throws IOException {
        Path originalPath = Paths.get(filePath);
        Path tempPath = originalPath.resolveSibling(originalPath.getFileName() + ".tmp");

        
        try (BufferedWriter bw = Files.newBufferedWriter(tempPath)) {
            if (header != null && !header.isEmpty()) {
                bw.write(header);
                bw.newLine();
            }
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }

        try {
           
            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            
            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
        }
      
    }
}
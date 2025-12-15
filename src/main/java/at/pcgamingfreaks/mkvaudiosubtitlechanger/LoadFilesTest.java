package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoadFilesTest {
    public static void main(String[] args) {
        int depth = 2;

        getDirectoriesAtDepth("/mnt/media/Anime", 2);

        try (Stream<Path> paths = Files.walk(Paths.get("/mnt/media/Anime"), 2)) {
            List<File> result = paths.map(Path::toFile)
                    .filter(File::isDirectory)
                    .collect(Collectors.toList());
            System.out.println(result);
        } catch (IOException e) {
        }
    }

    private static List<File> getDirectoriesAtDepth(String path, int depth) {
        List<File> result = new ArrayList<>();
        File rootDir = Path.of(path).toFile();
        if (!rootDir.exists()) throw new RuntimeException("Invalid path");

        exploreDirectory(rootDir, 0, depth, result);
        return result;
    }

    /**
     * Recursively explores directories to find items at the target depth.
     *
     * @param currentDir The current directory being explored
     * @param currentDepth The current depth level
     * @param targetDepth The target depth to collect files
     * @param result The collection to store found files
     */
    private static void exploreDirectory(File currentDir, int currentDepth, int targetDepth, List<File> result) {
        if (currentDepth == targetDepth) {
            // We've reached the target depth, add this directory to results
            result.add(currentDir);
            return;
        }

        // Get all files and directories in the current directory
        File[] files = currentDir.listFiles();
        if (files == null) return;

        // Recursively explore subdirectories
        for (File file : files) {
            if (file.isDirectory()) {
                exploreDirectory(file, currentDepth + 1, targetDepth, result);
            } else if (currentDepth + 1 == targetDepth) {
                // If files at the next level would be at the target depth, include them
                result.add(file);
            }
        }
    }
}

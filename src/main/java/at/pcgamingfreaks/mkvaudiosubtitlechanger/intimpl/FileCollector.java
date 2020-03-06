package at.pcgamingfreaks.mkvaudiosubtitlechanger.intimpl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;

import java.io.File;
import java.util.List;

public interface FileCollector {
    List<File> loadFiles(String path);

    List<FileAttribute> loadAttributes(File file);
}

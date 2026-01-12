package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ResultStatistic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class FileFilter {
    private final Set<String> excluded;
    private final Pattern includePattern;
    private final Date filterDate;
    private final LastExecutionHandler lastExecutionHandler;

    private final String EXTENSION_GROUP = "extension";
    private final Pattern extensionPattern = Pattern.compile(String.format(".*(?<%s>\\..*)", EXTENSION_GROUP));

    public boolean accept(File pathName, Set<String> fileExtensions) {
        // Ignore files irrelevant for statistics
        if (!hasProperFileExtension(pathName, new HashSet<>(fileExtensions))) {
            log.debug("Ignored {}", pathName);
            return false;
        }

        if (!hasMatchingPattern(pathName)
                || isExcluded(pathName, new HashSet<>(excluded))
                || lastExecutionHandler != null && !isNewOrChanged(pathName)
                || !isNewer(pathName, filterDate)) {
            log.debug("Excluded {}", pathName);
            ResultStatistic.getInstance().excluded();
            return false;
        }

        return true;
    }

    private boolean hasProperFileExtension(File pathName, Set<String> fileExtensions) {
        Matcher matcher = extensionPattern.matcher(pathName.getName());
        return matcher.find() && fileExtensions.contains(matcher.group(EXTENSION_GROUP));
    }

    private boolean hasMatchingPattern(File pathName) {
        return includePattern.matcher(pathName.getName()).matches();
    }

    private boolean isNewer(File pathName, Date date) {
        return isNewer(pathName, date.toInstant());
    }

    private boolean isNewer(File pathName, Instant date) {
        if (date == null) return true;
        try {
            BasicFileAttributes attributes = Files.readAttributes(pathName.toPath(), BasicFileAttributes.class);
            return attributes.creationTime().toInstant().isAfter(date)
                    || attributes.lastModifiedTime().toInstant().isAfter(date);
        } catch (IOException e) {
            log.warn("File attributes could not be read", e);
        }
        return true;
    }

    private boolean isExcluded(File pathName, Set<String> excludedDirs) {
        if (excludedDirs.contains(pathName.getPath())) return true;

        String[] pathSplit = pathName.getPath().split("/");
        for (String excludedDir : excludedDirs) {
            String[] excludeSplit = excludedDir.split("/");
            if (excludeSplit.length > pathSplit.length) continue;
            boolean matchingPaths = true;
            for (int i = 0; i < excludeSplit.length; i++) {
                if (!excludeSplit[i].equals(pathSplit[i])) {
                       matchingPaths = false;
                       break;
                }
            }
            if (matchingPaths) return true;
        }

        return false;
    }

    private boolean isNewOrChanged(File pathname) {
        Instant lastExecutionDate = lastExecutionHandler.get(pathname.getAbsolutePath());
        return lastExecutionDate == null || isNewer(pathname, lastExecutionDate);
    }
}

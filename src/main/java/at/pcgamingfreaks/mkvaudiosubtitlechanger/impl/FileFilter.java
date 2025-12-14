package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.ResultStatistic;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class FileFilter {
    private final Set<String> excluded;
    private final Pattern includePattern;
    private final Date filterDate;

    private final String EXTENSION_GROUP = "extension";
    private final Pattern extensionPattern = Pattern.compile(String.format(".*(?<%s>\\..*)", EXTENSION_GROUP));

    public boolean accept(File pathName, Set<String> fileExtensions) {
        // Ignore files irrelevant for statistics
        if (!hasProperFileExtension(pathName, new HashSet<>(fileExtensions))) {
            log.debug("Ignored {}", pathName);
            return false;
        }

        ResultStatistic.getInstance().total();
        if (!hasMatchingPattern(pathName)
                || !isNewer(pathName)
                || isExcluded(pathName, new HashSet<>(excluded))) {
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

    private boolean isNewer(File pathName) {
        if (filterDate == null) return true;
        try {
            BasicFileAttributes attributes = Files.readAttributes(pathName.toPath(), BasicFileAttributes.class);
            return isNewer(DateUtils.convert(attributes.creationTime().toMillis()));
        } catch (IOException e) {
            log.warn("File attributes could not be read", e);
        }
        return true;
    }

    private boolean isNewer(Date creationDate) {
        return creationDate.toInstant().isAfter(filterDate.toInstant());
    }

    private boolean isExcluded(File pathName, Set<String> excludedDirs) {
        if (excludedDirs.contains(pathName.getPath())) return true;

        // TODO improve partial matches and wildcard?
        for (String excludedDir : excludedDirs) {
            if (pathName.getPath().startsWith(excludedDir)) return true;
        }

        return false;
    }
}

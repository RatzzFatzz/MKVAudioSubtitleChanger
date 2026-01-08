package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.yaml.YAML;
import at.pcgamingfreaks.yaml.YamlInvalidContentException;
import at.pcgamingfreaks.yaml.YamlKeyNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;

@Slf4j
public class LastExecutionHandler {
    private final File file;
    private YAML lastFileExecution;

    public LastExecutionHandler(String path) {
        file = new File(path);
        try {
            lastFileExecution = loadLastFileExecution(file);
        } catch (YamlInvalidContentException | IOException e) {
            log.warn("Couldn't find or read {}", path, e);
        }
    }

    public YAML loadLastFileExecution(File file) throws YamlInvalidContentException, IOException {
        if (file.exists() && file.isFile()) {
            return new YAML(file);
        }
        return new YAML("");
    }

    public Date get(String path) {
        if (!lastFileExecution.isSet(path)) return null;
        try {
            return Date.from(Instant.parse(lastFileExecution.getString(path)));
        } catch (YamlKeyNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(String path) {
        update(path, Date.from(Instant.now()));
    }

    public void update(String path, Date execution) {
        if (lastFileExecution == null) return;
        lastFileExecution.set(path, execution.toInstant());
    }

    public void persist() {
        try {
            lastFileExecution.save(file);
        } catch (IOException e) {
            log.warn("", e);
        }
    }
}



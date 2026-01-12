package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Properties;

@Slf4j
public class LastExecutionHandler {
    private final File file;
    private final Properties lastFileExecution;

    public LastExecutionHandler(String path) {
        file = new File(path);
        lastFileExecution = loadLastFileExecution(file);
    }

    public Properties loadLastFileExecution(File file) {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            properties.load(in);
        } catch (IOException e) {
            log.warn("Couldn't find or read {}", file.getPath(), e);
        }
        return properties;
    }

    public Instant get(String path) {
        if (!lastFileExecution.containsKey(path)) return null;
        return Instant.parse(lastFileExecution.getProperty(path));
    }

    public void update(String path) {
        update(path, Instant.now());
    }

    public void update(String path, Instant execution) {
        if (lastFileExecution == null) return;
        lastFileExecution.put(path, execution.toString());
    }

    public void persist() {
        try (FileOutputStream out = new FileOutputStream(file)) {
            lastFileExecution.store(out, "MKVAudioSubtitleChanger - Last file execution");
        } catch (IOException e) {
            log.warn("Persisting last file execution dates failed", e);
        }
    }
}



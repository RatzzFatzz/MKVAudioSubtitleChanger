package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class LastExecutionHandlerTest {
    private static final String LAST_EXECUTION_PATH = ".";
    private static final String LAST_EXECUTION_FILE = "./last-execution.properties";
    private static final String TEST_MKV_FILE = "/arst/file.mkv";

    @AfterEach
    void destruct() {
        File file = new File(LAST_EXECUTION_FILE);
        if (file.exists()) file.delete();
    }

    @Test
    void missingFile() throws IOException {
        LastExecutionHandler underTest = new LastExecutionHandler(LAST_EXECUTION_PATH);
        assertNull(underTest.get(TEST_MKV_FILE));
        underTest.update(TEST_MKV_FILE);
        assertNotNull(underTest.get(TEST_MKV_FILE));
        underTest.persist();
        File file = new File(LAST_EXECUTION_FILE);
        assertTrue(file.exists());
        assertTrue(Files.readString(file.toPath()).contains(TEST_MKV_FILE + "="));
    }

    @Test
    void emptyFile() throws IOException {
        File file = new File(LAST_EXECUTION_FILE);
        file.createNewFile();
        missingFile(); // does the checks needed for empty file case
    }

    @Test
    void existingFileNoChanges() throws IOException {
        File file = new File(LAST_EXECUTION_FILE);
        file.createNewFile();
        Files.writeString(file.toPath(), TEST_MKV_FILE + "=" + Instant.now());
        String expected = Files.readString(file.toPath()).replace(":", "\\:");

        LastExecutionHandler underTest = new LastExecutionHandler(LAST_EXECUTION_PATH);
        assertNotNull(underTest.get(TEST_MKV_FILE));
        underTest.persist();
        File file1 = new File(LAST_EXECUTION_FILE);
        assertTrue(file1.exists());
        assertTrue(Files.readString(file.toPath()).contains(expected), "File contains expected value");
    }

    @Test
    void existingFileWithChanges() throws IOException {
        File file = new File(LAST_EXECUTION_FILE);
        file.createNewFile();
        Files.writeString(file.toPath(), TEST_MKV_FILE + "=" + Instant.now());
        String expected = Files.readString(file.toPath());

        LastExecutionHandler underTest = new LastExecutionHandler(LAST_EXECUTION_PATH);
        assertNotNull(underTest.get(TEST_MKV_FILE));
        underTest.update(TEST_MKV_FILE);
        assertNotNull(underTest.get(TEST_MKV_FILE));
        underTest.persist();
        File file1 = new File(LAST_EXECUTION_FILE);
        assertTrue(file1.exists());
        assertNotEquals(expected, Files.readString(file.toPath()));
    }
}
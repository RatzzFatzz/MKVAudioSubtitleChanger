package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class LastExecutionHandlerTest {
    private static final String LAST_EXECUTION_YML = "./last-execution.yml";

    @AfterEach
    void destruct() {
        File file = new File("./last-execution.yml");
        if (file.exists()) file.delete();
    }

    @Test
    void missingFile() throws IOException {
        LastExecutionHandler underTest = new LastExecutionHandler("./last-execution.yml");
        assertNull(underTest.get("/arst"));
        underTest.update("/arst");
        assertNotNull(underTest.get("/arst"));
        underTest.persist();
        File file = new File("./last-execution.yml");
        assertTrue(file.exists());
        assertTrue(Files.readString(file.toPath()).contains("/arst: "));
    }

    @Test
    void emptyFile() throws IOException {
        File file = new File(LAST_EXECUTION_YML);
        file.createNewFile();
        missingFile(); // does the checks needed for empty file case
    }

    @Test
    void existingFileNoChanges() throws IOException {
        File file = new File(LAST_EXECUTION_YML);
        file.createNewFile();
        Files.writeString(file.toPath(), "/arst: \"" + Instant.now() + "\"");
        String expected = Files.readString(file.toPath());

        LastExecutionHandler underTest = new LastExecutionHandler(LAST_EXECUTION_YML);
        assertNotNull(underTest.get("/arst"));
        underTest.persist();
        File file1 = new File(LAST_EXECUTION_YML);
        assertTrue(file1.exists());
        assertEquals(expected, Files.readString(file.toPath()));
    }

    @Test
    void existingFileWithChanges() throws IOException {
        File file = new File(LAST_EXECUTION_YML);
        file.createNewFile();
        Files.writeString(file.toPath(), "/arst: \"" + Instant.now() + "\"");
        String expected = Files.readString(file.toPath());

        LastExecutionHandler underTest = new LastExecutionHandler(LAST_EXECUTION_YML);
        assertNotNull(underTest.get("/arst"));
        underTest.update("/arst");
        assertNotNull(underTest.get("/arst"));
        underTest.persist();
        File file1 = new File(LAST_EXECUTION_YML);
        assertTrue(file1.exists());
        assertNotEquals(expected, Files.readString(file.toPath()));
    }
}
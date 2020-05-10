package config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.MKVToolProperties;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
public class MKVToolPropertiesTest {

    @Test
    public void testPathIsValid() {
        try(PrintWriter out = new PrintWriter("mkvDirectoryPath", "UTF-8")){
            out.print("src/test/resources");
        }catch(FileNotFoundException | UnsupportedEncodingException e){
            log.error("File not found!");
        }
        MKVToolProperties.getInstance().defineMKVToolNixPath();
        assertEquals("src/test/resources\\mkvmerge.exe", MKVToolProperties.getInstance().getMkvmergePath());

        try(PrintWriter out = new PrintWriter("mkvDirectoryPath", "UTF-8")){
            out.print("src/test/resources/");
        }catch(FileNotFoundException | UnsupportedEncodingException e){
            log.error("File not found!");
        }
        MKVToolProperties.getInstance().defineMKVToolNixPath();
        assertEquals("src/test/resources/mkvmerge.exe", MKVToolProperties.getInstance().getMkvmergePath());
    }

    @Test
    public void testCheckForSeparator() {
        try(PrintWriter out = new PrintWriter("mkvDirectoryPath", "UTF-8")){
            out.print("src/test/resources");
        }catch(FileNotFoundException | UnsupportedEncodingException e){
            log.error("File not found!");
        }
        MKVToolProperties.getInstance().defineMKVToolNixPath();
        assertTrue(MKVToolProperties.getInstance().getDirectoryPath().endsWith("/") || MKVToolProperties.getInstance().getDirectoryPath().endsWith("\\"));

        try(PrintWriter out = new PrintWriter("mkvDirectoryPath", "UTF-8")){
            out.print("src/test/resources/");
        }catch(FileNotFoundException | UnsupportedEncodingException e){
            log.error("File not found!");
        }
        MKVToolProperties.getInstance().defineMKVToolNixPath();
        assertTrue(MKVToolProperties.getInstance().getDirectoryPath().endsWith("/") || MKVToolProperties.getInstance().getDirectoryPath().endsWith("\\"));

        try(PrintWriter out = new PrintWriter("mkvDirectoryPath", "UTF-8")){
            out.print("src\\test\\resources");
        }catch(FileNotFoundException | UnsupportedEncodingException e){
            log.error("File not found!");
        }
        MKVToolProperties.getInstance().defineMKVToolNixPath();
        assertTrue(MKVToolProperties.getInstance().getDirectoryPath().endsWith("/") || MKVToolProperties.getInstance().getDirectoryPath().endsWith("\\"));

        try(PrintWriter out = new PrintWriter("mkvDirectoryPath", "UTF-8")){
            out.print("src\\test\\resources\\");
        }catch(FileNotFoundException | UnsupportedEncodingException e){
            log.error("File not found!");
        }
        MKVToolProperties.getInstance().defineMKVToolNixPath();
        assertTrue(MKVToolProperties.getInstance().getDirectoryPath().endsWith("/") || MKVToolProperties.getInstance().getDirectoryPath().endsWith("\\"));
    }

    @AfterEach
    public void afterAll() {
        File file = new File("mkvDirectoryPath");
        file.delete();
    }
}

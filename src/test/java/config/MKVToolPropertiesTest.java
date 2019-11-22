package config;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

@Log4j2
public class MKVToolPropertiesTest {

    @Test
    public void testPathIsValid() {
        try(PrintWriter out = new PrintWriter("mkvDirectoryPath", "UTF-8")){
            out.print("test/resources");
        }catch(FileNotFoundException | UnsupportedEncodingException e){
            log.error("File not found!");
        }
        try(PrintWriter out = new PrintWriter("mkvDirectoryPath", "UTF-8")){
            out.print("test/resources/");
        }catch(FileNotFoundException | UnsupportedEncodingException e){
            log.error("File not found!");
        }finally{
            File file = new File("mkvDirectoryPath");
            file.delete();
        }

    }
}

import config.MKVToolProperties;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

@Log4j2
public class Main {
    public static void main(String[] args) {
        File path = new File("mkvDirectoryPath");
        MKVToolProperties prop;
        if(!path.exists()) {
            while(true) {
                readPath();
                prop = new MKVToolProperties();
                if(prop.pathsAreValid()) {
                    break;
                }
            }
            log.info("Path is valid!");
        }else if(path.exists()) {
            prop = new MKVToolProperties();
            if(!prop.pathsAreValid()) {
                readPath();
            }
            log.info("Path is valid!");
        }
    }

    private static void readPath() {
        System.out.println("Please enter a valid path to mkvtoolnix!");
        Scanner input = new Scanner(System.in);
        try(PrintWriter out = new PrintWriter("mkvDirectoryPath", "UTF-8")){
            out.print(input.nextLine());
        }catch(FileNotFoundException | UnsupportedEncodingException e) {
            log.error("File not found!");
        }
    }
}

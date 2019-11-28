import config.MKVToolProperties;
import lombok.extern.log4j.Log4j2;
import query.QueryBuilder;

import java.util.Scanner;

@Log4j2
public class Main {


    public static void main(String[] args) {
        MKVToolProperties.getInstance().defineMKVToolNixPath();
        Scanner input = new Scanner(System.in);
        log.info("Please enter path to file");
        String path = input.nextLine();
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.executeUpdateOnAllFiles(path);
    }


}

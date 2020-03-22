import at.pcgamingfreaks.mkvaudiosubtitlechanger.AttributeUpdaterKernel;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {


    public static void main(String[] args) {
//        MKVToolProperties.getInstance().defineMKVToolNixPath();
//        GUI gui = new GUI();
        AttributeUpdaterKernel kernel = new AttributeUpdaterKernel();
        kernel.execute();
    }


}

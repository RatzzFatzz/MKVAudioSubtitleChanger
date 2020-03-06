import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import lombok.extern.log4j.Log4j2;
import query.QueryBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log4j2
public class GUI {
    private String path;
    private JButton openFileBrowser;
    private JButton startOperation;
    private JButton openProperties;
    private JTextPane outputArea;

    public GUI() {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("MKV Audio and Subtitle Changer");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel top = new JPanel(new GridLayout(1, 3, 20, 20));

        outputArea = new JTextPane();
        outputArea.setEditable(false);

        openFileBrowser = new JButton("Browse directory");
        openFileBrowser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                try{
                    if(! readFile("lastDir", Charset.defaultCharset()).isEmpty()){
                        String temp = readFile("dir.txt", Charset.defaultCharset());
                        fileChooser.setCurrentDirectory(new File(temp));
                    }
                }catch(IOException ie){
                    log.info("Couldn't start FileChooser with default path");
                }
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setPreferredSize(new Dimension(600, 500));
                Action details = fileChooser.getActionMap().get("viewTypeDetails");
                details.actionPerformed(null);
                fileChooser.showOpenDialog(null);
                try{
                    if(FileAttribute.pathIsValid(fileChooser.getSelectedFile().getAbsolutePath())){
                        path = fileChooser.getSelectedFile().getAbsolutePath();
                        try(PrintWriter out = new PrintWriter("lastDir", "UTF-8")){
                            out.print(fileChooser.getCurrentDirectory());
                        }catch(FileNotFoundException | UnsupportedEncodingException fne){
                            log.error(fne);
                        }
                        startOperation.setEnabled(true);
                    }
                }catch(NullPointerException ne){
                    outputArea.setText("File or directory not found!\n" + (outputArea.getText() == null ? "" : outputArea.getText()));
                    log.error("File or directory not found!", ne);
                }
            }
        });

        openProperties = new JButton("Open properties");
        openProperties.setEnabled(false);

        startOperation = new JButton("Start updating");
        startOperation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                QueryBuilder queryBuilder = new QueryBuilder();
                if(queryBuilder.executeUpdateOnAllFiles(path, outputArea)){
                    outputArea.setText("All files updated!\n" + (outputArea.getText() == null ? "" : outputArea.getText()));
                }
            }
        });
        startOperation.setEnabled(false);

        top.add(openFileBrowser);
        top.add(startOperation);
        top.add(openProperties);

        frame.add(top, BorderLayout.NORTH);
        frame.add(outputArea);

        frame.setVisible(true);
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}

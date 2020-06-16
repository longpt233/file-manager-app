package Application;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class FileOpenDemo extends JTextArea{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    String data = "";
    JScrollPane jScrollPane;

    public FileOpenDemo(File file){
        super();
        try {
            Scanner sc = new Scanner(file);
            //System.out.println(sc.nextLine());
            while(sc.hasNextLine()){
                data += sc.nextLine() + "\n";
                //System.out.println(data);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        setText(data);
        setEditable(false);
        
        jScrollPane = new JScrollPane(this);
        jScrollPane.setPreferredSize(new Dimension(400,300));
    }

    public static void showFileViewer(JFrame parent, File file, String title) {
        JOptionPane.showMessageDialog(parent, new FileOpenDemo(file).jScrollPane, title, JOptionPane.PLAIN_MESSAGE);
    }


}
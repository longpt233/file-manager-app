package Application;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import javax.swing.JTextField;

import Exception.DoesntExist;

/**
 * To display address to current folder
 */
public class AddressBar extends JTextField {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    FileManagerApplication program;
    String address;

    public AddressBar(FileManagerApplication program) {
        this.program = program;
        this.address = program.fileManager.getCurNode().getPath();
        setAddress(address);

        addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    goToAddress(getText());
                }
            }
        });
    }

    public void setAddress(String address) {
        this.address = address;
        setText(this.address);
    }

    public void goToAddress(String address) {
        setAddress(address);
        try {
            program.fileManager.goTo(address);
        } catch (DoesntExist e) {
            JOptionPane.showMessageDialog(program, "File or Folder doesn't exist !!!", "ERROR !!!", JOptionPane.ERROR_MESSAGE);
        }

    }
    
}
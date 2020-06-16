package Application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

import Exception.DoesntExist;
import Exception.NameAlreadyExisted;
import Node.Node;


public class FolderDisplayPanel extends JPanel{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private BufferedImage folderImg;
    private BufferedImage fileImg;
    private Node curNode;

    public FileManagerApplication program;
    private ArrayList<Element> list;
    public Element curElement;
    private JPopupMenu popupMenu;
    private JPopupMenu elementPopupMenu;

    boolean cut = false;

    public FolderDisplayPanel(FileManagerApplication program){
        super();
        list = new ArrayList<>();
        this.program = program;
        setBackground(Color.WHITE);
        FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
        setLayout(fl);
        loadIcon();
        createPopUpMenu();
        addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.getButton() == MouseEvent.BUTTON3){
                    showPopupMenu(e.getX(), e.getY());
                }
                curElement.disShowRenameField();
            }

        });
    }

    /*
    *   tao su kien nhay chuot trong ca 2 truowng hop la nhay vao file, thu muc hoac nhay
    *   vao khoang trong
    * */
    public void createPopUpMenu(){
        popupMenu = new JPopupMenu();
        JMenuItem newFolder = new JMenuItem("New Folder");
        JMenuItem newFile = new JMenuItem("New File");
        JMenuItem paste = new JMenuItem("Paste");
        JMenuItem popList[] = {newFolder, newFile, paste};
        newFolder.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                program.createNewFolder();
            }
        });
        newFile.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                program.createNewFile();
            }
        });
        paste.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //program.pasteOnCopy(program.fileManager.getCurNode());
                paste();
            }
        });

        for (int i = 0; i < popList.length; i++) {
            popList[i].setPreferredSize(new Dimension(150,25));
            popupMenu.add(popList[i]);
        }

        elementPopupMenu = new JPopupMenu();
        JMenuItem cut = new JMenuItem("Cut");
        JMenuItem copy = new JMenuItem("Copy");
        JMenuItem reName = new JMenuItem("Rename");
        JMenuItem delete = new JMenuItem("Delete");
        JMenuItem deepDelete = new JMenuItem("Deep Delete");
        
        JMenuItem[] elmList = {cut, copy, reName, delete, deepDelete};

        setActionElmPopupMenu(elmList);      // set su kien cho tung jmenu

        for (int i = 0; i < elmList.length; i++) {
            elmList[i].setPreferredSize(new Dimension(150,25));
            elementPopupMenu.add(elmList[i]);
        }


    }

    private void setActionElmPopupMenu(JMenuItem[] elmList){
        for (int i = 0; i < elmList.length; i++) {
            if(elmList[i].getText().equals("Rename")){
                elmList[i].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e){
                        reName();
                    }
                });
            }else if(elmList[i].getText().equals("Copy")){
                elmList[i].addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        saveCopyNode();
                    }
                });
            }else if(elmList[i].getText().equals("Delete")){
                elmList[i].addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        moveToRecycleBin();
                    }
                });
            }else if(elmList[i].getText().equals("Cut")){
                elmList[i].addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        saveCutNode();
                    }
                });
            }else if(elmList[i].getText().equals("Deep Delete")){
                elmList[i].addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        deleteCurElement();
                    }
                });
            }
        }
    }

    public void saveCopyNode(){
        program.setCopiedNode(curElement.node);
        cut = false;
    }

    public void saveCutNode(){
        program.setCopiedNode(curElement.node);
        cut = true;
    }

    public void paste(){
        if(cut){
            program.pasteOnCut(program.fileManager.getCurNode());
        }else{
            program.pasteOnCopy(program.fileManager.getCurNode());
        }
    }

    public void deleteCurElement(){
        int option = JOptionPane.showConfirmDialog(this, "Are you sure to delete this file/folder ???", "SURE ???", 
            JOptionPane.YES_NO_OPTION);
        if(option == JOptionPane.YES_OPTION){
            program.fileManager.deleteFolder(curElement.node);
            program.updateInfo();
        }
        
    }

    public void moveToRecycleBin(){
        program.fileManager.moveToRecycleBin(curElement.node);
        program.updateInfo();
    }

    public void reName(){
        curElement.showRenameField();
    }

    public void showPopupMenu(int x, int y){     // nhau vao khoang trong
        popupMenu.show(this, x, y);
    }

    public void showElmPopupMenu(int x, int y){   // nhay vao thu muc, file
        elementPopupMenu.show(this, x, y);
    }

    /**
     * set all elements border color and background color (but the element has current choosen node) 
     * @param borCol color of a element border
     * @param backCol color of a element backbround
     */
    public void setAllComponents(Color borCol, Color backCol){
        for (int i = 0; i < list.size(); i++) {
            if(curElement == list.get(i)) continue;

            list.get(i).disShowRenameField();

            if(list.get(i).getCurBorderColor().equals(borCol) && list.get(i).getBackground().equals(backCol)) continue;
            list.get(i).setCurBorderColor(borCol);
            list.get(i).setBackground(backCol);
            list.get(i).updateUI();
        }
    }

    public BufferedImage getImageOfNode(Node node){
        if(node.isDirectory()) return folderImg;
        return fileImg;
    }

    public void setSystemCurNode(Node node){
        this.curNode = node;
        program.getFileManager().setCurNode(node);
    }

    public void loadIcon(){
        try {
            folderImg = ImageIO.read(new File("src/Image/FolderIcon.png"));
            fileImg = ImageIO.read(new File("src/Image/FileIcon.png"));
        } catch (Exception e) {
            System.out.println("Image can't be load !!!");
        }
    }

    public Node getCurNode() {
        return curNode;
    }

    public void setCurNode(Node curNode) {
        this.curNode = curNode;
    }

    private void updateCurNode(){
        list.clear();
        this.removeAll();
        for (int i = 0; i < curNode.childNodes.size(); i++) {
            Element element = new Element(this, curNode.childNodes.get(i));
            list.add(element);
            this.add(element);
        }
        this.updateUI();
    }

    public void update(Node sysCurNode){
        this.curNode = sysCurNode;
        updateCurNode();
        //if(sysCurNode != null) program.addressBar.setAddress(sysCurNode.getPath());
    }

    public Element getCurElement(){
        return this.curElement;
    }

    public void setCurElement(Element curElement) {
        this.curElement = curElement;
    }

}
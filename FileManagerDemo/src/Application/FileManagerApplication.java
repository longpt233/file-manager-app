package Application;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import Exception.DoesntExist;
import Exception.NameAlreadyExisted;
import FileManager.FileManager;
import Node.Node;

public class FileManagerApplication extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    Tree tree;
    FileManager fileManager;
    FolderDisplayPanel displayPanel;
    int width, height;
    Node copiedNode;
    AddressBar addressBar;

    FileManagerApplication(int width, int height, String title) {
        super(title);
        fileManager = new FileManager(this);
        // fileManager.firstScan();
        fileManager.scan(fileManager.getRoot());

        this.getContentPane().setBackground(Color.WHITE);

        setPreferredSize(new Dimension(width, height));

        this.width = width;
        this.height = height;

        prepareAndShowGUI();

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();

        setVisible(true);
    }

    public void prepareAndShowGUI() {


        tree=new Tree(fileManager.getRoot());
        // SET TREE VIEW BANG CAI JTREE TAO O TREN
        JScrollPane treeView = new JScrollPane(tree.getjTree());
        treeView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        treeView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        treeView.setPreferredSize(new Dimension(300, 600));

        // jTree.setCellRenderer(ds);

        //DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) jTree.getCellRenderer();
        Icon openIcon;
        Icon leafIcon;
        try {
            openIcon = new ImageIcon("src/Image/Folder_Icon.png");
            leafIcon = new ImageIcon("src/Image/File_Icon.png");
        } catch (Exception e) {
            System.out.println("Image can't be loaded");
        }

        JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.setBackground(Color.WHITE);
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        jMenuBar.add(file);
        jMenuBar.add(edit);
        JMenuItem[] items = { new JMenuItem("New Folder"), new JMenuItem("New File") };
        for (int i = 0; i < items.length; i++) {
            if (items[i].getText().equals("New Folder")) {
                items[i].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        createNewFolder();
                    }

                });
            } else if (items[i].getText().equals("New File")) {
                items[i].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        createNewFile();
                    }

                });
            }
            items[i].setPreferredSize(new Dimension(150, 25));
            file.add(items[i]);
        }

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreePath[] paths = tree.getSelectionPaths();
                if(paths == null) return;
                for (TreePath path : paths) {
                    Node node = (Node) path.getLastPathComponent();
                    if (!node.isDirectory()){
                        FileOpenDemo.showFileViewer(fileManager.program, new File(node.getPath()), "File Viewer Demo !!!");
                        return;
                    } 
                    fileManager.scan(node);
                    node.setPre(fileManager.getCurNode());
                    fileManager.setCurNode(node);
                    addressBar.setAddress(fileManager.getCurNode().getPath());
                    // System.out.println(node.getName());
                }
                displayPanel.update(fileManager.getCurNode());
            }
        });

        /*
         * jTree.addTreeSelectionListener(new TreeSelectionListener() {
         * 
         * @Override public void valueChanged(TreeSelectionEvent e) { TreePath[] paths =
         * jTree.getSelectionPaths(); for (TreePath path : paths) { Node node = (Node)
         * path.getLastPathComponent(); fileManager.scan(node); //Node pre = node;
         * node.setPre(fileManager.getCurNode()); fileManager.setCurNode(node);
         * //System.out.println(node.getName()); //fileManager.openAFolderNode(node); }
         * displayPanel.update(fileManager.getCurNode()); // jTree.updateUI(); }
         * 
         * });
         */

        displayPanel = new FolderDisplayPanel(this);
        displayPanel.setPreferredSize(new Dimension(2 * width / 3, 0));
        displayPanel.update(fileManager.getCurNode());
        JScrollPane folderView = new JScrollPane(displayPanel);
        folderView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        folderView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        folderView.getVerticalScrollBar().setUnitIncrement(16);
        // folderView.setPreferredSize(new Dimension(500, 600));

        JButton backButton = new JButton(new ImageIcon("src/Image/arrowback.png"));
        backButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                fileManager.back();
                displayPanel.update(fileManager.getCurNode());

            }

        });

        JLabel addressJLabel = new JLabel("Address: ");
        addressBar = new AddressBar(this);

        JComponent[] jComponents1 = { treeView, folderView, jMenuBar, backButton, addressBar, addressJLabel };
        jComponents = jComponents1;
        layout(jComponents);
        addComponents(jComponents);

    }

    JComponent[] jComponents;

    /**
     * Layout all components.
     * <p>
     * If you want to re-Layout the frame of program you need to call this method
     * </p>
     * 
     * @param jComponents
     */
    public void layout(JComponent[] jComponents) {
        setLayout(null);

        jComponents[2].setBounds(0, -1, width, 30);
        int dh = jComponents[2].getBounds().height;
        jComponents[3].setBounds(0, dh - 2, 50, 25);

        jComponents[5].setBounds(jComponents[3].getBounds().width + 20, dh - 5, 60, 30);
        jComponents[4].setBounds(jComponents[3].getBounds().width + jComponents[5].getBounds().width + 20, 
            dh - 1, width - (jComponents[3].getBounds().width + 95), 25);


        dh += jComponents[3].getBounds().height;
        jComponents[0].setBounds(0, dh, width / 3, height - dh - 40);
        jComponents[1].setBounds(jComponents[0].getBounds().width + 2, dh, 2 * width / 3 - 15, height - dh - 40);
    }

    /**
     * Add all components to this Frame.
     * 
     * @param jComponents
     */
    public void addComponents(JComponent[] jComponents) {
        for (int i = 0; i < jComponents.length; i++) {
            if(jComponents[i] instanceof AddressBar){
                jComponents[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            }
            else if (!(jComponents[i] instanceof JButton) && !(jComponents[i] instanceof JMenuBar) && !(jComponents[i] instanceof JLabel))
                jComponents[i].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            
            add(jComponents[i]);
        }
    }

    /**
     * show JOptionpane to ask user name of new folder and create it
     */
    public void createNewFolder() {
        try {
            String name = JOptionPane.showInputDialog(this, "Type Name of Folder");
            if (name == null || name.equals("")) {
                return;
            }
            fileManager.createNewFolder(name);
        } catch (NameAlreadyExisted e) {
            JOptionPane.showMessageDialog(this, "Folder Name Already Exists", null, JOptionPane.PLAIN_MESSAGE);
        }

        displayPanel.update(fileManager.getCurNode());
        tree.updateUI();
    }

    /**
     * show JOptionpane to ask user name of new file and create it
     */
    public void createNewFile() {
        try {
            String name = JOptionPane.showInputDialog(this, "Type Name of File");
            if (name == null || name.equals("")) {
                return;
            }

            fileManager.createNewFile(name);
        } catch (NameAlreadyExisted e) {
            JOptionPane.showMessageDialog(this, "File Name Already Exists", null, JOptionPane.PLAIN_MESSAGE);
        }

        displayPanel.update(fileManager.getCurNode());
        tree.updateUI();
    }

    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        setUIFont(new javax.swing.plaf.FontUIResource("Segoe UI", Font.LAYOUT_LEFT_TO_RIGHT, 13));
                        if ("Windows".equals(info.getName())) {
                            UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Failed!!");
                }
                new FileManagerApplication(800, 600, "File Manager Demo");
            }
        });
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void updateInfo() {
        fileManager.getCurNode().sortChildNodes();
        tree.updateUI();
        displayPanel.update(fileManager.getCurNode());
    }

    public void pasteOnCopy(Node desNode) {
        if (copiedNode == null)
            return;
        String srcPath = copiedNode.getPath();
        String des = desNode.getPath();
        try {
            fileManager.copyNew(srcPath, des + "/" + copiedNode.getName());
            Node cloneNode = new Node(copiedNode);
            fileManager.getCurNode().addNewChild(cloneNode);
            updateInfo();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NameAlreadyExisted e) {
            int option = JOptionPane.showConfirmDialog(this, "Replace this destination ???", "Name Already Exists !!!",
                    JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    fileManager.copyReplace(srcPath, des + "/" + copiedNode.getName());
                    // Node cloneNode = new Node(copiedNode);
                    // fileManager.getCurNode().addNewChild(cloneNode);
                    updateInfo();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    public Node getCopiedNode() {
        return copiedNode;
    }

    public void setCopiedNode(Node copiedNode) {
        this.copiedNode = copiedNode;
    }

    public void pasteOnCut(Node desNode) {
        if (copiedNode == null)
            return;
        try {
            fileManager.move(copiedNode, fileManager.getCurNode());
            updateInfo();
            copiedNode = null;
        } catch (NameAlreadyExisted e) {
            int option = JOptionPane.showConfirmDialog(this, "Replace This Destination ???", "Replace ???",
                    JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    fileManager.deleteFolder((Node) desNode.getChildNode(copiedNode.getName()));
                } catch (DoesntExist e1) {
                }
                pasteOnCut(desNode);
            }
        }
        
    }

    public AddressBar getAddressBar() {
        return addressBar;
    }

    public void setAddressBar(AddressBar addressBar) {
        this.addressBar = addressBar;
    }

    public void update(String address){
        addressBar.setAddress(address);
    }

}

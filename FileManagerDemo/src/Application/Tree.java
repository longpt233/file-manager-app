package Application;

import FileManager.FileManager;
import Node.Node;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class Tree extends JTree {
    private  JTree jTree;
    private FileManager  manager;   // chua du lieu quan li
    DefaultTreeCellRenderer ds;
    public Tree(FileManager  manager) {
        this.manager=manager;
        ds = new DefaultTreeCellRenderer() {     // set lai cho tung cell trong jtree
            private int height, width;
            private static final long serialVersionUID = 1L;
            private Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                          boolean leaf, int row, boolean hasFocus) {
                JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
                        hasFocus);
                label.setBorder(border);   // set border cho cai chu
                height = 30;// leaf ? 30 : 30;
                width = leaf ? 120 : 150;      // kich thuoc cua cai chu
                return label;      // tra ve 1 component
            }

            @Override
            public Dimension getPreferredSize() {    // set size cho tong the
                Dimension d = super.getPreferredSize();
                d.height = height;
                d.width = width;
                return d;
            }
        };


        jTree = new JTree(manager.getRoot());


        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) jTree.getCellRenderer();
        Icon openIcon;
        Icon leafIcon;
        Icon closeIcon;
        try {
            openIcon = new ImageIcon("src/Image/Folder_Icon.png");
            leafIcon = new ImageIcon("src/Image/File_Icon.png");
            closeIcon=new ImageIcon("src/Image/Folder_close.png");
            renderer.setClosedIcon(closeIcon);
            renderer.setOpenIcon(openIcon);
            renderer.setLeafIcon(leafIcon);
        } catch (Exception e) {
            System.out.println("Image can't be loaded");
        }

        jTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                TreePath[] paths = jTree.getSelectionPaths();  // luu lai tat ca duong dan
                System.out.println(paths.toString());
                if(paths == null) return;
                for (TreePath path : paths) {
                    Node node = (Node) path.getLastPathComponent();
                    if (!node.isDirectory()){
                        FileOpenDemo.showFileViewer(manager.program, new File(node.getPath()), "File Viewer Demo !!!");
                        return;
                    }
                    manager.scan(node);
                    node.setPre(manager.getCurNode());
                    manager.setCurNode(node);
                    //  addressBar.setAddress(manager.getCurNode().getPath());
                    // System.out.println(node.getName());
                }
                // displayPanel.update(manager.getCurNode());
            }
        });
    }



        @Override
        public void updateUI() {
            super.updateUI();

            setCellRenderer(ds);
            setRowHeight(0);
        }
    public JTree getjTree() {
        return jTree;
    }
}





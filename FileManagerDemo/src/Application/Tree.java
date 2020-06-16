package Application;

import Node.Node;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class Tree extends JTree {
    private  JTree jTree;
    public Tree(Node root) {
        DefaultTreeCellRenderer ds;
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
        jTree = new JTree(root) {
            private static final long serialVersionUID = 1L;

            @Override
            public void updateUI() {
                super.updateUI();
                setCellRenderer(ds);
                setRowHeight(0);
            }
        };
    }

    public JTree getjTree() {
        return jTree;
    }
}

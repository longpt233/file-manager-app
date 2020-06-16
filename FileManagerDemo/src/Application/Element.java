package Application;

import Node.Node;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import Exception.*;

public class Element extends JPanel {
    private static final long serialVersionUID = 1L;

    private FolderDisplayPanel panel;
    public Node node;
    private Color curBorderColor = Color.LIGHT_GRAY;

    private JTextField reNameField;

    private Element seft;
    private Font font = new Font("Segoe UI",Font.PLAIN,18);

    public Element(FolderDisplayPanel panel, Node node) {
        this.panel = panel;
        this.node = node;
        seft = this;

        setPreferredSize(new Dimension(100, 100));
        setBackground(Color.WHITE);
        addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                panel.setCurElement(seft);
                if (e.getClickCount() == 2) {
                    try {
                        panel.program.fileManager
                                .openAFolderNode(panel.program.fileManager.getCurNode().getChildNode(node.getName()));
                    } catch (DoesntExist e1) {
                        JOptionPane.showMessageDialog(panel, "Name Doesn't Exist");
                    }
                    panel.update(panel.program.fileManager.getCurNode());
                }else if(e.getButton() == MouseEvent.BUTTON3){
                    panel.showElmPopupMenu(getLocation().x + e.getX(), getLocation().y + e.getY());
                }

                panel.setAllComponents(Color.LIGHT_GRAY, Color.WHITE);

                setSelectedEffect();
                updateUI();

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                panel.setAllComponents(Color.LIGHT_GRAY, Color.WHITE);
                setBackground(Color.MAGENTA);
                updateUI();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                if(seft == panel.curElement) {
                    setSelectedEffect();
                }
                else setBackground(Color.WHITE);
                updateUI();
            }

        });

        reNameField = new JTextField();
        reNameField.setBounds(rect);
        reNameField.setFont(font);

        reNameField.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    String text = reNameField.getText();
                    if(text == null || text.equals("")) {
                        JOptionPane.showMessageDialog(seft, "Name Will Not Be Change !!!");
                    }else{
                        try {
                            node.setName(text);
                        } catch (NameAlreadyExisted e1) {
                            JOptionPane.showMessageDialog(seft, "Name Already Exists !!!");
                            disShowRenameField();
                            return;
                        }
                        panel.program.updateInfo();
                    }
                    disShowRenameField();
                }
            }
        });

        reNameField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        setForeground(Color.WHITE);
        setLayout(null);
    }

    public void setSelectedEffect(){
        setBackground(Color.CYAN);
        curBorderColor = Color.BLUE;
    }

    /**
     * Draw a String centered in the middle of a Rectangle.
     *
     * @param g The Graphics instance.
     * @param text The String to draw.
     * @param rect The Rectangle to center the text in.
     */
    public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }
    Rectangle rect = new Rectangle(1, 74, 98, 25);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(panel.getImageOfNode(node), 20, 5, 60, 60, null);
        g.setColor(Color.BLACK);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        drawCenteredString(g, node.getName(), rect, font);
        g2d.setColor(curBorderColor);
        g2d.drawRect(0, 0, 99, 99);

    }

    public Color getCurBorderColor() {
        return curBorderColor;
    }

    public void setCurBorderColor(Color curBorderColor) {
        this.curBorderColor = curBorderColor;
    }

    public void showRenameField(){
        this.add(reNameField);
        reNameField.setHorizontalAlignment(JTextField.CENTER);
        reNameField.setText("");
        updateUI();
    }
    public void disShowRenameField(){
        this.remove(reNameField);
        updateUI();
    }



}

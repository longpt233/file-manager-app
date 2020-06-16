package Node;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.tree.TreeNode;

import Exception.DoesntExist;
import Exception.NameAlreadyExisted;

public class Node implements TreeNode {

    public enum Type{
        FILE, FOLDER
    }

    public Type type;

    Node pre;
    String name;
    Node parent;
    public ArrayList<Node> childNodes;

    /**
     *  1 Folder or 1 file will be a node in the file system manager
     * @param parent
     * @param name
     */
    public Node(Node parent, String name) {
        this.parent = parent;
        this.name = name;
        this.childNodes = new ArrayList<>();
    }
    public Node(Node parent, String name, Type type) {
        this.parent = parent;
        this.name = name;
        this.childNodes = new ArrayList<>();
        this.type = type;
    }
    public Node(Node node) {      // coppy constructor
        this.parent = node.parent;
        this.name = node.name;
        this.childNodes = new ArrayList<>();
        this.type = node.type;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        if(childIndex < 0 || childIndex > childNodes.size() - 1) return null;
        return childNodes.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return childNodes.size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return 0;
    }

    public int indexOf(String childName){
        for (int i = 0; i < this.childNodes.size(); i++) {
            if(this.childNodes.get(i).name.equals(childName)){
                return i;
            }
        }
        return -1;
    }

    public Node getChildNode(String childNodeName) throws DoesntExist {
        int index = indexOf(childNodeName);
        if(index < 0) throw new DoesntExist();
        return this.childNodes.get(index);
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        if(type == Type.FILE) return true;
        
        //if(childNodes.size() == 0) return true;
        return false;
    }

    @Override
    public Enumeration<? extends TreeNode> children() {
        Iterator<? extends TreeNode> iterator = childNodes.iterator();
        Enumeration<? extends TreeNode> result = new Enumeration<TreeNode>() {

            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public TreeNode nextElement() {
                return iterator.next();
            }
        };
        return result;
    }

    /**
     * 
     * @return true if this node is a folder or direction,
     *          false if this node is a file
     */
    public boolean isDirectory(){
        return type == Type.FOLDER;
    }

    public void scanChildNode(){
        File file = new File(this.getPath());
        if(file.isDirectory()){
            try {
                listFilesOrFolder(file);
            } catch (NameAlreadyExisted e) {
                System.out.println(e.getMessage());
            }
            sortChildNodes();
        }
    }

    public void listFilesOrFolder(final File folder) throws NameAlreadyExisted {
        for (final File fileEntry : folder.listFiles()) {
            if(fileEntry.isDirectory()){
                addNewChild(new Node(this, fileEntry.getName(), Type.FOLDER));
            }else{
                addNewChild(new Node(this, fileEntry.getName(), Type.FILE));
            }
            
        }
    }

    public void addChild(Node child){
        if(child == null) return;
        //if(contains(child)) return;
        childNodes.add(child);
        child.setParent(this);
    }

    public void addNewChild(Node child) throws NameAlreadyExisted {
        if(child == null) return;
        if(contains(child)) {
            throw new NameAlreadyExisted();
        }
        childNodes.add(child);
        child.setParent(this);
    }

    public void setParent(Node parent){
        if(parent == null) return;
        this.parent = parent;
    }

    /**
     * 
     * @param node
     * @return true if node has already in childNodes of this node.
     */
    public boolean contains(Node node){
        if(node == null) throw new NullPointerException();
        for (int i = 0; i < childNodes.size(); i++) {
            if(childNodes.get(i).name.equals(node.name)) return true;
        }
        return false;
    }

    public String getPath(){
        if(parent != null) return parent.getPath() + "/" + this.name;
        return this.name;
    }

    public String getInfo(){
        String data = "";
        for (int i = 0; i < childNodes.size(); i++) {
            data = data + "---" + childNodes.get(i).getName();
        }
        return data;
    }

    public String getName() {
        return this.name;
    }



    public Node getPre() {
        return pre;
    }

    public void setPre(Node pre) {
        this.pre = pre;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Change the current name of folder reference by this node to
     * {@code}newName{@code}
     * 
     * @param newName new name of node
     * @throws NameAlreadyExisted
     */
    public void setName(String newName) throws NameAlreadyExisted {
        Node parent = (Node)getParent();
        Node temp = null;
        try {
            temp = parent.getChildNode(newName);
        } catch (DoesntExist e1) {
            
        }
        if(temp != null){
            throw new NameAlreadyExisted();
        }
        String path = getPath();
        int j;
        for(j=path.length()-1; j>0; j--){
            if(path.charAt(j) == '/')
                break;
        }
        
        String tempPath = path.substring(0,j);
        String newPath = tempPath + "/" + newName;

        File curFile = new File(path);
        File newFile = new File(newPath);
        try {
            curFile.renameTo(newFile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        this.name = newName;
        
    }

    public void sortChildNodes(){
        this.childNodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.name.compareTo(o2.name);
            }
        });
    }
    
}
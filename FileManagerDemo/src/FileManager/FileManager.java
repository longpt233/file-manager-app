package FileManager;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import Application.FileManagerApplication;
import Application.FileOpenDemo;
import Exception.DoesntExist;
import Exception.NameAlreadyExisted;
import Node.Node;

public class FileManager {

    public FileManagerApplication program;
    Node root;
    Node curNode;
    Node curChosenNode;

    /**
     * 1 file or 1 folder is a node in the file system tree.
     * <p>
     * This class is used to manager that tree.
     * <p>
     */
    public FileManager() {
        root = new Node(null, "Root", Node.Type.FOLDER);
        curNode = root;
    }

    public FileManager(FileManagerApplication program) {
        root = new Node(null, "Root", Node.Type.FOLDER);
        curNode = root;
        this.program = program;
    }

    public void scan(Node node) {

        if(node.childNodes.size() == 0 && node.isDirectory())
            node.scanChildNode();
    }

    /**
     * Create new folder (or direction) or file in current folder
     * 
     * @param newNode
     * @throws NameAlreadyExisted
     */
    public void addNewNode(Node newNode) throws NameAlreadyExisted {
        if (!curNode.contains(newNode))
            curNode.addChild(newNode);
        else {
            throw new NameAlreadyExisted();
        }
    }

    public void addNewNode(Node newNode, Node parent) throws NameAlreadyExisted {
        if (!parent.contains(newNode))
            parent.addChild(newNode);
        else {
            throw new NameAlreadyExisted();
        }
    }

    /**
     * Create new folder (or direction) or file in current folder
     * 
     * @param newNode
     * @throws NameAlreadyExisted
     */
    public Node addNewNode(Node parent, String name, Node.Type type) throws NameAlreadyExisted {
        Node node = new Node(parent, name, type);
        addNewNode(node, parent);
        return node;
    }

    /**
     * Open a file or folder in current folder that is being reference by
     * {@code}curNode{@code}
     * <p>If it is a file open it in {@code}FileOpenDemo.java{@code}</p>
     * 
     * @param node
     */
    public void openAFolderNode(Node node) {
        if (node.type == Node.Type.FILE){
            FileOpenDemo.showFileViewer(program, new File(node.getPath()), "File Viewer Demo !!!");
            return;
        }
        scan(node);
        node.setPre(curNode);
        curNode = node;
        program.update(curNode.getPath());
    }

    public void back() {
        if (curNode.getPre() == null)
            return;
        Node pre = curNode;
        curNode = curNode.getPre();
        pre.setPre(null);
        program.update(curNode.getPath());
    }

    public void printCurNodeInfo() {
        System.out.println(curNode.getInfo());
    }

    /**
     * Create new folder in the current folder (that is being reference by
     * {@code}curNode{@code})
     * 
     * @param name of file
     */
    public void createNewFolder(String name) throws NameAlreadyExisted {
        Node newNode = addNewNode(curNode, name, Node.Type.FOLDER);
        File theDir = new File(newNode.getPath());
        if (!theDir.exists()) {
            System.out.println("creating directory: " + theDir.getName());
            boolean result = false;
            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                System.out.println(se.getMessage());
            }
            if (result) {
                System.out.println("DIR created");
            }
        }
        curNode.sortChildNodes();
    }

    /**
     * Create new file in the current folder (that is being reference by
     * {@code}curNode{@code})
     * 
     * @param name of file
     */
    public void createNewFile(String name) throws NameAlreadyExisted {
        Node newNode = addNewNode(curNode, name, Node.Type.FILE);
        File theDir = new File(newNode.getPath());
        if (!theDir.exists()) {
            System.out.println("creating file: " + theDir.getName());
            boolean result = false;
            try {
                theDir.createNewFile();
                result = true;
            } catch (SecurityException se) {
                System.out.println(se.getMessage());
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
            if (result) {
                System.out.println("file created");
            }
        }
        curNode.sortChildNodes();
    }



    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public Node getCurNode() {
        return curNode;
    }

    public void setCurNode(Node curNode) {
        this.curNode = curNode;
    }

    public Node getCurChosenNode() {
        return curChosenNode;
    }

    public void setCurChosenNode(Node curChosenNode) {
        this.curChosenNode = curChosenNode;
    }

    private void firstScan(Node root) {
        if (root.type == Node.Type.FOLDER) {
            scan(root);
            for (int i = 0; i < root.childNodes.size(); i++) {
                firstScan(root.childNodes.get(i));
            }
        } else {
            return;
        }
    }

    public void firstScan() {
        firstScan(root);
    }

    /**
     * Go to a folder
     * @param path
     */
    public void goToFolder(String path) {
        String[] nodesPath = path.replace("[", "").replace("]", "").split(", ");
        ArrayList<String> nodes = new ArrayList<>(Arrays.asList(nodesPath));
        nodes.remove(nodes.get(0));
        Node node = getRoot();
        while (nodes.size() != 0) {
            try {
                node = node.getChildNode(nodes.remove(0));
            } catch (DoesntExist e) {
                System.out.println(e.getMessage());
            }
        }
        curNode = node;
        scan(curNode);
    }

    /**
     * Copy a file or folder, return if File or Folder 's name already exists
     * @param srcPath path to source file or folder
     * @param desPath path to destination file or folder to paste
     * @throws IOException
     * @throws NameAlreadyExisted if File or Folder 's name already exists
     */
    public void copyNew(String srcPath, String desPath) throws IOException, NameAlreadyExisted {
        File sourceFolder = new File(srcPath);
        File destinationFolder = new File(desPath);
        if(destinationFolder.exists()) throw new NameAlreadyExisted();
        copyFolder(sourceFolder, destinationFolder);
    }
    /**
     * Copy a file or folder, replace if File or Folder 's name already exists
     * @param srcPath path to source file or folder
     * @param desPath path to destination file or folder to paste
     * @throws IOException
     */
    public void copyReplace(String srcPath, String desPath) throws IOException {
        File sourceFolder = new File(srcPath);
        File destinationFolder = new File(desPath);
        copyFolder(sourceFolder, destinationFolder);
    }

    private static void copyFolder(File sourceFolder, File destinationFolder) throws IOException {
        //Check if sourceFolder is a directory or file
        //If sourceFolder is file; then copy the file directly to new location
        
        if (sourceFolder.isDirectory()) {
            //Verify if destinationFolder is already present; If not then create it
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();
                //System.out.println("Directory created :: " + destinationFolder);
            }
             
            //Get all files from source directory
            String files[] = sourceFolder.list();
             
            //Iterate over all files and copy them to destinationFolder one by one
            for (String file : files) {
                File srcFile = new File(sourceFolder, file);
                File destFile = new File(destinationFolder, file);
                 
                //Recursive function call
                copyFolder(srcFile, destFile);
            }
        }else{
            //Copy the file content from one place to another 
            Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
            //System.out.println("File copied :: " + destinationFolder);
        }
    }

    public void deleteFolder(Node node){
        scan(node);
        if(node.childNodes.size() != 0){
            for (int i = 0; i < node.childNodes.size(); i++) {
                deleteFolder(node.childNodes.get(i));
                i--;
            }
        }
        File delFile = new File(node.getPath());
        delFile.delete();
        
        Node parent = (Node)node.getParent();
        parent.childNodes.remove(node);
    }

    public void moveToRecycleBin(Node node){
        node.childNodes.clear();
        Node parent = (Node)node.getParent();
        parent.childNodes.remove(node);
        File delFile = new File(node.getPath());
        node = null;
        Desktop desktop = Desktop.getDesktop();
        desktop.moveToTrash(delFile);
    }

    public void move(Node srcNode, Node desNode) throws NameAlreadyExisted {
        File sourceFile = new File(srcNode.getPath());
        File destFile = new File(desNode.getPath() + "/" + srcNode.getName());
        
        if (sourceFile.renameTo(destFile)) {
            System.out.println("File moved successfully");
        } else {
            System.out.println("Failed to move file");
        }
        Node parent = (Node)srcNode.getParent();
        desNode.addNewChild(srcNode);
        parent.childNodes.remove(srcNode);
        
    }

    public void goTo(String path) throws DoesntExist {
        String paths[] = path.split("/");
        Node pointer = root;
        
        for (int i = 1; i < paths.length; i++) {
            pointer = pointer.getChildNode(paths[i]);
            scan(pointer);
        }
        curNode = pointer;
        program.updateInfo();
    }

}
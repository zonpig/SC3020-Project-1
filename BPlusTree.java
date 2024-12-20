import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BPlusTree implements Serializable {
    private static final long serialVersionUID = 1L;

    private Node rootNode;

    public BPlusTree(String bPlusFileName) throws IOException {
        rootNode = new LeafNode();
    }

    public void serializeTree(String fileName) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(fileName);
                ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(this);
        }
    }

    public static BPlusTree deserializeTree(String fileName) throws IOException, ClassNotFoundException {
        BPlusTree tree;
        try (FileInputStream fileIn = new FileInputStream(fileName);
                ObjectInputStream in = new ObjectInputStream(fileIn)) {
            tree = (BPlusTree) in.readObject();
        }
        return tree;
    }

    // count the number of nodes in this B+ tree
    public int countNodes() {
        return rootNode.countNodes();
    }

    // count the number of levels in this B+ tree
    public int countLevels() {
        if (rootNode.getClass().toString().equals("class LeafNode"))
            return 1;
        int numOfLevels = 1;
        InternalNode internalNode = (InternalNode) rootNode;
        Node childNode;
        while (true) {
            childNode = internalNode.getChildPointers()[0];
            if (childNode.isLeaf()) {
                numOfLevels++;
                break;
            }
            internalNode = (InternalNode) childNode;
            numOfLevels++;
        }
        return numOfLevels;
    }

    public void rootNodeContent() {
        System.out.print("Keys in the root node are:");
        for (int i = 0; i < Node.n; i++) {
            if (rootNode.getKeys()[i] == Float.MAX_VALUE)
                break;
            System.out.print(" " + rootNode.getKeys()[i]);
        }
        System.out.println();
    }

    // retrieve root node of this B+ tree
    public Node getRootNode() {
        return rootNode;
    }

    public void enumerateNodes() {
        rootNode.enumerateNodes();
    }

    public void bulkLoad(ArrayList<Address> addressList) {
        // Find number of unique keys
        // Create a Set to store unique keys
        Set<Float> uniqueKeys = new HashSet<>();

        // Iterate through addressList and add keys to the Set
        for (Address address : addressList) {
            uniqueKeys.add(address.getBlock().getRecords()[address.getIndex()].getFg_pct_home());
        }

        // Get the count of unique keys
        int uniqueKeyCount = uniqueKeys.size();

        // Create Leaf Nodes
        int numberOfLeafNodes = (int) Math.ceil((double) uniqueKeyCount / Node.n);
        int numberOfKeysLastNode = uniqueKeyCount % Node.n;
        ArrayList<LeafNode> listOfLeafs = new ArrayList<LeafNode>();
        for (int leaf = 0; leaf < numberOfLeafNodes; leaf++) {
            listOfLeafs.add(new LeafNode());
        }

        // Set NextLeafNode
        for (int i = 0; i < numberOfLeafNodes - 1; i++) {
            listOfLeafs.get(i).setNextLeafNode(listOfLeafs.get(i + 1));
        }

        // insert records into leaf nodes
        int insertPos = 0;
        int leafnum = 0;
        float prevKey = -10000; // set impossible value
        float curKey = 0;
        LeafNode curLeaf = null;
        for (Address address : addressList) {
            curKey = address.getBlock().getRecords()[address.getIndex()].getFg_pct_home();
            curLeaf = listOfLeafs.get(leafnum);
            if (prevKey == curKey) {
                curLeaf.bulkInsertDupli(address, insertPos - 1);
            } else if (leafnum == numberOfLeafNodes - 2 && numberOfKeysLastNode < (Node.n + 1) / 2
                    && insertPos > Math.ceil((Node.n + numberOfKeysLastNode) / 2)) {
                leafnum += 1;
                curLeaf = listOfLeafs.get(leafnum);
                insertPos = 0;
                curLeaf.bulkInsert(address, curKey, insertPos);
                insertPos++;
                prevKey = curKey;
            } else if (insertPos < Node.n) {
                curLeaf.bulkInsert(address, curKey, insertPos);
                insertPos++;
                prevKey = curKey;
            } else {
                leafnum += 1;
                curLeaf = listOfLeafs.get(leafnum);
                insertPos = 0;
                curLeaf.bulkInsert(address, curKey, insertPos);
                insertPos++;
                prevKey = curKey;
            }
        }
        // Create 1st Level Child Nodes
        if (numberOfLeafNodes > 1) {
            int numberOfL1ChildNodes = (int) Math.ceil((double) numberOfLeafNodes / (Node.n + 1));
            numberOfKeysLastNode = numberOfLeafNodes % Node.n;
            ArrayList<InternalNode> listOfL1ChildNodes = new ArrayList<InternalNode>();
            for (int childL1 = 0; childL1 < numberOfL1ChildNodes; childL1++) {
                listOfL1ChildNodes.add(new InternalNode());
            }

            // insert parent nodes from leaf nodes
            InternalNode curNode = null;
            int L1ChildNum = 0;
            insertPos = -1;
            float firstKey;
            for (LeafNode leaf : listOfLeafs) {
                // get first key of leaf
                firstKey = leaf.getSubtreeLB();
                if (insertPos == -1 || insertPos >= Node.n) {
                    if (insertPos >= Node.n) {
                        L1ChildNum++;
                    }
                    curNode = listOfL1ChildNodes.get(L1ChildNum);
                    leaf.setParent(curNode);
                    // put inside the internal node
                    curNode.bulkInsertFirst(leaf);
                    insertPos = 0;
                } else if (L1ChildNum == numberOfL1ChildNodes - 2 && numberOfKeysLastNode < (Node.n) / 2
                        && insertPos > Math.ceil((Node.n + numberOfKeysLastNode) / 2)) {
                    L1ChildNum++;
                    curNode = listOfL1ChildNodes.get(L1ChildNum);
                    leaf.setParent(curNode);
                    curNode.bulkInsertFirst(leaf);
                    insertPos = 0;
                } else {
                    curNode = listOfL1ChildNodes.get(L1ChildNum);
                    leaf.setParent(curNode);
                    // put inside the internal node
                    curNode.bulkInsert(firstKey, insertPos, leaf);
                    insertPos++;
                }
            }

            ArrayList<InternalNode> oldlistOfChildNodes = listOfL1ChildNodes;
            int numberOfChildNodes = (int) Math.ceil((double) numberOfL1ChildNodes / (Node.n + 1));
            numberOfKeysLastNode = numberOfL1ChildNodes % Node.n;
            while (numberOfChildNodes > 1) {
                ArrayList<InternalNode> newlistOfChildNodes = new ArrayList<InternalNode>();
                for (int child = 0; child < numberOfChildNodes; child++) {
                    newlistOfChildNodes.add(new InternalNode());
                }

                // insert child nodes
                int childNum = 0;
                insertPos = -1;
                for (InternalNode node : oldlistOfChildNodes) {
                    // get first key of child
                    firstKey = node.getSubtreeLB();
                    if (insertPos == -1 || insertPos >= Node.n) {
                        if (insertPos >= Node.n) {
                            childNum++;
                        }
                        curNode = newlistOfChildNodes.get(childNum);
                        node.setParent(curNode);
                        // put inside the internal node
                        curNode.bulkInsertFirst(node);
                        insertPos = 0;
                    } else if (childNum == numberOfChildNodes - 2 && numberOfKeysLastNode < (Node.n) / 2
                            && insertPos > Math.ceil((Node.n + numberOfKeysLastNode) / 2)) {
                        // 2nd last node & last node has fewer than minimum keys & insertPos > half of
                        // last 2 nodes
                        childNum++;
                        curNode = newlistOfChildNodes.get(childNum);
                        node.setParent(curNode);
                        curNode.bulkInsertFirst(node);
                        insertPos = 0;
                    } else {
                        curNode = newlistOfChildNodes.get(childNum);
                        node.setParent(curNode);
                        // put inside the internal node
                        curNode.bulkInsert(firstKey, insertPos, node);
                        insertPos++;
                    }
                }
                oldlistOfChildNodes = newlistOfChildNodes;
                numberOfChildNodes = (int) Math.ceil((double) numberOfChildNodes / Node.n);
                numberOfKeysLastNode = numberOfChildNodes % Node.n;
            }
            rootNode = curNode;
        } else {
            rootNode = curLeaf;
        }
    }

    // insert record into B+ tree
    public void insertRecord(Address address) {
        rootNode.insertRecord(address);
        if (rootNode.getParent() != null)
            rootNode = rootNode.getParent();
    }

    // search for records with "FG_PCT_home" within lowerKey and upperKey, both
    // inclusively
    // number of index nodes process access
    // number of data blocks the process access
    // average of "FG3_PCT_home" of the records returned
    // running time of this range query
    public float rangeQuery(float lowerKey, float upperKey) {
        return rootNode.rangeQuery(lowerKey, upperKey);

    }

}

// child class of node, has max of n pointers pointing to actual records, and 1 pointer pointing to next leaf node
// has minimum of floor((n+1)/2) keys

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LeafNode extends Node {
    private static final long serialVersionUID = 1L;

    // Has a total of n+1 pointers
    private List<Address>[] dataPointers; // 4 bytes * n, pointers to ArrayList of references to actual records stored
                                          // in blocks
    private Node nextLeafNode; // pointer to the neighboring leaf node

    @SuppressWarnings("unchecked")
    public LeafNode() {
        super();
        dataPointers = (List<Address>[]) new List[n]; // Suppress unchecked conversion warning
        Arrays.fill(dataPointers, new ArrayList<Address>());
        nextLeafNode = null;
    }

    public Node getNextLeafNode() {
        return nextLeafNode;
    }

    public void setNextLeafNode(Node nextLeafNode) {
        this.nextLeafNode = nextLeafNode;
    }

    public List<Address>[] getDataPointers() {
        return dataPointers;
    }

    public void bulkInsert(Address address, float key, int insertPos) {
        this.keys[insertPos] = key;
        this.dataPointers[insertPos] = new ArrayList<Address>();
        this.dataPointers[insertPos].add(address);
    }

    public void bulkInsertDupli(Address address, int insertPos) {
        this.dataPointers[insertPos].add(address);
    }

    public void insertRecord(Address address) {
        Block block = address.getBlock();
        int index = address.getIndex();
        float key = block.getRecords()[index].getFg_pct_home();

        if (!isFull() || containsKey(key)) {
            int insertPos = 0;

            // Find the position to insert the new key
            while (insertPos < Node.n && this.keys[insertPos] <= key) {
                if (this.keys[insertPos] == key) {
                    // Handle duplicate key
                    this.dataPointers[insertPos].add(address);
                    return; // Exit early if we inserted into the linked list
                }
                insertPos++;
            }

            // Shift keys and pointers right if necessary
            if (insertPos < Node.n && this.keys[insertPos] != Float.MAX_VALUE) {
                for (int i = Node.n - 1; i > insertPos; i--) {
                    this.keys[i] = this.keys[i - 1];
                    this.dataPointers[i] = this.dataPointers[i - 1];
                }
            }

            // Insert the new key and update the data pointer
            this.keys[insertPos] = key;
            this.dataPointers[insertPos] = new ArrayList<>();
            this.dataPointers[insertPos].add(address);

        } else { // Leaf node is full, need to split
            int insertPos = 0;

            // Find position for the key in the current node
            while (insertPos < Node.n && this.keys[insertPos] <= key) {
                insertPos++;
            }

            LeafNode rightChild = new LeafNode();
            if (this.nextLeafNode != null) {
                rightChild.setNextLeafNode(this.nextLeafNode); // Link to next node
            }
            this.setNextLeafNode(rightChild);

            int mid = (Node.n + 1) / 2; // Determine mid point for splitting

            // Distributing keys and pointers
            for (int i = mid; i < Node.n; i++) {
                rightChild.keys[i - mid] = this.keys[i];
                rightChild.dataPointers[i - mid] = this.dataPointers[i];
                this.keys[i] = Float.MAX_VALUE; // Mark as empty
                this.dataPointers[i] = null; // Clear data pointer
            }

            // Insert the new address into the appropriate child
            if (insertPos < mid) {
                this.insertRecord(address);
            } else {
                rightChild.insertRecord(address);
            }

            // Handle parent node creation or linking
            if (this.getParent() == null) {
                InternalNode parentNode = new InternalNode();
                this.setParent(parentNode);
                rightChild.setParent(parentNode);
                parentNode.getChildPointers()[0] = this;
                parentNode.getChildPointers()[1] = rightChild;
                parentNode.getKeys()[0] = rightChild.getSubtreeLB();
            } else {
                this.getParent().insertChild(rightChild);
                rightChild.setParent(this.getParent());
            }
        }
    }

    public void enumerateNodes() {
        System.out.print("Keys in this leaf node are:");
        for (int i = 0; i < Node.n; i++) {
            if (this.keys[i] == Float.MAX_VALUE)
                break;
            System.out.print(" " + this.keys[i]);
            if (this.keys[i] != this.dataPointers[i].get(0).getBlock().getRecords()[this.dataPointers[i].get(0)
                    .getIndex()].getFg_pct_home()) {

            }
        }
        System.out.println();
        System.out.println("The parent is: " + this.getParent());

        if (this.getNextLeafNode() != null) {
            this.getNextLeafNode().enumerateNodes();
        }
    }

    public float rangeQuery(float lowerKey, float upperKey) {
        boolean resultFound = false;
        float resultSum = 0;
        float result;

        Block block;
        int index;
        Record record;

        List<Address> addresses;
        Set<Block> scannedBlocks = new HashSet<>();

        boolean exceeded = false; // check if exceeded upperKey already
        LeafNode current = this;
        int numRecords = 0;

        // Search for records with the key >= lowerKey and <= upperKey
        while (!exceeded) {
            for (int i = 0; i < n; i++) {
                if (current.getKeys()[i] != Float.MAX_VALUE && current.getKeys()[i] >= lowerKey
                        && current.getKeys()[i] <= upperKey) {
                    resultFound = true;
                    addresses = current.getDataPointers()[i];
                    for (Address address : addresses) {
                        block = address.getBlock();
                        index = address.getIndex();
                        record = block.getRecords()[index];
                        scannedBlocks.add(block);
                        resultSum += record.getFg3_pct_home();
                        numRecords++;
                        // System.out.println(record.getFg_pct_home());
                    }
                    continue;
                } else if (current.getKeys()[i] != Float.MAX_VALUE && current.getKeys()[i] > upperKey) {
                    System.out.println("current key is " + current.getKeys()[i]);
                    System.out.println("Upper limit exceeded!");
                    exceeded = true;
                    break; // Since keys are sorted, no more matching keys will be found
                }
            }
            current = (LeafNode) current.getNextLeafNode();
            if (current == null)
                break;
        }

        System.out.printf("No. of data block accesses: %d\n", (int) scannedBlocks.size());
        System.out.printf("No. of records found: %d\n", (int) numRecords);

        // key not found
        if (!resultFound) {
            return -1;
        }

        result = resultSum / numRecords;

        return result;

    }

    // for leaf node, there is no subtree, so the lower bound exists in this node as
    // the first key
    public float getSubtreeLB() {
        return this.keys[0];
    }

    public boolean containsKey(float key) {
        for (float k : this.keys) {
            if (key == k)
                return true;
        }

        return false;
    }

    public boolean isLeaf() {
        return true;
    }

    public int countNodes() {
        if (this.parent == null)
            return 1;
        return 0;
    }
}

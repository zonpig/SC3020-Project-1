import java.util.ArrayList;

public class BPlusTree {
    private Node rootNode;

    public BPlusTree() {
        rootNode = new LeafNode();
    }

    // count the number of nodes in this B+ tree
    public int countNodes() {
        // IMPLEMENTATION
        return rootNode.countNodes();
    }

    // count the number of levels in this B+ tree
    public int countLevels() {
        // IMPLEMENTATION
        if(rootNode.getClass().toString().equals("class LeafNode")) return 1;
        int numOfLevels = 1;
        InternalNode internalNode = (InternalNode) rootNode;
        Node childNode;
        while(true) {
            childNode = internalNode.getChildPointers()[0];
            if(childNode.isLeaf()) {
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
        for(int i=0; i<Node.n; i++) {
            if(rootNode.getKeys()[i] == Float.MAX_VALUE) break;
            System.out.print(" " + rootNode.getKeys()[i]);
        }
        System.out.println();
    }

    // retrieve root node of this B+ tree
    // will likely use this for expriement 2, to display the keys of the root node
    public Node getRootNode() {
        return rootNode;
    }

    public void enumerateNodes() {
        rootNode.enumerateNodes();
    }

    public void bulkLoad(ArrayList<Address> addressList, int numRecords){
        // Sorting the list by fg_pct_home
        System.out.println("Sorting the list by fg_pct_home");
        addressList.sort((a1, a2) -> Float.compare(
            a1.getBlock().getRecords()[a1.getIndex()].getFg_pct_home(),
            a2.getBlock().getRecords()[a2.getIndex()].getFg_pct_home()
        ));

        // Create Leaf Nodes
        int numberOfLeafNodes = (int) Math.ceil((double) numRecords / Node.n);
        // System.out.println("Number of leaf nodes: " + numberOfLeafNodes);
        ArrayList<LeafNode> listOfLeafs = new ArrayList<LeafNode>();
        for (int leaf=0; leaf < numberOfLeafNodes; leaf++) {
            listOfLeafs.add(new LeafNode());
            // System.out.println("Add leaf node: " + leaf);
        }

        // Set NextLeafNode
        for (int i = 0; i < numberOfLeafNodes - 1; i++){
            listOfLeafs.get(i).setNextLeafNode(listOfLeafs.get(i+1));
        }

        // insert records into leaf nodes
        int insertPos = 0;
        int leafnum = 0;
        float prevKey = -10000; // set impossible value
        float curKey = 0;
        LeafNode curLeaf = null;
        InternalNode curNode = null;
        for (Address address : addressList) {
            curKey = address.getBlock().getRecords()[address.getIndex()].getFg_pct_home();
            curLeaf = listOfLeafs.get(leafnum);
            if (prevKey == curKey){
                curLeaf.bulkInsertDupli(address,insertPos-1);
            }
            else if (insertPos < Node.n) {
                curLeaf.bulkInsert2(address,curKey, insertPos);
                insertPos++;
                prevKey = curKey;
            }
            else {
                leafnum +=1;
                curLeaf = listOfLeafs.get(leafnum);
                insertPos = 0;
                curLeaf.bulkInsert2(address,curKey, insertPos);
                insertPos++;
                prevKey = curKey;
            }
        }

        rootNode = curLeaf;

        // Create 1st Level Child Nodes
        if (numberOfLeafNodes > 1) {
            int numberOfL1ChildNodes = (int) Math.ceil((double) numberOfLeafNodes / Node.n);
            ArrayList<InternalNode> listOfL1ChildNodes = new ArrayList<InternalNode>();
            for (int childL1=0; childL1 < numberOfL1ChildNodes; childL1++) {
                listOfL1ChildNodes.add(new InternalNode());
            }

            // insert child nodes from leaf nodes
            int L1ChildNum = 0;
            insertPos = 0;
            float firstKey;
            for (LeafNode leaf: listOfLeafs){
                //get first key of leaf
                firstKey = leaf.getSubtreeLB();
                if (insertPos < Node.n) {
                    curNode = listOfL1ChildNodes.get(L1ChildNum);
                    leaf.setParent(curNode);
                    // put inside the internal node
                    curNode.bulkInsert2(firstKey, insertPos,leaf);
                    insertPos++;
                    }
                else {
                    L1ChildNum++;
                    insertPos = 0;
                    curNode = listOfL1ChildNodes.get(L1ChildNum);
                    leaf.setParent(curNode);
                    curNode.bulkInsert2(firstKey, insertPos,leaf);
                    insertPos++;
                } 
            }

            int numberOfChildNodes = (int) Math.ceil((double) numberOfL1ChildNodes / Node.n);
            while (numberOfChildNodes > Node.n){
                ArrayList<InternalNode> listOfChildNodes = new ArrayList<InternalNode>();
                for (int child=0; child < numberOfChildNodes; child++) {
                    listOfChildNodes.add(new InternalNode());
                }

                // insert child nodes
                int childNum = 0;
                insertPos = 0;
                for (InternalNode node: listOfChildNodes){
                    //get first key of child
                    firstKey = node.getSubtreeLB();
                    if (insertPos < Node.n) {
                        curNode = listOfChildNodes.get(childNum);
                        node.setParent(curNode);
                        // put inside the internal node
                        curNode.bulkInsert2(firstKey, insertPos,node);
                        insertPos++;
                    }
                    else {
                        childNum++;
                        insertPos = 0;
                        curNode = listOfChildNodes.get(childNum);
                        node.setParent(curNode);
                        curNode.bulkInsert2(firstKey, insertPos,node);
                        insertPos++;
                    }
                }
                numberOfChildNodes = (int) Math.ceil((double) numberOfChildNodes / Node.n);
            }
        }

        if (curNode!=null){
            rootNode = curNode;
        }
}


    // insert record into B+ tree
    public void insertRecord(Address address) {
        // IMPLEMENTATION
        rootNode.insertRecord(address);
        if(rootNode.getParent() != null) rootNode = rootNode.getParent();
    }


    // search for records with "FG_PCT_home" equal to certain value and return the average "FG3_PCT_home" of those records
    // need to report number of index nodes AND number of data blocks the process access, maybe can use some sort of print statement within this method
    // need to report running time of this search query
    public float searchQuery(float key) {
        return rootNode.searchQuery(key);
    }

    // search for records with "FG_PCT_home" within lowerKey and upperKey, both inclusively, and return the average "FG3_PCT_home" of those records
    // need to report number of index nodes AND number of data blocks the process access, maybe can use some sort of print statement within this method
    // need to report running time of this range query
    public float rangeQuery(float lowerKey, float upperKey) {
        // IMPLEMENTATION
        return rootNode.rangeQuery(lowerKey, upperKey);
        
    }
}

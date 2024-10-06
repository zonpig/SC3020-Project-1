import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Disk {
    private final int DISK_SIZE;
    private final int MAX_BLOCKS;
    private final String diskFileName;
    private ArrayList<Integer> availIndex = new ArrayList<>(); // keep track of the available slots in block to store a
                                                               // record
    private Set<Block> availBlocks; // HashSet to track if there are any Blocks available for insertion, otherwise
                                    // will have to create new Block
    private Set<Block> blockSet; // HashSet to track the Blocks which have records on them

    public Disk(String diskFileName) throws IOException {
        this.DISK_SIZE = 4 * 1024 * 1024; // 4MB = 4 * 2^20
        this.MAX_BLOCKS = this.DISK_SIZE / Block.BLOCK_SIZE;
        this.diskFileName = diskFileName;
        this.blockSet = new HashSet<>();
        this.availBlocks = new HashSet<>();

        for (int i = 0; i < MAX_BLOCKS; i++) { // initially, all slots should be available for record to be
            // inserted into
            availIndex.add(i);
        }

        // Initialize disk file with empty blocks
        RandomAccessFile diskFile = new RandomAccessFile(diskFileName, "rw");
        diskFile.close();
    }

    public Address insertRecord(Record record) { // insert record into an available Block
        Address address;

        // Check if maximum blocks are reached
        if (blockSet.size() >= MAX_BLOCKS) {
            return null;
        }

        if (availBlocks.size() < 1) {
            // Get the first available index for a new block
            int block_index = availIndex.get(0);

            // Create New Block
            Block newBlock = new Block(block_index, diskFileName);

            // Write the record to the block and return the address
            address = newBlock.addRecord(record);

            // Write new Block to the disk

            // Add the block to the set of blocks and remove the block from the available
            blockSet.add(newBlock);
            availBlocks.add(newBlock);
            availIndex.remove(0);

            return address;
        } else {
            Block availBlock = availBlocks.iterator().next();

            address = availBlock.addRecord(record);
            if (availBlock.isFull()) {
                availBlocks.remove(availBlock);
            }
            return address;
        }
    }

    public int getNumBlocks() { // get number of Blocks stored on Disk
        return blockSet.size();
    }

    public Set<Block> getBlockSet() { // get the set of all Blocks stored on Disk
        return blockSet;
    }
}

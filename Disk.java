import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Disk implements AutoCloseable {
    private final int DISK_SIZE;
    private final int MAX_BLOCKS;
    private final String diskFileName;
    private ArrayList<Integer> availIndex = new ArrayList<>(); // track available slots in block
    private Set<Block> availBlocks; // track available blocks for insertion
    private Set<Block> blockSet; // track blocks which have records
    private RandomAccessFile diskFile; // file for disk operations

    public Disk(String diskFileName) throws IOException {
        this.DISK_SIZE = 500 * 1024 * 1024; // 500MB
        this.MAX_BLOCKS = this.DISK_SIZE / Block.BLOCK_SIZE;
        this.diskFileName = diskFileName;
        this.blockSet = new HashSet<>();
        this.availBlocks = new HashSet<>();
        
        for (int i = 0; i < MAX_BLOCKS; i++) {
            availIndex.add(i);
        }

        // Initialize disk file with empty blocks
        diskFile = new RandomAccessFile(diskFileName, "rw");
        diskFile.setLength(DISK_SIZE); // Set the size of the file to represent the disk
    }

    public Address insertRecord(Record record) {
        Address address;

        if (blockSet.size() >= MAX_BLOCKS) {
            return null;
        }

        if (availBlocks.isEmpty()) {
            int block_index = availIndex.get(0);
            Block newBlock = new Block(block_index, diskFileName);

            address = newBlock.addRecord(record);

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

    public void deleteRecord(Address address) {
        Block block = address.getBlock();
        block.deleteRecord(address.getIndex());
        if (block.isEmpty()) {
            availBlocks.remove(block);
            blockSet.remove(block);
        } else {
            availBlocks.add(block);
        }
    }

    public int getNumBlocks() {
        return blockSet.size();
    }

    public Set<Block> getBlockSet() {
        return blockSet;
    }

    @Override
    public void close() throws IOException {
        if (diskFile != null) {
            diskFile.close(); // Close the RandomAccessFile
        }
    }
}
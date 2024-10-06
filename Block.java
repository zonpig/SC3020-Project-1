import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;

public class Block implements Serializable {
    private static final long serialVersionUID = 1L; // To ensure consistency during serialization.

    public static final int BLOCK_SIZE = 4096; // 4096 bytes block size
    public static final int RECORD_SIZE = 32; // 32 bytes record size
    public static final int MAX_NUM_RECORDS = BLOCK_SIZE / RECORD_SIZE;
    private int block_index;
    private ArrayList<Integer> availIndex = new ArrayList<>(); // keep track of the available slots in block to store a
                                                               // record
    private String diskFileName;

    public Block(int block_index, String diskFileName) {
        this.block_index = block_index;
        this.diskFileName = diskFileName;
        for (int i = 0; i < MAX_NUM_RECORDS; i++) { // initially, all slots should be available for record to be
                                                    // inserted into
            availIndex.add(i);
        }
    }

    public Address addRecord(Record record) {
        int offset = availIndex.get(0);
        try {
            RandomAccessFile diskFile = new RandomAccessFile(diskFileName, "rw");
            diskFile.seek(block_index * BLOCK_SIZE + offset * RECORD_SIZE);
            diskFile.write(record.toByteArray());
            diskFile.close();
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());

        }
        availIndex.remove(0);
        return new Address(this, block_index, offset);
    }

    public boolean isFull() {
        if (availIndex.size() == 0)
            return true;
        return false;
    }

    public Record[] getRecords() {
        Record[] records = new Record[MAX_NUM_RECORDS - availIndex.size()];
        int recordCount = 0; // Counter for how many records are actually added to the array
        try (RandomAccessFile raf = new RandomAccessFile(diskFileName, "r")) {
            // Seek to the starting byte position for this block
            raf.seek(block_index * BLOCK_SIZE); // Adjust seek position based on block size

            // Loop through each index to check for available records
            for (int i = 0; i < MAX_NUM_RECORDS; i++) {
                // Check if the current index is not available
                if (!availIndex.contains(i)) {
                    // Create a byte array to hold the data
                    byte[] buffer = new byte[RECORD_SIZE];

                    // Read the bytes into the buffer
                    int bytesRead = raf.read(buffer);

                    // Check if we have read enough bytes to create a Record
                    if (bytesRead == RECORD_SIZE) {
                        // Create a Record object from the byte array
                        records[recordCount] = new Record(buffer);
                        recordCount++; // Increment the record count

                        // Optional: Stop if we've reached the maximum number of records
                        if (recordCount >= MAX_NUM_RECORDS) {
                            break; // No need to continue if the array is full
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
}

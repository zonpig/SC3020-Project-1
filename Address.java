// class to represent which block a record is in and where in the block it is in

import java.io.Serializable;

public class Address implements Serializable {
    private Block block;
    private int block_index;
    private int index;

    public Address(Block block, int block_index, int index) {
        this.block = block;
        this.block_index = block_index;
        this.index = index;
    }

    public Block getBlock() {
        return block;
    }

    public int getBlockIndex() {
        return block_index;
    }

    public int getIndex() {
        return index;
    }
}

//
// Created by Bryan Lim on 26/9/24.
//

#include "Disk.h"

#include <stdexcept>

// Constructor implementation
Disk::Disk() : DISK_SIZE(500 * 1024 * 1024),  // 500MB
               MAX_BLOCKS(DISK_SIZE / Block::BLOCK_SIZE) {}

// Insert a record into an available block
Address* Disk::insertRecord(Record* record) {
    if (blockSet.size() >= MAX_BLOCKS) {
        return nullptr;  // No more blocks can be created
    }

    Address* address;

    if (availBlocks.empty()) {
        // No available blocks, create a new block
        Block* newBlock = new Block();
        address = newBlock->addRecord(record);
        blockSet.insert(newBlock);
        availBlocks.insert(newBlock);
    } else {
        // Use an available block
        Block* availBlock = *availBlocks.begin();  // Get the first available block
        address = availBlock->addRecord(record);

        // If the block is full after insertion, remove it from the available set
        if (availBlock->isFull()) {
            availBlocks.erase(availBlock);
        }
    }

    return address;
}

// Delete a record from the disk
void Disk::deleteRecord(Address* address) {
    Block* block = address->getBlock();
    block->deleteRecord(address->getIndex());

    // If the block becomes empty, remove it from the disk
    if (block->isEmpty()) {
        availBlocks.erase(block);
        blockSet.erase(block);
        delete block;  // Free the memory allocated for the block
    } else {
        // If the block still has space, add it to available blocks
        availBlocks.insert(block);
    }
}

// Get the number of blocks stored on the disk
int Disk::getNumBlocks() const {
    return blockSet.size();
}

// Get the set of all blocks stored on the disk
const std::unordered_set<Block*>& Disk::getBlockSet() const {
    return blockSet;
}
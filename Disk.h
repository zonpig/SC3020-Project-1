//
// Created by Bryan Lim on 26/9/24.
//

#ifndef DISK_H
#define DISK_H

#include "Block.h"
#include "Address.h"
#include "Record.h"
#include <unordered_set>

class Disk {
private:
    const int DISK_SIZE;      // Total size of the disk in bytes
    const int MAX_BLOCKS;     // Maximum number of blocks that can be stored on the disk
    std::unordered_set<Block*> availBlocks; // Tracks blocks with available space for new records
    std::unordered_set<Block*> blockSet;    // Tracks all blocks that have records

public:
    // Constructor
    Disk();

    // Insert a record into an available block
    Address* insertRecord(Record* record);

    // Delete a record from the disk
    void deleteRecord(Address* address);

    // Get the number of blocks stored on the disk
    int getNumBlocks() const;

    // Get the set of all blocks stored on the disk
    const std::unordered_set<Block*>& getBlockSet() const;

};



#endif //DISK_H

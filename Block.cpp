//
// Created by Bryan Lim on 25/9/24.
//

#include "Block.h"

Block::Block(){
    for(int i = 0; i < MAX_NUM_RECORDS; i++)
    {
        availIndex.push_back(i); // all slots are intially available
    }
}

Address* Block::addRecord(Record* record){
    int offset = availIndex.front();
    records[offset] = record;
    availIndex.erase(availIndex.begin());

    return new Address(this, offset);
}

void Block::deleteRecord(int index){
    availIndex.push_back(index); // make the slot available again
}


// Check if the block is full
bool Block::isFull() const {
    return availIndex.empty();
}

// Check if the block is empty
bool Block::isEmpty() const {
    return availIndex.size() == MAX_NUM_RECORDS;
}

// Get the list of records
Record** Block::getRecords() {
    return records;
}
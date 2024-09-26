//
// Created by Bryan Lim on 25/9/24.
//

#ifndef BLOCK_H
#define BLOCK_H

#include "Record.h"
#include "Address.h"
#include <vector>

class Block {
public:
    static const int BLOCK_SIZE = 4096;
    static const int RECORD_SIZE = 32;
    static const int MAX_NUM_RECORDS = BLOCK_SIZE / RECORD_SIZE;

private:
    Record* records[MAX_NUM_RECORDS];
    std::vector<int> availIndex;

public:
    Block();
    Address* addRecord(Record* record);
    void deleteRecord(int index);
    bool isFull() const;
    bool isEmpty() const;
    Record** getRecords();
};



#endif //BLOCK_H

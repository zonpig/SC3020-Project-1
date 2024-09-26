//
// Created by Bryan Lim on 25/9/24.
//

#include "Address.h"
#include "Block.h"

// Constructor for Address
Address::Address(Block* block, int index) : block(block), index(index) {}

// Getter for the Block object
Block* Address::getBlock() const {
    return block;
}

// Getter for the index within the Block
int Address::getIndex() const {
    return index;
}

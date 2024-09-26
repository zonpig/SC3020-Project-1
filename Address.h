//
// Created by Bryan Lim on 25/9/24.
//

#ifndef ADDRESS_H
#define ADDRESS_H

class Block;

class Address {
private:
    Block* block;
    int index;

public:
    Address(Block* block, int index);
    Block* getBlock() const;
    int getIndex() const;
};

#endif //ADDRESS_H

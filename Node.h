//
// Created by Bryan Lim on 26/9/24.
//

#ifndef NODE_H
#define NODE_H

#include <algorithm>
#include <iostream>
#include <limits>
#include <vector>

class InternalNode;
class Disk;
class Address;

class Node {

public:
    static const int n = 31; // number of keys

protected:
    InternalNode* parent;
    float keys[n]; // array to hold keys

public:
    // Constructor
    Node();

    // Getters
    float* getKeys();
    InternalNode* getParent() const;

    // Setters
    void setParent(InternalNode* parent);

    // Check if node is full
    bool isFull() const;

    // Get the number of keys in the node
    int getNumKeys() const;

    // Pure virtual functions (abstract methods)
    virtual void insertRecord(Address* address) = 0;
    virtual bool deleteRecord(float key, Disk* disk, Node* leftSibling, Node* rightSibling) = 0;
    virtual float searchQuery(float key) = 0;
    virtual float rangeQuery(float lowerKey, float upperKey) = 0;
    virtual float getSubtreeLB() const = 0;
    virtual void enumerateNodes() const = 0;
    virtual bool isLeaf() const = 0;
    virtual int countNodes() const = 0;

    // Destructor
    virtual ~Node() = default;

};



#endif //NODE_H

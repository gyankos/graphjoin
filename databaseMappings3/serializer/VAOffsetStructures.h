//
// Created by Giacomo Bergami on 19/08/16.
//

#pragma once

/**
 * Header of a stored file.
 */
typedef struct {
    unsigned long size;
    unsigned long offsetOUT;
    unsigned int  id;
    unsigned int  bbb;      // Packing
} HEADER ;

/**
 * Element for the edges and Outgoing. It is used for the adjacency
 */
class EDGES_OUTIN {
public:
    unsigned int adjacentHash;
    unsigned int adjacentID;
    EDGES_OUTIN(unsigned int hash, unsigned int id) : adjacentID{id}, adjacentHash{hash} {};
    bool operator<( const EDGES_OUTIN& other ) const {
        return ((adjacentHash) < (other.adjacentHash)) || (adjacentHash == other.adjacentHash && adjacentID < other.adjacentID);
    }
    bool operator==( const EDGES_OUTIN& other ) const {
        return  (adjacentHash == other.adjacentHash && adjacentID == other.adjacentID);
    }
};


/**
 * Mapping the whole entry of a vertex values file
 */
typedef struct {
    HEADER* vertexHeader;
    unsigned int* stringArrayHeader;
    char* stringArray;
    unsigned int* outSize;
    EDGES_OUTIN* outVertices;
} VAEntry;

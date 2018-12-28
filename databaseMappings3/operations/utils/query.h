//
// Created by giacomo on 04/11/18.
//

#ifndef DATABASEMAPPINGS3_QUERY_H
#define DATABASEMAPPINGS3_QUERY_H


#include "query_chunk.h"

class query {
    std::vector<query_chunk> qcks;
public:
    void emplace_front(int l, int r);

    void compileOverLeftVertex(VAEntry* left);

    bool compileOverRightVertex(VAEntry* right);
};


#endif //DATABASEMAPPINGS3_QUERY_H

//
// Created by giacomo on 04/11/18.
//

#ifndef DATABASEMAPPINGS3_QUERY_CHUNK_H
#define DATABASEMAPPINGS3_QUERY_CHUNK_H


#include <cstring>
#include "../../serializer/VAOffsetStructures.h"
#include "../../serializer/mappers.h"

class query_chunk {
public:
    char *ptrLeftVal;

    int posLeft, posRight;
    query_chunk(int l, int r);;
    void compileOverLeftVertex(VAEntry* left);
    bool compileOverRightVertex(VAEntry* right);
};


#endif //DATABASEMAPPINGS3_QUERY_CHUNK_H

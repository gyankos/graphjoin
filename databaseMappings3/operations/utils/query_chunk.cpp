//
// Created by giacomo on 04/11/18.
//

#include "query_chunk.h"

query_chunk::query_chunk(int l, int r) : posLeft{l}, posRight{r}, ptrLeftVal{nullptr} {}

void query_chunk::compileOverLeftVertex(VAEntry *left) {
    ptrLeftVal = ATTRIBUTE(left,posLeft);
}

bool query_chunk::compileOverRightVertex(VAEntry *right) {
    char *ptrRightVal = ATTRIBUTE(right,posRight);
    return (std::strcmp(ptrLeftVal,ptrRightVal) == 0);
}

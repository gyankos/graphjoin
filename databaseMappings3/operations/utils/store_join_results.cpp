//
// Created by giacomo on 04/11/18.
//

#include "store_join_results.h"

void resultProposed(const std::string& resultFileName, FILE **result, unsigned long *cont) {
    *result = fopen(resultFileName.c_str(),"w");
    if (!*result) {
        std::cerr << "Error while opening result file \"" << resultFileName <<"\": " << strerror(errno) << std::endl;
        exit(1);
    }
    *cont = 0;
}

void storeVertex(unsigned int left, unsigned int right, FILE *result, unsigned long *cont) {
    //clock_t c = std::clock();
    JOINRESULT vertex;
    vertex.isVertex = 1;
    vertex.isEdge = 0;
    vertex.left = left;
    vertex.right = right;
    fwrite(&vertex, sizeof(JOINRESULT),1,result);
    //*cont += (std::clock() - c);
}

void storeEdge(unsigned int leftSrc, unsigned int rightSrc, unsigned int leftDst, unsigned int rightDst, FILE *result,
               unsigned long *cont) {
    //clock_t c = std::clock();
    JOINRESULT edge;
    edge.isEdge = 1;
    edge.isVertex = 0;
    edge.left = leftDst;
    edge.right = rightDst;
    fwrite(&edge, sizeof(JOINRESULT),1,result);
    //*cont += (std::clock() - c);
}

clock_t save(FILE *result, unsigned long *cont) {
    fclose(result);
    return *cont;
}

//
// Created by giacomo on 04/11/18.
//

#ifndef DATABASEMAPPINGS3_STORE_JOIN_RESULTS_H
#define DATABASEMAPPINGS3_STORE_JOIN_RESULTS_H

#include <cstdio>
#include <ctime>
#include <string>
#include <iostream>
#include <cstring>

typedef struct {
    unsigned int isVertex;
    unsigned int isEdge;
    unsigned int left;
    unsigned int right;
} JOINRESULT;

void resultProposed(const std::string& resultFileName, FILE** result, unsigned long* cont);
void storeVertex(unsigned int left, unsigned int right, FILE* result, unsigned long* cont);
void storeEdge(unsigned int leftSrc, unsigned int rightSrc, unsigned int leftDst, unsigned int rightDst, FILE* result, unsigned long* cont);
clock_t save(FILE* result, unsigned long* cont);

#endif //DATABASEMAPPINGS3_STORE_JOIN_RESULTS_H

//
// Created by giacomo on 01/11/18.
//

#ifndef DATABASEMAPPINGS3_HASHING_H
#define DATABASEMAPPINGS3_HASHING_H

#include <vector>
#include <string>
extern "C" {
#include <string.h>
};

unsigned int hashCode(char* string);

unsigned int doHash3(std::vector<std::string> &stringarray, std::vector<int>& hashPosition);

#endif //DATABASEMAPPINGS3_HASHING_H

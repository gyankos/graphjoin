//
// Created by giacomo on 04/11/18.
//

#ifndef DATABASEMAPPINGS3_CSVSERIALIZE_H
#define DATABASEMAPPINGS3_CSVSERIALIZE_H

#include <string>
#include <vector>
#include <map>

std::pair<double,double> csvSerialize(std::string &csvVertices, std::string csvEdges, std::vector<int> &numHashes, int currentSeed, int opSize);

#endif //DATABASEMAPPINGS3_CSVSERIALIZE_H

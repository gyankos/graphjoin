//
// Created by giacomo on 04/11/18.
//

#ifndef DATABASEMAPPINGS3_EQ_JOIN_PROPOSED_H
#define DATABASEMAPPINGS3_EQ_JOIN_PROPOSED_H

#include <string>
#include <map>

double EqJoin(const std::string &leftPath, const std::string &rightPath, const std::string &resultPath,
              std::map<unsigned int, unsigned int>& join);

#endif //DATABASEMAPPINGS3_EQ_JOIN_PROPOSED_H

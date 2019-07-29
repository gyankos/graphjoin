//
// Created by giacomo on 04/11/18.
//

#ifndef DATABASEMAPPINGS3_EQ_CONJ_JOIN_BLK_H
#define DATABASEMAPPINGS3_EQ_CONJ_JOIN_BLK_H

#include <map>
#include <string>

double EqConjunctiveJoinForBulks(const std::string &leftPath, const std::string &rightPath, const std::string &resultPath,
                                 std::map<unsigned int, unsigned int> &join);

#endif //DATABASEMAPPINGS3_EQ_CONJ_JOIN_BLK_H

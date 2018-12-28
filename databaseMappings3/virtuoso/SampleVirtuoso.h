//
// Created by giacomo on 14/12/18.
//

#ifndef DATABASEMAPPINGS3_SAMPLEVIRTUOSO_H
#define DATABASEMAPPINGS3_SAMPLEVIRTUOSO_H

//
// Created by Giacomo Bergami on 10/09/16.
//

#ifndef CGRAPH2_SAMPLEVIRTUOSO_H
#define CGRAPH2_SAMPLEVIRTUOSO_H

//#define BASIC_VALUE "http://jackbergus.alwaysdata.net/values/"
#define BASIC_EDGE  "http://jackbergus.alwaysdata.net/edges/"
#define BASIC_PROPERTY "http://jackbergus.alwaysdata.net/property/"

#include <map>
#include "VirtuosoGraph.h"
#include <vector>
#include <string>

class sampleVirtuoso {
    Virtuoso* connection;
    VirtuosoGraph* handler;
    bool isLeft;
    std::string baseIRI;
public:
    ~sampleVirtuoso();
    sampleVirtuoso(std::string& graphIRIName, bool isLeft);
    void addNewVertex(std::vector<std::string>& header, std::string& lines);
    void addNewEdge(unsigned long src, unsigned long dst);
};


#endif //CGRAPH2_SAMPLEVIRTUOSO_H


#endif //DATABASEMAPPINGS3_SAMPLEVIRTUOSO_H

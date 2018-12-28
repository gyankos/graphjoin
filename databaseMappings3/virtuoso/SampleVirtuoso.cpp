//
// Created by giacomo on 14/12/18.
//

#include "SampleVirtuoso.h"

#include <string>
#include <iostream>
#include <sstream>
#include "utils/SchemaValues.h"
#include "SampleVirtuoso.h"

sampleVirtuoso::~sampleVirtuoso() {
    if (handler != nullptr)
        delete handler;
    if (connection != nullptr)
        delete connection;
}

sampleVirtuoso::sampleVirtuoso(std::string& graphName, bool left)  {
    connection = new Virtuoso();
    handler = connection->openGraph(GRAPH_BASE_IRI(graphName));
    isLeft = left;
    baseIRI = handler->getGraphIRI();
}

void sampleVirtuoso::addNewEdge(unsigned long src, unsigned long dst) {
    std::string baseIRI = handler->getGraphIRI();
    handler->addTriplet(baseIRI+"values/"+std::to_string(src)+(isLeft ? "L" : "R"),BASIC_EDGE,baseIRI+"values/"+std::to_string(dst)+(isLeft ? "L" : "R"));
}

void sampleVirtuoso::addNewVertex(std::vector<std::string>& header, std::string& lines) {
    std::string valueIRI;
    std::string iteme;
    std::istringstream ss{lines};
    for (unsigned long i = 0, n = header.size(); i<n; i++) {
        if (i == 0) {
            std::getline(ss, iteme, ',');
            valueIRI = baseIRI+"values/"+iteme+(isLeft ? "L" : "R");
            std::string id{BASIC_PROPERTY};
            id += "Id";
            handler->addValueTriplet(valueIRI,id,STRING_VALUE(iteme)+(isLeft ? "L" : "R"));
        } else {
            std::getline(ss, iteme, ',');
            handler->addValueTriplet(valueIRI,BASIC_PROPERTY+header[i]+(isLeft ? "1" : "2"),STRING_VALUE(iteme));
        }
    }
}

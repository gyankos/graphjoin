//
// Created by Giacomo Bergami on 10/09/16.
//

#ifndef VIRTUOSOCONNECT_VIRTUOSOGRAPH_H
#define VIRTUOSOCONNECT_VIRTUOSOGRAPH_H


#include <string>
#include <librdf.h>
#include "Virtuoso.h"

class VirtuosoGraph {
    Virtuoso* connection;
    librdf_node* context_node;
    std::string iriname;
public:
    VirtuosoGraph();
    VirtuosoGraph(Virtuoso* connection, std::string graphName);
    VirtuosoGraph(const VirtuosoGraph& cp);
    ~VirtuosoGraph();

    void transaction();
    bool addTriplet(std::string src, std::string pred, std::string dst);
    bool addValueTriplet(std::string src, std::string pred, std::string dst);



    std::string getGraphIRI();

    void commit();

    int countOutgoingEdges(std::string src, std::string prop);
};

#endif //VIRTUOSOCONNECT_VIRTUOSOGRAPH_H

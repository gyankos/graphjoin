//
// Created by Giacomo Bergami on 10/09/16.
//

#include "VirtuosoGraph.h"
#include "utils/triplets.h"

VirtuosoGraph::VirtuosoGraph(Virtuoso *connection, std::string graphName) {
    this->connection = connection;
    context_node=librdf_new_node_from_uri_string(this->connection->world, (const unsigned char *)graphName.c_str());
    iriname = graphName;
}

VirtuosoGraph::VirtuosoGraph(const VirtuosoGraph &cp) {
    this->connection= cp.connection;
    this->context_node = cp.context_node;
    iriname = cp.iriname;
}

VirtuosoGraph::~VirtuosoGraph() {
    librdf_free_node(context_node);
}

bool VirtuosoGraph::addTriplet(std::string src, std::string pred, std::string dst) {
    return (add_triple(this->connection->world,context_node,this->connection->model,src.c_str(),pred.c_str(),dst.c_str())>0);
}

bool VirtuosoGraph::addValueTriplet(std::string src, std::string pred, std::string dst) {
    return (add_triple_typed(this->connection->world,context_node,this->connection->model,src.c_str(),pred.c_str(),dst.c_str())>0);
}


void VirtuosoGraph::transaction() {
    librdf_model_transaction_start(this->connection->model);
}

void VirtuosoGraph::commit() {
    librdf_model_transaction_commit(this->connection->model);
}

std::string VirtuosoGraph::getGraphIRI() {
    return iriname;
}

VirtuosoGraph::VirtuosoGraph() {
    connection = nullptr;
    context_node = nullptr;
}

int VirtuosoGraph::countOutgoingEdges(std::string src, std::string prop) {
    std::string query = "SELECT (COUNT(DISTINCT ?instance) AS ?count) FROM <" + iriname + "> WHERE { <" + src + "> <" + prop + "> ?instance.}";
    VirtuosoQuery* q = connection->compileQuery(query);
    int result = -1;
    if (q->operator()()) {
        result = q->getCellAsInt(0);
        q->nextRow();
        q->hasAnotherResult();
    }
    delete q;
    return result;
}


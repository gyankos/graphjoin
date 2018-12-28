//
// Created by Giacomo Bergami on 10/09/16.
//

#ifndef VIRTUOSOCONNECT_VIRTUOSO_H
#define VIRTUOSOCONNECT_VIRTUOSO_H

#include <stdlib.h>
#include <stdio.h>
#include <ctype.h>
#include <string.h>
#include <stdarg.h>

#include <unistd.h>

#include <redland.h>
#include <raptor.h>
#include "rdf_heuristics.h"
#include "rdf_hash_internal.h"
#include <string>

typedef int  VIRTUOSO_STATUS;
#define STORAGE_FAILURE     1
#define MODEL_FAILURE       2
#define VIRTUOSO_OK         0

class VirtuosoGraph;
class VirtuosoQuery;

class Virtuoso {
    librdf_storage *storage;
    librdf_node* context_node;
    VIRTUOSO_STATUS status = 0;
public:
    Virtuoso();
    ~Virtuoso();
    VirtuosoGraph* openGraph(std::string name);
    VirtuosoQuery* compileQuery(std::string query);

    librdf_world* world;
    librdf_model* model;
};

#include "VirtuosoGraph.h"
#include "VirtuosoQuery.h"

#endif //VIRTUOSOCONNECT_VIRTUOSO_H

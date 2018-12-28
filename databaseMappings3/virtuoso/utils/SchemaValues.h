//
// Created by Giacomo Bergami on 10/09/16.
//

#pragma once

#include <string>


#define SPARQL_STRING_DEF "\"^^<http://www.w3.org/2001/XMLSchema#string>"
#define CHARARRAY_STRING_VALUE(x) "\"" (x) SPARQL_STRING_DEF
#define STRING_VALUE(x) "\""+(x)+SPARQL_STRING_DEF
#define GRAPH_BASE_IRI(x) "http://jackbergus.alwaysdata.net/graph"+x

#define SPARQL_INT_DEF "\"^^<http://www.w3.org/2001/XMLSchema#integer>"

int asIntValue(unsigned char *val);
std::string asStringValue(unsigned char *val);

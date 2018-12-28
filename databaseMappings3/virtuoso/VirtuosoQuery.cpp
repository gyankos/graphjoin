//
// Created by Giacomo Bergami on 11/09/16.
//

#include <iostream>
#include "VirtuosoQuery.h"
#include "utils/SchemaValues.h"

#define CELL_IS_INTEGER     1
#define CELL_IS_STRING      2
#define CELL_IS_UNKNOWN     0

VirtuosoQuery::VirtuosoQuery(Virtuoso *parent, std::string query) {
    this->connection = parent;
    this->query = librdf_new_query(parent->world,(char*)"sparql",NULL,(const unsigned char*)query.c_str(),NULL);
    this->result = nullptr;
}

VirtuosoQuery::VirtuosoQuery(const VirtuosoQuery &cp) {
    this->connection = cp.connection;
    this->query = cp.query;
    this->result = cp.result;
}


VirtuosoQuery::~VirtuosoQuery() {
    if (result) {
        librdf_free_query_results(result);
        result = nullptr;
    }
    librdf_free_query(query);
}

bool VirtuosoQuery::operator()() {
    // If the query is compiled, it means that either it is the first time, and hence it has to be run,
    // or there are some results already (either empty or not)
    if (query) {
        // If there is no result but there is a query, then the query has to be run
        if (!result) {
            // Running the query
            result = librdf_model_query_execute(connection->model,query);
            // Checking the final result
            if (!result) {
                librdf_free_query(query);
                query = nullptr;
                return false;
            } else return true;
        } else return true; // The result is here and ready
    } else return false;
}

bool VirtuosoQuery::reset() {
    if (query) {
        if (result) {
            librdf_free_query_results(result);
            result = nullptr;
            return true;
        } else return true; // The query was never performed. If it was performed, then it should be either true, or query free'd
    } else return false;
}

void VirtuosoQuery::print_binding_results() {
    if (this->operator()()) {
        if (librdf_query_results_is_bindings(result)) {
            unsigned long long count = 0;
            while (!librdf_query_results_finished(result)) {
                std::cout << "Row " << count << "{";
                int i;
                for (i=0; i<librdf_query_results_get_bindings_count(result); i++) {
                    librdf_node *node_value = librdf_query_results_get_binding_value(result,i);
                    const char *name = librdf_query_results_get_binding_name(result,i);
                    unsigned char *value = librdf_node_to_string(node_value);
                    std::cout << name << ":" << value << ", ";
                    librdf_free_node(node_value);
                }
                std::cout << "}" << std::endl;
                librdf_query_results_next(result);
            }
        }
    }
}

long long VirtuosoQuery::countResults() {
    if (this->operator()()) {
        if (librdf_query_results_is_bindings(result)) {
            return librdf_query_results_get_bindings_count(result);
        } else return librdf_query_results_get_count(result); // for other formats (unknown included)
    } else return -1;
}

bool                VirtuosoQuery::isTable()                  {   return (librdf_query_results_is_bindings(result)>0);        }
unsigned long long  VirtuosoQuery::currentRow()               {   return this->curr_row_pos;                                  }
int                 VirtuosoQuery::getColNum()                {   return librdf_query_results_get_bindings_count(result);     }
bool                VirtuosoQuery::hasAnotherResult()         {   return (librdf_query_results_finished(result)>0);           }
void                VirtuosoQuery::nextRow()                  {   librdf_query_results_next(result);                          }

int                 VirtuosoQuery::getCellAsInt(int pos) {
    librdf_node *node_value = librdf_query_results_get_binding_value(result,pos);
    int toret = asIntValue(librdf_node_to_string(node_value));
    librdf_free_node(node_value);;
    return toret;
}

std::string                 VirtuosoQuery::getCellAsString(int pos) {
    librdf_node *node_value = librdf_query_results_get_binding_value(result,pos);
    std::string toret = asStringValue(librdf_node_to_string(node_value));
    librdf_free_node(node_value);
    return toret;
}

const char *VirtuosoQuery::getIthColumnName(int pos) {
    return pos < librdf_query_results_get_bindings_count(result)
           ? librdf_query_results_get_binding_name(result, pos)
           : nullptr;
}





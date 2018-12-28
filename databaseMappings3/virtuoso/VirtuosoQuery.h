//
// Created by Giacomo Bergami on 11/09/16.
//

#ifndef CGRAPH2_VIRTUOSOQUERY_H
#define CGRAPH2_VIRTUOSOQUERY_H

#include "Virtuoso.h"

class VirtuosoQuery {
    Virtuoso* connection;
    librdf_query* query;
    librdf_query_results* result;
    unsigned long long curr_row_pos;
public:
    /**
     * The constructor only initializes the query, but it doesn't perform it
     * @param parent
     * @param query
     * @return
     */
    VirtuosoQuery(Virtuoso* parent, std::string query);
    VirtuosoQuery(const VirtuosoQuery& cp);

    /**
     * Attempts to re-run the query
     * @return True, if I could re-run the query (condition: there were no error beforehand in the first execution of it
     */
    bool reset();

    /**
     * Checks if the result is a table
     * @return
     */
    bool isTable();
    /**
     * Returns the position of the current row
     * @return
     */
    unsigned long long currentRow();
    int getColNum();

    /**
     * Returns the name of the columnt at the given position
     * @param pos
     * @return
     */
    const char *getIthColumnName(int pos);
    /**
     * Returns whether there is yet another row to read
     * @return
     */
    bool hasAnotherResult();
    /**
     * Returns the pos-th cell for the current row as an integer
     * @param pos
     * @return
     */
    int getCellAsInt(int pos);

    long long countResults();

    /**
     * Performs the query
     * @return If the query succeeded or not
     */
    bool operator()();

    void nextRow();

    ~VirtuosoQuery();

    void print_binding_results();


    std::string getCellAsString(int pos);
};


#endif //CGRAPH2_VIRTUOSOQUERY_H

//
// Created by Giacomo Bergami on 10/09/16.
//

#include <librdf.h>
#include "../rdf_heuristics.h"
#include "triplets.h"


int add_triple(librdf_world *world, librdf_node *context, librdf_model* model, const char *s, const char *p, const char *o)
{
    librdf_node *subject, *predicate, *object;
    librdf_statement* statement=NULL;
    int rc;

    if(librdf_heuristic_is_blank_node(s))
        subject=librdf_new_node_from_blank_identifier(world, (const unsigned char *)librdf_heuristic_get_blank_node(s));
    else
        subject=librdf_new_node_from_uri_string(world, (const unsigned char *)s);

    predicate=librdf_new_node_from_uri_string(world, (const unsigned char *)p);

    if(librdf_heuristic_is_blank_node(o))
        object=librdf_new_node_from_blank_identifier(world, (const unsigned char *)librdf_heuristic_get_blank_node(o));
    else
        object=librdf_new_node_from_uri_string(world, (const unsigned char *)o);

    statement=librdf_new_statement(world);
    librdf_statement_set_subject(statement, subject);
    librdf_statement_set_predicate(statement, predicate);
    librdf_statement_set_object(statement, object);

    rc=librdf_model_context_add_statement(model, context, statement);

    librdf_free_statement(statement);
    return rc;
}

int add_triple_typed(librdf_world *world, librdf_node *context, librdf_model* model, const char *s, const char *p, const char *o)
{
    librdf_node *subject, *predicate, *object;
    librdf_statement* statement=NULL;
    int rc;

    if(librdf_heuristic_is_blank_node(s))
        subject=librdf_new_node_from_blank_identifier(world, (const unsigned char *)librdf_heuristic_get_blank_node(s));
    else
        subject=librdf_new_node_from_uri_string(world, (const unsigned char *)s);

    predicate=librdf_new_node_from_uri_string(world, (const unsigned char *)p);

    if(librdf_heuristic_is_blank_node(o))
        object=librdf_new_node_from_blank_identifier(world, (const unsigned char *)librdf_heuristic_get_blank_node(o));
    else
        object=librdf_new_node_from_literal(world, (const unsigned char *)o, NULL, 0);

    statement=librdf_new_statement(world);
    librdf_statement_set_subject(statement, subject);
    librdf_statement_set_predicate(statement, predicate);
    librdf_statement_set_object(statement, object);

    rc=librdf_model_context_add_statement(model, context, statement);

    librdf_free_statement(statement);
    return rc;
}
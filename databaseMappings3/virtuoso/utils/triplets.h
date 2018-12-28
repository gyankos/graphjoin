//
// Created by Giacomo Bergami on 10/09/16.
//

#ifndef VIRTUOSOCONNECT_TRIPLETS_H
#define VIRTUOSOCONNECT_TRIPLETS_H

#ifdef __cplusplus
extern "C" {
#endif
int add_triple(librdf_world *world, librdf_node *context, librdf_model* model, const char *s, const char *p, const char *o);
int add_triple_typed(librdf_world *world, librdf_node *context, librdf_model* model, const char *s, const char *p, const char *o);
#ifdef __cplusplus
}
#endif


#endif //VIRTUOSOCONNECT_TRIPLETS_H

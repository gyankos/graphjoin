//
// Created by Giacomo Bergami on 19/08/16.
//

#pragma once

/**
 * Data structure belonging to the Index file (each slot has a id)
 */
typedef struct {
    unsigned int id;
    unsigned int hash;
    unsigned long offset;
} INDEXFILE;

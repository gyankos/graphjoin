//
// Created by Giacomo Bergami on 19/08/16.
//

#pragma once

/**
 * Data structure belonging to the Hash file (each slot has an hash)
 */
typedef struct {
    unsigned int hash;
    unsigned int bbb;       // Packing
    unsigned long offset;
} HASHFILE;

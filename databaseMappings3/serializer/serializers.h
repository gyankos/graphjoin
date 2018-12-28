//
// Created by Giacomo Bergami on 16/08/16.
//

#pragma once

#include <stdio.h>
#include <string.h>
#include <vector>
#include <map>
#include "VAOffsetStructures.h"
#include "mappers.h"
#include "csvSerialize.h"

void serialize_vertex_values(FILE *VAIndexFile,           //Where to store the values
                             unsigned long *VAOffset,      //Offset for the next VAFile
                             unsigned long *HashOffset,    //Offset for the next hash entry
                             unsigned int id,
                             unsigned long valueSize,          //Values
                             std::vector<std::string>& valueArray,
                             std::vector<EDGES_OUTIN>& out);

void serialize_vertex_values2(FILE *VAIndexFile,           //Where to store the values
                              unsigned long *VAOffset,      //Offset for the next VAFile
                              unsigned long *HashOffset,    //Offset for the next hash entry
                              VAEntry_updating* elem);           //Outgoing set

void serialize_vertex_id(FILE *index, unsigned int id, unsigned int hash, unsigned long offset);

void serialize_hash(FILE *index, unsigned int hash, unsigned long offset);

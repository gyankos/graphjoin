//
// Created by Giacomo Bergami on 17/08/16.
//

#pragma once

#include <string>
#include <fstream>
#include <fcntl.h>
#include <sys/mman.h>
#include <set>
#include <vector>
#include "VAOffsetStructures.h"
#include "VertexIndexArrayField.h"


class VAEntry_updating {
public:
    HEADER* vertexHeader;                   //From pointer
    unsigned int* stringArrayHeader;        //From pointer
    char* stringArray;                      //From pointer
    std::vector<EDGES_OUTIN> outEdges;
    unsigned long offset;
    unsigned int hash;
    unsigned int new_id;
    VAEntry_updating() : vertexHeader{nullptr}, stringArray{nullptr}, stringArrayHeader{nullptr}, hash{0}, new_id{0}, offset{0} {}
    unsigned long stringArraySize;
};

// Returns the size of the outgoing set
#define VOUT_SIZE(ptr) ((ptr)->outSize[0])
// Returns the array of the outgoing elements
#define VOUT(ptr)      (((ptr)->outVertices))

// Returns the number of values
#define NATTRIBUTES(ptr) ((ptr)->stringArrayHeader[0])

#define ATTRIBUTE(ptr,pos)     ((char*)(((pos) == 0) ? ((ptr)->stringArray) : (&((ptr)->stringArray[(ptr)->stringArrayHeader[(pos)]]))));

// gets the VOUT information from scratch, that is by using no function and only using the mmap pointers
//#define VOUT_FROM_SCRATCH(vertexID,mmapVAFile,indexfile) ((unsigned int*)(&((((char*)mmapVAFile))[(indexfile)[vertexID].offset+((HEADER*)(&((char*)mmapVAFile)[((INDEXFILE*)(indexfile))[vertexID].offset]))->offsetOUT])))
#define VOUT_SSIZE(vertexID,mmapVAFile,indexfile)        ((((unsigned int*)(&((((char*)mmapVAFile))[(indexfile)[vertexID].offset+((HEADER*)(&((char*)mmapVAFile)[((INDEXFILE*)(indexfile))[vertexID].offset]))->offsetOUT]))))[0])
#define VOUT_ARRAY(vertexID,mmapVAFile,indexfile)        ((((EDGES_OUTIN*)(&((((char*)mmapVAFile))[(indexfile)[vertexID].offset+((HEADER*)(&((char*)mmapVAFile)[((INDEXFILE*)(indexfile))[vertexID].offset]))->offsetOUT+sizeof(unsigned int)])))))

/**
 * Given a VA file (mmapVAFile) and the offset within it, updates elem to point to the provided description
 */
void descriptorFromOffset(VAEntry* elem, unsigned long offset, char* mmapVAFile);

void partialdescriptorFromVertexID(VAEntry_updating* elem, unsigned int vertexID, INDEXFILE *f, char* mmapVAFile);

/**
 * Given a VA file (mmapVAFile) and the vertex ID with the INDEXFILE information, returns the vertex descriptor in elem
 */
void descriptorFromVertexID(VAEntry* elem, unsigned int vertexID, INDEXFILE *f, char* mmapVAFile);

void* mmapFile(std::string file, unsigned long* size, int* fd);
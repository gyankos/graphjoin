//
// Created by Giacomo Bergami on 17/08/16.
//

#include "mappers.h"
#include "VertexIndexArrayField.h"

void descriptorFromOffset(VAEntry* elem, unsigned long offset, char* mmapVAFile) {
    // (unsigned int*)(&(mmapVAFile)[(f)[vertexID].offset+((HEADER*)(&mmapVAFile[offset]))->offsetIN]);
    unsigned long tmpOffset = offset;
    elem->vertexHeader = (HEADER*)(&mmapVAFile[offset]);
    tmpOffset += sizeof(HEADER);
    elem->stringArrayHeader = (unsigned int*)(&mmapVAFile[tmpOffset]);
    tmpOffset += sizeof(unsigned int)*(elem->stringArrayHeader[0]);
    elem->stringArray = &mmapVAFile[tmpOffset];
    elem->outSize = (unsigned int*)(&mmapVAFile[offset+elem->vertexHeader->offsetOUT]);
    elem->outVertices = (EDGES_OUTIN*)(&mmapVAFile[offset+elem->vertexHeader->offsetOUT+ sizeof(unsigned int)]);
}

void descriptorFromVertexID(VAEntry* elem, unsigned int vertexID, INDEXFILE *f, char* mmapVAFile) {
    descriptorFromOffset(elem,f[vertexID].offset,mmapVAFile);
}

void partialdescriptorFromVertexID(VAEntry_updating* elem, unsigned int vertexID, INDEXFILE *f, char* mmapVAFile) {
    unsigned long offset = f[vertexID].offset;
    unsigned long tmpOffset = offset;
    elem->vertexHeader = (HEADER*)(&mmapVAFile[offset]);
    tmpOffset += sizeof(HEADER);
    elem->stringArrayHeader = (unsigned int*)(&mmapVAFile[tmpOffset]);
    tmpOffset += sizeof(unsigned int)*(elem->stringArrayHeader[0]);
    elem->stringArray = &mmapVAFile[tmpOffset];
    elem->stringArraySize = (elem->vertexHeader->offsetOUT+offset) - tmpOffset;
    elem->hash = f[vertexID].hash;
    //printf("%d\n",elem->stringArraySize);
}


void* mmapFile(std::string file, unsigned long* size, int* fd) {
    {
        std::ifstream in(file, std::ifstream::ate | std::ifstream::binary);
        *size = in.tellg();
    }
    char *full_path = realpath(file.data(), NULL);
    *fd = open(full_path,O_RDONLY);
    free(full_path);
    void* addr = mmap(NULL,*size, PROT_READ, MAP_SHARED, *fd, 0 );
    return addr;
}
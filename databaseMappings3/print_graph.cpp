//
// Created by giacomo on 04/11/18.
//

#include <string>
#include <iostream>
#include <cassert>
#include "serializer/VAOffsetStructures.h"
#include "serializer/VertexIndexArrayField.h"
#include "serializer/HashIndexArrayField.h"
#include "serializer/mappers.h"

extern "C" {
#include <unistd.h>
}


void printElem(VAEntry* elem,INDEXFILE *f,char* vaoffset) {
    std::cout << "\tID = " << elem->vertexHeader->id << std::endl;
    //std::cout << "BBB = " << elem->vertexHeader->bbb << std::endl;

    char* att = ATTRIBUTE(elem,3);
    std::cout << "\t- Attribute 3 = " << att << std::endl;
    att = ATTRIBUTE(elem,5);
    std::cout << "\t- Attribute 5 = " << att << std::endl;

    //std::cout << "VIN SIZE=" << VIN_SIZE(elem) << std::endl;;
    unsigned int size2 = VOUT_SIZE(elem);
    std::cout << "\t- VOUT = ";
    EDGES_OUTIN* Nv = VOUT(elem);
    //unsigned int* array = VOUT_ARRAY(f[i].id,vaoffset,f);
    for (unsigned int j=0; j<size2; j++)
        std::cout << Nv->adjacentID  << ", ";
    std::cout << std::endl;
}

int main(int argc, char** argv) {
    std::string graph{argv[1]};
    std::string primary = graph+"_Hash.bin";
    std::string secondary = graph+"_Index.bin";
    std::string values = graph+"_VAOffset.bin";


    std::cout <<sizeof(EDGES_OUTIN) << " " << sizeof(unsigned int)*2<< std::endl;
    unsigned long size = 0;
    int fd;
    INDEXFILE *f = (INDEXFILE *)mmapFile(graph.c_str(),&size,&fd);
    unsigned int id = 0;
    unsigned int hash = 0;
    unsigned long offset = 0, limitOffset = 0;
    int i = 0;
    while(f[i++].offset!=0);
    i-=1;

    unsigned long sized = 0;
    int vafd = 0;
    HASHFILE *h = (HASHFILE *)mmapFile(primary.c_str(),&sized,&fd);

    unsigned long ssize = 0;
    int sfd = 0;
    char* vaoffset = (char*)mmapFile(values.c_str(),&ssize,&sfd);

    VAEntry va;
    //INDEXFILE l = f[6593];
    //std::cout << "ID=" << l.id << " hash=" << l.hash << std::endl;
    //descriptorFromOffset(&va,l.offset,vaoffset);

    /*std::cout << "EHSIZE=" << sizeof(EDGES_OUTIN) << std::endl;
    std::cout << "VOUT SIZE=" << VOUT_SIZE(&va);
    int sz = VOUT_SIZE(&va);
    for (int i=0; i<sz; i++) {
        EDGES_OUTIN eo = VOUT(&va)[i];
        std::cout << "\t\t hash=" << eo.adjacentHash << " id=" << eo.adjacentID << std::endl;
    }*/


    /*for (int i=0; i<size / sizeof(INDEXFILE); i++) {
        VAEntry_updating elem;
        partialdescriptorFromVertexID(&elem,i,f,vaoffset);
        printElem2(&elem,f,vaoffset);
    }*/

    for (int i= 0; i<sized/sizeof(HASHFILE); i++) {
        hash = h[i].hash;
        limitOffset = (i==(sized/sizeof(HASHFILE))-1) ? (ssize) : h[i+1].offset;
        offset = h[i].offset;
        std::cout << "Hash=" << hash << " + Offset = " << offset << " ==> LimitOffset " << limitOffset << std::endl;
        assert (offset < limitOffset);
        while (offset<limitOffset) {
            VAEntry elem;
            descriptorFromOffset(&elem,offset,vaoffset);
            printElem(&elem,f,vaoffset);
            std::cout << std::endl;
            offset += elem.vertexHeader->size;
        }
        //std::getchar();
    }
    //quit(fd)

    close(sfd);
    close(fd);
    close(vafd);
}
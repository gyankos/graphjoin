//
// Created by Giacomo Bergami on 16/08/16.
//


#include <string>
#include <fstream>
#include <set>
#include <iostream>
#include "serializers.h"
#include "mappers.h"
#include "HashIndexArrayField.h"
#include "VAOffsetStructures.h"

unsigned long getStringArraySize(char** c_stringarray) {
    int i=0;
    unsigned long len = 0;
    while (c_stringarray[i]!=NULL) {
        len += (strlen(c_stringarray[i++])+1);
    }
    return len;
}

unsigned long getStringArraySize2(std::vector<std::string>& valueArray) {
    unsigned long toret = 0, len = valueArray.size();
    for (int i=0; i<len; i++) {
        toret += ((valueArray[i].size())+1);
    }
    return toret;
}

void serialize_vertex_id(FILE* index, unsigned int id, unsigned int hash, unsigned long offset) {
    INDEXFILE l;
    l.id = id;
    l.hash = hash;
    l.offset = offset;
    fwrite(&l, sizeof(INDEXFILE),1, index);
}

void serialize_hash(FILE* index, unsigned int hash, unsigned long offset) {
    HASHFILE l;
    l.hash = hash;
    l.bbb = 0;
    l.offset = offset;
    fwrite(&l, sizeof(HASHFILE),1, index);
}

void serialize_vertex_values(FILE *VAIndexFile,           //Where to store the values
                             unsigned long *VAOffset,      //Offset for the next VAFile
                             unsigned long *HashOffset,    //Offset for the next hash entry
                             unsigned int id,
                             unsigned long valueSize,          //Values
                             std::vector<std::string>& valueArray,
                             std::vector<EDGES_OUTIN>& out)           //Outgoing set
{
    int i = 0;
    int size = sizeof(unsigned int);

    HEADER values;
    int hSize = sizeof(HEADER);
    memset(&values,0,hSize);

    //Storing the HEADER of the vertex
    unsigned long valueArraySize = getStringArraySize2(valueArray);
    values.id = id;
    values.bbb = 0;
    //Header                  //Header of the values
    values.offsetOUT = sizeof(HEADER)+sizeof(unsigned int)*(valueSize)+valueArraySize;//hSize+((valueSize)*sizeof(unsigned long))+valueArraySize;
    //values.offsetOUT =  values.offsetIN + sizeof(unsigned int)+sizeof(EDGES_OUTIN)*in.size();
    values.size = values.offsetOUT + sizeof(unsigned int)+sizeof(EDGES_OUTIN)*out.size();
    *VAOffset = *VAOffset + values.size;
    *HashOffset = *HashOffset + values.size;

    fwrite(&values, sizeof(HEADER),1, VAIndexFile);
    //Storing the size of the value array
    fwrite(&valueSize,sizeof(unsigned int),1, VAIndexFile);


    // b. coping both element size and String
    unsigned int sizeString = 0;
    for (i = 0; i<valueSize-1; i++) { //Skipping the last integer, I already have the offsetIN for the end of the last string
        sizeString += (valueArray[i].size())+1;
        //Writing the number
        fwrite(&sizeString,sizeof (unsigned int),1, VAIndexFile);
    }

    for (i = 0; i<valueSize; i++) {
        //Writing the string
        fwrite(valueArray[i].c_str(),(valueArray[i].size())+1,1, VAIndexFile);
    }

    //Writing the In Element
    unsigned int sizeOut = out.size();
    fwrite(&sizeOut,sizeof(unsigned int),1,VAIndexFile);
    fwrite(&out[0],sizeof(EDGES_OUTIN),sizeOut, VAIndexFile);
}


void serialize_vertex_values2(FILE *VAIndexFile,           //Where to store the values
                             unsigned long *VAOffset,      //Offset for the next VAFile
                             unsigned long *HashOffset,    //Offset for the next hash entry
                              VAEntry_updating* elem)           //Outgoing set
{
    int i = 0;
    int size = sizeof(unsigned int);

    HEADER values;
    int hSize = sizeof(HEADER);
    memset(&values,0,hSize);

    //Storing the HEADER of the vertex
    //Header                  //Header of the values
    values.id = elem->new_id;
    values.bbb = 0;
    values.offsetOUT = elem->vertexHeader->offsetOUT; /// TODO XXX
    //unsigned int val = elem->inEdges.size(), val2 = elem->outEdges.size();
    unsigned int val2 = elem->outEdges.size();
    //values.offsetOUT =  values.offsetIN + sizeof(unsigned int)+sizeof(EDGES_OUTIN)*val;
    values.size = values.offsetOUT + sizeof(unsigned int)+sizeof(EDGES_OUTIN)*val2;
    *VAOffset = *VAOffset + values.size;
    *HashOffset = *HashOffset + values.size;

    fwrite(&values, sizeof(HEADER),1, VAIndexFile);
    fwrite(elem->stringArrayHeader,sizeof(unsigned int),elem->stringArrayHeader[0],VAIndexFile);
    fwrite((void*)elem->stringArray,elem->stringArraySize,1,VAIndexFile);
    //Writing the In Element
    //fwrite(&val,sizeof(val),1,VAIndexFile);
    //fwrite(&elem->inEdges[0],sizeof(EDGES_OUTIN),val, VAIndexFile);
    fwrite(&val2,sizeof(val2),1,VAIndexFile);
    fwrite(&elem->outEdges[0],sizeof(EDGES_OUTIN),val2, VAIndexFile);
    //fflush(VAIndexFile);
}
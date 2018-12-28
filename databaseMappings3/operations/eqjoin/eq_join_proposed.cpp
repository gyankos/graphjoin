//
// Created by Giacomo Bergami on 17/08/16.
// Implementation of the algorithm based upon the bucketing over both vertices and their outgoing vertices
//


#include <set>
#include <list>
#include <fstream>
#include <unistd.h>
#include <iostream>
#include <unordered_map>
#include <cassert>
#include "eq_join_proposed.h"
#include "../../serializer/mappers.h"
#include "../../serializer/HashIndexArrayField.h"
#include "../utils/query.h"
#include "../utils/store_join_results.h"

/*
#include "../Join.h"
#include "joinProposed.h"
#include "../../../../io/serializers.h"
#include "../../../../io/mappers.h"
#include "../../../../rbtree/RBTreeSet.h"
#include "../../../../numeric/compares.h"
#include "../../../../io/file/HashIndexArrayField.h"
#include "../../result/resultActions.h"
#include "../../../../io/file/VAOffsetStructures.h"
#include "../../result/ResultFactory.h"
#include "../../access/accessActions.h"
#include "../../access/AccessFactory.h"
#include "../../result/concrete/resultProposed.h"*/

double EqJoin(const std::string &leftPath, const std::string &rightPath, const std::string &resultPath,
              std::map<unsigned int, unsigned int>& join) {


    //Reading the schema stored in the folder, and performing the join between the vertices.
    /*std::map<std::string,int> leftMap, rightMap;
    int count = 0;
    {
        std::ifstream schemaLeft(leftPath+"schema.txt");
        std::string line;
        while (schemaLeft >> line)
        {
            leftMap[line] = count++;
        }
    }
    count = 0;
    {
        std::ifstream schemaRight(rightPath+"schema.txt");
        std::string line;
        while (schemaRight >> line)
        {
            rightMap[line] = count++;
        }
    }*/

    //std::string resultFileName = resultPath+"bulk_result.bin";
    //FILE * result = fopen(resultFileName.c_str(),"w");
    //resultActions* solution = ResultFactory::generateInstance(solutionStorage,resultPath+"bulk_result.bin");

    unsigned long sizeLeftVAOffset = 0, sizeRightVAOffset = 0;
    int fdLeftVAOffset = 0, fdRightVAOffset = 0;
    char* fileOffsetLeft = (char*)mmapFile(leftPath+"_VAOffset.bin",&sizeLeftVAOffset,&fdLeftVAOffset);
    char* fileOffsetRight = (char*)mmapFile(rightPath+"_VAOffset.bin",&sizeRightVAOffset,&fdRightVAOffset);
    FILE* result = NULL;
    unsigned long cont = 0;
    resultProposed(resultPath, &result, &cont);

    query qt1;
    query qt2;
    for (auto it = join.begin(), end = join.end(); it != end; it++) {
        qt1.emplace_front(it->first, it->second);
        qt2.emplace_front(it->first, it->second);
    }

    clock_t  t = clock();
    //std::vector<hashstruct> hashes_with_offset; // it is ordered because the hash file is ordered, and the search is ordered, too
    {
        int fdLeft = 0, fdRight = 0;
        unsigned long sizeLeft = 0, sizeRight = 0;
        HASHFILE* left = (HASHFILE*)mmapFile(leftPath+"_Hash.bin",&sizeLeft,&fdLeft);
        HASHFILE* right = (HASHFILE*)mmapFile(rightPath+"_Hash.bin",&sizeRight,&fdRight);

        int fdLeft2 = 0, fdRight2 = 0;
        unsigned long sizeLeft2 = 0, sizeRight2 = 0;
        INDEXFILE *indexLeft = (INDEXFILE*)mmapFile(leftPath+"_Index.bin",&sizeLeft2,&fdLeft2),
                *indexRight = (INDEXFILE*)mmapFile(rightPath+"_Index.bin",&sizeRight2,&fdRight2);

        unsigned long i = 0, j = 0;
        sizeLeft = sizeLeft / sizeof(HASHFILE);
        sizeRight = sizeRight / sizeof(HASHFILE);

        //unsigned long vertices = 0;
        //unsigned long edges = 0;

        //bool startLeft = true, startRight = true;
        while ((i < sizeLeft)&&(j < sizeRight)) {
            //std::cout << "hash = "<< left[i].hash << " offset=" << left[i].offset << " bbb="<< left[i].bbb << std::endl;
            if (left[i].hash < right[j].hash) i++;
            else if (left[i].hash > right[j].hash) j++;
            else {
                unsigned long hash = left[i].hash;
                unsigned long offsetU = left[i].offset;
                unsigned long offsetV = right[j].offset;
                i++;
                j++;
                unsigned long uEnd = (i== sizeLeft-1) ? sizeLeftVAOffset : left[i].offset;
                unsigned long vEnd = (j==sizeRight-1) ? sizeRightVAOffset : right[j].offset;

                //std::cout << hash << " " << leftStart << " " << leftEnd << " " << rightStart << " " << rightEnd << std::endl;
                //hashes_with_offset.emplace_back(hash,leftStart,leftEnd,rightStart,rightEnd);

                    while (offsetU<uEnd) {
                        VAEntry u;
                        descriptorFromOffset(&u,offsetU,fileOffsetLeft);
                        unsigned int NuSize = VOUT_SIZE(&u);
                        EDGES_OUTIN* Nu = VOUT(&u);
                        qt1.compileOverLeftVertex(&u);
                        bool hasMult = false;

                        while (offsetV<vEnd) {
                            VAEntry v;
                            descriptorFromOffset(&v,offsetV,fileOffsetRight);
                            if (qt1.compileOverRightVertex(&v)) {

                                //vertices++;

                                storeVertex(u.vertexHeader->id,v.vertexHeader->id, result, &cont);

                                unsigned int NvSize = VOUT_SIZE(&v);
                                EDGES_OUTIN* Nv = VOUT(&v);
                                //std::cout << "|NU|=" << NuSize << std::endl;

                                int i = 0; int j = 0;
                                unsigned int hL = Nu[i].adjacentHash,  hR = Nv[j].adjacentHash;
                                //assert(Nu[i].adjacentHash==);
                                while ((i<NuSize) && (j<NvSize)) {
                                    if (hL< hR) {
                                        i++;
                                        if (i<NuSize) hL = Nu[i].adjacentHash;
                                    } else if (hL>hR) {
                                        j++;
                                        if (j<NvSize) hR = Nv[j].adjacentHash;
                                    } else {
                                        //std::cout << "Hash = " << hL << std::endl;
                                        int ii = i, jj = j;
                                        while (ii<NuSize && Nu[ii].adjacentHash == hL) {
                                            jj = j;
                                            VAEntry up;
                                            descriptorFromVertexID(&up,indexLeft[i].id,indexLeft,fileOffsetLeft);
                                            qt2.compileOverLeftVertex(&up);
                                            while (jj<NvSize && Nv[jj].adjacentHash == hR) {
                                                VAEntry vp;
                                                descriptorFromVertexID(&vp,indexRight[j].id,indexRight,fileOffsetRight);
                                                if (qt2.compileOverRightVertex(&vp)) {
                                                    //edges++;
                                                    // TODO: only for other data structures solution->storeVertexInternal(up.vertexHeader->id,vp.vertexHeader->id);
                                                    storeEdge(u.vertexHeader->id,v.vertexHeader->id,up.vertexHeader->id,vp.vertexHeader->id, result, &cont);
                                                }

                                                //std::cout << "\t" << Nu[ii].adjacentID << ", " << Nv[jj].adjacentID << std::endl;
                                                jj++;
                                            }
                                            ii++;
                                        }
                                        i = ii;
                                        j = jj;
                                        if (((i<NuSize) && (j<NvSize))) {
                                            hL = Nu[i].adjacentHash;
                                            hR = Nv[j].adjacentHash;
                                        }
                                    }
                                }

                            } // NEXT
                            offsetV += v.vertexHeader->size;

                        } // NEXT
                        offsetU += u.vertexHeader->size; // next
                    }

            }
        }
        // quit hashes file
        close(fdLeft);
        close(fdRight);
        close(fdLeft2);
        close(fdRight2);

        // std::cout << "V=" << vertices << " E=" << edges << std::endl;
    }
    //std::cout << mult << std::endl;
    //close(fdLeft);
    //close(fdRight);
    close(fdLeftVAOffset);
    close(fdRightVAOffset);
    //clock_t saveClocks = ((double)save(result, &cont));
    //double storageTime = (saveClocks)/((CLOCKS_PER_SEC/1000.0));
    t = clock() - t;
    //double algorithmTime = (((double)(t-saveClocks))/(CLOCKS_PER_SEC/1000.0));

    return (((double)t)/(CLOCKS_PER_SEC/1000.0));

    // quit all files

}
//
// Created by giacomo on 07/07/19.
//

//
// Created by Giacomo Bergami on 17/08/16.
// Implementation of the algorithm based upon the bucketing over both vertices and their outgoing vertices
//


#include <set>
#include <list>
#include <string>
#include <map>
#include <fstream>
#include <unistd.h>
#include <iostream>
#include <unordered_map>
#include <cassert>
#include "eq_disj_join.h"
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

std::map<unsigned long, std::pair<unsigned long, unsigned long>> BI{};
unsigned long BI_MAX, BI_i, BI_j, sizeLeft, sizeRight;
HASHFILE *left, *right;
FILE* result;
std::set<unsigned long> notLeft, notRight;
unsigned long sizeLeftVAOffset, sizeRightVAOffset;
char* fileOffsetLeft;
char* fileOffsetRight;
query qt2;
INDEXFILE *indexLeft, *indexRight;

inline static std::map<unsigned long, std::pair<unsigned long, unsigned long>>::iterator is_in_bi(unsigned long kp) {
    if (kp <= BI_MAX)
        return BI.find(kp);
    else {
        while ((BI_i < sizeLeft)&&(BI_j < sizeRight)) {
            if (left[BI_i].hash < right[BI_j].hash) BI_i++;
            else if (left[BI_i].hash > right[BI_j].hash) BI_j++;
            else {
                unsigned long hash = left[BI_i].hash;
                BI_MAX = hash;
                BI[hash] = std::make_pair(left[BI_i].offset, right[BI_j].offset);
                BI_i++;
                BI_j++;
                if (hash == kp)
                    return BI.find(kp);
                else if (hash > kp)
                    return BI.end();
            }
        }
        return BI.end();
    }
}

void disjunctive(unsigned long l, unsigned long r, EDGES_OUTIN* EL, unsigned long EL_i, unsigned long EL_size, EDGES_OUTIN* ER, unsigned long ER_j, unsigned long ER_size, unsigned long kp) {
    std::map<unsigned long, std::pair<unsigned long, unsigned long>>::iterator it = is_in_bi(kp);
    if (it != BI.end()) {
        // Left join
        while ((EL_i < EL_size) && (EL[EL_i].adjacentHash == kp)) {
            VAEntry up;
            descriptorFromVertexID(&up, EL[EL_i].adjacentID, indexLeft, fileOffsetLeft);
            if (notLeft.find(up.vertexHeader->id) == notLeft.end()) {
                qt2.compileOverLeftVertex(&up);
                unsigned long offsetV = it->second.second;
                unsigned long vEnd = ((ER_j) == sizeRight-2) ? sizeRightVAOffset : right[ER_j+1].offset;
                while (offsetV < vEnd) {
                    VAEntry vp;
                    descriptorFromOffset(&vp,offsetV,fileOffsetRight);
                    if ((notRight.find(vp.vertexHeader->id) == notRight.end()) && qt2.compileOverRightVertex(&vp)) {
                        storeEdge(l,r,up.vertexHeader->id,vp.vertexHeader->id, result, NULL);
                    }
                    offsetV += vp.vertexHeader->size;
                }
            }
            EL_i++;
        }

        // Right join
        while ((ER_j < ER_size) && (ER[ER_j].adjacentHash == kp)) {
            VAEntry vp;
            descriptorFromVertexID(&vp, ER[ER_j].adjacentID, indexRight, fileOffsetRight);
            if (notRight.find(vp.vertexHeader->id) == notRight.end()) {
                unsigned long offsetU = it->second.first;
                unsigned long uEnd = ((EL_i) == sizeLeft-2) ? sizeLeftVAOffset : left[EL_i+1].offset;
                while (offsetU < uEnd) {
                    VAEntry up;
                    descriptorFromOffset(&up,offsetU,fileOffsetLeft);
                    qt2.compileOverLeftVertex(&up);
                    if ((notLeft.find(up.vertexHeader->id) == notLeft.end()) && qt2.compileOverRightVertex(&vp)) {
                        storeEdge(l,r,up.vertexHeader->id,vp.vertexHeader->id, result, NULL);
                    }
                    offsetU += up.vertexHeader->size; // next
                }
            }
            ER_j++;
        }
    }
}

double EqDisjunctiveJoin(const std::string &leftPath, const std::string &rightPath, const std::string &resultPath,
                         std::map<unsigned int, unsigned int> &join) {

    notLeft.clear();
    notRight.clear();
    BI.clear();
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

    sizeRightVAOffset = 0;
    sizeLeftVAOffset = 0;
    int fdLeftVAOffset = 0, fdRightVAOffset = 0;
    fileOffsetLeft = (char *) mmapFile(leftPath + "_VAOffset.bin", &sizeLeftVAOffset, &fdLeftVAOffset);
    fileOffsetRight = (char *) mmapFile(rightPath + "_VAOffset.bin", &sizeRightVAOffset, &fdRightVAOffset);
    result = NULL;
    unsigned long cont = 0;
    resultProposed(resultPath, &result, &cont);

    query qt1;
    for (auto it = join.begin(), end = join.end(); it != end; it++) {
        qt1.emplace_front(it->first, it->second);
        qt2.emplace_front(it->first, it->second);
    }

    clock_t  t = clock();
    //std::vector<hashstruct> hashes_with_offset; // it is ordered because the hash file is ordered, and the search is ordered, too
    {
        int fdLeft = 0, fdRight = 0;
        sizeLeft = 0, sizeRight = 0;
        left = (HASHFILE*)mmapFile(leftPath+"_Hash.bin",&sizeLeft,&fdLeft);
        right = (HASHFILE*)mmapFile(rightPath+"_Hash.bin",&sizeRight,&fdRight);

        int fdLeft2 = 0, fdRight2 = 0;
        unsigned long sizeLeft2 = 0, sizeRight2 = 0;
        indexRight = (INDEXFILE *) mmapFile(rightPath + "_Index.bin", &sizeRight2, &fdRight2);
        indexLeft = (INDEXFILE *) mmapFile(leftPath + "_Index.bin", &sizeLeft2, &fdLeft2);

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
                BI_MAX = std::max(hash, BI_MAX);
                BI[hash] = std::make_pair(left[i].offset, right[j].offset);
                BI_i = i;
                BI_j = j;

                unsigned long offsetU = left[i].offset;
                unsigned long offsetV = right[j].offset;
                i++;
                j++;
                unsigned long uEnd = (i == sizeLeft-1) ? sizeLeftVAOffset : left[i].offset;
                unsigned long vEnd = (j == sizeRight-1) ? sizeRightVAOffset : right[j].offset;

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
                            // inserting the vertex in the part of the result
                            storeVertex(u.vertexHeader->id,v.vertexHeader->id, result, &cont);

                            unsigned int NvSize = VOUT_SIZE(&v);
                            EDGES_OUTIN* Nv = VOUT(&v);
                            //std::cout << "|NU|=" << NuSize << std::endl;

                            int i = 0; int j = 0;
                            unsigned int hL = Nu[i].adjacentHash,  hR = Nv[j].adjacentHash;
                            //assert(Nu[i].adjacentHash==);
                            while ((i<NuSize) && (j<NvSize)) {
                                if (hL< hR) {
                                    // Not in intersection: call Disjunction
                                    notLeft.clear();
                                    notRight.clear();
                                    disjunctive(u.vertexHeader->id,v.vertexHeader->id, Nu, i, NuSize, Nv, j, NvSize, hL);

                                    i++;
                                    if (i<NuSize) hL = Nu[i].adjacentHash;
                                } else if (hL>hR) {
                                    // Not in intersection: call Disjunction
                                    notLeft.clear();
                                    notRight.clear();
                                    disjunctive(u.vertexHeader->id,v.vertexHeader->id, Nu, i, NuSize, Nv, j, NvSize, hR);

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
                                                notLeft.insert(ii);
                                                notRight.insert(jj);
                                                storeEdge(u.vertexHeader->id,v.vertexHeader->id,up.vertexHeader->id,vp.vertexHeader->id, result, &cont);
                                            }

                                            //std::cout << "\t" << Nu[ii].adjacentID << ", " << Nv[jj].adjacentID << std::endl;
                                            jj++;
                                        }
                                        ii++;
                                    }
                                    disjunctive(u.vertexHeader->id,v.vertexHeader->id, Nu, i, ii, Nv, j, jj, hL); // hL == hR
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

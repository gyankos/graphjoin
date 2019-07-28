//
// Created by giacomo on 01/11/18.
//

#define _GLIBCXX_PARALLEL
#include <thread>
#include <unordered_map>
#include <iostream>
#include <string>
#include <sstream>
#include <fstream>
#include <cstdio>
#include <cstring>
#include <vector>
#include <map>
#include <ctime>
#include <algorithm>
#include <malloc.h>
#include "entry.h"
#include "../hashing/hashing.h"
#include "serializers.h"

std::map<unsigned int, std::vector<unsigned int>> hashToVertices;
std::unordered_map<unsigned int, std::vector<std::string>> vertexToValues;
std::unordered_map<unsigned int, unsigned int> vertexToHash;
unsigned int prime = 17;

void vertex_load(std::string csvVertices, std::vector<int> numHashes) {
    std::ifstream dataFile(csvVertices);
    std::string vertexLine;
    hashToVertices.clear();
    vertexToHash.clear();
    vertexToValues.clear();

    while (std::getline(dataFile, vertexLine)) {
        std::istringstream iss(vertexLine);
        std::string s;
        unsigned int id = 0;
        bool firstSet = true;
        unsigned int h = 3;
        std::vector<std::string> ptr;
        unsigned int count_args = 0;
        while (std::getline(iss, s, ',')) {
            if (firstSet) {
                id = std::stoul(s);
                firstSet = false;
            } else {
                vertexToValues[id].emplace_back(s);
                if (std::find(numHashes.begin(), numHashes.end(), count_args) != numHashes.end())
                    h = h * prime + hashCode((char*)s.c_str());
                count_args++;
            }
        }
        // Associating to each vertex its hash value
        vertexToHash[id] = h;
        hashToVertices[h].emplace_back(id);
    }
    //std::cout << "vertices done" << std::endl;
}

typedef typename std::unordered_multimap<unsigned int, unsigned int> ADJLIST;
ADJLIST adjList;

void edge_load(std::string csvEdges) {
    std::ifstream dataFile(csvEdges);
    std::string edgeLine;
    unsigned int src, dst;
    adjList.clear();

    while (dataFile >> src >> dst) {
        adjList.emplace(src, dst);
    }
    //std::cout << "edges done" << std::endl;
}

std::pair<double,double> csvSerialize(std::string &csvVertices, std::string csvEdges, std::vector<int> &numHashes, int currentSeed, int operandSize) {

    // Reading the vertices csv
    std::ifstream edgeFile(csvEdges);

    auto start = std::chrono::high_resolution_clock::now();

    std::thread first (vertex_load, csvVertices, numHashes);
    std::thread second (edge_load,csvEdges);

    // Barrier for wait
    first.join();
    second.join();
    auto finishLoad = std::chrono::high_resolution_clock::now();
    //std::cout << operandSize << "," << currentSeed << ",loading," << loadingTime << std::endl;

    std::string tmpFile = csvVertices + "_Hash.bin";
    FILE *primaryIndex = fopen(tmpFile.c_str(), "w");
    tmpFile = csvVertices + "_VAOffset.bin";
    FILE *table = fopen(tmpFile.c_str(), "w");
    tmpFile = csvVertices + "_Index.bin";
    FILE *secondaryIndex = fopen(tmpFile.c_str(), "w");
    unsigned long VAOffset = 0;
    unsigned long HashOffset = 0;

    auto it = hashToVertices.begin();
    while (it != hashToVertices.end()) {
        //if (cnt % 10000 == 1)
        //    std::cout << cnt << "-th Hash" << std::endl;

        unsigned int hash = it->first;

        // Writing the hash value
        serialize_hash(primaryIndex, hash, HashOffset);

        for (unsigned int &id : it->second) {
            //unsigned int id = it->second;
            std::vector<EDGES_OUTIN> out;
            auto ptr = adjList.find(id);
            while (ptr != adjList.cend() && ptr->first == id) {
                out.emplace_back(vertexToHash[ptr->second], ptr->second);
                ptr++;
            }
            std::sort(out.begin(), out.end());
            unsigned long thiOffset = VAOffset;
            serialize_vertex_values(table, &VAOffset, &HashOffset, id, vertexToValues[id].size(), vertexToValues[id], out);
            serialize_vertex_id(secondaryIndex, id, hash, thiOffset);
        }
        it++;
    }
    auto finishIndex = std::chrono::high_resolution_clock::now();
    malloc_stats();
    std::chrono::duration<double> loadingTime = finishLoad - start;
    std::chrono::duration<double> d = finishIndex - start;

    return std::make_pair(loadingTime.count(), d.count()-loadingTime.count());
    //std::cout << operandSize << "," << currentSeed << ",total," << d << std::endl;
    //std::cout << operandSize << "," << currentSeed << ",indexing," << (d-loadingTime) << std::endl;
    //std::cout << csvEdges << "\t" << d << std::endl;
}
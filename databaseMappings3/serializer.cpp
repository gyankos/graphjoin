//
// Created by giacomo on 03/11/18.
//
#define _GLIBCXX_PARALLEL
#include <iostream>
#include <string>
#include <sstream>
#include <fstream>
#include <algorithm>
#include <map>
#include <vector>
#include "serializer/csvSerialize.h"

extern "C" {
    #include <stdio.h>
}

int main(int argc, char** argv) {
    // e.g., graph.txt_
    std::string prefix {argv[1]};
    // e.g., _0.400000_1_0
    std::string postfix {argv[2]};
    // e.g., 10,100,1000,....
    std::string positions {argv[3]};
    // e.g., 5,6,7,....
    std::string seedpos {argv[4]};
    // e.g., 5,6,7,....
    std::string joinArgs {argv[5]};

    std::vector<int> sizes;
    {
        std::string token;
        std::istringstream tokenStream(positions);
        while (std::getline(tokenStream, token, ',')) {
            sizes.push_back(std::atoi(token.c_str()));
        }
        std::sort(sizes.begin(), sizes.end());
    }

    std::vector<int> seeds;
    {
        std::string token;
        std::istringstream tokenStream(seedpos);
        while (std::getline(tokenStream, token, ',')) {
            seeds.push_back(std::atoi(token.c_str()));
        }
        std::sort(seeds.begin(), seeds.end());
    }

    std::vector<int> hashArgs;
    {
        std::string token;
        std::istringstream tokenStream(joinArgs);
        while (std::getline(tokenStream, token, ',')) {
            hashArgs.push_back(std::atoi(token.c_str()));
        }
        std::sort(hashArgs.begin(), hashArgs.end());
    }

    unsigned long N = sizes.size();
    unsigned long M = seeds.size();
    for (unsigned long i = 0; i<N; i++) {
        int currentGraphSize = sizes[i];
        std::string cgsString = std::to_string(currentGraphSize);
        double loading = 0, indexing = 0;

        for (unsigned long j = 0; j<M; j++) {
            int currentSeed = seeds[j];
            std::string csString = std::to_string(currentSeed);
            std::string common{prefix};
            common += cgsString;
            common+= "_";
            common+= csString;
            common+= "_";
            common+= postfix;

            const std::string &edgeFile{common};
            std::string vertexFile = common + "_vertices.csv";

            //for (int h = 0; h<5; h++)
                std::pair<double, double> ret = csvSerialize(vertexFile, edgeFile, hashArgs, currentSeed, currentGraphSize);
                loading += ret.first;
                indexing += ret.second;
        }
        std::cout << cgsString  << ",loading," << (loading) << std::endl;
        std::cout << cgsString  << ",indexing," << (indexing) << std::endl;
    }
}
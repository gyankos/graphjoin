

#include <iostream>
#include <fstream>
#include <cstring>
#include <sstream>
#include <map>
#include "operations/eqjoin/eq_join_proposed.h"
#include <vector>
#include <algorithm>

int main(int argc, char** argv) {
    // e.g., graph.txt_
    std::string prefix {argv[1]};
    // e.g., _0.400000_1_0
    std::string postfix {argv[2]};
    // e.g., 10,100,1000,....
    std::string positions {argv[3]};
    // e.g., 5,6,7,....
    std::string seedpos {argv[4]};

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

    std::map<unsigned int, unsigned int> map;
    {
        // Left operand field Comparison
        std::string leftOperandArgs{argv[5]};
        // Right operand field Comparison
        std::string rightOperandArgs{argv[6]};

        {
            std::string lTok, rTok;
            std::istringstream ltStr(leftOperandArgs), rtStr(rightOperandArgs);
            while (std::getline(ltStr, lTok, ',') && std::getline(rtStr, rTok, ',')) {
                map[std::stoul(lTok)] = static_cast<unsigned int>(std::stoul(rTok));
            }
        }
    }

    unsigned long N = sizes.size();
    for (unsigned long i = 0; i<N; i++) {
        int currentGraphSize = sizes[i];
        std::string cgsString = std::to_string(currentGraphSize);
        std::string outFile{argv[7]};
        outFile += cgsString;

        std::string leftOperandEdge, rightOperandEdge;

        {
            int currentSeed = seeds[0];
            std::string csString = std::to_string(currentSeed);
            leftOperandEdge = prefix;
            leftOperandEdge += cgsString;
            leftOperandEdge += "_";
            leftOperandEdge += csString;
            leftOperandEdge += "_";
            leftOperandEdge += postfix;
        }

        {
            int currentSeed = seeds[1];
            std::string csString = std::to_string(currentSeed);
            rightOperandEdge = prefix;
            rightOperandEdge += cgsString;
            rightOperandEdge += "_";
            rightOperandEdge += csString;
            rightOperandEdge += "_";
            rightOperandEdge += postfix;
        }

        std::string leftOperandVertex = leftOperandEdge + "_vertices.csv";
        std::string rightOperandVertex = rightOperandEdge + "_vertices.csv";
        // TODO: check the operation
        std::cout << cgsString << "," << EqJoin(leftOperandVertex, rightOperandVertex, outFile, map) << std::endl;

        //"/media/giacomo/Data/Progetti/journalgraphjoin/databaseMappings3/operators/graph.txt_100000000_5_0.400000_1_0_vertices.csv"
        // "/media/giacomo/Data/Progetti/journalgraphjoin/databaseMappings3/operators/graph.txt_100000000_6_0.400000_1_0_vertices.csv"
        // "3,5" "3,5" "/media/giacomo/Data/out/100000000"

    }








    return 0;
}
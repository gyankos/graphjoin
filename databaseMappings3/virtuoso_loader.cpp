//
// Created by giacomo on 14/12/18.
//

#include "virtuoso/test.h"

#include <string>
#include <fstream>
#include <vector>
#include <iostream>
#include <cmath>
#include <ctime>
#include <sstream>
#include <algorithm>
#include "virtuoso/Virtuoso.h"
#include "virtuoso/SampleVirtuoso.h"

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

    // Left operand headers
    std::vector<std::string> schemaLeft;
    std::string leftToken;

    // Right operand headers
    std::vector<std::string> schemaRight;
    std::string rightToken;

    std::istringstream left{argv[5]};
    std::istringstream right{argv[6]};

    while (std::getline(left, leftToken, ',')) {
        schemaLeft.push_back(leftToken);
    }
    while (std::getline(right, rightToken, ',')) {
        schemaRight.push_back(rightToken);
    }


    unsigned long N = sizes.size();
    unsigned long M = seeds.size();
    for (unsigned long i = 0; i<N; i++) {
        int currentGraphSize = sizes[i];
        std::string cgsString = std::to_string(currentGraphSize);
        std::string leftGraphName = cgsString +"L/";
        std::string rightGraphName = cgsString +"R/";

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

        std::cout << leftOperandVertex << "\t" << rightOperandVertex << std::endl;
        // Starting the connection with virtuoso
        clock_t t = std::clock();
        {
            sampleVirtuoso sv{leftGraphName, true};
            {
                std::ifstream vertexFile{leftOperandVertex};
                std::string vertex;
                while (std::getline(vertexFile, vertex)) {
                    sv.addNewVertex(schemaLeft, vertex);
                }
            }

            std::ifstream edgeFile{leftOperandEdge};
            {
                std::ifstream edgeFile{leftOperandEdge};
                unsigned long src, dst;
                while (edgeFile >> src >> dst) {
                    sv.addNewEdge(src, dst);
                }
            }

        }
        {
            sampleVirtuoso sv{rightGraphName, true};
            {
                std::ifstream vertexFile{rightOperandVertex};
                std::string vertex;
                while (std::getline(vertexFile, vertex)) {
                    sv.addNewVertex(schemaLeft, vertex);
                }
            }

            std::ifstream edgeFile{rightOperandEdge};
            {
                std::ifstream edgeFile{leftOperandEdge};
                unsigned long src, dst;
                while (edgeFile >> src >> dst) {
                    sv.addNewEdge(src, dst);
                }
            }
        }
        std::cout << cgsString << "," << ((double)((double)(std::clock() - t))/(CLOCKS_PER_SEC / 1000.0)) << std::endl;
    }
}
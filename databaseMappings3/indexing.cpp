//
// Created by giacomo on 03/11/18.
//
#include <iostream>
#include <string>
#include <sstream>
#include <fstream>
#include <algorithm>
#include <map>
#include <vector>

extern "C" {
    #include <stdio.h>
}

std::map<unsigned long, unsigned long> indexMapping;
unsigned long index = 0;

unsigned long updatedIndex(unsigned long original) {
    std::map<unsigned long, unsigned long>::iterator lb = indexMapping.lower_bound(original);
    if(lb != indexMapping.end() && !(indexMapping.key_comp()(original, lb->first))) {
        // key already exists
        // update lb->second if you care to
        return lb->second;
    } else {
        // the key does not exist in the map
        // add it to the map
        unsigned long toret = index++;
        indexMapping.insert(lb, std::map<unsigned long, unsigned long>::value_type(original, toret));    // Use lb as a hint to insert,
        return toret;
        // so it can avoid another lookup
    }
}

void clearIndex() {
    indexMapping.clear();
    index = 0;
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

    unsigned long N = sizes.size();
    unsigned long M = seeds.size();
    for (unsigned long i = 0; i<N; i++) {
        int currentGraphSize = sizes[i];
        std::string cgsString = std::to_string(currentGraphSize);

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
            std::string edgeFileTMP = common + "_tmp";
            std::string vertexFile = common + "_vertices.csv";
            std::string vertexFileTMP = common + "_vertices.csv_tmp";

            {
                std::map<unsigned long, std::vector<unsigned long>> map;
                std::cout << std::endl << edgeFile << std::endl;
                std::ifstream infile(edgeFile);
                unsigned long src, dst;
                while (infile >> src >> dst) {
                    src = updatedIndex(src);
                    dst = updatedIndex(dst);
                    map[src].emplace_back(dst);
                }

                for (auto it = map.begin(); it != map.end(); it++) {
                    std::sort(it->second.begin(), it->second.end());
                }

                FILE* file = fopen(edgeFileTMP.c_str(), "w");
                for (auto it = map.begin(); it != map.end(); it++) {
                    src = it->first;
                    for (unsigned long dst : it->second) {
                        fprintf(file, "%u\t%u\n", src, dst);
                    }
                }
                fclose(file);
                std::remove(edgeFile.c_str());
                std::rename(edgeFileTMP.c_str(), edgeFile.c_str());
            }

            {
                std::cout << std::endl << vertexFile << std::endl;
                std::ifstream infile(vertexFile);
                std::string line;
                std::map<unsigned long, std::string> map;
                while (std::getline(infile, line)) {
                    std::string token;
                    std::istringstream tokenStream(line);
                    unsigned long id = 0;
                    unsigned long tokenLength = 0;
                    while (std::getline(tokenStream, token, ',')) {
                        tokenLength = token.length()+1;
                        id = updatedIndex(std::stoul(token.c_str()));
                        break;
                    }
                    std::string nline{line};
                    nline.replace(0, tokenLength, std::to_string(id)+",");
                    map[id] = nline;
                }

                FILE* file = fopen(vertexFileTMP.c_str(), "w");
                for (auto it = map.begin(); it != map.end(); it++) {
                    fprintf(file, "%s\n", it->second.c_str());
                }
                fclose(file);
                std::remove(vertexFile.c_str());
                std::rename(vertexFileTMP.c_str(), vertexFile.c_str());
            }

            clearIndex();
        }
    }
}
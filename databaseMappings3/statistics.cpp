/*
 * statistics.cpp
 * This file is part of databaseMappings3
 *
 * Copyright (C) 2018 - Giacomo Bergami
 *
 * graphSampler is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * graphSampler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with graphSampler. If not, see <http://www.gnu.org/licenses/>.
 */


#include <map>
#include <boost/dynamic_bitset.hpp>
#include <unordered_map>
#include <iostream>
#include <sstream>
#include <fstream>
#include <random>
#include <stack>
#include <unordered_set>
#include <bits/unordered_set.h>
#include <queue>

template<typename Out> void split(const std::string &s, char delim, Out result) {
    std::stringstream ss;
    ss.str(s);
    std::string item;
    while (std::getline(ss, item, delim)) {
        *(result++) = item;
    }
}

std::vector<std::string> split(const std::string &s, char delim) {
    std::vector<std::string> elems;
    split(s, delim, std::back_inserter(elems));
    return elems;
}

int main(int argc, char** argv) {

    int lineno = 0;
    char* file = "conf.txt";
    if (argc > 1) {
        file = argv[1];
    }
    std::ifstream cfgfile(file);
    std::unordered_map<std::string,std::string> options;
    if (cfgfile.good()) {
        std::string line;

        while( std::getline(cfgfile, line) ){
            std::istringstream is_line(line);
            std::string key;
            if(line.find("#") && std::getline(is_line, key, '=') )
            {
                std::string value;
                if( std::getline(is_line, value) )
                    options[key] = value;
            }
        }
    } else {
        std::cerr << "Error: configuration file '" << file << "' does not exists. I'm going to use the default settings " << std::endl;
    }

    std::string graph{"graph.txt"};
    if (options.find("graph")!=options.end()) {
        graph = options["graph"];
    }

    std::string line;
    std::ifstream infile(graph);
    std::unordered_map<unsigned long, std::vector<std::pair<bool,unsigned long>>> map;
    unsigned long vCount = 0;
    unsigned long count = 0;

    bool isFirst = true;
    unsigned long id = 0;
    unsigned long counterId = 1;
    if (infile.good()) {
        std::cout << "Loading the graph file (" << graph << ")..." << std::endl;
        while (std::getline(infile, line)) {
            if (line.find("#") != 0) {
                unsigned long src, dst;
                std::istringstream iss(line);
                std::string token;
                int c = 0;
                while(std::getline(iss, token, '\t')) {
                    if (c++)
                        dst = std::stoul (token,nullptr,0);
                    else
                        src = std::stoul (token,nullptr,0);
                }
                map[src].emplace_back(false,dst);
                count++;
                if (options.find("undirected")!=options.end()) {
                    map[dst].emplace_back(false,src);
                    count++;
                }
                if (isFirst) {
                    id = src;
                    isFirst = false;
                } else {
                    unsigned long tmp;
                    if ((tmp = map[src].size()) > counterId) {
                        id = src;
                        counterId = tmp;
                    }
                }
                if (count % 100000 == 0) {
                    std::cout << count << std::endl;
                    std::cout << "MAX = " << id << " VAL = " << counterId << std::endl;
                }
                vCount = std::max(src,vCount);
                vCount = std::max(dst,vCount);
            }
        }
        std::cout << "Adjacency matrix loaded in main memory" << std::endl;
    } else {
        std::cerr << "Error: the adjacency list file '" << graph << "' does not exists. I'm going to use the default settings " << std::endl;
    }

}

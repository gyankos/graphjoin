/*
 * unordered.cpp
 * This file is part of usergenerator
 *
 * Copyright (C) 2018 - Giacomo Bergami
 *
 * usergenerator is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * usergenerator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with usergenerator. If not, see <http://www.gnu.org/licenses/>.
 */

 
#include <boost/dynamic_bitset.hpp>
#include <unordered_map>
#include <iostream>
#include <sstream>
#include <string>
#include <fstream>
#include <utility>
#include <random>
#include <memory>
#include <array>
#include <algorithm>
#include <vector>

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
	{
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
	}
	
	std::string graph{"graph.txt"};
	if (options.find("graph")!=options.end()) {
		graph = options["graph"];
	}
	
	std::string line;
	std::cout << "Loading the graph file (" << graph << ")..." << std::endl;
	std::ifstream infile(graph);
	std::unordered_map<unsigned long, std::vector<std::pair<bool,unsigned long>>> map;
	unsigned long vCount = 0;
	unsigned long count = 0;
	while (std::getline(infile, line)) {
		if (line.find("#") != 0) {
			unsigned long src, dst;
			std::istringstream iss(line);
			std::string token;
			int c = 0;
			while(std::getline(iss, token, '\t')) {
				if (c++)
					src = std::stoul (token,nullptr,0);
				else 
					dst = std::stoul (token,nullptr,0);
			}
			map[src].emplace_back(false,dst);
			count++;
			if (count % 100000 == 0)
				std::cout << count << std::endl;
			vCount = std::max(src,vCount);
			vCount = std::max(dst,vCount);
		}
	}
	std::vector<unsigned long> adjSCount{vCount,0};
	std::cout << "Adjacency matrix loaded in main memory" << std::endl;

	std::vector<unsigned long> l{10};
	if (options.find("samples")!=options.end()) {
		l.clear();
		std::string samples = options["samples"];
		{
			std::vector<std::string> splitted = split(samples,',');
			transform(splitted.begin(),splitted.end(),std::back_inserter(l),
			          [](std::string i){ unsigned long j = std::stoul(i); return j; });
		}
		std::sort(l.begin(), l.end());
	}
	
	// Selecting the next vertex step
	std::mt19937 rvGen;
	int samplerVertex = 0;
	if (options.find("vertex")!=options.end()) {
		samplerVertex = std::stoi(options["vertex"]);
	}
	rvGen.seed(samplerVertex);
	
	// Skipping to the next vertex
	std::mt19937 rjGen;
	int samplerSkip = 1;
	if (options.find("skip")!=options.end()) {
		samplerSkip = std::stoi(options["skip"]);
	}
	rjGen.seed(samplerSkip);
	
	// Vector of next??
	std::vector<unsigned long> next{10};
	std::mt19937 nchoiceGen;
	if (options.find("next")!=options.end()) {
		next.clear();
		std::string samples = options["next"];
		{
			std::vector<std::string> splitted = split(samples,',');
			transform(splitted.begin(),splitted.end(),std::back_inserter(next),
			          [](std::string i){ unsigned long j = std::stoul(i); return j; });
		}
		std::sort(next.begin(), next.end());
	}
	//
	
	// Randomly choosing the jumped vertex
	std::uniform_int_distribution<unsigned long> rv;
	rv = std::uniform_int_distribution<unsigned long>(0, vCount);
	
	// Jump probability
	std::uniform_real_distribution<double> rj{0.0,1.0};
	
	double jumpProb = 0.4;
	if (options.find("jumpProb")!=options.end()) {
		std::istringstream i(options["jumpProb"]);
		i >> jumpProb;
	}
	
	for (unsigned long samplerNext : next) {
		nchoiceGen.seed(samplerNext);
		unsigned long counter = 0;
		bool startFirst = false;
		unsigned long u = 0;
		if (options.find("starting")!=options.end()) {
			startFirst = true;
	  		std::string::size_type sz;   // alias of size_t
			startVertex = std::stol(str_dec,&sz);
		} else {
			u = rv(rvGen);
		}
		std::cout << "starting from vertex " << u << std::endl;
		
		// first visited vertex, u
		boost::dynamic_bitset<> db{u};
		for (unsigned long size : l) {
			do {
				int v;
				int step;
				auto nuf = map.find(u);
				unsigned long Nusize = 0;
				if (nuf != map.end()) {
					Nusize = nuf->second.size();
				}
				//hasToJump
				double doJump = rj(rjGen);
				if ((Nusize>0) && doJump > jumpProb) {
					step = 0;
					std::uniform_int_distribution<> nuNext(0, Nusize-1);
					do {
						unsigned long idgen = nuNext(nchoiceGen);
						if (!nuf->second[idgen].first) {
							adjSCount[u]++;
						}
						nuf->second[idgen].first = true;
						v = nuf->second[idgen].second;
						db[v] = true;
					} while ((adjSCount[u] == Nusize)&&(step++<Nusize));

					if (step <= Nusize) {
						u = v;
						db[u] = true;
					} else {
						u = rv(rvGen);
						db[u] = true;
					}
				} else {
					u = rv(rvGen);
					db[u] = true;
				}
			} while (counter<size);
			
			  std::ofstream myfile;
			  std::string filename = graph+"_"+std::to_string(size)+"_"+std::to_string(samplerNext)+"_"+std::to_string(jumpProb)+"_"+std::to_string(samplerSkip)+"_"+std::to_string(samplerVertex);
			  myfile.open (filename);
			  for (auto it=map.begin(); it!=map.end(); ++it) {
			  	if (db[it->first]) {
			  		for (std::pair<bool,unsigned long> cp : it->second) {
			  			if (cp.first) {
			  				myfile << it->first << "\t" << cp.second << "\n";
			  			}
			  		}
			  	}
			  }
			  myfile.close();
		}
	}
}

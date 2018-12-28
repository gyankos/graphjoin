//
// Created by giacomo on 01/11/18.
//

#include <iostream>
#include "entry.h"

entry::entry() {};
entry::entry(unsigned int i, unsigned int h, unsigned long o, bool hasMatch) : id{i}, hash{h}, /*offsetInOutgoingFile{o},*/ hasFoundMatch{hasMatch} {}
entry::entry(const entry& cp) {
    id = cp.id;
    hash = cp.hash;
    if (hash==0) {
        std::cerr << id << std::endl;
        exit(1);
    }
    //offsetInOutgoingFile = cp.offsetInOutgoingFile;
    hasFoundMatch = cp.hasFoundMatch;
};
//
// Created by giacomo on 01/11/18.
//

#ifndef DATABASEMAPPINGS3_ENTRY_H
#define DATABASEMAPPINGS3_ENTRY_H


class entry {
public:
    unsigned int id;
    unsigned int hash;
    //unsigned long offsetInOutgoingFile;
    bool hasFoundMatch;
    entry(unsigned int i, unsigned int h, unsigned long o, bool hasMatch);
    entry(const entry& cp);
    entry();
} ;


#endif //DATABASEMAPPINGS3_ENTRY_H

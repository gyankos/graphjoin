//
// Created by giacomo on 04/11/18.
//

#include "query.h"

void query::emplace_front(int l, int r) {
    qcks.emplace_back(l,r);
}

void query::compileOverLeftVertex(VAEntry *left) {
    for (int i = 0; i<qcks.size(); i++) {
        qcks[i].compileOverLeftVertex(left);
    }
}

bool query::compileOverRightVertex(VAEntry *right) {
    for (int i = 0; i<qcks.size(); i++) {
        if (!qcks[i].compileOverRightVertex(right)) return false;
    }
    return true;
}

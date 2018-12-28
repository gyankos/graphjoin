//
// Created by giacomo on 01/11/18.
//

#include "hashing.h"

unsigned int doHash3(std::vector<std::string> &stringarray, std::vector<int>& hashPosition) {
    unsigned int prime = 17;
    unsigned int result = 3;
    int c = 0;
    int N = stringarray.size();
    for (int i : hashPosition) {
        result = result * prime + hashCode((char*)stringarray.at(i).c_str());
    }
    return result;
}

unsigned int hashCode(char *string) {
    unsigned int len = strlen(string), h = 0;
    if (h == 0 && len > 0) {
        for (int i = 0; i < len; i++) {
            h = 31 * h + (unsigned int)string[i];
        }
    }
    return h;
}

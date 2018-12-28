//
// Created by Giacomo Bergami on 11/09/16.
//

#include "SchemaValues.h"

void removeSubstrs(std::string& s,
                   const std::string& p) {
    std::string::size_type n = p.length();

    for (std::string::size_type i = s.find(p);
         i != std::string::npos;
         i = s.find(p))
        s.erase(i, n);
}

int asIntValue(unsigned char *val) {
    std::string validString{(char*)val};
    validString.erase(0, 1);
    removeSubstrs(validString,SPARQL_INT_DEF);
    return std::stoi(validString);
}

std::string asStringValue(unsigned char *val) {
    std::string validString{(char*)val};
    validString.erase(0, 1);
    removeSubstrs(validString,SPARQL_STRING_DEF);
    return validString;
}
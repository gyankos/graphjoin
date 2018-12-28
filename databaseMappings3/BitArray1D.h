//
// Created by giacomo on 15/12/18.
//

#ifndef DATABASEMAPPINGS3_BITARRAY1D_H
#define DATABASEMAPPINGS3_BITARRAY1D_H


#include <climits>
#include <stdexcept>
#include <vector>

class Bitarray1D {
    using Element = unsigned int;

    class Bit {
        Element& element;
        const Element mask;
    public:
        Bit(Element& element, Element mask) : element{element}, mask{mask} {}
        Bit(const Bit& other) : Bit{other.element, other.mask} {}
        operator bool() const { return element & mask; }
        Bit& operator=(bool b) { element = b ? element | mask : element & ~mask; return *this; }
        Bit& operator=(const Bit& other) { return this->operator=(bool(other)); }
    };

    static const std::size_t element_bits = CHAR_BIT * sizeof (Element);

    std::size_t bits;
    std::vector<Element> array;

public:
    Bitarray1D(std::size_t bits);
    ~Bitarray1D() = default;
    std::size_t size() const;
    Bit operator[](std::size_t index);
    void set(std::size_t index, bool value = true);

};


#endif //DATABASEMAPPINGS3_BITARRAY1D_H

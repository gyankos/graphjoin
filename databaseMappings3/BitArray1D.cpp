//
// Created by giacomo on 15/12/18.
//

#include "BitArray1D.h"


Bitarray1D::Bitarray1D(std::size_t bits)
        : bits{bits},
          array{}
{
    // rounded-up division
    array.resize((bits + element_bits - 1) / element_bits);
}

Bitarray1D::Bit Bitarray1D::operator[](std::size_t index) {
    if (index >= bits)
        throw std::out_of_range("Index out of range");
    return Bit(array[index / element_bits], 1u << (index % element_bits));
}

void Bitarray1D::set(std::size_t index, bool value) {
    this->operator[](index) = value;
}

/*
 * IteratorOfKeyWithIterator.java
 * This file is part of databaseMappings
 *
 * Copyright (C) 2016 - Giacomo Bergami
 *
 * databaseMappings is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * databaseMappings is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with databaseMappings. If not, see <http://www.gnu.org/licenses/>.
 */



package it.giacomobergami.utils.datastructures.trees.iterator;

import it.giacomobergami.utils.datastructures.MultiMapValuePointer;
import it.giacomobergami.utils.datastructures.Pair;

import java.util.Iterator;

/**
 * Scans the data structure uniformly and returns the pair <Key,Iterator<E>> over the values of the iteration
 */
public class IteratorOfKeyWithIterator<Key extends Comparable<Key>,E> implements Iterator<Pair<Key,Iterator<E>>> {

    private TreeMultiMapValuePointerIterator<Key,E> it;

    public IteratorOfKeyWithIterator(TreeMultiMapValuePointerIterator<Key, E> it) {
        this.it = it;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public Pair<Key, Iterator<E>> next() {
        if (it.hasNext()) {
            MultiMapValuePointer<Key, E> snd = it.next();
            Key k = it.getCurrentKey();
            return new Pair<>(k,snd.iterator());
        } else return null;
    }
}

/*
 * IteratorOfValues.java
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

import java.util.Iterator;

/**
 * Created by bergami on 4/13/16.
 * Implements an iterator over a collection of iterators' elements
 */
public class IteratorOfValues<Key extends Comparable<Key>,E> implements Iterator<E> {

    private TreeMultiMapValuePointerIterator<Key,E> itcoll;
    private Iterator<E> currIt;


    public IteratorOfValues(TreeMultiMapValuePointerIterator<Key,E> itcoll) {
        this.itcoll = itcoll;
        currIt = this.itcoll.hasNext() ? this.itcoll.next().iterator() : null;
    }

    public boolean hasNext() {
        if (currIt!= null && currIt.hasNext()) return  true;
        else {
            //if the current iterator is null, it means that it has no elements over which iterate.
            //Same thing happens if the current iterator has no elements, and it is not null
            if (currIt!= null && (!currIt.hasNext())) {
                currIt = null;
            }
            if (currIt==null) {
                while (itcoll.hasNext() && (currIt==null)) {
                    currIt = itcoll.next().iterator();
                    if (!currIt.hasNext()) currIt=null;
                }
            }
            //at this point if currIt is not null, then it means that it exists at least one element E
            return  currIt!=null;
        }
    }

    public E next() {
        return hasNext() ? currIt.next() : null;
    }

}

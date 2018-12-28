/*
 * UnidirectionalSetIterator.java
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



package it.giacomobergami.utils.datastructures.iterators;

import it.giacomobergami.utils.datastructures.pointers.UnidirectionalSetPointer;

import java.util.Iterator;

/**
 * Created by vasistas on 16/04/16.
 */
public class UnidirectionalSetIterator<E> implements Iterator<E> {


    private UnidirectionalSetPointer<E> head;

    public UnidirectionalSetIterator(UnidirectionalSetPointer<E> head) {
        this.head = head;
    }

    public boolean hasNext() {
        return (head!=null);
    }

    public E next() {
        if (head==null)
        return null;
        else {
            E toret = head.value;
            head = head.next;
            return toret;
        }
    }
}

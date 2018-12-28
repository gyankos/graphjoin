/*
 * UnidirectionalSet.java
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



package it.giacomobergami.utils.datastructures.sets;

import it.giacomobergami.utils.datastructures.iterators.UnidirectionalSetIterator;
import it.giacomobergami.utils.datastructures.pointers.UnidirectionalSetPointer;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by vasistas on 16/04/16.
 */
public class UnidirectionalSet<E> implements ISet<E> {

    public int size; // policy: only read
    private UnidirectionalSetPointer<E> head;

    public UnidirectionalSet() {
        this.head = null;
        size = 0;
    }

    @Override
    public Optional<E> get(int position) {
        return head==null ? Optional.<E>empty() : head.get(position);
    }

    @Override
    public E first() {
        return head==null ? null : (head.value);
    }

    @Override
    public boolean add(E value) {
        if (value==null) return false;
        UnidirectionalSetPointer<E> toadd = new UnidirectionalSetPointer<E>(value);
        if (head == null) {
            head = toadd;
            size++;
            return true;
        } else {
            boolean toret = head.insert(toadd);
            if (toret) size++;
            return toret;
        }
    }

    @Override
    public boolean remove(E value) {
        if (value==null) return false;
        if (head==null) {
            return false;
        } else  {
            if (Objects.equals(head.value,value)) {
                size--;
                head = head.next;
                return true;
            } else return head.remove(value);
        }
    }


    @Override
    public Iterator<E> iterator() {
        return new UnidirectionalSetIterator<>(head);
    }

    @Override
    public boolean contains(E src) {
        return head==null ? false : head.contains(src);
    }

    @Override
    public int getSize() {
        return size;
    }

}

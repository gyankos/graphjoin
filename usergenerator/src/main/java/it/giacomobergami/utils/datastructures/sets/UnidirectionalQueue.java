/*
 * UnidirectionalQueue.java
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

import java.util.*;

/**
 * Created by vasistas on 16/04/16.
 */
public class UnidirectionalQueue<E> implements ISet<E> {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnidirectionalQueue)) return false;
        UnidirectionalQueue<?> that = (UnidirectionalQueue<?>) o;
        if (size != that.size) return false;
        return head != null ? head.equals(that.head) : that.head == null;
    }

    @Override
    public int hashCode() {
        int result = size;
        result = 31 * result + (head != null ? head.hashCode() : 0);
        return result;
    }

    public int size; // policy: only read
    private UnidirectionalSetPointer<E> head;
    private UnidirectionalSetPointer<E> tail;

    public UnidirectionalQueue(Collection<E> value) {
        this();
        if (!value.isEmpty()) {
            for (E x : value) add(x);
        }
    }

    public UnidirectionalQueue(E value) {
        this();
        add(value);
    }

    public UnidirectionalQueue() {
        this.head = null;
        this.tail = null;
        size = 0;
    }

    @Override
    public Optional<E> get(int position) {
        return head==null ? Optional.<E>empty() : head.get(position);
    }

    @Override
    public E first() {
        return head == null ? null : head.value;
    }

    @Override
    public boolean add(E value) {
        if (value==null) return false;
        UnidirectionalSetPointer<E> toadd = new UnidirectionalSetPointer<E>(value);
        if (head==null)
            head = tail = toadd;
        else {
            tail.next = toadd;
            toadd.prev = tail;
            tail = toadd;
        }
        size++;
        return true;
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

    public void clear() {
        head = tail = null;
        size = 0;
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


    public void sort() {
        if (getSize()<=0) return;
        Object[] toSort = new Object[getSize()];
        int i = 0;
        for (E obj : this) toSort[i++] = obj;
        Arrays.sort(toSort);
        UnidirectionalSetPointer<E> ptr = head;
        i = 0;
        do {
            ptr.value = (E)toSort[i++];
            ptr = ptr.next;
        } while (ptr!=null);
    }

    public static void main(String args[]) {
        UnidirectionalQueue<Integer> q = new UnidirectionalQueue<>();
        q.add(1);
        q.add(2);
        q.add(3);
        q.add(4);
        q.iterator().forEachRemaining(System.out::println);
    }

}

/*
 * UnidirectionalSetPointer.java
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



package it.giacomobergami.utils.datastructures.pointers;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by vasistas on 15/04/16.
 */
public class UnidirectionalSetPointer<E> {
    public E value; //policy: the value is only checked
    public UnidirectionalSetPointer<E> next, prev;

    public UnidirectionalSetPointer(E value) {
        this.value = value;
        this.next = null;
        this.prev = null;
    }
    public boolean insert(UnidirectionalSetPointer<E> newValue) {
        if (newValue==null) return false;
        E val = newValue.value;

        UnidirectionalSetPointer<E> current = this;

        while (current!=null) {
            if (Objects.equals(current.value,val))
                return false;
            if (current.next==null) {
                newValue.prev = current;
                current.next = newValue;
                return true;
            } else {
                current=current.next;
            }
        }
        return false; //I've reached current==null
    }

    public boolean remove(E value) {
        if (value==null) return false;

        UnidirectionalSetPointer<E> current = this;

        while (current!=null) {
            if (Objects.equals(current.value,value)) {
                if (current.prev==null) throw new UnsupportedOperationException("Cannot remove an element directly if it is the first element of a list");
                else {
                    current.prev.next = current.next;
                    if (current.next!=null)
                        current.next.prev = current.prev;
                    return true;
                }
            } else {
                current = current.next;
            }
        }
        return false;

    }


    public boolean contains(E val) {
        if (val==null) return false;
        UnidirectionalSetPointer<E> current = this;

        while (current!=null) {
            if (current.value.equals(val)) return true;
            if (current.next==null) {
                return false;
            } else {
                current = current.next;
            }
        }
        return false; //I've reached current==null
    }

    public Optional<E> get(int position) {
        if (position<0) return  Optional.empty();
        int pos = position;
        UnidirectionalSetPointer<E> current = this;

        while (current!=null && pos>0) {
            current=current.next;
            pos--;
        }
        return current==null ? Optional.<E>empty() : Optional.<E>of(current.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnidirectionalSetPointer)) return false;

        UnidirectionalSetPointer<?> that = (UnidirectionalSetPointer<?>) o;

        return value.equals(that.value) && Objects.equals(next,that.next);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

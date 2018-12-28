/*
 * IteratorOfIterator.java
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
 * Created by vasistas on 17/04/16.
 */
public class IteratorOfIterator<E> implements Iterator<E> {

    private Iterator<Iterator<E>> iit;
    private Iterator<E> it;

    public IteratorOfIterator(Iterator<Iterator<E>> iit) {
        this.iit = iit;
        this.it = iit == null ? null : (iit.hasNext() ? iit.next() : null);
    }

    public boolean hasNext() {
        do {
            if (it == null) {
                if (iit.hasNext()) it = iit.next();
            }
            if (it == null || (!it.hasNext())) {
                it = null;
            } else return true;
        } while (iit!=null && iit.hasNext());
        return false;
    }

    public E next() {
        return (hasNext()) ? it.next() : null;
    }

}

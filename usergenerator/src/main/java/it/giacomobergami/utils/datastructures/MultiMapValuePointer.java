/*
 * MultiMapValuePointer.java
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



package it.giacomobergami.utils.datastructures;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

/**
 * Implements an entry of a MultiMap. its aim is to quicken the access to the <Key,Value> node, as much as possible
 */
public interface MultiMapValuePointer<K extends Comparable,V> {
    K getKey();
    Optional<V> get(int position);
    boolean add(V value);
    Iterator<V> iterator();
    boolean contains(V src);
    int getSize();
    void update(Collection<V> newValues);
    void removeNode();
}

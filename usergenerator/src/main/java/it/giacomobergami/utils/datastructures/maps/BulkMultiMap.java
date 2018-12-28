/*
 * BulkMultiMap.java
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



package it.giacomobergami.utils.datastructures.maps;

import it.giacomobergami.utils.datastructures.Pair;
import it.giacomobergami.utils.datastructures.sets.UnidirectionalStack;

import java.util.*;

/**
 * Created by vasistas on 16/04/16.
 * This map is generated when new (unique) elements are generated, and are prepared to be inserted into a RBTree
 */
public class BulkMultiMap<K extends Comparable<K>, V> implements Map<K,Collection<V>> {
    
    private UnidirectionalStack<Pair<K,UnidirectionalStack<V>>> map;

    public BulkMultiMap() {
        this.map = new UnidirectionalStack<>();
    }

    @Override
    public int size() {
        return map.size;
    }

    @Override
    public boolean isEmpty() {
        return map.size==0;
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("Error: this is only a bulk element. You cannot perform search operations");
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Error: this is only a bulk element. You cannot perform search operations");
    }

    @Override
    public Collection<V> get(Object key) {
        throw new UnsupportedOperationException("Error: this is only a bulk element. You cannot perform search operations");
    }

    @Override
    public Collection<V> put(K key, Collection<V> value) {
        map.add(new Pair<K, UnidirectionalStack<V>>(key,new UnidirectionalStack<>(value)));
        return null;
    }

    @Override
    public Collection<V> remove(Object key) {
        throw new UnsupportedOperationException("Error: this is only a bulk element. You cannot perform update operations");
    }

    @Override
    public void putAll(Map<? extends K, ? extends Collection<V>> m) {
        for (Entry<? extends K, ? extends Collection<V>> e: m.entrySet()) put(e.getKey(),e.getValue());
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("Error: use the entrySet() command");
    }

    @Override
    public Collection<Collection<V>> values() {
        throw new UnsupportedOperationException("Error: use the entrySet() command");
    }

    @Override
    public Set<Entry<K, Collection<V>>> entrySet() {
        return new Set<Entry<K, Collection<V>>>() {
            @Override
            public int size() {
                return map.size;
            }

            @Override
            public boolean isEmpty() {
                return map.size==0;
            }

            @Override
            public boolean contains(Object o) {
                throw new UnsupportedOperationException("Error: this is only a bulk element. You cannot perform search operations");
            }

            @Override
            public Iterator<Entry<K, Collection<V>>> iterator() {
                return new Iterator<Entry<K, Collection<V>>>() {

                    Iterator<Pair<K,UnidirectionalStack<V>>> it = map.iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public Entry<K, Collection<V>> next() {
                        return new Entry<K, Collection<V>>() {

                            Pair<K,UnidirectionalStack<V>> p = it.next();

                            @Override
                            public K getKey() {
                                return p.first;
                            }

                            @Override
                            public Collection<V> getValue() {
                                Iterator<V> it2 = p.value.iterator();
                                List<V> coll = new ArrayList<>(p.value.size);
                                it2.forEachRemaining(coll::add);
                                return coll;
                            }

                            @Override
                            public Collection<V> setValue(Collection<V> value) {
                                throw new UnsupportedOperationException("Error: this is only a bulk element. You cannot perform update operations");
                            }
                        };
                    }
                };
            }

            @Override
            public Object[] toArray() {
                Object[] a =  new Object[map.size];
                int i = 0;
                Iterator<Pair<K, UnidirectionalStack<V>>> it = map.iterator();
                while (it.hasNext()) a[i++] = it.next();
                return a;
            }

            @Override
            public <T> T[] toArray(T[] a) {
                int i = 0;
                Iterator<Pair<K, UnidirectionalStack<V>>> it = map.iterator();
                while (it.hasNext()) a[i++] = (T)it.next();
                return a;
            }

            @Override
            public boolean add(Entry<K, Collection<V>> kCollectionEntry) {
                put(kCollectionEntry.getKey(),kCollectionEntry.getValue());
                return true;
            }

            @Override
            public boolean remove(Object o) {
                throw new UnsupportedOperationException("Error: this is only a bulk element. You cannot perform update operations");
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends Entry<K, Collection<V>>> c) {
                boolean b = true;
                for (Entry<K, Collection<V>> x: c) b = b && add(x);
                return b;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException("Error: this is only a bulk element. You cannot perform update operations");
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException("Error: this is only a bulk element. You cannot perform update operations");
            }

            @Override
            public void clear() {
                map.clear();
            }
        };
    }
}

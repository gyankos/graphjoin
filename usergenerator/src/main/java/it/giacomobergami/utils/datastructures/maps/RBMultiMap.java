/*
 * RBMultiMap.java
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
import it.giacomobergami.utils.datastructures.pointers.RBTreePointer;
import it.giacomobergami.utils.datastructures.trees.iterator.TreeKeyIterator;
import it.giacomobergami.utils.datastructures.trees.rbtree.RBTree;

import java.util.*;

/**
 * Created by vasistas on 16/04/16.
 */
public class RBMultiMap<K extends Comparable<K>, V> implements Map<K,Collection<V>> {

    private final boolean isStack;
    private RBTree<K,V> tree;
    private int keyCount = 0;

    /**
     *
     * @param isStack if this parameter is true, the overflow list doesn't check if the element already exists
     *                (assuming unique insertions) but adds the element at the beginning of the list (it behaves
     *                as a stack). Otherwise, the list is scanned linearly and the elment is added
     */
    public RBMultiMap(boolean isStack) {
        this.isStack = isStack;
        this.tree = new RBTree<>(isStack);
    }

    @Override
    public int size() {
        return keyCount;
    }

    @Override
    public boolean isEmpty() {
        return tree.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return tree.lookup((K)key)!=null;
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("I do not want to scan all the tree for searching for a fukkin value. Please, provide its index");
    }

    @Override
    public Collection<V> get(Object key) {
        RBTreePointer<K, V> e = tree.lookup((K) key);
        List<V> list = new ArrayList<>(keyCount);
        if (e==null) return list;
        keyCount --;
        e.iterator().forEachRemaining(list::add);
        return list;
    }

    @Override
    public Collection<V> put(K key, Collection<V> value) {
        List<V> list = new ArrayList<>(keyCount);
        Pair<Boolean, RBTreePointer<K, V>> cp = tree.insertWithPair(key);
        boolean isNew = cp.first;
        RBTreePointer<K, V> e = cp.value;
        e.iterator().forEachRemaining(list::add);
        e.update(value);
        if (isNew) keyCount++;
        return list;
    }

    @Override
    public Collection<V> remove(Object key) {
        RBTreePointer<K, V> e = tree.lookup((K) key);
        List<V> list = new ArrayList<>(keyCount);
        if (e==null) return list;
        keyCount --;
        e.iterator().forEachRemaining(list::add); //saving all the previous values
        e.removeNode();
        return list;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Collection<V>> m) {
        for (Entry<? extends K, ? extends Collection<V>> e : m.entrySet()) put(e.getKey(),e.getValue());
    }

    @Override
    public void clear() {
        tree = new RBTree<>(isStack);
    }

    /**
     * Warning: if you modify this set, the Tree is going to be modified consequently.
     *          hence, if you perform the RetainAll operation, the provided collection is
     *          going to be scanned searching for the elements, while the tree is surfed
     * @return
     */
    @Override
    public Set<K> keySet() {
        return new Set<K>() {
            @Override
            public int size() {
                return keyCount;
            }

            @Override
            public boolean isEmpty() {
                return tree.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                return tree.lookup((K)o)!=null;
            }

            @Override
            public Iterator<K> iterator() {
                return new TreeKeyIterator<>(tree.root);
            }

            @Override
            public Object[] toArray() {
                Object[] a = new Object[keyCount];
                Iterator<K> it = iterator();
                int i = 0;
                while (it.hasNext()) a[i++] = it.next();
                return a;
            }

            @Override
            public <T> T[] toArray(T[] a) {
                Iterator<K> it = iterator();
                int i = 0;
                while (it.hasNext()) a[i++] = (T)it.next();
                return a;
            }

            @Override
            public boolean add(K k) {
                return tree.insertWithPointer(k)!=null;
        }

            @Override
            public boolean remove(Object o) {
                return tree.delete((K)o);
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                for (Object o : c) if (!contains(o)) return false;
                return true;
            }

            @Override
            public boolean addAll(Collection<? extends K> c) {
                boolean toret = true;
                for (K x : c) toret = toret && add(x);
                return toret;
            }


            @Override
            public boolean retainAll(Collection<?> c) {

                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                boolean toret = true;
                for (Object x : c) toret = toret && remove(x);
                return toret;
            }

            @Override
            public void clear() {
                tree = new RBTree<>(isStack);
            }
        };
    }

    @Override
    public Collection<Collection<V>> values() {
        return null;
    }

    @Override
    public Set<Entry<K, Collection<V>>> entrySet() {
        return null;
    }
}

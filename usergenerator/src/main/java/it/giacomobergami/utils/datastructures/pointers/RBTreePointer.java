/*
 * RBTreePointer.java
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

import it.giacomobergami.utils.datastructures.MultiMapValuePointer;
import it.giacomobergami.utils.datastructures.sets.UnidirectionalSet;
import it.giacomobergami.utils.datastructures.sets.UnidirectionalStack;
import it.giacomobergami.utils.datastructures.trees.rbtree.Color;
import it.giacomobergami.utils.datastructures.trees.rbtree.RBTree;
import it.giacomobergami.utils.datastructures.trees.rbtree.RedBlackNode;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

/**
 * Created by vasistas on 17/04/16.
 */
public class RBTreePointer<K extends Comparable<K>,V> implements MultiMapValuePointer<K,V> {

    private final RBTree<K, V> master;
    private RedBlackNode<K,V> n;
    private K rememberOldKey;

    public RBTreePointer(RBTree<K, V> master, RedBlackNode<K, V> node) {
        this.master = master;
        this.n = node;
        this.rememberOldKey = node.key;
    }

    @Override
    public K getKey() {
        return rememberOldKey;
    }

    @Override
    public Optional<V> get(int position) {
        return n==null ? Optional.empty() : n.get(position);
    }

    public V first() { return n.overflowList.first(); }

    @Override
    public boolean add(V value) {
        if (n==null) {
            n = master.insertWithPointer(rememberOldKey).n;
        }
        return n.add(value);
    }

    @Override
    public Iterator<V> iterator() {
        return n==null ? new Iterator<V>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public V next() {
                return null;
            }
        } : n.iterator();
    }

    @Override
    public boolean contains(V src) {
        return n==null ? false : n.contains(src);
    }

    @Override
    public int getSize() {
        return n==null ? 0 :n.getSize();
    }

    @Override
    public void update(Collection<V> newValues) {
        if (n==null) {
            n = master.insertWithPointer(rememberOldKey).n;
        }
        n.overflowList = n.isStack ? new UnidirectionalStack<>() : new UnidirectionalSet<>();
        for (V x : newValues) add(x);
    }

    @Override
    public void removeNode() {
        if (n == null)
            return;  // Key not found, do nothing
        if (n.left != null && n.right != null) {
            // Copy key/value from predecessor and then delete it instead
            RedBlackNode<K,V> pred = RBTree.maximumNode(n.left);
            n.key   = pred.key;
            n.overflowList = pred.overflowList;
            n = pred;
        }

        assert n.left == null || n.right == null;
        RedBlackNode<K,V> child = (n.right == null) ? n.left : n.right;
        if (RBTree.nodeColor(n) == Color.BLACK) {
            n.color = RBTree.nodeColor(child);
            RBTree.<K,V>deleteCase1(master,n);
        }
        RBTree.replaceNode(master,n, child);
        n = null;
    }

}

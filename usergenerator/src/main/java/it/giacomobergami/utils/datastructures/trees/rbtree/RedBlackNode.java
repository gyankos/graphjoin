/*
 * RedBlackNode.java
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



package it.giacomobergami.utils.datastructures.trees.rbtree;

import it.giacomobergami.utils.datastructures.Pair;
import it.giacomobergami.utils.datastructures.sets.ISet;
import it.giacomobergami.utils.datastructures.sets.UnidirectionalSet;
import it.giacomobergami.utils.datastructures.sets.UnidirectionalStack;
import it.giacomobergami.utils.datastructures.trees.utils.PositionIteratorEnum;

import java.util.Iterator;
import java.util.Optional;

/**
 * Created by vasistas on 16/04/16.
 */
public class RedBlackNode<Key extends Comparable<Key>, Value> implements ISet<Value>   {

    public static final int RED   = 0;
    public final boolean isStack;
    public Color color;

    public RedBlackNode(Key k, Value value, boolean isStack) {
        this.isStack = isStack;
        overflowList = isStack ? new UnidirectionalStack<>() : new UnidirectionalSet<>();
        if (value!=null) overflowList.add(value);
        key = k;
        left = parent = right = null;
    }

    public RedBlackNode(Key key, Value value, Color nodeColor, RedBlackNode<Key, Value> left, RedBlackNode<Key, Value> right, boolean isStack) {
        this(key,value,isStack);
        this.color = nodeColor;
        this.left = left;
        this.right = right;
        if (left  != null)  left.parent = this;
        if (right != null) right.parent = this;
        this.parent = null;
    }

    public RedBlackNode<Key, Value> grandparent() {
        assert parent != null; // Not the root node
        assert parent.parent != null; // Not child of root
        return parent.parent;
    }
    public RedBlackNode<Key, Value> sibling() {
        assert parent != null; // Root node has no sibling
        return  (this == parent.left ?  parent.right : parent.left);
    }
    public RedBlackNode<Key, Value> uncle() {
        assert parent != null; // Root node has no uncle
        assert parent.parent != null; // Children of root have no uncle
        return parent.sibling();
    }

    private static final PositionIteratorEnum NONE = PositionIteratorEnum.None;
    public RedBlackNode<Key,Value> parent, left, right;
    public Key key;
    public PositionIteratorEnum visit = NONE;
    public ISet<Value> overflowList;



    public Iterator<Pair<Key, Value>> valueIterator() {
        return new Iterator<Pair<Key, Value>>() {

            Iterator<Value> it = overflowList.iterator();


            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Pair<Key, Value> next() {
                return it.hasNext() ? new Pair<>(key,it.next()) : null;
            }
        };
    }




    public boolean isEmpty() {return overflowList.getSize()==0;}

    public String toString() {
        return "" + key;
    }


    @Override
    public Optional<Value> get(int position) {
        return overflowList.get(position);
    }

    @Override
    public Value first() {
        return overflowList.first();
    }

    @Override
    public boolean add(Value value) {
        return overflowList.add(value);
    }

    @Override
    public boolean remove(Value value) {
        return overflowList.remove(value);
    }

    @Override
    public Iterator<Value> iterator() {
        return overflowList.iterator();
    }

    public boolean contains(Value k) {
        return overflowList.remove(k);
    }

    @Override
    public int getSize() {
        return overflowList.getSize();
    }

    public boolean hasNext() {
        return ((parent!=null && parent.left==this)  // if I am a left child, I still have to visit the parent
                || (parent!=null && parent.right==this &&
                (    visit== PositionIteratorEnum.Left
                        ||  visit== PositionIteratorEnum.Center
                        || (visit== PositionIteratorEnum.Right && (right!=null ||
                        (((this.visit= PositionIteratorEnum.None)!=null) && parent.hasNext())))
                        || (this.visit== PositionIteratorEnum.None && parent.hasNext())))  //If I am a right child, either the siblings or the parent has still to be visited

                || (parent==null && (visit== PositionIteratorEnum.Left || visit== PositionIteratorEnum.Center ||
                (visit== PositionIteratorEnum.Right && right!=null)))
        ); // If the recursion meets the root, then I have to check if the root has been visited yet
    }


}

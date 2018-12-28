/*
 * AbstractTreeIterator.java
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

import it.giacomobergami.utils.datastructures.trees.rbtree.RBTree;
import it.giacomobergami.utils.datastructures.trees.rbtree.RedBlackNode;
import it.giacomobergami.utils.datastructures.trees.utils.PositionIteratorEnum;

import java.util.Iterator;

/**
 * Created by vasistas on 16/04/16.
 */
public abstract class AbstractTreeIterator <Key extends Comparable<Key>, E, Iterated> implements Iterator<Iterated> {

    private RedBlackNode<Key,E> current;
    protected final RBTree<Key, E> root;
    private boolean justStarted;

    public AbstractTreeIterator(RedBlackNode<Key,E> current, RBTree<Key, E> root) {
        this.current = current;
        this.root = root;
        if (this.current!=null) this.current.visit = PositionIteratorEnum.Left;
        justStarted = true;
    }

    public Key getCurrentKey() {
        return (current!=null && !justStarted) ? current.key : null;
    }

    @Override
    public boolean hasNext() {
        return current!=null && current.hasNext();
    }

    protected abstract Iterated transformCurrentStep(RBTree<Key, E> root,RedBlackNode<Key,E> current);


    @Override
    public Iterated next() {
        justStarted = false;
        if (current==null) return null;
        while (current!=null) {
            switch (current.visit) {
                case Left:{
                    if (current.left!=null) {
                        current.visit = PositionIteratorEnum.Center;
                        current = current.left;
                        current.visit = PositionIteratorEnum.Left; //Initialization of the left node's visit
                    } else {
                        current.visit= PositionIteratorEnum.Center;
                    }
                } break;

                case Center: {
                    current.visit= PositionIteratorEnum.Right;
                    return transformCurrentStep(root,current);
                }

                case Right: {
                    current.visit= PositionIteratorEnum.None;
                    if (current.right!=null) {
                        current = current.right;
                        current.visit = PositionIteratorEnum.Left;
                    } else {
                        current = current.parent;
                    }
                } break;

                case None:{
                    current = current.parent;
                } break;
            }
        }
        return null;
    }

}

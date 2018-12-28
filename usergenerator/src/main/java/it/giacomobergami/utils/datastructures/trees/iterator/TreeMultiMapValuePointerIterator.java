/*
 * TreeMultiMapValuePointerIterator.java
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

import it.giacomobergami.utils.datastructures.MultiMapValuePointer;
import it.giacomobergami.utils.datastructures.pointers.RBTreePointer;
import it.giacomobergami.utils.datastructures.trees.rbtree.RBTree;
import it.giacomobergami.utils.datastructures.trees.rbtree.RedBlackNode;

/**
 * The result of iterating over the tree is the pointer to the current tree node. All the different possible types of
 * iterators use this interface that holds the current point of the iteration.
 * @param <Key>
 * @param <E>
 */
public  class TreeMultiMapValuePointerIterator<Key extends Comparable<Key>, E> extends AbstractTreeIterator<Key,E,MultiMapValuePointer<Key,E>> {

    public TreeMultiMapValuePointerIterator(RedBlackNode<Key, E> current, RBTree<Key, E> root) {
        super(current, root);
    }

    @Override
    protected MultiMapValuePointer<Key, E> transformCurrentStep(RBTree<Key, E> root, RedBlackNode<Key, E> current) {
        return new RBTreePointer<>(root,current);
    }

}

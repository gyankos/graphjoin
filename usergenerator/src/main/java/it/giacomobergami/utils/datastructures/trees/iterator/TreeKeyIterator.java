/*
 * TreeKeyIterator.java
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

/**
 * Iterates only over the key of the tree
 * @param <Key>
 * @param <E>
 */
public  class TreeKeyIterator<Key extends Comparable<Key>, E> extends AbstractTreeIterator<Key,E,Key> {

    public TreeKeyIterator(RedBlackNode<Key, E> current) {
        super(current, null);
    }

    @Override
    protected Key transformCurrentStep(RBTree<Key, E> root, RedBlackNode<Key, E> current) {
        return current.key;
    }

}

/*
 * RBTree.java
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
import it.giacomobergami.utils.datastructures.pointers.RBTreePointer;
import it.giacomobergami.utils.datastructures.trees.iterator.TreeMultiMapValuePointerIterator;

public class RBTree<K extends Comparable<K>,V>
{
    public static final boolean VERIFY_RBTREE = false;
    private static final int INDENT_STEP = 4;
    private int count;
    public boolean isStack; //Policy Insertion of the neighbours

    public RedBlackNode<K,V> root;

    public RBTree(boolean isStack) {
        this.isStack = isStack;
        root = null;
        //verifyProperties();
        count = 0;
    }


    //////////////////
    private void verifyProperties() {
        if (VERIFY_RBTREE) {
            verifyProperty1(root);
            verifyProperty2(root);
            // Property 3 is implicit
            verifyProperty4(root);
            verifyProperty5(root);
        }
    }
    private static void verifyProperty1(RedBlackNode<?,?> n) {
        assert nodeColor(n) == Color.RED || nodeColor(n) == Color.BLACK;
        if (n == null) return;
        verifyProperty1(n.left);
        verifyProperty1(n.right);
    }
    private static void verifyProperty2(RedBlackNode<?,?> root) {
        assert nodeColor(root) == Color.BLACK;
    }
    public static Color nodeColor(RedBlackNode<?, ?> n) {
        return n == null ? Color.BLACK : ((RedBlackNode<?,?>)n).color;
    }
    private static void verifyProperty4(RedBlackNode<?,?> n) {
        if (nodeColor(n) == Color.RED) {
            assert nodeColor(n.left)   == Color.BLACK;
            assert nodeColor(n.right)  == Color.BLACK;
            assert nodeColor(n.parent) == Color.BLACK;
        }
        if (n == null) return;
        verifyProperty4(n.left);
        verifyProperty4(n.right);
    }
    private static void verifyProperty5(RedBlackNode<?,?> root) {
        verifyProperty5Helper(root, 0, -1);
    }
    private static int verifyProperty5Helper(RedBlackNode<?,?> n, int blackCount, int pathBlackCount) {
        if (nodeColor(n) == Color.BLACK) {
            blackCount++;
        }
        if (n == null) {
            if (pathBlackCount == -1) {
                pathBlackCount = blackCount;
            } else {
                assert blackCount == pathBlackCount;
            }
            return pathBlackCount;
        }
        pathBlackCount = verifyProperty5Helper(n.left,  blackCount, pathBlackCount);
        pathBlackCount = verifyProperty5Helper(n.right, blackCount, pathBlackCount);
        return pathBlackCount;
    }
    //////////////////




    private RedBlackNode<K,V> lookupNode(K key) {
        RedBlackNode<K,V> n = root;
        while (n != null) {
            int compResult = key.compareTo(n.key);
            if (compResult == 0) {
                return n;
            } else if (compResult < 0) {
                n = (RedBlackNode<K,V>)n.left;
            } else {
                assert compResult > 0;
                n = (RedBlackNode<K,V>)n.right;
            }
        }
        return n;
    }
    public RBTreePointer<K,V> lookup(K key) {
        RedBlackNode<K,V> n = lookupNode(key);
        return n == null ? null : new RBTreePointer<>(this,n);
    }
    private static <K extends Comparable<K>,V> void rotateLeft(RBTree<K,V> master, RedBlackNode<K,V> n) {
        RedBlackNode<K,V> r = n.right;
        replaceNode(master,n, r);
        n.right = r.left;
        if (r.left != null) {
            r.left.parent = n;
        }
        r.left = n;
        n.parent = r;
    }

    private static  <K extends Comparable<K>,V>  void rotateRight(RBTree<K,V> master, RedBlackNode<K,V> n) {
        RedBlackNode<K,V> l = n.left;
        replaceNode(master, n, l);
        n.left = l.right;
        if (l.right != null) {
            l.right.parent = n;
        }
        l.right = n;
        n.parent = l;
    }
    public static <K extends Comparable<K>,V> void replaceNode(RBTree<K, V> master, RedBlackNode<K, V> oldn, RedBlackNode<K, V> newn) {
        if (oldn.parent == null) {
            master.root = newn;
        } else {
            if (oldn == oldn.parent.left)
                oldn.parent.left = newn;
            else
                oldn.parent.right = newn;
        }
        if (newn != null) {
            newn.parent = oldn.parent;
        }
    }
    public RBTreePointer<K,V> insertWithPointer(K key) {
        return insertWithPair(key,null).value;
    }

    public Pair<Boolean,RBTreePointer<K,V>> insertWithPair(K key) {
        return insertWithPair(key,null);
    }

    public Pair<Boolean,RBTreePointer<K,V>> insertWithPair(K key, V value) {
        RedBlackNode<K,V> insertedNode = new RedBlackNode<K,V>(key, value, Color.RED, null, null,isStack);
        boolean toret = false;
        if (root == null) {
            root = insertedNode;
        } else {
            RedBlackNode<K,V> n = root;
            while (true) {
                int compResult = key.compareTo(n.key);
                if (compResult == 0) {
                    n.add(value);
                    return new Pair<>(false,new RBTreePointer<>(this,n));
                } else if (compResult < 0) {
                    if (n.left == null) {
                        n.left = insertedNode;
                        toret = true;
                        break;
                    } else {
                        n = n.left;
                    }
                } else {
                    assert compResult > 0;
                    if (n.right == null) {
                        n.right = insertedNode;
                        toret = true;
                        break;
                    } else {
                        n = n.right;
                    }
                }
            }
            insertedNode.parent = n;
        }
        insertCase1(insertedNode);
        //verifyProperties();
        if (toret) count++;
        return new Pair<>(true,new RBTreePointer<>(this,insertedNode));
    }
    public boolean insert(K key, V value) {
        RedBlackNode<K,V> insertedNode = new RedBlackNode<K,V>(key, value, Color.RED, null, null,isStack);
        boolean toret = false;
        if (root == null) {
            toret = true;
            root = insertedNode;
        } else {
            RedBlackNode<K,V> n = root;
            while (true) {
                int compResult = key.compareTo(n.key);
                if (compResult == 0) {
                    return n.add(value);
                } else if (compResult < 0) {
                    if (n.left == null) {
                        n.left = insertedNode;
                        toret = true;
                        break;
                    } else {
                        n = n.left;
                    }
                } else {
                    assert compResult > 0;
                    if (n.right == null) {
                        n.right = insertedNode;
                        toret = true;
                        break;
                    } else {
                        n = n.right;
                    }
                }
            }
            insertedNode.parent = n;
        }
        insertCase1(insertedNode);
        //verifyProperties();
        if (toret) count++;
        return toret;
    }
    private void insertCase1(RedBlackNode<K,V> n) {
        if (n.parent == null)
            n.color = Color.BLACK;
        else
            insertCase2(n);
    }
    private void insertCase2(RedBlackNode<K,V> n) {
        if (nodeColor(n.parent) == Color.BLACK)
            return; // Tree is still valid
        else
            insertCase3(n);
    }
    void insertCase3(RedBlackNode<K,V> n) {
        if (nodeColor(n.uncle()) == Color.RED) {
            n.parent.color = Color.BLACK;
            n.uncle().color = Color.BLACK;
            n.grandparent().color = Color.RED;
            insertCase1(n.grandparent());
        } else {
            insertCase4(this, n);
        }
    }
    void insertCase4(RBTree<K,V> master, RedBlackNode<K,V> n) {
        if (n == n.parent.right && n.parent == n.grandparent().left) {
            rotateLeft(this,n.parent);
            n = n.left;
        } else if (n == n.parent.left && n.parent == n.grandparent().right) {
            rotateRight(master, n.parent);
            n = n.right;
        }
        insertCase5(n);
    }
    void insertCase5(RedBlackNode<K,V> n) {
        n.parent.color = Color.BLACK;
        n.grandparent().color = Color.RED;
        if (n == n.parent.left && n.parent == n.grandparent().left) {
            rotateRight(this, n.grandparent());
        } else {
            assert n == n.parent.right && n.parent == n.grandparent().right;
            rotateLeft(this,n.grandparent());
        }
    }
    public boolean delete(K key) {
        RedBlackNode<K,V> n = lookupNode(key);
        if (n == null)
            return false;  // Key not found, do nothing
        if (n.left != null && n.right != null) {
            // Copy key/value from predecessor and then delete it instead
            RedBlackNode<K,V> pred = maximumNode(n.left);
            n.key   = pred.key;
            n.overflowList = pred.overflowList;
            n = pred;
        }

        assert n.left == null || n.right == null;
        RedBlackNode<K,V> child = (n.right == null) ? n.left : n.right;
        if (nodeColor(n) == Color.BLACK) {
            n.color = nodeColor(child);
            deleteCase1(this,n);
        }
        replaceNode(this,n, child);

        //verifyProperties();
        count = count - n.getSize();
        return true;
    }
    public boolean delete(K key, V value) {
        RedBlackNode<K,V> n = lookupNode(key);
        if (n == null) return false;
        boolean toret = n.remove(value);
        if (toret) count--;
        if (n.getSize()==0) toret = delete(key);
        //n is already empty; I don't need to remove elements furtherly
        return toret;
    }

    public static <K extends Comparable<K>,V> RedBlackNode<K,V> maximumNode(RedBlackNode<K, V> n) {
        assert n != null;
        while (n.right != null) {
            n = n.right;
        }
        return n;
    }
    public static <K extends Comparable<K>,V> void deleteCase1(RBTree<K, V> master, RedBlackNode<K, V> n) {
        if (n.parent == null)
            return;
        else
            deleteCase2(master,n);
    }
    private static <K extends Comparable<K>,V> void deleteCase2(RBTree<K,V> master, RedBlackNode<K,V> n) {
        if (nodeColor(n.sibling()) == Color.RED) {
            n.parent.color = Color.RED;
            n.sibling().color = Color.BLACK;
            if (n == n.parent.left)
                rotateLeft(master,n.parent);
            else
                rotateRight(master,n.parent);
        }
        deleteCase3(master,n);
    }
    private static  <K extends Comparable<K>,V>  void deleteCase3(RBTree<K,V> master, RedBlackNode<K,V> n) {
        if (nodeColor(n.parent) == Color.BLACK &&
            nodeColor(n.sibling()) == Color.BLACK &&
            nodeColor(n.sibling().left) == Color.BLACK &&
            nodeColor(n.sibling().right) == Color.BLACK)
        {
            n.sibling().color = Color.RED;
            deleteCase1(master,n.parent);
        }
        else
            deleteCase4(master,n);
    }
    private static <K extends Comparable<K>,V> void deleteCase4(RBTree<K,V> master, RedBlackNode<K,V> n) {
        if (nodeColor(n.parent) == Color.RED &&
            nodeColor(n.sibling()) == Color.BLACK &&
            nodeColor(n.sibling().left) == Color.BLACK &&
            nodeColor(n.sibling().right) == Color.BLACK)
        {
            n.sibling().color = Color.RED;
            n.parent.color = Color.BLACK;
        }
        else
            deleteCase5(master,n);
    }
    private static  <K extends Comparable<K>,V> void deleteCase5(RBTree<K,V> master, RedBlackNode<K,V> n) {
        if (n == n.parent.left &&
            nodeColor(n.sibling()) == Color.BLACK &&
            nodeColor(n.sibling().left) == Color.RED &&
            nodeColor(n.sibling().right) == Color.BLACK)
        {
            n.sibling().color = Color.RED;
            n.sibling().left.color = Color.BLACK;
            rotateRight(master,n.sibling());
        }
        else if (n == n.parent.right &&
                 nodeColor(n.sibling()) == Color.BLACK &&
                 nodeColor(n.sibling().right) == Color.RED &&
                 nodeColor(n.sibling().left) == Color.BLACK)
        {
            n.sibling().color = Color.RED;
            n.sibling().right.color = Color.BLACK;
            rotateLeft(master,n.sibling());
        }
        deleteCase6(master,n);
    }
    private static  <K extends Comparable<K>,V> void  deleteCase6(RBTree<K,V> master, RedBlackNode<K,V> n) {
        n.sibling().color = nodeColor(n.parent);
        n.parent.color = Color.BLACK;
        if (n == n.parent.left) {
            assert nodeColor(n.sibling().right) == Color.RED;
            n.sibling().right.color = Color.BLACK;
            rotateLeft(master,n.parent);
        }
        else
        {
            assert nodeColor(n.sibling().left) == Color.RED;
            n.sibling().left.color = Color.BLACK;
            rotateRight(master,n.parent);
        }
    }
    public void print() {
        printHelper(root, 0);
    }

    private static void printHelper(RedBlackNode<?,?> n, int indent) {
        if (n == null) {
            System.out.print("<empty tree>");
            return;
        }
        if (n.right != null) {
            printHelper(n.right, indent + INDENT_STEP);
        }
        for (int i = 0; i < indent; i++)
            System.out.print(" ");
        if (nodeColor(n) == Color.BLACK)
            System.out.println(n.key);
        else
            System.out.println("<" + n.key + ">");
        if (n.left != null) {
            printHelper(n.left, indent + INDENT_STEP);
        }
    }
    public static void main(String[] args) {
        RBTree<Integer,Integer> t = new RBTree<Integer,Integer>(false);
        t.print();

        java.util.Random gen = new java.util.Random();

        for (int i = 0; i < 5; i++) {
            int x = gen.nextInt(10000);
            int y = gen.nextInt(10000);

            t.print();
            System.out.println("Inserting " + x + " -> " + y);
            System.out.println();

            t.insert(x, y);
            assert t.lookup(x).equals(y);
        }
        for (int i = 0; i < 6; i++) {
            int x = gen.nextInt(10000);

            t.print();
            System.out.println("Deleting key " + x);
            System.out.println();

            t.delete(x);
        }
    }

    public TreeMultiMapValuePointerIterator<K,V> treeIterator() {
        return new TreeMultiMapValuePointerIterator<K,V>(root,this);
    }

    public boolean isEmpty() {
        return root == null;
    }

    public boolean contains(K searchhash, V elem) {
        RedBlackNode<K, V> l = lookupNode(searchhash);
        return l==null ? false : lookupNode(searchhash).contains(elem);
    }

    public int getSize() {
        return count;
    }

    public void clear() {
        root = null;
    }
}


/*
 * GoogleGraphVertex.java
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


package Neo4J.general;

import Neo4J.general.pointer.GraphIdPointerLong;
import Neo4J.general.pointer.Pointer;
import it.giacomobergami.utils.datastructures.HashableTreeMap;
import it.giacomobergami.utils.datastructures.sets.UnidirectionalStack;

import java.util.*;

/**
 * Implements the vertex from the old implementation. Uses the old RBStrategy for the neighbours
 */
public class GoogleGraphVertex extends INeighVertex {

    public static final boolean strategy = true;
    public INeigh neigh;

    public boolean addNeighbour(GoogleGraphVertex av) {
        return neigh.addNeighbour(av);
    }

    ////////////////////////
    public GoogleGraphVertex(GoogleGraphVertex v, Set<String> hashingScheme, Pointer id) {
        super(hashingScheme,id,true);
        this.attributes = new HashableTreeMap<>();
    }

    public GoogleGraphVertex(AbstractNode v, Set<String> hashingScheme, Pointer id) {
        super(hashingScheme, id,true);
        this.attributes = new HashableTreeMap<>();
        this.attributes.putAll(v.getValues());
    }

    public GoogleGraphVertex(Set<String> hashingScheme, Pointer id) {
            super(hashingScheme, id,true);
        this.attributes = new HashableTreeMap<>();
    }
    public GoogleGraphVertex(String type, Set<String> hashingScheme, Pointer id) {
        super(type,hashingScheme, id,true);
        attributes = new HashableTreeMap();
    }

    public GoogleGraphVertex(Collection<String> type, Set<String> hashingScheme, Pointer id) {
        super(hashingScheme, id,true);
        attributes = new HashableTreeMap<>();
    }
    ////////////////////////

    @Override
    public String getAttribute(String attr) {
        return attributes.get(attr);
    }

    @Override
    public void updateAll(Map<String, String> externMap) {
        attributes.putAll(externMap);
    }

    @Override
    public void setAttribute(String attr, String value) {
        attributes.put(attr,value);
    }

    @Override
    public Map<String,Comparable> cloneAttributes() {
        return (Map<String, Comparable>) attributes.clone();
    }

    protected HashableTreeMap<String,String> attributes;



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(attributes.toString());
        sb.append(":[");
        for (String s : type) {
            sb.append(s);
            sb.append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Set<String> getVertexSchema() {
        return attributes.keySet();
    }

    @Override
    public HashableTreeMap<String, String> getValues() {
        return attributes;
    }

    /**
     * Defines the single node operations over the node
     */
    public static class Operations {

        /**
         * Restriction of the attributes of the node v over
         * @param v
         * @param attributes
         * @return
         */
        public static GoogleGraphVertex restrict(AbstractNode v, Collection<String> attributes) {
            Set<String> hKeys = new HashSet<>(v.getVertexSchema());
            hKeys.retainAll(attributes);
            GoogleGraphVertex n = new GoogleGraphVertex(v.getType(), GraphIdPointerLong.anyPointer());
            for (String s : hKeys) {
                n.attributes.put(s,v.getAttribute(s));
            }
            return n;
        }

        public static GoogleGraphVertex restrictToVertexSchema(AbstractNode v, AbstractNode vs) {
            Set<String> hKeys = new HashSet<>(v.getVertexSchema());
            hKeys.retainAll(vs.getVertexSchema());
            GoogleGraphVertex n = new GoogleGraphVertex(v.getType(), GraphIdPointerLong.anyPointer());
            for (String s : hKeys) {
                n.attributes.put(s,v.getAttribute(s));
            }
            n.type = vs.getType();
            return n;
        }
/*
        public static NewBulkNode generateBulk(GoogleGraphVertex a, GoogleGraphVertex b) {
            UnidirectionalStack<Map.Entry<String,String>> s = getCommonAttributes(a.attributes,b.attributes);
            if (s==null) return null;
            else return new NewBulkNode(a,b,s);
        }
*/
        public static boolean testBulk(GoogleGraphVertex a, GoogleGraphVertex b) {
            return getCommonAttributes(a.attributes,b.attributes)!=null;
        }

        /**
         * Returns an element (Set of attributes) if the two vertices match, ana null element otherwise
         * @param l
         * @param r
         * @return
         */
        public static UnidirectionalStack<Map.Entry<String,String>> getCommonAttributes(TreeMap<String,String> l, TreeMap<String,String> r) {
            Iterator<Map.Entry<String,String>> lit = l.entrySet().iterator(), rit = r.entrySet().iterator();
            UnidirectionalStack<Map.Entry<String,String>> entryAttributeSet = new UnidirectionalStack<>();
            Map.Entry<String,String> a, b;
            if (lit.hasNext()) a = lit.next(); else a = null;
            if (rit.hasNext()) b = rit.next(); else b = null;
            if (lit.hasNext() && rit.hasNext()) {
                Set<String> toret = new HashSet<>();
                do {
                    int test = Objects.compare(a,b,(x,y)-> (x==null || y==null) ? -1 : (x==null ? -1 : (y==null ? 1 : x.getKey().toString().compareTo(y.getKey()))));
                    if (test<0) {
                        entryAttributeSet.add(a);
                        if (lit.hasNext()) a = lit.next(); else a = null;
                    } else if (test>0) {
                        entryAttributeSet.add(b);
                        if (rit.hasNext()) b = rit.next(); else b = null;
                    } else {
                        if (b.getValue().equals(a.getValue()))
                            entryAttributeSet.add(a);
                        else return null;
                        if (lit.hasNext()) a = lit.next(); else a = null;
                        if (rit.hasNext()) b = rit.next(); else b = null;
                    }
                } while (lit.hasNext() && rit.hasNext());
                int test = Objects.compare(a,b,(x,y)-> (x==null || y==null) ? -1 : (x==null ? -1 : (y==null ? 1 : x.getKey().toString().compareTo(y.getKey()))));
                if (test<0) {
                    if (a!=null) entryAttributeSet.add(a);
                    if (b!=null) entryAttributeSet.add(b);
                } else if (test>0) {
                    if (a!=null) entryAttributeSet.add(b);
                    if (b!=null) entryAttributeSet.add(a);
                }
                return entryAttributeSet;
            }
            while (a!=null) {

                entryAttributeSet.add(a);
                if (lit.hasNext()) a = lit.next(); else a = null;
            }
            while (b!=null) {

                entryAttributeSet.add(b);
                if (rit.hasNext())b = rit.next(); else b = null;
            }
            return entryAttributeSet;
        }
    }

    public static void printElement(UnidirectionalStack<Map.Entry<String,String>> e) {
        System.out.print("{");
        if (e!=null) {
            for (Map.Entry<String, String> x : e) {
                System.out.print(x.toString());
                System.out.print(", ");
            }
        }
        System.out.println("}");
    }

}

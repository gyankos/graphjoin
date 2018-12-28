/*
 * AbstractNode.java
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


import Neo4J.general.pointer.Pointer;
import it.giacomobergami.utils.DoubleHashObjects;
import it.giacomobergami.utils.datastructures.HashableHashSet;
import it.giacomobergami.utils.datastructures.HashableTreeMap;
import it.giacomobergami.utils.datastructures.iterators.Collector;
import it.giacomobergami.utils.datastructures.iterators.MapIterator;

import java.util.*;

/**
 * Created by vasistas on 24/02/16.
 */
public abstract class AbstractNode implements Comparable<AbstractNode>, DoubleHashObjects {

    private final Set<String> hashingScheme;
    //UnidirectionalStack<NewBulkNode> appearsInStack;
    public final Pointer id;
    public int hash;
    public int hashSearch;

    public void updateHashes() {
        //hashCode
        HashableTreeMap<String, String> tmp; //= getValues();
        //hash = tmp != null ? tmp.hashCode() : 0;
        //hash = 31 * hash + (getType() != null ? getType().hashCode() : 0);

        //hashValue
        tmp = getHashableValues();
        hashSearch = (hashingScheme == null ? new HashableHashSet(getValues().values()) : new Collector<>(new MapIterator<String, String>(hashingScheme.iterator()) {
            @Override
            public String apply(String s) {
                return getAttribute(s);
            }
        }, HashableHashSet::new).get()).hashCode();
        //hashSearch = tmp != null ? new HashableHashSet<>(tmp.values()).hashCode() : 0;
    }

    protected AbstractNode(Set<String> hashingScheme, Pointer id) {
        this.hashingScheme = hashingScheme;
        this.id = id;
        this.hash = id.hashCode();
        //appearsInStack = new UnidirectionalStack<>();
    }

    public abstract String getAttribute(String attr);

    public abstract void updateAll(Map<String, String> externMap);

    public abstract void setAttribute(String attr, String value);

    public abstract Map<String,Comparable> cloneAttributes();

    abstract public Set<String> getVertexSchema();

    abstract public HashableTreeMap<String,String> getValues();

    public HashableTreeMap<String,String> getHashableValues() {
        return hashingScheme==null ? getValues() :  getValues()
                .entrySet()
                .stream()
                .filter(e->hashingScheme.contains(e.getKey()))
                .collect(HashableTreeMap.stringCollector());
    }

    abstract public HashableHashSet<String> getType();


    public boolean isEmptyNode() {
        return getType().isEmpty() && getVertexSchema().isEmpty();
    }

    public Pointer getId() {
        return id;
    }

    @Override
    public int compareTo(AbstractNode right) {
        int cmp = getValues().compareTo(right.getValues());
        if (cmp!=0)
            return cmp;
        else return getType().compareTo(right.getType());
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public int searchhash() {
        return hashSearch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractNode)) return false;

        AbstractNode node = ((AbstractNode) o);

        if (getValues() != null ? !getValues().equals(node.getValues()) : node.getValues() != null) return false;
        return getType() != null ? getType().equals(node.getType()) : node.getType() == null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getValues().toString());
        sb.append(":[");
        for (String s : this.getType()) {
            sb.append(s);
            sb.append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

    public Set<String> getHashingScheme() {
        return hashingScheme == null ? getVertexSchema() : hashingScheme;
    }

    public abstract boolean removeNeighbour(AbstractNode dst);

    public abstract  boolean hasNeighbour(AbstractNode right);

    /*public void appearsInNew(NewBulkNode nbn) {
        appearsInStack.add(nbn);
    }*/

    /*public  Iterator<NewBulkNode> appearsIn() {
        return appearsInStack.iterator();
    }*/
}

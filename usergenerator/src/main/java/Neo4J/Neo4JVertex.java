/*
 * Neo4JVertex.java
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


package Neo4J;


import Neo4J.general.AbstractNode;
import Neo4J.general.pointer.GraphIdPointerLong;
import it.giacomobergami.utils.datastructures.HashableHashSet;
import it.giacomobergami.utils.datastructures.HashableTreeMap;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Created by vasistas on 18/12/15.
 */
public class Neo4JVertex extends AbstractNode {


    private Node self;

    public Neo4JVertex(Node self, Neo4JGraph owner, long id) {
        super(owner.getVertexLabelHashableSchema(self),new GraphIdPointerLong(id));
        this.self = self;
    }

    public Neo4JVertex(Node self, Neo4JGraph owner) {
        this(self,owner,self.getId());
    }

    @Override
    public void setAttribute(String attr, String value) {
        self.setProperty(attr,value);
    }

    @Override
    public Map<String, Comparable> cloneAttributes() {
        return self.getAllProperties().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, stringObjectEntry -> (Comparable)stringObjectEntry));
    }

    @Override
    public Set<String> getVertexSchema() {
        return self.getAllProperties().keySet();
    }

    @Override
    public HashableTreeMap<String, String> getValues() {
        HashableTreeMap<String,String> toret = new HashableTreeMap<>();
        Map<String, Object> p = self.getAllProperties();
        for (String k : p.keySet()) {
            toret.put(k,p.get(k).toString());
        }
        return toret;
    }

    @Override
    public String getAttribute(String attr) {
        return this.self.hasProperty(attr) ? this.self.getProperty(attr).toString(): null;
    }

    @Override
    public void updateAll(Map<String, String> externMap) {
        for (String key : externMap.keySet()) {
            this.self.setProperty(key,externMap.get(key));
        }
    }

    @Override
    public HashableHashSet<String> getType() {
        HashableHashSet<String> toret = new HashableHashSet<>();
        for (Label k : self.getLabels()) {
            toret.add(k.name());
        }
        return toret;
    }

    @Override
    public boolean removeNeighbour(AbstractNode dst) {
        throw new RuntimeException("Error: this action is not implemented (removeNeighbour)");
    }

    @Override
    public boolean hasNeighbour(AbstractNode right) {
        throw new RuntimeException("Error: this action is not implemented (hasNeighbour)");
    }

    public Node getRaw() {
        return self;
    }

}

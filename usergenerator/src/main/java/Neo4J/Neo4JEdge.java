/*
 * Neo4JEdge.java
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

import Neo4J.general.GeneralEdge;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import java.util.Map;

/**
 * Mapping the current Neo4J Edge into a Kripke edge
 */
public class Neo4JEdge extends GeneralEdge {

    private Relationship edge;

    public Neo4JEdge(final Neo4JGraph owner, Relationship edge) {
        this(owner,edge.getStartNode(),edge.getEndNode());
    }

    public Neo4JEdge(final Neo4JGraph owner, Node rsrc, Node rdst) {
        super(new Neo4JVertex(rsrc, owner,rsrc.getId()),new Neo4JVertex(rdst, owner,rdst.getId()));
        this.edge = new Relationship() {
            @Override
            public long getId() {
                return 0;
            }

            @Override
            public void delete() {
                owner.removeEdge(src,dst);
            }

            @Override
            public Node getStartNode() {
                return rsrc;
            }

            @Override
            public Node getEndNode() {
                return rdst;
            }

            @Override
            public Node getOtherNode(Node node) {
                return node.equals(rsrc) ? rsrc : rdst;
            }

            @Override
            public Node[] getNodes() {
                return new Node[]{rsrc,rdst};
            }

            @Override
            public RelationshipType getType() {
                return RelationshipType.withName(owner.getGlobalEdgeLabel());
            }

            @Override
            public boolean isType(RelationshipType relationshipType) {
                return relationshipType.name().equals(owner.getGlobalEdgeLabel());
            }

            @Override
            public GraphDatabaseService getGraphDatabase() {
                return owner.getRaw();
            }

            /**
             * Current edges have no properties
             * @param s
             * @return
             */
            @Override
            public boolean hasProperty(String s) {
                return false;
            }

            @Override
            public Object getProperty(String s) {
                return null;
            }

            @Override
            public Object getProperty(String s, Object o) {
                return null;
            }

            @Override
            public void setProperty(String s, Object o) {
            }

            @Override
            public Object removeProperty(String s) {
                return null;
            }

            @Override
            public Iterable<String> getPropertyKeys() {
                return null;
            }

            @Override
            public Map<String, Object> getProperties(String... strings) {
                return null;
            }

            @Override
            public Map<String, Object> getAllProperties() {
                return null;
            }
        };
    }

    public Relationship raw() {
        return edge;
    }

}
